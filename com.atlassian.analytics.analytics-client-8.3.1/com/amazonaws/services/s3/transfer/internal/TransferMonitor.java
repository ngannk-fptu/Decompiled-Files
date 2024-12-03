/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.internal;

import java.util.concurrent.Future;

public interface TransferMonitor {
    public Future<?> getFuture();

    public boolean isDone();
}

