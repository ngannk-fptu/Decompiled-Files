/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.threads;

import java.util.concurrent.Executor;

public interface ResizableExecutor
extends Executor {
    public int getPoolSize();

    public int getMaxThreads();

    public int getActiveCount();

    public boolean resizePool(int var1, int var2);

    public boolean resizeQueue(int var1);
}

