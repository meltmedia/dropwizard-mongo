package com.meltmedia.dropwizard.mongo.junit;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.mongodb.MongoClient;

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

  public MongoRule( String host, int port ) {
    this.host = host;
    this.port = port;
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
          mongoClient = new MongoClient(host, port);
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
