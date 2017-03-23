package com.meltmedia.dropwizard.mongo.example;

import java.util.function.Consumer;

import com.meltmedia.dropwizard.mongo.MongoClientBuilder;
import com.mongodb.MongoClient;

import io.dropwizard.cli.ConfiguredCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;

public class Commands {
  public static void register(Bootstrap<ExampleConfiguration> bootstrap) {
    bootstrap.addCommand(new ListCollections());
  }
  public static class ListCollections extends ConfiguredCommand<ExampleConfiguration> {

    protected ListCollections() {
      super("list-collections", "lists the collections in the configured database.");
    }

    @Override
    protected void run( Bootstrap<ExampleConfiguration> bootstrap, Namespace namespace, ExampleConfiguration configuration ) throws Exception {
      try( MongoClient client = new MongoClientBuilder()
        .withConfiguration(configuration.getMongo())
        .build() ) {
        client.getDatabase(configuration.getMongo().getDatabase())
          .listCollectionNames()
          .forEach((Consumer<String>)System.out::println);
      }
    }
  }
}
