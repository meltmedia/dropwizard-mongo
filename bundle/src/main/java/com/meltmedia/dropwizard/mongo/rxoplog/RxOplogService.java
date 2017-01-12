package com.meltmedia.dropwizard.mongo.rxoplog;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.bson.BsonTimestamp;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.meltmedia.dropwizard.mongo.rxoplog.NamespaceMatcher;
import com.mongodb.BasicDBObject;
import com.mongodb.CursorType;
import com.mongodb.MongoClient;
import com.mongodb.MongoInterruptedException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * A service that provides a hot sharable Observable for the oplog.  The hot observable
 * has no back pressure support, so it is more appropriate for signaling change in a
 * collection, using a sampling operator like throttleLast, rather than for processing
 * each oplog event.
 * 
 * @author Christian Trimble
 *
 */
public class RxOplogService {
  public static Logger logger = LoggerFactory.getLogger(RxOplogService.class);

  public static class Builder {
    private Supplier<MongoClient> mongoClient;
    private List<Consumer<NamespaceMatcher.Builder>> namespaces = Lists.newArrayList();

    public Builder withMongoClient(Supplier<MongoClient> mongoClient) {
      this.mongoClient = mongoClient;
      return this;
    }

    public Builder matchNamespaces( Consumer<NamespaceMatcher.Builder> ns ) {
      namespaces.add(ns);
      return this;
    }

    public Builder matchDatabase( String name ) {
      matchNamespaces(ns->ns.matchDatabase(name));
      return this;
    }

    public Builder with( Consumer<Builder> builderOps ) {
      builderOps.accept(this);
      return this;
    }

    public RxOplogService build() {
      if( mongoClient == null ) throw new IllegalArgumentException("mongo client required");
      return new RxOplogService(mongoClient, namespaces);
    }
  }

  public static Builder builder() { return new Builder(); }

  public RxOplogService( Supplier<MongoClient> mongoClient, List<Consumer<NamespaceMatcher.Builder>> namespaces ) {
    this.mongoClient = mongoClient;
    this.namespaces = namespaces;
  }

  Supplier<MongoClient> mongoClient;
  List<Consumer<NamespaceMatcher.Builder>> namespaces;

  volatile BsonTimestamp ts;
  BehaviorSubject<Status> statusSubject = BehaviorSubject.createDefault(new Status(State.NO_SUBSCRIBERS, "waiting for subscribers"));

  private NamespaceMatcher buildMatcher() {
    return NamespaceMatcher.builder()
      .with(builder->namespaces.forEach(nsOps->nsOps.accept(builder)))
      .build();
  }

  public Observable<Document> oplog = Observable.<Document>create(emitter->{
    Disposable disposable = Schedulers.io().scheduleDirect(()->{
        NamespaceMatcher nsMatcher = buildMatcher();

        logger.info("oplog watch starting");
        try {
          MongoCollection<Document> collection = null;

          statusSubject.onNext(new Status(State.GET_OPLOG, "getting reference to oplog.rs"));
          while( !emitter.isDisposed() && collection == null ) {
            try {
              collection = mongoClient.get()
              .getDatabase("local")
              .getCollection("oplog.rs");
            } catch( MongoInterruptedException ie ) {
              throw ie;
            } catch( Exception e ) {
              logger.warn("could not get oplog collection.", e);
              statusSubject.onNext(new Status(State.GET_OPLOG_FAILURE, e.getMessage()));
              TimeUnit.SECONDS.wait(30l);
            }
          }

          statusSubject.onNext(new Status(State.SYNC_TIMESTAMP, "synchronizing timestamp"));
          while( !emitter.isDisposed() && ts == null ) {
              // find the first oplog entry.
            try {
              FindIterable<Document> tsCursor = collection
                .find()
                .sort(new BasicDBObject("$natural", -1))
                .limit(1);

              Document tsDoc = tsCursor.first();

              if( tsDoc == null ) {
                logger.warn("could not find latest oplog document");
                statusSubject.onNext(new Status(State.SYNC_TIMESTAMP_ERROR, "cannot find latest oplog document, retrying..."));
                TimeUnit.SECONDS.sleep(30l);
                continue;
              }

              ts = (BsonTimestamp) tsDoc.get("ts");

              // if this document matches, emit it?
            } catch( MongoInterruptedException ie ) {
              throw ie;
            } catch( Exception e ) {
              logger.warn("could not find latest oplog document", e);
              statusSubject.onNext(new Status(State.SYNC_TIMESTAMP_ERROR, e.getMessage()));
              TimeUnit.SECONDS.sleep(30l);
              continue;
            }
          }

          logger.info("latest timestamp is "+ts);
          statusSubject.onNext(new Status(State.TIMESTAMP_SYCHRONIZED, "ready to read oplog entries"));

          // watch the oplog, while we can.
          while( !emitter.isDisposed() ) {
            logger.debug("finding more oplog entries");

            // build the query
            BasicDBObject query = new BasicDBObject("ts", new BasicDBObject("$gt", ts));
            nsMatcher.query().ifPresent(nsQuery->query.append("ns", nsQuery));

            if( logger.isDebugEnabled() ) {
              logger.debug("Oplog query:"+query.toJson());
            }

            try (
            com.mongodb.client.MongoCursor<Document> docCursor = collection
              .find(query)
              .cursorType(CursorType.TailableAwait)
              .noCursorTimeout(true)
              .oplogReplay(true)
              .maxAwaitTime(1, TimeUnit.SECONDS)
              .iterator()) {

            while( !emitter.isDisposed() && docCursor.hasNext() ) {
              statusSubject.onNext(new Status(State.READING, "reading oplog entries"));
              Document document = docCursor.next();
              emitter.onNext(document);
              ts = (BsonTimestamp)document.get("ts");
            }

            logger.debug("oplog cursor out of results");
            } catch( MongoInterruptedException ie ) {
              throw ie;
            }
            catch( Exception e ) {
              logger.warn("oplog cursor threw an exception", e);
              statusSubject.onNext(new Status(State.READ_ERROR, e.getMessage()));
              TimeUnit.SECONDS.sleep(30l);
            }
          }

          emitter.onComplete();
          statusSubject.onNext(new Status(State.NO_SUBSCRIBERS, "no subscribers"));

          logger.info("oplog watch terminating");
        }
        catch( MongoInterruptedException ie ) {
          Thread.currentThread().interrupt();
          if( !emitter.isDisposed() ) {
            logger.debug("oplog watch interrupted", ie);
          }
          emitter.onComplete();
          statusSubject.onNext(new Status(State.NO_SUBSCRIBERS, "no subscribers"));
        }
        catch( InterruptedException ie ) {
          Thread.currentThread().interrupt();
          if( !emitter.isDisposed() ) {
            logger.debug("oplog watch interrupted", ie);
          }
          emitter.onComplete();
          statusSubject.onNext(new Status(State.NO_SUBSCRIBERS, "no subscribers"));
        }
        catch( Exception e ) {
          logger.warn("oplog watch terminating due to exception", e);
          statusSubject.onNext(new Status(State.TERMINAL_ERROR, e.getMessage()));
          emitter.onError(e);
        }
      });
    emitter.setDisposable(disposable);
  }).share();

  public Observable<Document> getOplog() {
    return oplog;
  }

  public Observable<Document> getOplog( String database, String collection ) {
    String ns = database+"."+collection;
    return getOplog()
      .filter(doc->ns.equals(doc.get("ns")));
  }

  public Observable<Status> getStatus() {
    return statusSubject.distinctUntilChanged();
  }

  public static class Status {
    private State state;
    private String message;

    public Status( State state, String message ) {
      this.state = state;
      this.message = message;
    }

    public State getState() {
      return state;
    }

    public String getMessage() {
      return message;
    }

    @Override
    public boolean equals( Object o ) {
      if (o == null) return false;
      if (getClass() != o.getClass()) return false;
      final Status other = (Status) o;
      return Objects.equal(this.state, other.state)
          && Objects.equal(this.message, other.message);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(
        this.state, this.message);
    }
  }

  public static enum State {
    NO_SUBSCRIBERS(true),
    GET_OPLOG(true),
    GET_OPLOG_FAILURE(false),
    SYNC_TIMESTAMP(true),
    SYNC_TIMESTAMP_ERROR(false),
    TIMESTAMP_SYCHRONIZED(true),
    READING(true),
    READ_ERROR(false),
    TERMINAL_ERROR(false);
    private final boolean healthy;
    State(boolean healthy) {
      this.healthy = healthy;
    }
    public boolean healthy() { return healthy; }
  }
}
