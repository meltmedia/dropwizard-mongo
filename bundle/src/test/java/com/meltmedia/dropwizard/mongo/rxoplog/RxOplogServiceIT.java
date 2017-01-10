package com.meltmedia.dropwizard.mongo.rxoplog;

import org.bson.Document;
import org.junit.ClassRule;
import org.junit.Test;

import com.meltmedia.dropwizard.mongo.junit.MongoRule;
import com.meltmedia.dropwizard.mongo.rxoplog.RxOplogService;

import io.reactivex.observers.TestObserver;

import static org.hamcrest.Matchers.*;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.*;

/**
 * Integration tests for the RxOplogService.
 * 
 * @author Christian Trimble
 */
public class RxOplogServiceIT {
  @ClassRule
  public static MongoRule mongo = new MongoRule("localhost", 27017);
  
  @Test
  public void clientWorks() {
    Document document = mongo.getClient()
      .getDatabase("local")
      .getCollection("oplog.rs")
      .find()
      .limit(1)
      .first();
    
    assertThat(document, notNullValue());
  }
  
  @Test
  public void shouldRespondToChange() throws InterruptedException {
    RxOplogService service = RxOplogService.builder()
      .withMongoClient(mongo::getClient)
      .build();

    OnNextSemaphore<Document> s1 = new OnNextSemaphore<Document>();
    TestObserver<Document> o1 = new TestObserver<Document>(s1);

    service.getOplog()
      .subscribe(o1);
    
    // racing here.  need a way to wait for the service to have synced the timestamp
    // and be ready to emit the first onNext call.
    TimeUnit.SECONDS.sleep(1l);
    
    // insert a document into the collection.
    mongo.getClient().getDatabase("example").getCollection("test").insertOne(new Document().append("value", 3));
    
    s1.awaitOnNext(1, 10l, TimeUnit.SECONDS);
    
    o1.cancel();
    
    Optional<String> result = o1.values().stream().map(d->d.get("ns", String.class)).filter(ns->"example.test".equals(ns)).findFirst();
    
    assertThat("a change was found", result.isPresent(), equalTo(true));
  }
  
  @Test
  public void shouldFilterSpecificCollections() throws InterruptedException {
    RxOplogService service = RxOplogService.builder()
      .withMongoClient(mongo::getClient)
      .matchNamespaces(ns->ns
        .matchDatabase("example", db->db
          .matchCollections("test")))
      .build();
    
    OnNextSemaphore<Document> s1 = new OnNextSemaphore<Document>();
    TestObserver<Document> o1 = new TestObserver<Document>(s1);

    service.getOplog()
      .subscribe(o1);
    
    TimeUnit.SECONDS.sleep(1l);
    
    mongo.getClient()
      .getDatabase("example1")
      .getCollection("test")
      .insertOne(new Document().append("value", 1));
    
    mongo.getClient()
      .getDatabase("example")
      .getCollection("test")
      .insertOne(new Document().append("value", 2));
    
    s1.awaitOnNext(1, 10l, TimeUnit.SECONDS);
    
    o1.cancel();
    
    Set<String> ns = o1.values().stream()
      .map(d->(String)d.get("ns"))
      .collect(Collectors.toSet());
    
    assertThat(ns, containsInAnyOrder("example.test"));
  }
  
  
  @Test
  public void shouldFilterAllCollectionsInDB() throws InterruptedException {
    RxOplogService service = RxOplogService.builder()
      .withMongoClient(mongo::getClient)
      .matchNamespaces(ns->ns
        .matchDatabase("example", db->db
          .matchAllCollections()))
      .build();
    
    OnNextSemaphore<Document> s1 = new OnNextSemaphore<Document>();
    TestObserver<Document> o1 = new TestObserver<Document>(s1);

    service.getOplog().subscribe(o1);
    
    TimeUnit.SECONDS.sleep(1l);
    
    mongo.getClient()
      .getDatabase("example1")
      .getCollection("test")
      .insertOne(new Document().append("value", 1));
    
    mongo.getClient()
      .getDatabase("example")
      .getCollection("test")
      .insertOne(new Document().append("value", 2));
    
    s1.awaitOnNext(1, 10l, TimeUnit.SECONDS);
    
    o1.cancel();
    
    Set<String> ns = o1.values().stream()
      .map(d->(String)d.get("ns"))
      .collect(Collectors.toSet());
    
    assertThat(ns, containsInAnyOrder("example.test"));
  }
  
}
