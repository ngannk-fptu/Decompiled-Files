/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.concurrent;

import io.netty.util.concurrent.SingleThreadEventExecutor;

public interface RejectedExecutionHandler {
    public void rejected(Runnable var1, SingleThreadEventExecutor var2);
}

