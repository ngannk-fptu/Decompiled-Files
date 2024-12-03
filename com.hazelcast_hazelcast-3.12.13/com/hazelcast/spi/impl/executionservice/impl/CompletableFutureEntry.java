/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.executionservice.impl;

import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.executionservice.impl.BasicCompletableFuture;
import java.util.concurrent.Future;

final class CompletableFutureEntry<V> {
    final BasicCompletableFuture<V> completableFuture;

    CompletableFutureEntry(Future<V> future, NodeEngine nodeEngine) {
        this.completableFuture = new BasicCompletableFuture<V>(future, nodeEngine);
    }

    boolean processState() {
        return this.completableFuture.isDone();
    }
}

