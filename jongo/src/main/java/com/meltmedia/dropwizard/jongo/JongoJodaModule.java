package com.meltmedia.dropwizard.jongo;

import java.io.IOException;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Adds support for Joda Time's DateTime class to Jongo.
 * 
 * @author Christian Trimble
 */
public class JongoJodaModule extends JodaModule {
  private static final long serialVersionUID = 1L;

  public JongoJodaModule() {
    this.addSerializer(DateTime.class, new BsonDateTimeSerializer());
    this.addDeserializer(DateTime.class, new BsonDateTimeDeserializer());
  }

  public static class BsonDateTimeDeserializer extends JsonDeserializer<DateTime> {
    @Override
    public DateTime deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException, JsonProcessingException {
      Object deserialized = jp.getEmbeddedObject();
      if( deserialized instanceof Long ) {
        return getDateFromBackwardFormat((Long) deserialized);
      }
      return new DateTime((Date) deserialized, DateTimeZone.UTC);
    }

    private DateTime getDateFromBackwardFormat( Long deserialized ) {
      return new DateTime(deserialized);
    }
  }

  public static class BsonDateTimeSerializer extends JsonSerializer<DateTime> {
    @Override
    public void serialize( DateTime value, JsonGenerator jgen, SerializerProvider provider ) throws IOException, JsonProcessingException {
      if( value == null ) {
        jgen.writeNull();
      }
      else {
        jgen.writeObject(value.toDate());
      }
    }

  }
}
