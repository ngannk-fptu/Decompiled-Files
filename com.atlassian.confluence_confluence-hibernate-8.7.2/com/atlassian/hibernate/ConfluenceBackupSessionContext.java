/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.context.spi.CurrentSessionContext
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.springframework.orm.hibernate5.SessionHolder
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package com.atlassian.hibernate;

import com.atlassian.spring.container.ContainerManager;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Deprecated(forRemoval=true)
public class ConfluenceBackupSessionContext
implements CurrentSessionContext {
    private final SessionFactoryImplementor sessionFactory;

    public ConfluenceBackupSessionContext(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session currentSession() throws HibernateException {
        SessionFactory sessionFactory = (SessionFactory)ContainerManager.getComponent((String)"sessionFactoryForBackup5", SessionFactory.class);
        Object obj = TransactionSynchronizationManager.getResource((Object)sessionFactory);
        if (obj instanceof Session) {
            return (Session)obj;
        }
        if (obj instanceof SessionHolder) {
            SessionHolder sessionHolder = (SessionHolder)obj;
            return sessionHolder.getSession();
        }
        throw new HibernateException("Failed to find " + Session.class.getName() + " from the current thread");
    }
}

