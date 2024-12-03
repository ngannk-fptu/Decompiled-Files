/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.internal.DisabledCaching;
import org.hibernate.cache.internal.EnabledCaching;
import org.hibernate.cache.internal.NoCachingRegionFactory;
import org.hibernate.cache.spi.CacheImplementor;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceInitiator;

public class CacheInitiator
implements SessionFactoryServiceInitiator<CacheImplementor> {
    public static final CacheInitiator INSTANCE = new CacheInitiator();

    @Override
    public CacheImplementor initiateService(SessionFactoryImplementor sessionFactory, SessionFactoryOptions sessionFactoryOptions, ServiceRegistryImplementor registry) {
        RegionFactory regionFactory = registry.getService(RegionFactory.class);
        return !NoCachingRegionFactory.class.isInstance(regionFactory) ? new EnabledCaching(sessionFactory) : new DisabledCaching(sessionFactory);
    }

    @Override
    public Class<CacheImplementor> getServiceInitiated() {
        return CacheImplementor.class;
    }
}

