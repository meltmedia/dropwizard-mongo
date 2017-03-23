package com.meltmedia.dropwizard.mongo;

import static java.util.Optional.ofNullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meltmedia.dropwizard.mongo.MongoConfiguration.Credentials;
import com.meltmedia.dropwizard.mongo.MongoConfiguration.Server;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;

public class MongoClientBuilder {
  public static Logger log = LoggerFactory.getLogger(MongoClientBuilder.class);
  protected MongoConfiguration configuration;
  protected Consumer<MongoClientOptions.Builder> defaultOptions = c->{};
  
  public MongoClientBuilder withConfiguration( MongoConfiguration configuration ) {
    this.configuration = configuration;
    return this;
  }
  
  public MongoClientBuilder withDefaultOptions( Consumer<MongoClientOptions.Builder> defaultOptions ) {
    this.defaultOptions = defaultOptions;
    return this;
  }
  
  public MongoClient build() {
    ofNullable(configuration)
      .orElseThrow(()->new IllegalArgumentException("the configuration is required"));
    ofNullable(defaultOptions)
      .orElseThrow(()->new IllegalArgumentException("the defaultOptions are required"));

    try {
      List<ServerAddress> servers = createServerAddressList(configuration.getSeeds());
      List<MongoCredential> credentials = createCredentials(configuration);
      
      MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder();
      defaultOptions
        .andThen(createClientOptionsConsumer(configuration))
        .accept(optionsBuilder);
      MongoClientOptions options = optionsBuilder.build();

      log.info("Mongo database is {}", configuration.getDatabase());

      return new MongoClient(servers, credentials, options);
    } catch( Exception e ) {
      throw new RuntimeException("Could not configure MongoDB client.", e);
    } 
  }
  
  public static Consumer<com.mongodb.MongoClientOptions.Builder> createClientOptionsConsumer( MongoConfiguration configuration ) {
    return b->{
      setNotNull(configuration::getAlwaysUseMBeans, b::alwaysUseMBeans);
      setNotNull(configuration::getConnectionsPerHost, b::connectionsPerHost);
      setNotNull(configuration::getConnectTimeout, b::connectTimeout);
      setNotNull(configuration::getCursorFinalizerEnabled, b::cursorFinalizerEnabled);
      setNotNull(configuration::getDescription, b::description);
      setNotNull(configuration::getHeartbeatConnectTimeout, b::heartbeatConnectTimeout);
      setNotNull(configuration::getHeartbeatFrequency, b::heartbeatFrequency);
      setNotNull(configuration::getHeartbeatSocketTimeout, b::heartbeatSocketTimeout);
      setNotNull(configuration::getLocalThreshold, b::localThreshold);
      setNotNull(configuration::getMaxConnectionIdleTime, b::maxConnectionIdleTime);
      setNotNull(configuration::getMaxConnectionLifeTime, b::maxConnectionLifeTime);
      setNotNull(configuration::getMaxWaitTime, b::maxWaitTime);
      setNotNull(configuration::getMinConnectionsPerHost, b::minConnectionsPerHost);
      setNotNull(configuration::getMinHeartbeatFrequency, b::minHeartbeatFrequency);
      setNotNull(configuration::getReadConcern, b::readConcern);
      setNotNull(configuration::getRequiredReplicaSetName, b::requiredReplicaSetName);
      setNotNull(configuration::getServerSelectionTimeout, b::serverSelectionTimeout);
      setNotNull(configuration::getSocketKeepAlive, b::socketKeepAlive);
      setNotNull(configuration::getSocketTimeout, b::socketTimeout);
      setNotNull(configuration::getSslEnabled, b::sslEnabled);
      setNotNull(configuration::getSslInvalidHostNameAllowed, b::sslInvalidHostNameAllowed);
      setNotNull(configuration::getThreadsAllowedToBlockForConnectionMultiplier, b::threadsAllowedToBlockForConnectionMultiplier);
      setNotNull(configuration::getWriteConcern, b::writeConcern);
    };
  }
  
  static List<ServerAddress> createServerAddressList( List<Server> seeds ) {
    List<ServerAddress> servers = new ArrayList<>();
    for( Server seed : seeds ) {
      servers.add(new ServerAddress(seed.getHost(), seed.getPort()));
    }
    log.info("Found {} mongo seed servers", servers.size());
    for( ServerAddress server : servers ) {
      log.info("Found mongo seed server {}:{}", server.getHost(), server.getPort());
    }
    return servers;
  }
  
  static List<MongoCredential> createCredentials( MongoConfiguration configuration ) {
    Credentials credentialConfig = configuration.getCredentials();
    List<MongoCredential> credentials = credentialConfig == null ?
    Collections.<MongoCredential>emptyList() :
    Collections.singletonList(MongoCredential.createCredential(credentialConfig.getUserName(), configuration.getDatabase(), credentialConfig
      .getPassword().toCharArray()));
      
    if( credentials.isEmpty() ) {
      log.info("Found {} mongo credentials.", credentials.size());
    }
    else {
      for( MongoCredential credential : credentials ) {
        log.info("Found mongo credential for {} on database {}.", credential.getUserName(), credential.getSource());
      }
    }
      
    return credentials;
  }
  
  public static Consumer<com.mongodb.MongoClientOptions.Builder> createDefaultOptions() {
    return b->{
      b.writeConcern(WriteConcern.ACKNOWLEDGED);
    };
  }
  
  static <T> void setNotNull( Supplier<T> supplier, Consumer<T> setter ) {
    Optional.ofNullable(supplier.get()).ifPresent(setter);
  }
}
