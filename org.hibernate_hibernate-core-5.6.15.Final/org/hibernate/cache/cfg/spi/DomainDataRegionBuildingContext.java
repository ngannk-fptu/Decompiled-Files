/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cache.cfg.spi;

import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public interface DomainDataRegionBuildingContext {
    public CacheKeysFactory getEnforcedCacheKeysFactory();

    public SessionFactoryImplementor getSessionFactory();
}

