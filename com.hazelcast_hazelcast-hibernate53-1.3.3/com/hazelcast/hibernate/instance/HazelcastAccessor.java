/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.logging.Logger
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.cache.spi.RegionFactory
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 */
package com.hazelcast.hibernate.instance;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.hibernate.AbstractHazelcastCacheRegionFactory;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;

@Deprecated
public final class HazelcastAccessor {
    static final ILogger LOGGER = Logger.getLogger(HazelcastAccessor.class);

    private HazelcastAccessor() {
    }

    public static HazelcastInstance getHazelcastInstance(Session session) {
        return HazelcastAccessor.getHazelcastInstance(session.getSessionFactory());
    }

    public static HazelcastInstance getHazelcastInstance(SessionFactory sessionFactory) {
        if (!(sessionFactory instanceof SessionFactoryImplementor)) {
            LOGGER.warning("SessionFactory is expected to be instance of SessionFactoryImplementor.");
            return null;
        }
        return HazelcastAccessor.getHazelcastInstance((SessionFactoryImplementor)sessionFactory);
    }

    public static HazelcastInstance getHazelcastInstance(SessionFactoryImplementor sessionFactory) {
        RegionFactory rf = (RegionFactory)sessionFactory.getSessionFactoryOptions().getServiceRegistry().getService(RegionFactory.class);
        if (rf instanceof AbstractHazelcastCacheRegionFactory) {
            return ((AbstractHazelcastCacheRegionFactory)rf).getHazelcastInstance();
        }
        LOGGER.warning("Current 2nd level cache implementation is not HazelcastCacheRegionFactory!");
        return null;
    }
}

