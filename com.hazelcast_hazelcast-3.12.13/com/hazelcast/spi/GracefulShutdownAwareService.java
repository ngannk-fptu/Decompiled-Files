/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi;

import java.util.concurrent.TimeUnit;

public interface GracefulShutdownAwareService {
    public boolean onShutdown(long var1, TimeUnit var3);
}

