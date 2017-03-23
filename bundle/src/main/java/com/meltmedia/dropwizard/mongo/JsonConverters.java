package com.meltmedia.dropwizard.mongo;

import java.util.Optional;

import com.fasterxml.jackson.databind.util.StdConverter;
import com.mongodb.ReadConcern;
import com.mongodb.ReadConcernLevel;
import com.mongodb.WriteConcern;

public class JsonConverters {
  public static class StringToWriteConcern extends StdConverter<String, WriteConcern> {
    @Override
    public WriteConcern convert( String value ) {
      if( value == null ) return null;
      return Optional.ofNullable(WriteConcern.valueOf(value))
        .orElseThrow(()->new RuntimeException(value+" cannot be convereted to "+WriteConcern.class.getName()));
    }
  }
  
  public static class StringToReadConcern extends StdConverter<String, ReadConcern> {
    @Override
    public ReadConcern convert( String value ) {
      if( value == null ) return null;
      if( "DEFAULT".equals(value) ) return ReadConcern.DEFAULT;
      return Optional.ofNullable(value)
        .map(this::level)
        .map(ReadConcern::new)
        .orElseThrow(()->new RuntimeException(value+" cannot be convereted to "+ReadConcern.class.getName()));
    }
    
    private ReadConcernLevel level( String value ) {
      try {
        return ReadConcernLevel.valueOf(value);
      } catch( IllegalArgumentException e ) {
        throw new RuntimeException(value+" cannot be convereted to "+ReadConcern.class.getName(), e);
      }
    }
  }
}
