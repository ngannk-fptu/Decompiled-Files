/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal.core.metrics;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

class TimedUtils {
    TimedUtils() {
    }

    static <T> void whenCompletableFuture(CompletionStage<T> stage, Consumer<CompletableFuture<T>> handle) {
        if (stage instanceof CompletableFuture) {
            handle.accept((CompletableFuture)stage);
        }
    }
}

