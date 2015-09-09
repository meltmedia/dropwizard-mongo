package com.meltmedia.dropwizard.jongo;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jongo.Jongo;
import org.jongo.marshall.jackson.JacksonMapper;
import org.jongo.marshall.jackson.configuration.MapperModifier;

import com.meltmedia.dropwizard.jongo.JongoBuilder;
import com.mongodb.MongoClient;

public class JongoBuilder {
  Supplier<MongoClient> client = Functions.supplierRequired("client");
  Supplier<String> databaseName = Functions.supplierRequired("databaseName");
  Consumer<JacksonMapper.Builder> mapperOps = m->{};
  JacksonMapper.Builder builder = new JacksonMapper.Builder();
  
  public JongoBuilder withClient( Supplier<MongoClient> client ) {
    this.client = client;
    return this;
  }
  
  public JongoBuilder withDatabaseName( Supplier<String> databaseName ) {
    this.databaseName = databaseName;
    return this;
  }
  
  public JongoBuilder addMapperOps( Consumer<JacksonMapper.Builder> mapperOp ) {
    this.mapperOps = mapperOps.andThen(mapperOp);
    return this;
  }
  
  public JongoBuilder addMapperModifier( MapperModifier m ) {
    this.mapperOps = mapperOps.andThen(b->b.addModifier(m));
    return this;
  }
  
  public JongoBuilder addMixin( Class<?> target, Class<?> mixin ) {
    addMapperModifier(m->m.addMixIn(target, mixin));
    return this;
  }
  
  @SuppressWarnings("deprecation")
  public Jongo build() {
    String resolvedName = databaseName.get();
    JacksonMapper.Builder builder = new JacksonMapper.Builder();
    mapperOps.accept(builder);
    return new Jongo(
      client.get().getDB(resolvedName),
      builder.build());
  }
}