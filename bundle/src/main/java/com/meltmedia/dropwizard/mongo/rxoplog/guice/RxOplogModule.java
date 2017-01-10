package com.meltmedia.dropwizard.mongo.rxoplog.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.meltmedia.dropwizard.mongo.rxoplog.RxOplogBundle;
import com.meltmedia.dropwizard.mongo.rxoplog.RxOplogService;

public class RxOplogModule extends AbstractModule {
  
  RxOplogBundle<?> bundle;
  
  public RxOplogModule( RxOplogBundle<?> bundle ) {
    this.bundle = bundle;
  }

  @Override
  protected void configure() {
  } 

  @Provides
  @Singleton
  public RxOplogService mongoConfiguration() {
    return bundle.getOplogService();
  }

}
