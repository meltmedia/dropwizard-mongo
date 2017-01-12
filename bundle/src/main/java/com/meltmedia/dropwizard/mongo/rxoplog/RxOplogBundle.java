package com.meltmedia.dropwizard.mongo.rxoplog;

import java.util.function.Consumer;
import java.util.function.Function;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * A bundle that provides a RxJava2 oplog service.
 * 
 * @author Christian Trimble
 */
public class RxOplogBundle<C extends Configuration> implements ConfiguredBundle<C> {
  public static class Builder<C extends Configuration> {
    Function<C, Consumer<RxOplogService.Builder>> builderOps;

    public Builder<C> with( Function<C, Consumer<RxOplogService.Builder>> builderOps ) {
      this.builderOps = builderOps;
      return this;
    }
    
    public RxOplogBundle<C> build() {
      return new RxOplogBundle<C>(builderOps);
    }
  }

  public static <C extends Configuration> Builder<C> builder() {
    return new Builder<C>();
  }

  Function<C, Consumer<RxOplogService.Builder>> builderOps;
  RxOplogService service;
  RxOplogHealthCheck healthCheck;
  
  public RxOplogBundle( Function<C, Consumer<RxOplogService.Builder>> builderOps ) {
    this.builderOps = builderOps;
  }
  
  @Override
  public void initialize( Bootstrap<?> arg0 ) {
  }

  @Override
  public void run( C config, Environment env ) throws Exception {
    service = RxOplogService.builder()
      .with(builderOps.apply(config))
      .build();
    
    healthCheck = new RxOplogHealthCheck(service);
    
    env.lifecycle().manage(healthCheck);
    env.healthChecks().register("mongo-oplog", healthCheck);
  }
  
  public RxOplogService getOplogService() {
    return service;
  }
}
