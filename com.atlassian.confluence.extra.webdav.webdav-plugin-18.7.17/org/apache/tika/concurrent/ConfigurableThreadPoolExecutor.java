/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.concurrent;

import java.util.concurrent.ExecutorService;

public interface ConfigurableThreadPoolExecutor
extends ExecutorService {
    public void setMaximumPoolSize(int var1);

    public void setCorePoolSize(int var1);
}

