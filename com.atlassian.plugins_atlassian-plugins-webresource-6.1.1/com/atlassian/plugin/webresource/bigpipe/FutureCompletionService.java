/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.bigpipe;

import com.atlassian.plugin.webresource.bigpipe.KeyedValue;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

interface FutureCompletionService<K, V> {
    public void add(K var1, CompletionStage<V> var2);

    public Iterable<KeyedValue<K, V>> poll();

    public Iterable<KeyedValue<K, V>> poll(long var1, TimeUnit var3) throws InterruptedException;

    public boolean isComplete();

    public void forceCompleteAll();

    public void waitAnyPendingToComplete() throws InterruptedException;
}

