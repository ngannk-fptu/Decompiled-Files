/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.event.config;

import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public interface EventThreadPoolConfiguration {
    public int getCorePoolSize();

    public int getMaximumPoolSize();

    public long getKeepAliveTime();

    @Nonnull
    public TimeUnit getTimeUnit();
}

