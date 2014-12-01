# Dropwizard Mongo

A Dropwizard bundle for MongoDB.

[![Build Status](https://travis-ci.org/meltmedia/dropwizard-mongo.svg)](https://travis-ci.org/meltmedia/dropwizard-mongo)

## Usage

### Maven

To use SNAPSHOTs of this project, you will need to include the sonatype repository in your POM.

```
<repositories>
    <repository>
        <snapshots>
        <enabled>true</enabled>
        </snapshots>
        <id>sonatype-nexus-snapshots</id>
        <name>Sonatype Nexus Snapshots</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
</repositories>
```

You will also need to include the project in your dependencies.

```
<dependency>
  <groupId>com.meltmedia.dropwizard</groupId>
  <artifactId>dropwizard-mongo</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Java

Define the MongoConfiguration class somewhere in your applications configuration.

```
import com.meltmedia.dropwizard.mongo.MongoConfiguration;

...

  @JsonProperty
  protected MongoConfiguration mongo;

  public MongoConfiguration getMongo() {
    return mongo;
  }
```

Then include the bundle in the `initialize` method of your application and keep a reference
to it.

```
import com.meltmedia.dropwizard.mongo.MongoBundle;

...

MongoBundle<ExampleConfiguration> mongoBundle;

@Override
public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
  bootstrap.addBundle(mongoBundle = MongoBundle.<ExampleConfiguration>builder()
    .withConfiguration(ExampleConfiguration::getMongo)
    .build());
}
```

Finally, use the bundle to access the client in your `run` method:

```
@Override
public void run(ExampleConfiguration config, Environment env) throws Exception {
  MongoClient client = mongoBundle.getClient();
  // do something cool.
}
```

### Configuration

Add the mongo configuraiton block to your applications config.

```
mongo:
  seeds:
  - host: localhost
    port: 27017
  database: example
  credentials:
    userName: example
    password: example
```

## Building

This project builds with Java8 and Maven 3.  Simply clone the repo and run

```
mvn clean install
```

from the root directory.

## Contributing

This project accepts PRs, so feel free to fork the project and send contributions back.

### Formatting

This project contains formatters to help keep the code base consistent.  The formatter will update Java source files and add headers to other files.  When running the formatter, I suggest the following procedure:

1. Make sure any outstanding stages are staged.  This will prevent the formatter from destroying your code.
2. Run `mvn format`, this will format the source and add any missing license headers.
3. If the changes look good and the project still compiles, add the formatting changes to your staged code.

If things go wrong, you can run `git checkout -- .` to drop the formatting changes. 
