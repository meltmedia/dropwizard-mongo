package com.meltmedia.dropwizard.mongo.rxoplog;

import io.reactivex.*;
import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * Tests showing different ways to make sharable observables in RxJava2.
 * 
 * @author Christian Trimble
 *
 */
public class ConnectableTest {

  @Test
  public void createHotConnectableSharableObservable() throws InterruptedException {
    Observable<Integer> observable = Observable.<Integer>create(emitter->{
      Disposable disposable = Schedulers.io().scheduleDirect(()->{
        int value = 0;
        while( !emitter.isDisposed() ) {
          emitter.onNext(value++);
        }
        emitter.onComplete();
      });
      emitter.setDisposable(disposable);
    }).share();
    
    OnNextSemaphore<Integer> s1 = new OnNextSemaphore<Integer>();
    OnNextSemaphore<Integer> s2 = new OnNextSemaphore<Integer>();
    TestObserver<Integer> o1 = new TestObserver<Integer>(s1);
    TestObserver<Integer> o2 = new TestObserver<Integer>(s2);
    
    observable.subscribe(o1);
    
    s1.awaitOnNext(1, 10l, TimeUnit.SECONDS);

    observable.subscribe(o2);
    
    s2.awaitOnNext(1, 10l, TimeUnit.SECONDS);
    
    o1.cancel();
    o2.cancel();
    
    assertThat(o1.values().get(0), equalTo(0));
    assertThat(o2.values().get(0), greaterThan(0));
    
    OnNextSemaphore<Integer> s3 = new OnNextSemaphore<Integer>();
    TestObserver<Integer> o3 = new TestObserver<Integer>(s3);
    
    observable.subscribe(o3);

    s3.awaitOnNext(1, 10l, TimeUnit.SECONDS);

    o3.cancel();
    
    assertThat(o3.values().get(0), equalTo(0));
  }
  
  /**
   * Shows a pattern where a hot observable can be shared and disposed of independently.
   * 
   * @throws InterruptedException
   */
  @Test
  public void disposeOfHotObservable() throws InterruptedException {
    // this pattern does not seem that great, since it requires creation of the worker
    // outside the observable.  In Dropwizard, this might require the RxOplogService's manager
    // to be registered before services requiring it.
    Worker worker = Schedulers.io().createWorker();
    Observable<Integer> observable = Observable.<Integer>create(emitter->{
      Disposable disposable = worker.schedule(()->{
        int value = 0;
        while( !emitter.isDisposed() && !worker.isDisposed() ) {
          emitter.onNext(value++);
        }
        emitter.onComplete();
      });
      emitter.setDisposable(disposable);
    }).share();
    
    OnNextSemaphore<Integer> s1 = new OnNextSemaphore<Integer>();
    TestObserver<Integer> o1 = new TestObserver<Integer>(s1);
    
    observable.subscribe(o1);
    
    s1.awaitOnNext(1, 10l, TimeUnit.SECONDS);
    
    worker.dispose();
    
    o1.await(10, TimeUnit.SECONDS);
    
    o1.cancel();
    
    assertThat(o1.isDisposed(), equalTo(true));
  }

}
