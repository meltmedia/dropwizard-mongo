package com.meltmedia.dropwizard.jongo.junit;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.marshall.jackson.JacksonMapper;
import org.jongo.marshall.jackson.configuration.MapperModifier;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.meltmedia.dropwizard.jongo.JongoBuilder;
import com.mongodb.MongoClient;

/**
 * This rule will drop the specified collection in Mongo before executing, DO NOT USE AGAINST PRODUCTION COLLECTIONS.
 * 
 * A rule for starting Jongo in tests. This test is designed to be used with the @Rule
 * annotation and in conjunction with the MongoRule.
 * 
 * @author Christian Trimble
 * 
 */
public class JongoRule
  implements TestRule
{
  public static class Builder {
    private JongoBuilder jongoBuilder = new JongoBuilder();

    public Builder withClient(Supplier<MongoClient> mongoClient) {
      jongoBuilder.withClient(mongoClient);
      return this;
    }
    
    public Builder withDatabaseName( String databaseName ) {
      jongoBuilder.withDatabaseName(()->databaseName);
      return this;
    }
    
    public Builder addMapperOps(Consumer<JacksonMapper.Builder> mapperOp ) {
      jongoBuilder.addMapperOps(mapperOp);
      return this;
    }
    
    public Builder addMapperModifier(MapperModifier m) {
      jongoBuilder.addMapperModifier(m);
      return this;
    }
    
    public Builder addMixin( Class<?> target, Class<?> mixin ) {
      jongoBuilder.addMixin(target, mixin);
      return this;
    }
 
    public JongoRule build() {
      return new JongoRule( jongoBuilder::build );
    }
  }
  
  public static Builder builder() {
    return new Builder();
  }

  protected Supplier<Jongo> jongoSupplier;
  protected Jongo jongo;  

  public JongoRule( Supplier<Jongo> jongoSupplier ) {
    this.jongoSupplier = jongoSupplier;
  }

  /**
   * When this rule is active, returns the Jongo instance.
   */
  public Jongo getJongo() {
    return jongo;
  }
  
  public MongoCollection dropAndGetCollection( String name ) {
    MongoCollection collection = jongo.getCollection(name);
    collection.drop();
    return collection;
  }

  @Override
  public Statement apply( final Statement statement, final Description description ) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        jongo = jongoSupplier.get();

        statement.evaluate();

        jongo = null;
      }

    };
  }
}
