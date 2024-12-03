/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ehcache.distribution.RMIAsynchronousCacheReplicator
 *  net.sf.ehcache.event.CacheEventListener
 *  net.sf.ehcache.event.CacheEventListenerFactory
 *  net.sf.ehcache.util.PropertyUtil
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.ehcache.replication.rmi;

import com.atlassian.cache.ehcache.replication.rmi.RMISynchronousCacheReplicator;
import java.util.Properties;
import net.sf.ehcache.distribution.RMIAsynchronousCacheReplicator;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;
import net.sf.ehcache.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RMICacheReplicatorFactory
extends CacheEventListenerFactory {
    protected static final int DEFAULT_ASYNCHRONOUS_REPLICATION_INTERVAL_MILLIS = 1000;
    protected static final int DEFAULT_ASYNCHRONOUS_REPLICATION_MAXIMUM_BATCH_SIZE = 1000;
    private static final Logger LOG = LoggerFactory.getLogger((String)RMICacheReplicatorFactory.class.getName());
    private static final String REPLICATE_PUTS = "replicatePuts";
    private static final String REPLICATE_PUTS_VIA_COPY = "replicatePutsViaCopy";
    private static final String REPLICATE_UPDATES = "replicateUpdates";
    private static final String REPLICATE_UPDATES_VIA_COPY = "replicateUpdatesViaCopy";
    private static final String REPLICATE_REMOVALS = "replicateRemovals";
    static final String REPLICATE_ASYNCHRONOUSLY = "replicateAsynchronously";
    private static final String ASYNCHRONOUS_REPLICATION_INTERVAL_MILLIS = "asynchronousReplicationIntervalMillis";
    private static final String ASYNCHRONOUS_REPLICATION_MAXIMUM_BATCH_SIZE = "asynchronousReplicationMaximumBatchSize";
    private static final int MINIMUM_REASONABLE_INTERVAL = 10;

    private static boolean extractBoolean(String key, Properties properties) {
        String booleanString = PropertyUtil.extractAndLogProperty((String)key, (Properties)properties);
        return booleanString == null || PropertyUtil.parseBoolean((String)booleanString);
    }

    private static int extractReplicationIntervalMilis(Properties properties) {
        int asynchronousReplicationIntervalMillis;
        block5: {
            String asynchronousReplicationIntervalMillisString = PropertyUtil.extractAndLogProperty((String)ASYNCHRONOUS_REPLICATION_INTERVAL_MILLIS, (Properties)properties);
            if (asynchronousReplicationIntervalMillisString != null) {
                try {
                    int asynchronousReplicationIntervalMillisCandidate = Integer.parseInt(asynchronousReplicationIntervalMillisString);
                    if (asynchronousReplicationIntervalMillisCandidate < 10) {
                        LOG.debug("Trying to set the asynchronousReplicationIntervalMillis to an unreasonable number. Using the default instead.");
                        asynchronousReplicationIntervalMillis = 1000;
                        break block5;
                    }
                    asynchronousReplicationIntervalMillis = asynchronousReplicationIntervalMillisCandidate;
                }
                catch (NumberFormatException e) {
                    LOG.warn("Number format exception trying to set asynchronousReplicationIntervalMillis. Using the default instead. String value was: '" + asynchronousReplicationIntervalMillisString + "'");
                    asynchronousReplicationIntervalMillis = 1000;
                }
            } else {
                asynchronousReplicationIntervalMillis = 1000;
            }
        }
        return asynchronousReplicationIntervalMillis;
    }

    private static int extractMaximumBatchSize(Properties properties) {
        String maximumBatchSizeString = PropertyUtil.extractAndLogProperty((String)ASYNCHRONOUS_REPLICATION_MAXIMUM_BATCH_SIZE, (Properties)properties);
        if (maximumBatchSizeString == null) {
            return 1000;
        }
        try {
            return Integer.parseInt(maximumBatchSizeString);
        }
        catch (NumberFormatException e) {
            LOG.warn("Number format exception trying to set maximumBatchSize. Using the default instead. String value was: '" + maximumBatchSizeString + "'");
            return 1000;
        }
    }

    public CacheEventListener createCacheEventListener(Properties properties) {
        boolean replicatePuts = RMICacheReplicatorFactory.extractBoolean(REPLICATE_PUTS, properties);
        boolean replicatePutsViaCopy = RMICacheReplicatorFactory.extractBoolean(REPLICATE_PUTS_VIA_COPY, properties);
        boolean replicateUpdates = RMICacheReplicatorFactory.extractBoolean(REPLICATE_UPDATES, properties);
        boolean replicateUpdatesViaCopy = RMICacheReplicatorFactory.extractBoolean(REPLICATE_UPDATES_VIA_COPY, properties);
        boolean replicateRemovals = RMICacheReplicatorFactory.extractBoolean(REPLICATE_REMOVALS, properties);
        boolean replicateAsynchronously = RMICacheReplicatorFactory.extractBoolean(REPLICATE_ASYNCHRONOUSLY, properties);
        int replicationIntervalMillis = RMICacheReplicatorFactory.extractReplicationIntervalMilis(properties);
        int maximumBatchSize = RMICacheReplicatorFactory.extractMaximumBatchSize(properties);
        if (replicateAsynchronously) {
            return new RMIAsynchronousCacheReplicator(replicatePuts, replicatePutsViaCopy, replicateUpdates, replicateUpdatesViaCopy, replicateRemovals, replicationIntervalMillis, maximumBatchSize);
        }
        return new RMISynchronousCacheReplicator(replicatePuts, replicatePutsViaCopy, replicateUpdates, replicateUpdatesViaCopy, replicateRemovals);
    }
}

