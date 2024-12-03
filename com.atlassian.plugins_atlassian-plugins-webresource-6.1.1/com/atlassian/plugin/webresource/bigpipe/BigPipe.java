/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.json.marshal.Jsonable
 */
package com.atlassian.plugin.webresource.bigpipe;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.webresource.bigpipe.FutureCompletionService;
import com.atlassian.plugin.webresource.bigpipe.KeyedValue;
import com.atlassian.plugin.webresource.bigpipe.QueueFutureCompletionService;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public final class BigPipe {
    private final FutureCompletionService<String, Jsonable> completor = new QueueFutureCompletionService<String, Jsonable>();

    public BigPipe push(String key, CompletionStage<Jsonable> promise) {
        this.completor.add(key, promise);
        return this;
    }

    public Iterable<KeyedValue<String, Jsonable>> getAvailableContent() {
        return this.completor.poll();
    }

    public Iterable<KeyedValue<String, Jsonable>> forceCompleteAll() {
        this.completor.forceCompleteAll();
        return this.completor.poll();
    }

    @ExperimentalApi
    public Iterable<KeyedValue<String, Jsonable>> waitForContent() throws InterruptedException {
        this.completor.waitAnyPendingToComplete();
        return this.completor.poll();
    }

    public Iterable<KeyedValue<String, Jsonable>> waitForContent(long timeout, TimeUnit unit) throws InterruptedException {
        return this.completor.poll(timeout, unit);
    }

    public boolean isComplete() {
        return this.completor.isComplete();
    }

    public boolean isNotComplete() {
        return !this.completor.isComplete();
    }
}

