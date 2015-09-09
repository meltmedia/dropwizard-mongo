package com.meltmedia.dropwizard.jongo;

import static com.meltmedia.dropwizard.jongo.Functions.consumerRequired;
import static com.meltmedia.dropwizard.jongo.Functions.mapSupplier;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jongo.Jongo;

import com.meltmedia.dropwizard.mongo.MongoConfiguration;
import com.mongodb.MongoClient;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class JongoBundle<C extends Configuration> implements ConfiguredBundle<C> {
  public static class Builder<C extends Configuration> {
    AtomicReference<C> configuration = new AtomicReference<>();
    Consumer<JongoBuilder> builderOps = b->{};
    public Builder<C> addJongoBuilderOps( Consumer<JongoBuilder> op ) {
      this.builderOps = builderOps.andThen(op);
      return this;
    }

    public Builder<C> withClient(Supplier<MongoClient> client) {
      return addJongoBuilderOps(op->op.withClient(client));
    }

    public Builder<C> withDatabaseName( Supplier<String> databaseName ) {
      return addJongoBuilderOps(op->op.withDatabaseName(databaseName));
    }

    public Builder<C> withDatabaseName( Function<C, MongoConfiguration> mongoConfiguration ) {
      Function<C, String> mapName = mongoConfiguration
        .andThen(MongoConfiguration::getDatabase);
      return addJongoBuilderOps(op->op
        .withDatabaseName(
          mapSupplier(configuration::get, mapName)));
    }

    public JongoBundle<C> build() {
      return new JongoBundle<C>(builderOps, configuration::set);
    }
  }
  Consumer<JongoBuilder> builderOps = b->{};
  Consumer<C> defineConfiguration = consumerRequired("defineConfiguraiton");
  JongoBuilder builder = new JongoBuilder();
  
  Jongo jongo;

  public JongoBundle( Consumer<JongoBuilder> builderOps, Consumer<C> defineConfiguration ) {
    this.builderOps = builderOps;
    this.defineConfiguration = defineConfiguration;
  }

  @Override
  public void initialize( Bootstrap<?> bootstrap ) {
    builderOps.accept(builder);
  }

  @Override
  public void run( C configuration, Environment environment ) throws Exception {
    defineConfiguration.accept(configuration);
    jongo = builder.build();
  }

  public Jongo getJongo() {
    return this.jongo;
  }

}
