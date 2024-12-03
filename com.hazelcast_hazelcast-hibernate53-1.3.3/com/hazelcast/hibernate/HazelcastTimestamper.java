/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.config.Config
 *  com.hazelcast.config.MapConfig
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.logging.Logger
 */
package com.hazelcast.hibernate;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.hibernate.CacheEnvironment;
import com.hazelcast.logging.Logger;

public final class HazelcastTimestamper {
    private static final int SEC_TO_MS = 1000;

    private HazelcastTimestamper() {
    }

    public static long nextTimestamp(HazelcastInstance instance) {
        if (instance == null) {
            throw new RuntimeException("No Hazelcast instance!");
        }
        if (instance.getCluster() == null) {
            throw new RuntimeException("Hazelcast instance has no cluster!");
        }
        return instance.getCluster().getClusterTime();
    }

    public static int getTimeout(HazelcastInstance instance, String regionName) {
        try {
            MapConfig cfg = instance.getConfig().findMapConfig(regionName);
            if (cfg.getTimeToLiveSeconds() > 0) {
                return cfg.getTimeToLiveSeconds() * 1000;
            }
        }
        catch (UnsupportedOperationException e) {
            Logger.getLogger(HazelcastTimestamper.class).finest((Throwable)e);
        }
        return CacheEnvironment.getDefaultCacheTimeoutInMillis();
    }

    public static long getMaxOperationTimeout(HazelcastInstance instance) {
        String maxOpTimeoutProp = null;
        try {
            Config config = instance.getConfig();
            maxOpTimeoutProp = config.getProperty("hazelcast.operation.call.timeout.millis");
        }
        catch (UnsupportedOperationException e) {
            Logger.getLogger(HazelcastTimestamper.class).finest((Throwable)e);
        }
        if (maxOpTimeoutProp != null) {
            return Long.parseLong(maxOpTimeoutProp);
        }
        return Long.MAX_VALUE;
    }
}

