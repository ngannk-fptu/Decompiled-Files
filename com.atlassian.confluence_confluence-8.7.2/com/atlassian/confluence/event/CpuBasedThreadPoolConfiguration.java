/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.config.EventThreadPoolConfiguration
 *  com.google.common.base.MoreObjects
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.event;

import com.atlassian.event.config.EventThreadPoolConfiguration;
import com.google.common.base.MoreObjects;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CpuBasedThreadPoolConfiguration
implements EventThreadPoolConfiguration {
    private static final Logger log = LoggerFactory.getLogger(CpuBasedThreadPoolConfiguration.class);
    public static final String CORE_POOL_SIZE_KEY = "atlassian.event.thread_pool_configuration.core_pool_size";
    public static final String MAXIMUM_POOL_SIZE_KEY = "atlassian.event.thread_pool_configuration.max_pool_size";
    public static final String QUEUE_SIZE_KEY = "atlassian.event.thread_pool_configuration.queue_size";
    private static final int AVAILABLE_PROCESSORS = Math.max(Runtime.getRuntime().availableProcessors(), 2);
    private static final int CORE_POOL_SIZE = Integer.getInteger("atlassian.event.thread_pool_configuration.core_pool_size", AVAILABLE_PROCESSORS / 2);
    private static final int MAXIMUM_POOL_SIZE = Integer.getInteger("atlassian.event.thread_pool_configuration.max_pool_size", AVAILABLE_PROCESSORS / 2);
    private static final long KEEP_ALIVE_TIME = 60L;
    protected static final int QUEUE_SIZE = Integer.getInteger("atlassian.event.thread_pool_configuration.queue_size", 128 + AVAILABLE_PROCESSORS * 32);

    public int getCorePoolSize() {
        return CORE_POOL_SIZE;
    }

    public int getMaximumPoolSize() {
        return MAXIMUM_POOL_SIZE;
    }

    public long getKeepAliveTime() {
        return 60L;
    }

    public TimeUnit getTimeUnit() {
        return TimeUnit.SECONDS;
    }

    public int getQueueSize() {
        return QUEUE_SIZE;
    }

    public CpuBasedThreadPoolConfiguration() {
        log.debug(MoreObjects.toStringHelper(this.getClass()).add("AVAILABLE_PROCESSORS", AVAILABLE_PROCESSORS).add("CORE_POOL_SIZE", CORE_POOL_SIZE).add("MAXIMUM_POOL_SIZE", MAXIMUM_POOL_SIZE).add("QUEUE_SIZE", QUEUE_SIZE).add("KEEP_ALIVE_TIME", 60L).toString());
    }
}

