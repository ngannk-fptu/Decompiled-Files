/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  org.hibernate.CacheMode
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core.persistence.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.CacheMode;
import com.atlassian.confluence.util.Cleanup;
import com.atlassian.spring.container.ContainerManager;
import java.util.Optional;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionCacheModeThreadLocal {
    private static final Logger log = LoggerFactory.getLogger(SessionCacheModeThreadLocal.class);
    private static final CacheMode DEFAULT_CACHE_MODE = CacheMode.NORMAL;
    private static final ThreadLocal<CacheMode> currentMode = new ThreadLocal();

    public static CacheMode get() {
        return Optional.ofNullable(currentMode.get()).orElse(DEFAULT_CACHE_MODE);
    }

    public static void set(CacheMode mode) {
        currentMode.set(mode);
        if (CacheMode.IGNORE == mode) {
            SessionCacheModeThreadLocal.currentSession().ifPresent(session -> session.setCacheMode(org.hibernate.CacheMode.IGNORE));
        } else if (CacheMode.NORMAL == mode) {
            SessionCacheModeThreadLocal.currentSession().ifPresent(session -> session.setCacheMode(org.hibernate.CacheMode.NORMAL));
        }
    }

    public static Cleanup temporarilySetCacheMode(CacheMode cacheMode) {
        CacheMode original = SessionCacheModeThreadLocal.get();
        log.debug("Setting Hibernate 2nd-level caching mode to {}", (Object)cacheMode);
        SessionCacheModeThreadLocal.set(cacheMode);
        return () -> {
            SessionCacheModeThreadLocal.set(original);
            log.debug("Restored Hibernate 2nd-level caching mode to {}", (Object)original);
        };
    }

    private static Optional<Session> currentSession() {
        if (!ContainerManager.isContainerSetup()) {
            log.debug("Container context is not ready");
            return Optional.empty();
        }
        SessionFactory sessionFactory = (SessionFactory)ContainerManager.getComponent((String)"sessionFactory", SessionFactory.class);
        if (sessionFactory == null) {
            log.debug("Cannot retrieve SessionFactory from application context");
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(sessionFactory.getCurrentSession());
        }
        catch (HibernateException he) {
            log.debug("Error getting current session", (Throwable)he);
            return Optional.empty();
        }
    }
}

