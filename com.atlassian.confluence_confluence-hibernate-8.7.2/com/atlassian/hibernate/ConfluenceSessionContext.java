/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.context.spi.CurrentSessionContext
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.internal.SessionFactoryRegistry
 *  org.springframework.orm.hibernate5.SessionHolder
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package com.atlassian.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionFactoryRegistry;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class ConfluenceSessionContext
implements CurrentSessionContext {
    private final SessionFactoryImplementor sessionFactory;

    public ConfluenceSessionContext(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session currentSession() throws HibernateException {
        Object obj = TransactionSynchronizationManager.getResource((Object)this.getSessionFactory());
        if (obj instanceof Session) {
            return (Session)obj;
        }
        if (obj instanceof SessionHolder) {
            SessionHolder sessionHolder = (SessionHolder)obj;
            return sessionHolder.getSession();
        }
        throw new HibernateException("Failed to find " + Session.class.getName() + " from the current thread");
    }

    private SessionFactory getSessionFactory() {
        return SessionFactoryRegistry.INSTANCE.getSessionFactory(this.sessionFactory.getUuid());
    }
}

