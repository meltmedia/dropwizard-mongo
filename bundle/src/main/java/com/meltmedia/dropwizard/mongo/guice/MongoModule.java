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
