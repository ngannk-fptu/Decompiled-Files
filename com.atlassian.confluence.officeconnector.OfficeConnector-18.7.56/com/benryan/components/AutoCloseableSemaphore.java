/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 */
package com.benryan.components;

import com.google.common.base.Throwables;
import java.util.concurrent.Semaphore;

public class AutoCloseableSemaphore
implements AutoCloseable {
    private final Semaphore semaphore;

    public AutoCloseableSemaphore(int permits) {
        this.semaphore = new Semaphore(permits);
    }

    public AutoCloseableSemaphore acquire() {
        try {
            this.semaphore.acquire();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Throwables.propagate((Throwable)e);
        }
        return this;
    }

    @Override
    public void close() {
        this.semaphore.release();
    }
}

