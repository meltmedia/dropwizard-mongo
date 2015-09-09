package com.meltmedia.dropwizard.jongo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.bson.types.ObjectId;
import org.jongo.MongoCollection;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.meltmedia.dropwizard.jongo.junit.JongoRule;
import com.meltmedia.dropwizard.mongo.junit.MongoRule;

/**
 * Ensure that when working with this project, one can map string ids to/from mongo
 * object ids.
 * 
 * @author Christian Trimble
 *
 */
public class ObjectIdMappingIT {
  @ClassRule public static MongoRule mongoRule = new MongoRule("localhost", 27017);
  
  public abstract class ObjectIdOnly {
    @org.jongo.marshall.jackson.oid.MongoObjectId
    private String _id;
  }
  
  @Rule public JongoRule jongoRule = JongoRule.builder()
    .withDatabaseName("jongo_id_test")
    .withClient(mongoRule::getClient)
    .addMixin(Entry.class, Mixins.ObjectIdMixin.class)
    .addMixin(UnderscoreId.class, ObjectIdOnly.class)
    .build();
  
  MongoCollection underscore;
  MongoCollection entry;
  
  @Before
  public void setUp() {
    underscore = jongoRule.dropAndGetCollection("underscore");
    entry = jongoRule.dropAndGetCollection("entry");
  }

  @Test
  public void mapsFieldWithDefaultName() {
    String id = objectIdString();
    UnderscoreId template = new UnderscoreId().withId(id).withAdditionalProperty("data", "data");
    underscore.update("{_id: #}", id)
      .upsert()
      .with(template)
      .getUpsertedId();
    UnderscoreId result = underscore.findOne("{_id: #}", id)
      .as(UnderscoreId.class);
    assertThat(result, equalTo(template));
  }
  
  @Test
  public void mapsFieldWithDifferentName() {
    String id = objectIdString();
    Entry template = new Entry().withId(id).withAdditionalProperty("data", "data");
    underscore.update("{_id: #}", id)
      .upsert()
      .with(template)
      .getUpsertedId();
    Entry result = underscore.findOne("{_id: #}", id)
      .as(Entry.class);
    assertThat(result, equalTo(template));
  }
  
  public static String objectIdString() {
    return new ObjectId().toString();
  }
}
