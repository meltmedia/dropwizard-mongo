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

Then include the bundle in the `initialize` method of your application.

```
import com.meltmedia.dropwizard.mongo.MongoBundle;

...
MongoBundle<C> mongoBundle;

@Override
public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
  bootstrap.addBundle(mongoBundle = MongoBundle.<ExampleConfiguration>builder()
    .withConfiguration(ExampleConfiguration::getMongo)
    .build());
}
```

Finally, use the bundle to access the client and database in your `run` method.

```
@Override
public void run(ExampleConfiguration config, Environment env) throws Exception {
  MongoClient client = mongoBundle.getClient();
  DB db = mongoBundle.getDB();
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

This project builds with Java8 and Maven 3.  After cloning the repo, install the bundle from the root of the project.

```
mvn clean install
```

### Integration Tests

You can also run integration tests while running the build.  First, you will need to
make sure the configuration passphrase is in the environment.

```
export EXAMPLE_PASSPHRASE='correct horse battery staple'
```

Then run the build with the `integration-tests` profile.

```
mvn clean install -P integration-tests
```

## Contributing

This project accepts PRs, so feel free to fork the project and send contributions back.

### Formatting

This project contains formatters to help keep the code base consistent.  The formatter will update Java source files and add headers to other files.  When running the formatter, I suggest the following procedure:

1. Make sure any outstanding stages are staged.  This will prevent the formatter from destroying your code.
2. Run `mvn format`, this will format the source and add any missing license headers.
3. If the changes look good and the project still compiles, add the formatting changes to your staged code.

If things go wrong, you can run `git checkout -- .` to drop the formatting changes. 
