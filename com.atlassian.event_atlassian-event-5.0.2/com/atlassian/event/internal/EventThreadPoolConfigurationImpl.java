/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.event.internal;

import com.atlassian.event.config.EventThreadPoolConfiguration;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public class EventThreadPoolConfigurationImpl
implements EventThreadPoolConfiguration {
    private static final int CORE_POOL_SIZE = 16;
    private static final int MAXIMUM_POOL_SIZE = 64;
    private static final long KEEP_ALIVE_TIME = 60L;

    @Override
    public int getCorePoolSize() {
        return 16;
    }

    @Override
    public int getMaximumPoolSize() {
        return 64;
    }

    @Override
    public long getKeepAliveTime() {
        return 60L;
    }

    @Override
    @Nonnull
    public TimeUnit getTimeUnit() {
        return TimeUnit.SECONDS;
    }
}

