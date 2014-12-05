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

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meltmedia.dropwizard.mongo.MongoClientFactory.Credentials;
import com.meltmedia.dropwizard.mongo.MongoClientFactory.Server;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class MongoBundle<C extends Configuration> implements ConfiguredBundle<C> {
  public static Logger log = LoggerFactory.getLogger(MongoBundle.class);
  public static interface FactoryAccessor<C extends Configuration> {
    public MongoClientFactory configuration(C configuration);
  }
  
  public static class Builder<C extends Configuration> {
    protected FactoryAccessor<C> factoryAccessor;
    protected String healthCheckName = "mongo";
    
    public Builder<C> withFactory( FactoryAccessor<C> factoryAccessor ) {
      this.factoryAccessor = factoryAccessor;
      return this;
    }
    
    public Builder<C> withHealthCheckName( String healthCheckName ) {
      this.healthCheckName = healthCheckName;
      return this;
    }
    
    public MongoBundle<C> build() {
      if( factoryAccessor == null ) {
        throw new IllegalArgumentException("configuration accessor is required.");
      }
      return new MongoBundle<C>(factoryAccessor, healthCheckName);
    }
  }
  
  public static <C extends Configuration> Builder<C> builder() {
    return new Builder<C>();
  }
  
  protected FactoryAccessor<C> factoryAccessor;
  protected String healthCheckName;

  public MongoBundle(FactoryAccessor<C> factoryAccessor, String healthCheckName) {
    this.factoryAccessor = factoryAccessor;
    this.healthCheckName = healthCheckName;
  }

  @Override
  public void run(C configuration, Environment environment) throws Exception {
    MongoClientFactory factory = factoryAccessor.configuration(configuration);
    environment.healthChecks().register(healthCheckName, new MongoHealthCheck(factory.getDB(environment)));
  }

  @Override
  public void initialize(Bootstrap<?> bootstrap) {
  }
  
}
