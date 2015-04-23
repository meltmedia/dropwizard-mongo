package com.meltmedia.dropwizard.mongo.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.meltmedia.dropwizard.mongo.MongoBundle;
import com.meltmedia.dropwizard.mongo.MongoConfiguration;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoModule extends AbstractModule {
  
  MongoBundle<?> bundle;
  
  public MongoModule( MongoBundle<?> bundle ) {
    this.bundle = bundle;
  }

  @Override
  protected void configure() {
  } 

  @Provides
  @Singleton
  public MongoConfiguration mongoConfiguration() {
    return bundle.getConfiguration();
  }
  
  @Provides
  @Singleton
  public MongoClient client() {
    return bundle.getClient();
  }

  @Provides
  @Singleton
  public DB db() {
    return bundle.getDB();
  }
}
