package com.meltmedia.dropwizard.mongo;

import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;

@RunWith(Parameterized.class)
public class CreateOptionsTest {
  
  @Parameters(name="{0}")
  public static Collection<Object[]> parameters() {
    return Arrays.asList(new Object[][] {
      { "alwaysUseMBeans", configuration(o->o.setAlwaysUseMBeans(true)), verification(b->b.alwaysUseMBeans(true)) },
      { "connectionsPerHost", configuration(o->o.setConnectionsPerHost(10)), verification(b->b.connectionsPerHost(10)) },
      { "connectionTimeout", configuration(o->o.setConnectTimeout(10)), verification(b->b.connectTimeout(10)) },
      { "cursorFinalizerEnabled", configuration(o->o.setCursorFinalizerEnabled(true)), verification(b->b.cursorFinalizerEnabled(true)) },
      { "description", configuration(o->o.setDescription("description")), verification(b->b.description("description")) },
      { "heartbeatConnectTimeout", configuration(o->o.setHeartbeatConnectTimeout(1000)), verification(b->b.heartbeatConnectTimeout(1000)) },
      { "heartbeatFrequency", configuration(o->o.setHeartbeatFrequency(1000)), verification(b->b.heartbeatFrequency(1000)) },
      { "heartbeatSocketTimeout", configuration(o->o.setHeartbeatSocketTimeout(1000)), verification(b->b.heartbeatSocketTimeout(1000)) },
      { "maxConnectionIdleTime", configuration(o->o.setMaxConnectionIdleTime(1000)), verification(b->b.maxConnectionIdleTime(1000)) },
      { "maxConnectionLifeTime", configuration(o->o.setMaxConnectionLifeTime(1000)), verification(b->b.maxConnectionLifeTime(1000)) },
      { "maxWaitTime", configuration(o->o.setMaxWaitTime(1000)), verification(b->b.maxWaitTime(1000)) },
      { "minConnectionsPerHost", configuration(o->o.setMinConnectionsPerHost(1)), verification(b->b.minConnectionsPerHost(1)) },
      { "minHeartbeatFrequency", configuration(o->o.setMinHeartbeatFrequency(1000)), verification(b->b.minHeartbeatFrequency(1000)) },
      { "readConcern", configuration(o->o.setReadConcern(ReadConcern.MAJORITY)), verification(b->b.readConcern(ReadConcern.MAJORITY)) },
      { "requiredReplicaSetName", configuration(o->o.setRequiredReplicaSetName("replica")), verification(b->b.requiredReplicaSetName("replica")) },
      { "serverSelectionTimeout", configuration(o->o.setServerSelectionTimeout(1000)), verification(b->b.serverSelectionTimeout(1000)) },
      { "socketKeepAlive", configuration(o->o.setSocketKeepAlive(true)), verification(b->b.socketKeepAlive(true)) },
      { "socketTimeout", configuration(o->o.setSocketTimeout(1000)), verification(b->b.socketTimeout(1000)) },
      { "sslEnabled", configuration(o->o.setSslEnabled(true)), verification(b->b.sslEnabled(true)) },
      { "sslInvalidHostNameAllowed", configuration(o->o.setSslInvalidHostNameAllowed(true)), verification(b->b.sslInvalidHostNameAllowed(true)) },
      { "threadsAllowedToBlockForConnectionMultiplier", configuration(o->o.setThreadsAllowedToBlockForConnectionMultiplier(5)), verification(b->b.threadsAllowedToBlockForConnectionMultiplier(5)) },
      { "writeConcern", configuration(o->o.setWriteConcern(WriteConcern.ACKNOWLEDGED)), verification(b->b.writeConcern(WriteConcern.ACKNOWLEDGED)) }
    });
  }

  public CreateOptionsTest( String name, MongoConfiguration configuraiton, Consumer<Builder> verification ) {
    this.configuraiton = configuraiton;
    this.verification = verification;
  }

  MongoConfiguration configuraiton;
  Consumer<MongoClientOptions.Builder> verification;
  
  @Test
  public void createOptions() {
    MongoClientOptions.Builder builder = mock(MongoClientOptions.Builder.class);
    MongoClientBuilder.createClientOptionsConsumer(configuraiton).accept(builder);
    verification.accept(Mockito.verify(builder, Mockito.times(1)));
    Mockito.verifyNoMoreInteractions(builder);
  }
  
  private static Consumer<MongoClientOptions.Builder> verification( Consumer<MongoClientOptions.Builder> verification ) {
    return verification;
  }
  
  private static MongoConfiguration configuration( Consumer<MongoConfiguration> o ) {
    MongoConfiguration configuration = new MongoConfiguration();
    o.accept(configuration);
    return configuration;
  }
}
