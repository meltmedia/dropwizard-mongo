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

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;

public class MongoConfiguration {
  public static Logger log = LoggerFactory.getLogger(MongoBundle.class);


  protected List<Server> seeds = new ArrayList<>();
  protected Credentials credentials;
  @NotNull
  protected String database;
  @JsonDeserialize(converter=JsonConverters.StringToWriteConcern.class)
  protected boolean enabled = true;
  protected Boolean alwaysUseMBeans;
  protected Integer connectionsPerHost;
  protected Integer connectTimeout;
  protected Boolean cursorFinalizerEnabled;
  protected String description;
  protected Integer heartbeatConnectTimeout;
  protected Integer heartbeatFrequency;
  protected Integer heartbeatSocketTimeout;
  protected Integer localThreshold;
  protected Integer maxConnectionIdleTime;
  protected Integer maxConnectionLifeTime;
  protected Integer maxWaitTime;
  protected Integer minConnectionsPerHost;
  protected Integer minHeartbeatFrequency;
  @JsonDeserialize(converter=JsonConverters.StringToReadConcern.class)
  protected ReadConcern readConcern;
  // MISSING: readPreferences - not sure how best to implement this.
  protected String requiredReplicaSetName;
  protected Integer serverSelectionTimeout;
  protected Boolean socketKeepAlive;
  protected Integer socketTimeout;
  protected Boolean sslEnabled;
  protected Boolean sslInvalidHostNameAllowed;
  protected Integer threadsAllowedToBlockForConnectionMultiplier;
  @JsonDeserialize(converter=JsonConverters.StringToWriteConcern.class)
  protected WriteConcern writeConcern;

  public List<Server> getSeeds() {
    return seeds;
  }

  public void setSeeds( List<Server> seeds ) {
    this.seeds = seeds;
  }
  
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled( boolean enabled ) {
    this.enabled = enabled;
  }

  public Credentials getCredentials() {
    return credentials;
  }

  public void setCredentials( Credentials credentials ) {
    this.credentials = credentials;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase( String database ) {
    this.database = database;
  }

  public Boolean getAlwaysUseMBeans() {
    return alwaysUseMBeans;
  }
  public void setAlwaysUseMBeans( Boolean alwaysUseMBeans ) {
    this.alwaysUseMBeans = alwaysUseMBeans;
  }
  public Integer getConnectionsPerHost() {
    return connectionsPerHost;
  }
  public void setConnectionsPerHost( Integer connectionsPerHost ) {
    this.connectionsPerHost = connectionsPerHost;
  }
  public Integer getConnectTimeout() {
    return connectTimeout;
  }
  public void setConnectTimeout( Integer connectTimeout ) {
    this.connectTimeout = connectTimeout;
  }
  public Boolean getCursorFinalizerEnabled() {
    return cursorFinalizerEnabled;
  }
  public void setCursorFinalizerEnabled( Boolean cursorFinalizerEnabled ) {
    this.cursorFinalizerEnabled = cursorFinalizerEnabled;
  }
  public Integer getHeartbeatConnectTimeout() {
    return heartbeatConnectTimeout;
  }
  public void setHeartbeatConnectTimeout( Integer heartbeatConnectTimeout ) {
    this.heartbeatConnectTimeout = heartbeatConnectTimeout;
  }
  public Integer getHeartbeatFrequency() {
    return heartbeatFrequency;
  }
  public void setHeartbeatFrequency( Integer heartbeatFrequency ) {
    this.heartbeatFrequency = heartbeatFrequency;
  }
  public Integer getHeartbeatSocketTimeout() {
    return heartbeatSocketTimeout;
  }
  public void setHeartbeatSocketTimeout( Integer heartbeatSocketTimeout ) {
    this.heartbeatSocketTimeout = heartbeatSocketTimeout;
  }
  public Integer getLocalThreshold() {
    return localThreshold;
  }
  public void setLocalThreshold( Integer localThreshold ) {
    this.localThreshold = localThreshold;
  }
  public Integer getMaxConnectionIdleTime() {
    return maxConnectionIdleTime;
  }
  public void setMaxConnectionIdleTime( Integer maxConnectionIdleTime ) {
    this.maxConnectionIdleTime = maxConnectionIdleTime;
  }
  public Integer getMaxConnectionLifeTime() {
    return maxConnectionLifeTime;
  }
  public void setMaxConnectionLifeTime( Integer maxConnectionLifeTime ) {
    this.maxConnectionLifeTime = maxConnectionLifeTime;
  }
  public Integer getMaxWaitTime() {
    return maxWaitTime;
  }
  public void setMaxWaitTime( Integer maxWaitTime ) {
    this.maxWaitTime = maxWaitTime;
  }
  public Integer getMinConnectionsPerHost() {
    return minConnectionsPerHost;
  }
  public void setMinConnectionsPerHost( Integer minConnectionsPerHost ) {
    this.minConnectionsPerHost = minConnectionsPerHost;
  }
  public Integer getMinHeartbeatFrequency() {
    return minHeartbeatFrequency;
  }
  public void setMinHeartbeatFrequency( Integer minHeartbeatFrequency ) {
    this.minHeartbeatFrequency = minHeartbeatFrequency;
  }
  public Integer getServerSelectionTimeout() {
    return serverSelectionTimeout;
  }
  public void setServerSelectionTimeout( Integer serverSelectionTimeout ) {
    this.serverSelectionTimeout = serverSelectionTimeout;
  }
  public Boolean getSocketKeepAlive() {
    return socketKeepAlive;
  }
  public void setSocketKeepAlive( Boolean socketKeepAlive ) {
    this.socketKeepAlive = socketKeepAlive;
  }
  public Integer getSocketTimeout() {
    return socketTimeout;
  }
  public void setSocketTimeout( Integer socketTimeout ) {
    this.socketTimeout = socketTimeout;
  }
  public Boolean getSslEnabled() {
    return sslEnabled;
  }
  public void setSslEnabled( Boolean sslEnabled ) {
    this.sslEnabled = sslEnabled;
  }
  public Boolean getSslInvalidHostNameAllowed() {
    return sslInvalidHostNameAllowed;
  }
  public void setSslInvalidHostNameAllowed( Boolean sslInvalidHostNameAllowed ) {
    this.sslInvalidHostNameAllowed = sslInvalidHostNameAllowed;
  }
  public Integer getThreadsAllowedToBlockForConnectionMultiplier() {
    return threadsAllowedToBlockForConnectionMultiplier;
  }
  public void setThreadsAllowedToBlockForConnectionMultiplier( Integer threadsAllowedToBlockForConnectionMultiplier ) {
    this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
  }
  public WriteConcern getWriteConcern() {
    return writeConcern;
  }
  public void setWriteConcern( WriteConcern writeConcern ) {
    this.writeConcern = writeConcern;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription( String description ) {
    this.description = description;
  }
  public ReadConcern getReadConcern() {
    return readConcern;
  }
  public void setReadConcern( ReadConcern readConcern ) {
    this.readConcern = readConcern;
  }
  public String getRequiredReplicaSetName() {
    return requiredReplicaSetName;
  }
  public void setRequiredReplicaSetName( String requiredReplicaSetName ) {
    this.requiredReplicaSetName = requiredReplicaSetName;
  }

  public static class Server {
    @NotNull
    protected String host;
    @NotNull
    protected Integer port;
    public String getHost() {
      return host;
    }
    public void setHost( String host ) {
      this.host = host;
    }
    public Integer getPort() {
      return port;
    }
    public void setPort( Integer port ) {
      this.port = port;
    }
  }
  
  public static class Credentials {
    @NotNull
    protected String userName;
    @NotNull
    protected String password;
    public String getUserName() {
      return userName;
    }
    public void setUserName( String userName ) {
      this.userName = userName;
    }
    public String getPassword() {
      return password;
    }
    public void setPassword( String password ) {
      this.password = password;
    }
  }


}
