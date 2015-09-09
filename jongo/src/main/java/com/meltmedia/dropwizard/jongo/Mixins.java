package com.meltmedia.dropwizard.jongo;

import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;
/**
 * Common Jackson mixins.
 * 
 * @author Christian Trimble
 *
 */
public class Mixins {
  public static abstract class StringIdMixin {
    @JsonProperty("_id")
    public String id;

    @JsonProperty("_id")
    public abstract String getId();

    @JsonProperty("_id")
    public abstract void setId( String id );
  }
  
  public static abstract class ObjectIdMixin {
    @MongoObjectId
    @MongoId
    String id;

    @MongoObjectId
    @MongoId
    public abstract String getId();

    @MongoObjectId
    @MongoId
    public abstract void setId( String id );    
  }
}
