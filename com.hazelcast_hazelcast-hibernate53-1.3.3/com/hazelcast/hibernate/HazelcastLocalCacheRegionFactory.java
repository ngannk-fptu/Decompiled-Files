/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.util.Clock
 *  org.hibernate.cache.cfg.spi.DomainDataRegionConfig
 *  org.hibernate.cache.spi.CacheKeysFactory
 *  org.hibernate.cache.spi.RegionFactory
 *  org.hibernate.cache.spi.support.RegionNameQualifier
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 */
package com.hazelcast.hibernate;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.hibernate.AbstractHazelcastCacheRegionFactory;
import com.hazelcast.hibernate.HazelcastTimestamper;
import com.hazelcast.hibernate.RegionCache;
import com.hazelcast.hibernate.local.LocalRegionCache;
import com.hazelcast.hibernate.local.TimestampsRegionCache;
import com.hazelcast.util.Clock;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.support.RegionNameQualifier;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class HazelcastLocalCacheRegionFactory
extends AbstractHazelcastCacheRegionFactory {
    public HazelcastLocalCacheRegionFactory() {
    }

    public HazelcastLocalCacheRegionFactory(CacheKeysFactory cacheKeysFactory) {
        super(cacheKeysFactory);
    }

    public HazelcastLocalCacheRegionFactory(HazelcastInstance instance) {
        super(instance);
    }

    @Override
    protected RegionCache createRegionCache(String unqualifiedRegionName, SessionFactoryImplementor sessionFactory, DomainDataRegionConfig regionConfig) {
        this.verifyStarted();
        assert (!RegionNameQualifier.INSTANCE.isQualified(unqualifiedRegionName, sessionFactory.getSessionFactoryOptions()));
        String qualifiedRegionName = RegionNameQualifier.INSTANCE.qualify(unqualifiedRegionName, sessionFactory.getSessionFactoryOptions());
        LocalRegionCache regionCache = new LocalRegionCache((RegionFactory)this, qualifiedRegionName, this.instance, regionConfig);
        this.cleanupService.registerCache(regionCache);
        return regionCache;
    }

    @Override
    protected RegionCache createTimestampsRegionCache(String unqualifiedRegionName, SessionFactoryImplementor sessionFactory) {
        this.verifyStarted();
        assert (!RegionNameQualifier.INSTANCE.isQualified(unqualifiedRegionName, sessionFactory.getSessionFactoryOptions()));
        String qualifiedRegionName = RegionNameQualifier.INSTANCE.qualify(unqualifiedRegionName, sessionFactory.getSessionFactoryOptions());
        return new TimestampsRegionCache((RegionFactory)this, qualifiedRegionName, this.instance);
    }

    public long nextTimestamp() {
        long result = this.instance == null ? Clock.currentTimeMillis() : HazelcastTimestamper.nextTimestamp(this.instance);
        return result;
    }
}

