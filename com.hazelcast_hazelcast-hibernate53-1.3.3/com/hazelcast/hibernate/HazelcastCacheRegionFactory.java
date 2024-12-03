/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  org.hibernate.cache.cfg.spi.DomainDataRegionConfig
 *  org.hibernate.cache.spi.CacheKeysFactory
 *  org.hibernate.cache.spi.RegionFactory
 *  org.hibernate.cache.spi.support.RegionNameQualifier
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 */
package com.hazelcast.hibernate;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.hibernate.AbstractHazelcastCacheRegionFactory;
import com.hazelcast.hibernate.RegionCache;
import com.hazelcast.hibernate.distributed.IMapRegionCache;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.support.RegionNameQualifier;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class HazelcastCacheRegionFactory
extends AbstractHazelcastCacheRegionFactory {
    public HazelcastCacheRegionFactory() {
    }

    public HazelcastCacheRegionFactory(CacheKeysFactory cacheKeysFactory) {
        super(cacheKeysFactory);
    }

    public HazelcastCacheRegionFactory(HazelcastInstance instance) {
        super(instance);
    }

    @Override
    protected RegionCache createRegionCache(String unqualifiedRegionName, SessionFactoryImplementor sessionFactory, DomainDataRegionConfig regionConfig) {
        this.verifyStarted();
        assert (!RegionNameQualifier.INSTANCE.isQualified(unqualifiedRegionName, sessionFactory.getSessionFactoryOptions()));
        String qualifiedRegionName = RegionNameQualifier.INSTANCE.qualify(unqualifiedRegionName, sessionFactory.getSessionFactoryOptions());
        return new IMapRegionCache((RegionFactory)this, qualifiedRegionName, this.instance);
    }

    @Override
    protected RegionCache createTimestampsRegionCache(String unqualifiedRegionName, SessionFactoryImplementor sessionFactory) {
        this.verifyStarted();
        assert (!RegionNameQualifier.INSTANCE.isQualified(unqualifiedRegionName, sessionFactory.getSessionFactoryOptions()));
        String qualifiedRegionName = RegionNameQualifier.INSTANCE.qualify(unqualifiedRegionName, sessionFactory.getSessionFactoryOptions());
        return new IMapRegionCache((RegionFactory)this, qualifiedRegionName, this.instance);
    }
}

