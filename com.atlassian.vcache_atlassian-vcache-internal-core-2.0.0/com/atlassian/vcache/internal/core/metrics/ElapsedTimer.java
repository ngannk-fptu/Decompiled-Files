/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal.core.metrics;

import java.util.Objects;
import java.util.function.LongConsumer;

public class ElapsedTimer
implements AutoCloseable {
    private final long startTime = System.nanoTime();
    private final LongConsumer handler;

    public ElapsedTimer(LongConsumer handler) {
        this.handler = Objects.requireNonNull(handler);
    }

    @Override
    public void close() {
        this.handler.accept(System.nanoTime() - this.startTime);
    }
}

