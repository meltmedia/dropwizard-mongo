package com.meltmedia.dropwizard.jongo;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

class Functions {

  static <U, V> Supplier<V> mapSupplier( Supplier<U> supplier, Function<U, V> mapping ) {
    return ()->mapping.apply(supplier.get());
  }

  static <T> Consumer<T> consumerRequired(String name) {
    return t->{throw new RuntimeException("Consumer "+name+" is required.");};
  }

  static <U, V> Function<U, V> functionRequired(String name) {
    return u->{throw new RuntimeException("Function "+name+" is required.");};
  }

  static <T> Supplier<T> supplierRequired(String name) {
    return ()->{throw new RuntimeException("Supplier "+name+" is required.");};
  }

}
