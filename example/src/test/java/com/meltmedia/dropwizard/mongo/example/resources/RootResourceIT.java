package com.meltmedia.dropwizard.mongo.example.resources;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.List;

import javax.ws.rs.core.UriBuilder;

import io.dropwizard.testing.junit.DropwizardAppRule;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.meltmedia.dropwizard.mongo.example.ExampleApplication;
import com.meltmedia.dropwizard.mongo.example.ExampleConfiguration;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;


public class RootResourceIT {

  @ClassRule
  public static final DropwizardAppRule<ExampleConfiguration> RULE =
          new DropwizardAppRule<ExampleConfiguration>(ExampleApplication.class, "conf/example.yml");
  
  public static UriBuilder rootPath() {
    return UriBuilder.fromUri(String.format("http://localhost:%d", RULE.getLocalPort()));
  }
  
  public static GenericType<List<String>> STRING_LIST = new GenericType<List<String>>(){};
  
  Client client;
  
  @Before
  public void setUp() {
    client = new Client();
  }
  
  @After
  public void tearDown() {
    client.destroy();
  }
 
  @Test
  public void shouldCreateNewDocument() {
    ClientResponse response = postDocument("test", "{\"name\": \"value\"}");

    assertThat(response.getStatus(), equalTo(201));
    assertThat(response.getHeaders().get("Location"), notNullValue());
  }
  
  @Test
  public void shouldListDocuments() {
    removeCollection("test2");

    String id1 = postDocument("test2", "{\"name\": \"value1\"}").getHeaders().get("X-Document-ID").get(0);
    String id2 = postDocument("test2", "{\"name\": \"value2\"}").getHeaders().get("X-Document-ID").get(0);
    
    List<String> ids = listCollection("test2");
    
    assertThat(ids, containsInAnyOrder(id1, id2));
  }
  
  @Test
  public void shouldListCollections() {
    removeCollection("test");
    
    postDocument("test", "{\"name\": \"value1\"}");
    
    List<String> collections = listCollections();
    
    assertThat(collections.contains("test"), equalTo(true));
  }
  
  public ClientResponse postDocument( String collection, String document ) {
    return client.resource(rootPath().path(collection).build())
        .entity(document, "application/json")
        .post(ClientResponse.class);    
  }
  
  public List<String> listCollection( String collection ) {
    return client.resource(rootPath().path("test2").build())
        .get(STRING_LIST);
  }
  
  public ClientResponse removeCollection( String collection ) {
    return client.resource(rootPath().path(collection).build())
        .delete(ClientResponse.class);
  }
  
  public List<String> listCollections() {
    return client.resource(rootPath().build()).get(STRING_LIST);
  }
}
