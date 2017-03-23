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

import static java.util.Optional.ofNullable;

import java.util.function.Consumer;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class MongoBundle<C extends Configuration> implements ConfiguredBundle<C> {
  public static Logger log = LoggerFactory.getLogger(MongoBundle.class);

  public static class Builder<C extends Configuration> {
    protected Function<C, MongoConfiguration> configurationAccessor;
    protected String healthCheckName = "mongo";
    protected Consumer<MongoClientOptions.Builder> defaultOptions = MongoClientBuilder.createDefaultOptions();
    
    public Builder<C> withConfiguration( Function<C, MongoConfiguration> configurationAccessor ) {
      this.configurationAccessor = configurationAccessor;
      return this;
    }
    
    public Builder<C> withHealthCheckName( String healthCheckName ) {
      this.healthCheckName = healthCheckName;
      return this;
    }
    
    public Builder<C> addDefaultClientOptions( Consumer<MongoClientOptions.Builder> defaultOptions ) {
      this.defaultOptions = this.defaultOptions.andThen(defaultOptions);
      return this;
    }
    
    public MongoBundle<C> build() {
      ofNullable(configurationAccessor)
        .orElseThrow(()->new IllegalArgumentException("configuration accessor is required."));
      ofNullable(defaultOptions)
        .orElseThrow(()->new IllegalArgumentException("defaultOptions is required."));
      ofNullable(healthCheckName)
        .orElseThrow(()->new IllegalArgumentException("healthCheckName is required."));

      return new MongoBundle<C>(configurationAccessor, defaultOptions, healthCheckName);
    }
  }
  
  public static <C extends Configuration> Builder<C> builder() {
    return new Builder<C>();
  }
  
  protected Function<C, MongoConfiguration> configurationAccessor;
  protected MongoConfiguration mongoConfiguration;
  protected Consumer<com.mongodb.MongoClientOptions.Builder> defaultOptions;
  protected String healthCheckName;
  protected MongoClient client;
  protected DB db;

  private MongoBundle(Function<C, MongoConfiguration> configurationAccessor, Consumer<com.mongodb.MongoClientOptions.Builder> defaultOptions, String healthCheckName) {
    this.configurationAccessor = configurationAccessor;
    this.defaultOptions = defaultOptions;
    this.healthCheckName = healthCheckName;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void run(C configuration, Environment environment) throws Exception {
    mongoConfiguration = configurationAccessor.apply(configuration);
    client = new MongoClientBuilder()
      .withDefaultOptions(defaultOptions)
      .withConfiguration(mongoConfiguration)
      .build();
    environment.lifecycle().manage(new Managed() {
      @Override public void start() throws Exception {}
      @Override public void stop() throws Exception { client.close(); }
    });
    db = client.getDB(mongoConfiguration.getDatabase());
    environment.healthChecks().register(healthCheckName, new MongoHealthCheck(db));
  }

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
  }
  
  public MongoClient getClient() {
    return client;
  }
  
  public DB getDB() {
    return db;
  }
  
  public MongoConfiguration getConfiguration() {
    return mongoConfiguration;
  }

}
