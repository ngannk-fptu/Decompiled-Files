/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.spi;

import java.util.concurrent.Callable;

public interface CancellableTask<T>
extends Callable<T> {
    public Result cancel();

    public static enum Result {
        CANCELLED,
        CANNOT_CANCEL,
        INTERRUPT;

    }
}

