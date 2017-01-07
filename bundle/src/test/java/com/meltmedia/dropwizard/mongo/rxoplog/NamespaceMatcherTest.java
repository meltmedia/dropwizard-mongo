package com.meltmedia.dropwizard.mongo.rxoplog;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

import com.meltmedia.dropwizard.mongo.rxoplog.NamespaceMatcher;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class NamespaceMatcherTest {
  @Test
  public void matchAll() {
    NamespaceMatcher matcher = NamespaceMatcher.builder()
      .build();
    
    Optional<Object> queryOptional = matcher.query();
    
    assertThat(queryOptional.isPresent(), equalTo(false));
  }
  
  @Test
  public void matchOneDatabaseAllCollections() {
    NamespaceMatcher matcher = NamespaceMatcher.builder()
      .matchDatabase("example", c->c
        .matchAllCollections())
      .build();
    
    Object regex = matcher.query()
      .get();
 
    assertThat(regex.toString(), containsString("example."));
  }

  @Test
  public void matchOneDatabaseOneCollection() {
    NamespaceMatcher matcher = NamespaceMatcher.builder()
      .matchDatabase("example", c->c
        .matchCollections("test"))
      .build();
    
    BasicDBList in = (BasicDBList)matcher.query()
      .map(BasicDBObject.class::cast)
      .get()
      .get("$in");
    
    List<String> values = in.stream()
      .map(String.class::cast)
      .collect(Collectors.toList());
    
    assertThat(values, containsInAnyOrder("example.test"));
  }
  
  @Test
  public void matchOneDatabaseMultipleCollections() {
    NamespaceMatcher matcher = NamespaceMatcher.builder()
      .matchDatabase("example", c->c
        .matchCollections("test1", "test2"))
      .build();
    
    BasicDBList in = (BasicDBList)matcher.query()
      .map(BasicDBObject.class::cast)
      .get()
      .get("$in");
    
    List<String> values = in.stream()
      .map(String.class::cast)
      .collect(Collectors.toList());
    
    assertThat(values, containsInAnyOrder("example.test1", "example.test2"));
  }

  
  @Test
  public void matchMultipleDatabasesMultipleCollections() {
    NamespaceMatcher matcher = NamespaceMatcher.builder()
      .matchDatabase("example1", c->c
        .matchCollections("test1", "test2"))
      .matchDatabase("example2", c->c
        .matchCollections("test1", "test2"))
      .build();
    
    BasicDBList in = (BasicDBList)matcher.query()
      .map(BasicDBObject.class::cast)
      .get()
      .get("$in");
    
    List<String> values = in.stream()
      .map(String.class::cast)
      .collect(Collectors.toList());
    
    assertThat(values, containsInAnyOrder(
      "example1.test1",
      "example1.test2",
      "example2.test1",
      "example2.test2"));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void matchMultipleDatabasesAllCollections() {
    NamespaceMatcher matcher = NamespaceMatcher.builder()
      .matchDatabase("example1", c->c
        .matchAllCollections())
      .matchDatabase("example2", c->c
        .matchAllCollections())
      .build();
    
    BasicDBList or = (BasicDBList)matcher.query()
      .map(BasicDBObject.class::cast)
      .get()
      .get("$or");
    
    List<String> regexs = or.stream()
      .map(Object::toString)
      .collect(Collectors.toList());
    
    assertThat(regexs, containsInAnyOrder(
      containsString("example1."),
      containsString("example2.")));
  }
  
  @SuppressWarnings("unchecked")
  @Test
  public void matchComplexQuery() {
    NamespaceMatcher matcher = NamespaceMatcher.builder()
      .matchDatabase("example1", c->c
        .matchAllCollections())
      .matchDatabase("example2", c->c
        .matchCollections("test1", "test2"))
      .matchDatabase("example3", c->c
        .matchAllCollections())
      .matchDatabase("example4", c->c
        .matchCollections("test1", "test2"))
      .build();
    
    BasicDBList or = (BasicDBList)matcher.query()
      .map(BasicDBObject.class::cast)
      .get()
      .get("$or");
    
    List<String> specificCollections = or.stream()
      .limit(1l)
      .map(BasicDBObject.class::cast)
      .map(o->o.get("$in"))
      .map(BasicDBList.class::cast)
      .flatMap(BasicDBList::stream)
      .map(String.class::cast)
      .collect(Collectors.toList());
    
    assertThat( specificCollections, containsInAnyOrder(
      "example2.test1",
      "example2.test2",
      "example4.test1",
      "example4.test2"));
    
    List<String> regexs = or.stream()
      .skip(1)
      .map(Object::toString)
      .collect(Collectors.toList());

    assertThat(regexs, containsInAnyOrder(
      containsString("example1."),
      containsString("example3.")));
  }


}
