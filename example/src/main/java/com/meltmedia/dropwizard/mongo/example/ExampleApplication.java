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
package com.meltmedia.dropwizard.mongo.example;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import com.meltmedia.dropwizard.crypto.CryptoBundle;
import com.meltmedia.dropwizard.mongo.MongoBundle;
import com.meltmedia.dropwizard.mongo.MongoClientFactory.Credentials;
import com.meltmedia.dropwizard.mongo.example.resources.RootResource;
import com.meltmedia.jackson.crypto.Encrypted;

public class ExampleApplication extends Application<ExampleConfiguration> {
  public static void main(String[] args) throws Exception {
    new ExampleApplication().run(args);
  }

  @Override
  public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
    bootstrap.addBundle(CryptoBundle.builder()
        .withMixins(om->{
          om.addMixInAnnotations(Credentials.class, EncryptCredentialsConfiguration.class);
        }).build());
    bootstrap.addBundle(MongoBundle.<ExampleConfiguration>builder()
        .withFactory(ExampleConfiguration::getMongo)
        .build());
  }

  @Override
  public void run(ExampleConfiguration config, Environment env) throws Exception {
    env.jersey().register(new RootResource(config.getMongo().getDB(env)));
  }

  public static interface EncryptCredentialsConfiguration {
    @Encrypted
    public String getPassword();
  }
}
