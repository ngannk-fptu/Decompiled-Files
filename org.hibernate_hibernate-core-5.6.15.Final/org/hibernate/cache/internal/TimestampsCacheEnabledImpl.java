/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cache.internal;

import java.io.Serializable;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsCache;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.jboss.logging.Logger;

public class TimestampsCacheEnabledImpl
implements TimestampsCache {
    private static final Logger log = Logger.getLogger(TimestampsCacheEnabledImpl.class);
    private final TimestampsRegion timestampsRegion;

    public TimestampsCacheEnabledImpl(TimestampsRegion timestampsRegion) {
        this.timestampsRegion = timestampsRegion;
    }

    @Override
    public TimestampsRegion getRegion() {
        return this.timestampsRegion;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void preInvalidate(String[] spaces, SharedSessionContractImplementor session) {
        SessionFactoryImplementor factory = session.getFactory();
        RegionFactory regionFactory = factory.getCache().getRegionFactory();
        StatisticsImplementor statistics = factory.getStatistics();
        boolean stats = statistics.isStatisticsEnabled();
        Long ts = regionFactory.nextTimestamp() + regionFactory.getTimeout();
        SessionEventListenerManager eventListenerManager = session.getEventListenerManager();
        boolean debugEnabled = log.isDebugEnabled();
        for (String space : spaces) {
            if (debugEnabled) {
                log.debugf("Pre-invalidating space [%s], timestamp: %s", (Object)space, (Object)ts);
            }
            try {
                eventListenerManager.cachePutStart();
                this.timestampsRegion.putIntoCache(space, ts, session);
            }
            finally {
                eventListenerManager.cachePutEnd();
            }
            if (!stats) continue;
            statistics.updateTimestampsCachePut();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void invalidate(String[] spaces, SharedSessionContractImplementor session) {
        StatisticsImplementor statistics = session.getFactory().getStatistics();
        boolean stats = statistics.isStatisticsEnabled();
        Long ts = session.getFactory().getCache().getRegionFactory().nextTimestamp();
        boolean debugEnabled = log.isDebugEnabled();
        for (String space : spaces) {
            if (debugEnabled) {
                log.debugf("Invalidating space [%s], timestamp: %s", (Object)space, (Object)ts);
            }
            SessionEventListenerManager eventListenerManager = session.getEventListenerManager();
            try {
                eventListenerManager.cachePutStart();
                this.timestampsRegion.putIntoCache(space, ts, session);
            }
            finally {
                eventListenerManager.cachePutEnd();
                if (stats) {
                    statistics.updateTimestampsCachePut();
                }
            }
        }
    }

    @Override
    public boolean isUpToDate(String[] spaces, Long timestamp, SharedSessionContractImplementor session) {
        StatisticsImplementor statistics = session.getFactory().getStatistics();
        boolean stats = statistics.isStatisticsEnabled();
        boolean debugEnabled = log.isDebugEnabled();
        for (String space : spaces) {
            Long lastUpdate = this.getLastUpdateTimestampForSpace((Serializable)((Object)space), session);
            if (lastUpdate == null) {
                if (!stats) continue;
                statistics.updateTimestampsCacheMiss();
                continue;
            }
            if (debugEnabled) {
                log.debugf("[%s] last update timestamp: %s", (Object)space, (Object)(lastUpdate + ", result set timestamp: " + timestamp));
            }
            if (stats) {
                statistics.updateTimestampsCacheHit();
            }
            if (lastUpdate < timestamp) continue;
            return false;
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Long getLastUpdateTimestampForSpace(Serializable space, SharedSessionContractImplementor session) {
        Long ts = null;
        try {
            session.getEventListenerManager().cacheGetStart();
            ts = (Long)this.timestampsRegion.getFromCache(space, session);
            session.getEventListenerManager().cacheGetEnd(ts != null);
        }
        catch (Throwable throwable) {
            session.getEventListenerManager().cacheGetEnd(ts != null);
            throw throwable;
        }
        return ts;
    }
}

