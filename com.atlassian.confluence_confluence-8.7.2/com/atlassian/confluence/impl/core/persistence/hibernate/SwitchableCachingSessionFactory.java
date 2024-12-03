/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.hibernate.ConfluenceSessionContext
 *  org.hibernate.CacheMode
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.context.spi.CurrentSessionContext
 *  org.hibernate.engine.jndi.spi.JndiService
 *  org.hibernate.engine.spi.AbstractDelegatingSessionBuilderImplementor
 *  org.hibernate.engine.spi.SessionBuilderImplementor
 *  org.hibernate.engine.spi.SessionFactoryDelegatingImpl
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.internal.SessionFactoryRegistry
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.core.persistence.hibernate;

import com.atlassian.confluence.core.persistence.hibernate.SessionCacheModeThreadLocal;
import com.atlassian.hibernate.ConfluenceSessionContext;
import org.hibernate.CacheMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.jndi.spi.JndiService;
import org.hibernate.engine.spi.AbstractDelegatingSessionBuilderImplementor;
import org.hibernate.engine.spi.SessionBuilderImplementor;
import org.hibernate.engine.spi.SessionFactoryDelegatingImpl;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionFactoryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SwitchableCachingSessionFactory
extends SessionFactoryDelegatingImpl {
    private static final Logger log = LoggerFactory.getLogger(SwitchableCachingSessionFactory.class);
    private final CurrentSessionContext currentSessionContext = new ConfluenceSessionContext((SessionFactoryImplementor)this);

    public SwitchableCachingSessionFactory(SessionFactoryImplementor delegate) {
        super(delegate);
        this.reregister();
    }

    private void reregister() {
        SessionFactoryRegistry.INSTANCE.addSessionFactory(this.getUuid(), this.getName(), this.getSettings().isSessionFactoryNameAlsoJndiName(), (SessionFactory)this, (JndiService)this.getServiceRegistry().getService(JndiService.class));
    }

    private static Session disableCacheIfRequired(Session session) {
        com.atlassian.confluence.core.persistence.hibernate.CacheMode current = SessionCacheModeThreadLocal.get();
        if (com.atlassian.confluence.core.persistence.hibernate.CacheMode.IGNORE == current) {
            log.debug("Opening session without L2 cache");
            session.setCacheMode(CacheMode.IGNORE);
        }
        return session;
    }

    public SessionBuilderImplementor withOptions() {
        final SessionBuilderImplementor delegate = this.delegate().withOptions();
        return new AbstractDelegatingSessionBuilderImplementor<SessionBuilderImplementor>(delegate){

            public Session openSession() {
                return SwitchableCachingSessionFactory.disableCacheIfRequired(delegate.openSession());
            }
        };
    }

    public Session openTemporarySession() throws HibernateException {
        return SwitchableCachingSessionFactory.disableCacheIfRequired(this.delegate().openTemporarySession());
    }

    public Session openSession() throws HibernateException {
        return SwitchableCachingSessionFactory.disableCacheIfRequired(this.delegate().openSession());
    }

    public Session getCurrentSession() throws HibernateException {
        return this.currentSessionContext.currentSession();
    }
}

