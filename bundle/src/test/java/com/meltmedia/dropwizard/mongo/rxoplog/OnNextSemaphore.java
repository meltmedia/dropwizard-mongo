package com.meltmedia.dropwizard.mongo.rxoplog;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 
 * @author Christian Trimble
 *
 * @param <T>
 */
public class OnNextSemaphore<T>
  implements Observer<T>
{
  protected final Semaphore onNextSemaphore = new Semaphore(0);

  @Override
  public void onSubscribe( Disposable d ) {
  }

  @Override
  public void onNext( T t ) {
    onNextSemaphore.release();
  }

  @Override
  public void onError( Throwable e ) {}

  @Override
  public void onComplete() {}
  
  public void awaitOnNext( int count ) throws InterruptedException {
    onNextSemaphore.acquire(count);
  }
  
  public boolean awaitOnNext( int count, long timeout, TimeUnit unit ) throws InterruptedException {
    return onNextSemaphore.tryAcquire(count, timeout, unit);
  }

}
