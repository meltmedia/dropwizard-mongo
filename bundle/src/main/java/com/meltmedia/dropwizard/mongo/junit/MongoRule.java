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
package com.meltmedia.dropwizard.mongo.junit;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.mongodb.MongoClient;

import java.util.Arrays;
import java.util.List;

/**
 * A JUnit Rule for creating a mongo client. This rule is designed to be used as
 * a ClassRule.
 * 
 * @author Christian Trimble
 */
public class MongoRule
  implements TestRule
{
  MongoClient mongoClient;
  String host;
  int port;
  String user;
  String password;
  String authenticationDatabase;

  public MongoRule( String host, int port ) {
    this.host = host;
    this.port = port;
  }

  public MongoRule( String host, int port , String user, String password ) {
    this(host, port);
    this.user = user;
    this.password = password;
  }

  public MongoRule( String host, int port, String user, String password, String authenticationDatabase ) {
    this(host, port, user, password);
    this.authenticationDatabase = authenticationDatabase;
  }

  /**
   * Return an instance of the mongo client when this rule is in effect.
   * 
   * @return
   */
  public MongoClient getClient() {
    return mongoClient;
  }

  /**
   * Wraps the provided statement with code to create and then clean up a
   * MongoClient object.
   */
  @Override
  public Statement apply( final Statement statement, final Description description ) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          final MongoCredential credential = MongoCredential.createCredential(user,authenticationDatabase, password.toCharArray());
          final List<MongoCredential> creds = Arrays.asList(credential);
          final ServerAddress dbAddress = new ServerAddress(host, port);
          mongoClient = new MongoClient(dbAddress, creds);
          statement.evaluate();
        } finally {
          if( mongoClient != null ) {
            try {
              mongoClient.close();
            } catch( Exception e ) {
            }
            mongoClient = null;
          }
        }
      }
    };
  }
}
