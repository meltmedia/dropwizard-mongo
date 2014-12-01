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

public class MongoConfiguration {
  protected List<ServerConfiguration> seeds = new ArrayList<>();
  protected CredentialsConfiguration credentials;
  @NotNull
  protected String database;
  protected String writeConcern = "ACKNOWLEDGED";
  protected boolean enabled = true;

  public List<ServerConfiguration> getSeeds() {
    return seeds;
  }

  public void setSeeds( List<ServerConfiguration> seeds ) {
    this.seeds = seeds;
  }

  public String getWriteConcern() {
    return writeConcern;
  }

  public void setWriteConcern( String writeConcern ) {
    this.writeConcern = writeConcern;
  }
  
  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled( boolean enabled ) {
    this.enabled = enabled;
  }

  public CredentialsConfiguration getCredentials() {
    return credentials;
  }

  public void setCredentials( CredentialsConfiguration credentials ) {
    this.credentials = credentials;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase( String database ) {
    this.database = database;
  }
  
  public static class ServerConfiguration {
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
  
  public static class CredentialsConfiguration {
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
