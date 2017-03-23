package com.meltmedia.dropwizard.mongo;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.fasterxml.jackson.databind.util.Converter;
import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class JsonConvertersTest {
  
  @Parameters(name="{1}-{2}")
  public static Collection<Object[]> parameters() {
    return Arrays.asList(new Object[][] {
      { JsonConverters.StringToReadConcern.class, "DEFAULT", equalTo(ReadConcern.DEFAULT) },
      { JsonConverters.StringToReadConcern.class, "LINEARIZABLE", equalTo(ReadConcern.LINEARIZABLE) },
      { JsonConverters.StringToReadConcern.class, "LOCAL", equalTo(ReadConcern.LOCAL) },
      { JsonConverters.StringToReadConcern.class, "MAJORITY", equalTo(ReadConcern.MAJORITY) },
      { JsonConverters.StringToReadConcern.class, "majority", any(Throwable.class) },
      { JsonConverters.StringToReadConcern.class, "unknown", any(Throwable.class) },
      { JsonConverters.StringToWriteConcern.class, "ACKNOWLEDGED", equalTo(WriteConcern.ACKNOWLEDGED) },
      { JsonConverters.StringToWriteConcern.class, "MAJORITY", equalTo(WriteConcern.MAJORITY) },
      { JsonConverters.StringToWriteConcern.class, "UNACKNOWLEDGED", equalTo(WriteConcern.UNACKNOWLEDGED) },
      { JsonConverters.StringToWriteConcern.class, "W1", equalTo(WriteConcern.W1) },
      { JsonConverters.StringToWriteConcern.class, "W2", equalTo(WriteConcern.W2) },
      { JsonConverters.StringToWriteConcern.class, "W3", equalTo(WriteConcern.W3) },
      { JsonConverters.StringToWriteConcern.class, "unknown", any(Throwable.class) }
    });
  }
  
  Class<Converter<String, ?>> converter;
  String input;
  Matcher<Object> matcher;

  public JsonConvertersTest( Class<Converter<String, ?>> converter, String input, Matcher<Object> matcher ) {
    this.converter = converter;
    this.input = input;
    this.matcher = matcher;
  }
  
  @Test
  public void correctResult() {
    Object result;
    try {
      result = converter.newInstance().convert(input);
    } catch( Throwable t ) {
      assertThat(t, matcher);
      return;
    }
    assertThat(result, matcher);
  }
}
