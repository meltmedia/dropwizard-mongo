package com.meltmedia.dropwizard.jongo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;

import org.bson.types.ObjectId;
import org.jongo.MongoCollection;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.meltmedia.dropwizard.mongo.junit.MongoRule;

import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class JongoBundleIT {
  @ClassRule public static MongoRule mongoRule = new MongoRule("localhost", 27017, "admin", "password", "admin");
  
  Bootstrap<?> bootstrap = mock(Bootstrap.class);
  Configuration configuration = mock(Configuration.class);
  Environment environment = mock(Environment.class);
  JongoBundle<Configuration> bundle;
  MongoCollection entries;

  @Before
  public void setUp() throws Exception {
    bundle = new JongoBundle.Builder<Configuration>()
      .withClient(mongoRule::getClient)
      .withDatabaseName(()->"test_bundle")
      .addJongoBuilderOps(op->op
        .addMixin(Entry.class, Mixins.ObjectIdMixin.class))
      .build();
    bundle.initialize(bootstrap);
    bundle.run(configuration, environment);
    entries = bundle.getJongo().getCollection("entries");
    entries.drop();
  }
  
  @Test
  public void shouldStoreWithObjectId() throws Exception {
    ObjectId id = new ObjectId();
    Entry expected = new Entry().withId(id.toString());
    
    entries.insert(expected);
    Entry actual = entries.findOne("{_id: #}", id).as(Entry.class);
    
    assertThat(actual, equalTo(expected));
  }
  
}
