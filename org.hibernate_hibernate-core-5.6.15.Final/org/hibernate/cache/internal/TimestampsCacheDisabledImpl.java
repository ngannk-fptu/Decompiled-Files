/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cache.internal;

import org.hibernate.cache.spi.TimestampsCache;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.jboss.logging.Logger;

public class TimestampsCacheDisabledImpl
implements TimestampsCache {
    private static final Logger log = Logger.getLogger(TimestampsCacheDisabledImpl.class);

    @Override
    public TimestampsRegion getRegion() {
        return null;
    }

    @Override
    public void preInvalidate(String[] spaces, SharedSessionContractImplementor session) {
        log.trace((Object)"TimestampsRegionAccess#preInvalidate - disabled");
    }

    @Override
    public void invalidate(String[] spaces, SharedSessionContractImplementor session) {
        log.trace((Object)"TimestampsRegionAccess#invalidate - disabled");
    }

    @Override
    public boolean isUpToDate(String[] spaces, Long timestamp, SharedSessionContractImplementor session) {
        log.trace((Object)"TimestampsRegionAccess#isUpToDate - disabled");
        return false;
    }
}

