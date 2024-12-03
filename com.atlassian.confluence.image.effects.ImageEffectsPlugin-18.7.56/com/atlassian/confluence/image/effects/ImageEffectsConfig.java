/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Named
 */
package com.atlassian.confluence.image.effects;

import java.util.concurrent.TimeUnit;
import javax.inject.Named;

@Named(value="globalConfig")
public class ImageEffectsConfig {
    public static final String PROPERTY_PREFIX = "atlassian.image_filter.";
    public static final String DISABLE_CACHE = "disable_cache";
    public static final String TRANSFORM_TIMEOUT_MS = "transform.timeout_ms";
    public static final String TRANSFORM_MAX_DATA_SIZE = "transform.max_data_size";
    public static final String THREAD_POOL_CONFIGURATION_CORE_POOL_SIZE = "thread_pool_configuration.core_pool_size";
    public static final String THREAD_POOL_CONFIGURATION_MAX_POOL_SIZE = "thread_pool_configuration.max_pool_size";
    public static final String THREAD_POOL_CONFIGURATION_QUEUE_SIZE = "thread_pool_configuration.queue_size";
    public static final String THREAD_POOL_CONFIGURATION_KEEP_ALIVE_TIME = "thread_pool_configuration.keep_alive_time";
    public static final String THREAD_POOL_CONFIGURATION_TIME_UNIT = "thread_pool_configuration.time_unit";
    private final int corePoolSize = Integer.getInteger("atlassian.image_filter.thread_pool_configuration.core_pool_size", 4);
    private final int maximumPoolSize = Integer.getInteger("atlassian.image_filter.thread_pool_configuration.max_pool_size", 4);
    private final int queueSize = Integer.getInteger("atlassian.image_filter.thread_pool_configuration.queue_size", 1000);
    private final long KEEP_ALIVE_TIME = 0L;
    private volatile boolean disableCache = Boolean.getBoolean("atlassian.image_filter.disable_cache");
    private volatile int transformTimeoutMs = Integer.getInteger("atlassian.image_filter.transform.timeout_ms", 10000);
    private volatile int transformMaxDataSize = Integer.getInteger("atlassian.image_filter.transform.max_data_size", 16000000);

    public int getCorePoolSize() {
        return this.corePoolSize;
    }

    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }

    public int getQueueSize() {
        return this.queueSize;
    }

    public TimeUnit getTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }

    public long getKeepAliveTime() {
        return 0L;
    }

    public boolean isDisableCache() {
        return this.disableCache;
    }

    public void setDisableCache(boolean newValue) {
        this.disableCache = newValue;
    }

    public int getTransformTimeoutMs() {
        return this.transformTimeoutMs;
    }

    public void setTransformTimeoutMs(int transformTimeoutMs) {
        this.transformTimeoutMs = transformTimeoutMs;
    }

    public int getTransformMaxDataSize() {
        return this.transformMaxDataSize;
    }

    public void setTransformMaxDataSize(int transformMaxDataSize) {
        this.transformMaxDataSize = transformMaxDataSize;
    }
}

