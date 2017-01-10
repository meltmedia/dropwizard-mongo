package com.meltmedia.dropwizard.mongo.rxoplog;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import jersey.repackaged.com.google.common.collect.Lists;

public class NamespaceMatcher {
  public static class Builder {
    private Map<String, NamespaceMatcher.CollectionMatcher.Builder> collectionMatchers = Maps.newHashMap();
    
    public NamespaceMatcher.Builder matchDatabase( String database ) {
      return matchDatabase(database, c->c.matchAllCollections());
    }
    
    public NamespaceMatcher.Builder matchDatabase( String database, Consumer<NamespaceMatcher.CollectionMatcher.Builder> collections) {
      collections.accept(collectionMatchers.computeIfAbsent(database, k->NamespaceMatcher.CollectionMatcher.builder()));
      return this;
    }
    
    public NamespaceMatcher.Builder with( Consumer<NamespaceMatcher.Builder> ops ) {
      ops.accept(this);
      return this;
    }
    
    public NamespaceMatcher build() {
      return new NamespaceMatcher(collectionMatchers.entrySet().stream()
        .map(entry->new NamespaceMatcher.DatabaseMatcher(entry.getKey(), entry.getValue().build()))
        .collect(Collectors.toList()));
    }
  }
  
  public static class DatabaseMatcher {
    private String name;
    private NamespaceMatcher.CollectionMatcher collections;
    
    public DatabaseMatcher(String name, NamespaceMatcher.CollectionMatcher collections) {
      this.name = name;
      this.collections = collections;
    }
  
    public String getName() {
      return name;
    }
  
    public NamespaceMatcher.CollectionMatcher getCollections() {
      return collections;
    }
  }

  public static class CollectionMatcher {
    public static class Builder {
      private List<String> collections = Lists.newArrayList();
      private boolean allCollections = false;
      
      public Builder matchCollections( String... names) {
        collections.addAll(Arrays.asList(names));
        return this;
      }
      
      public Builder matchAllCollections() {
        allCollections = true;
        return this;
      }
      
      public CollectionMatcher build() {
        if( !allCollections && collections.isEmpty() ) throw new IllegalArgumentException("no collections specified");
        if( allCollections ) {
          return new CollectionMatcher();
        } else {
          return new CollectionMatcher(collections);
        }
      }
    }
    
    public static Builder builder() {
      return new Builder();
    }
  
    private List<String> collections;
    private boolean allCollections;
    
    public CollectionMatcher( List<String> collections ) {
      if( collections.isEmpty() ) { throw new IllegalArgumentException("collection names must be specified"); }
      this.collections = Lists.newArrayList(collections);
      this.allCollections = false;
    }
    
    public CollectionMatcher() {
      this.collections = Lists.newArrayList();
      this.allCollections = true;
    }
  
    public List<String> getCollections() {
      return collections;
    }
  
    public boolean isAllCollections() {
      return allCollections;
    }
  }

  public static NamespaceMatcher.Builder builder() { return new Builder(); }

  private List<NamespaceMatcher.DatabaseMatcher> databases;
  public NamespaceMatcher( List<NamespaceMatcher.DatabaseMatcher> databases ) {
    this.databases = databases;
  }
  public List<NamespaceMatcher.DatabaseMatcher> getDatabases() {
    return databases;
  }
  
  Stream<String> databasesWithCollections() {
    return getDatabases().stream()
      .filter(db->!db.getCollections().isAllCollections())
      .flatMap(db->db.getCollections().getCollections().stream().map(c->db.getName()+"."+c));
  }
  
  Optional<BasicDBObject> collectionQuery() {
    List<String> exactMatches = databasesWithCollections().collect(Collectors.toList());
    
    if( exactMatches.isEmpty() ) {
      return Optional.empty();
    } else {
      BasicDBList exactMatchList = new BasicDBList();
      exactMatchList.addAll(exactMatches);
      return Optional.of(
        new BasicDBObject("$in", exactMatchList));
    }
  }
  
  Stream<String> databasesWithoutCollections() {
    return getDatabases().stream()
      .filter(db->db.getCollections().isAllCollections())
      .map(NamespaceMatcher.DatabaseMatcher::getName);
  }
  
  List<Pattern> databaseQueries() {
    return databasesWithoutCollections()
      .map(name->Pattern.compile("^"+Pattern.quote(name+".")))
      .collect(Collectors.toList());
  }
  
  Optional<Object> query() {
    BasicDBList queries = new BasicDBList();
    
    collectionQuery().ifPresent(queries::add);
    queries.addAll(databaseQueries());
    
    if( queries.size() == 0 ) {
      return Optional.empty();
    } else if( queries.size() == 1 ) {
      return Optional.of(queries.get(0));
    } else {
      return Optional.of(new BasicDBObject("$or", queries));
    }
  }
}