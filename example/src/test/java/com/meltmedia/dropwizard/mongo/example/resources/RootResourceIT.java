/**
 * Copyright (C) 2014 meltmedia (christian.trimble@meltmedia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meltmedia.dropwizard.mongo.example.resources;

import static org.hamcrest.Matchers.*;
import static javax.ws.rs.client.Entity.entity;
import static org.hamcrest.MatcherAssert.*;

import java.util.List;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import io.dropwizard.testing.junit.DropwizardAppRule;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.meltmedia.dropwizard.mongo.example.ExampleApplication;
import com.meltmedia.dropwizard.mongo.example.ExampleConfiguration;
import org.glassfish.jersey.client.JerseyClient;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.ClientResponse;

public class RootResourceIT {

  @ClassRule
  public static final DropwizardAppRule<ExampleConfiguration> RULE =
      new DropwizardAppRule<ExampleConfiguration>(ExampleApplication.class, "conf/example.yml");

  public static UriBuilder rootPath() {
    return UriBuilder.fromUri(String.format("http://localhost:%d", RULE.getLocalPort()));
  }

  public static GenericType<List<String>> STRING_LIST = new GenericType<List<String>>() {
  };

  JerseyClient client;

  @Before
  public void setUp() {
    client = JerseyClientBuilder.createClient();
  }

  @After
  public void tearDown() {
    client.close();
  }

  @Test
  public void shouldCreateNewDocument() {
    Response response = postDocument("test", "{\"name\": \"value\"}");

    assertThat(response.getStatus(), equalTo(201));
    assertThat(response.getHeaders().get("Location"), notNullValue());
  }

  @Test
  public void shouldListDocuments() {
    removeCollection("test2");

    String id1 =
        (String)postDocument("test2", "{\"name\": \"value1\"}")
        .getHeaders()
        .get("X-Document-ID")
        .get(0);
    String id2 =
        (String)postDocument("test2", "{\"name\": \"value2\"}")
        .getHeaders()
        .get("X-Document-ID")
        .get(0);

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

  public Response postDocument(String collection, String document) {
    return client.target(rootPath().path(collection).build())
      .request("application/json")
      .post(entity(document, "application/json"));
  }

  public List<String> listCollection(String collection) {
    return client.target(rootPath().path("test2").build())
      .request("application/json")
      .get(STRING_LIST);
  }

  public ClientResponse removeCollection(String collection) {
    return client.target(rootPath().path(collection).build())
      .request()
      .delete(ClientResponse.class);
  }

  public List<String> listCollections() {
    return client.target(rootPath().build())
      .request("application/json")
      .get(STRING_LIST);
  }
}
