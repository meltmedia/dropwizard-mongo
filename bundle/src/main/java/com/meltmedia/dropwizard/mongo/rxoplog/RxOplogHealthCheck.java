/**
 * Copyright (C) 2014 meltmedia (christian.trimble@meltmedia.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.meltmedia.dropwizard.mongo.rxoplog;

import java.util.concurrent.atomic.AtomicReference;

import com.codahale.metrics.health.HealthCheck;

import io.dropwizard.lifecycle.Managed;
import io.reactivex.disposables.Disposable;

public class RxOplogHealthCheck extends HealthCheck implements Managed {

  RxOplogService oplogService;
  AtomicReference<Result> currentResult = new AtomicReference<Result>(Result.healthy("booting..."));
  Disposable statusSubscription;

  public RxOplogHealthCheck(RxOplogService oplogService) {
    this.oplogService = oplogService;
  }

  @Override
  protected Result check() throws Exception {
    return currentResult.get();
  }

  @Override
  public void start() throws Exception {
    statusSubscription = oplogService.getStatus()
      .map(status->{
        if( status.getState().healthy() ) {
          return Result.healthy(status.getState().toString()+": "+status.getMessage());
        } else {
          return Result.unhealthy(status.getState().toString()+": "+status.getMessage());
        }
      })
      .subscribe(result->{
        currentResult.set(result);
      });
  }

  @Override
  public void stop() throws Exception {
    if( statusSubscription != null ) {
      statusSubscription.dispose();
    }
  }
}
