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
package com.meltmedia.dropwizard.mongo;

import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

public class MongoClientFactory {
  public static Logger log = LoggerFactory.getLogger(MongoBundle.class);
  
  @JsonIgnore
  protected MongoClient client;
  @JsonIgnore
  protected DB db;

  protected List<Server> seeds = new ArrayList<>();
  protected Credentials credentials;
  @NotNull
  protected String database;
  protected String writeConcern = "ACKNOWLEDGED";
  protected boolean enabled = true;

  public List<Server> getSeeds() {
    return seeds;
  }

  public void setSeeds( List<Server> seeds ) {
    this.seeds = seeds;
  }

  public String getWriteConcern() {
    return writeConcern;
  }

  public void setWriteConcern( String writeConcern ) {
    this.writeConcern = writeConcern;
  }
  
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled( boolean enabled ) {
    this.enabled = enabled;
  }

  public Credentials getCredentials() {
    return credentials;
  }

  public void setCredentials( Credentials credentials ) {
    this.credentials = credentials;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase( String database ) {
    this.database = database;
  }
  
  public MongoClient getClient(Environment environment) {
    if( client == null ) {
      client = buildClient();
      
      environment.lifecycle().manage(new Managed() {
        @Override public void start() throws Exception {}
        @Override public void stop() throws Exception { client.close(); }
      });
    }
    return client;
  }
  
  public DB getDB(Environment environment) {
    if( db == null ) {
      db = getClient(environment).getDB(getDatabase());
    }
    return db;
  }
    
  MongoClient buildClient() {
    try {
      // build the seed server list.
      List<ServerAddress> servers = new ArrayList<>();
      for( Server seed : getSeeds() ) {
        servers.add(new ServerAddress(seed.getHost(), seed.getPort()));
      }

      log.info("Found {} mongo seed servers", servers.size());
      for( ServerAddress server : servers ) {
        log.info("Found mongo seed server {}:{}", server.getHost(), server.getPort());
      }

      // build the credentials
      Credentials credentialConfig = getCredentials();
      List<MongoCredential> credentials = credentialConfig == null ?
        Collections.<MongoCredential>emptyList() :
        Collections.singletonList(MongoCredential.createMongoCRCredential(credentialConfig.getUserName(), getDatabase(), credentialConfig
        .getPassword().toCharArray()));

      if( credentials.isEmpty() ) {
        log.info("Found {} mongo credentials.", credentials.size());
      }
      else {
        for( MongoCredential credential : credentials ) {
          log.info("Found mongo credential for {} on database {}.", credential.getUserName(), credential.getSource());
        }
      }

      // build the options.
      MongoClientOptions options = new MongoClientOptions.Builder().writeConcern(writeConcern(getWriteConcern())).build();

      log.info("Mongo database is {}", getDatabase());

      return new MongoClient(servers, credentials, options);
    } catch( UnknownHostException e ) {
      throw new RuntimeException("Could not configure MongoDB client.", e);
    }    
  }

  static WriteConcern writeConcern( String writeConcernString ) {
    WriteConcern writeConcern = WriteConcern.valueOf(writeConcernString);
    if( writeConcern == null ) {
      throw new IllegalArgumentException(String.format("Unknown mongo write concern %s", writeConcernString));
    }
    return writeConcern;
  }
  
  public static class Server {
    @NotNull
    protected String host;
    @NotNull
    protected Integer port;
    public String getHost() {
      return host;
    }
    public void setHost( String host ) {
      this.host = host;
    }
    public Integer getPort() {
      return port;
    }
    public void setPort( Integer port ) {
      this.port = port;
    }
  }
  
  public static class Credentials {
    @NotNull
    protected String userName;
    @NotNull
    protected String password;
    public String getUserName() {
      return userName;
    }
    public void setUserName( String userName ) {
      this.userName = userName;
    }
    public String getPassword() {
      return password;
    }
    public void setPassword( String password ) {
      this.password = password;
    }
  }


}
