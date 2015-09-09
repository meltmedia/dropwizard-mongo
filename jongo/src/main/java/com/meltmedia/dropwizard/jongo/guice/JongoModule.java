package com.meltmedia.dropwizard.jongo.guice;

import org.jongo.Jongo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.meltmedia.dropwizard.jongo.JongoBundle;

public class JongoModule extends AbstractModule {
  JongoBundle<?> bundle;

  public JongoModule( JongoBundle<?> bundle ) {
    this.bundle = bundle;
  }

  @Override
  protected void configure() {
    // nothing to add here, we just need the provider.
  }

  @Provides
  @Singleton
  public Jongo providesJongo() {
    return bundle.getJongo();
  }

}
