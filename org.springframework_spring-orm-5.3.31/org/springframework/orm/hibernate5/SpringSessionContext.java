/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.SystemException
 *  javax.transaction.TransactionManager
 *  org.apache.commons.logging.LogFactory
 *  org.hibernate.FlushMode
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.context.spi.CurrentSessionContext
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package org.springframework.orm.hibernate5;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.orm.hibernate5.SpringFlushSynchronization;
import org.springframework.orm.hibernate5.SpringJtaSessionContext;
import org.springframework.orm.hibernate5.SpringSessionSynchronization;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SpringSessionContext
implements CurrentSessionContext {
    private final SessionFactoryImplementor sessionFactory;
    @Nullable
    private TransactionManager transactionManager;
    @Nullable
    private CurrentSessionContext jtaSessionContext;

    public SpringSessionContext(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
        try {
            JtaPlatform jtaPlatform = (JtaPlatform)sessionFactory.getServiceRegistry().getService(JtaPlatform.class);
            this.transactionManager = jtaPlatform.retrieveTransactionManager();
            if (this.transactionManager != null) {
                this.jtaSessionContext = new SpringJtaSessionContext(sessionFactory);
            }
        }
        catch (Exception ex) {
            LogFactory.getLog(SpringSessionContext.class).warn((Object)"Could not introspect Hibernate JtaPlatform for SpringJtaSessionContext", (Throwable)ex);
        }
    }

    public Session currentSession() throws HibernateException {
        Object value = TransactionSynchronizationManager.getResource((Object)this.sessionFactory);
        if (value instanceof Session) {
            return (Session)value;
        }
        if (value instanceof SessionHolder) {
            SessionHolder sessionHolder = (SessionHolder)((Object)value);
            Session session = sessionHolder.getSession();
            if (!sessionHolder.isSynchronizedWithTransaction() && TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new SpringSessionSynchronization(sessionHolder, (SessionFactory)this.sessionFactory, false));
                sessionHolder.setSynchronizedWithTransaction(true);
                FlushMode flushMode = session.getHibernateFlushMode();
                if (flushMode.equals((Object)FlushMode.MANUAL) && !TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                    session.setHibernateFlushMode(FlushMode.AUTO);
                    sessionHolder.setPreviousFlushMode(flushMode);
                }
            }
            return session;
        }
        if (value instanceof EntityManagerHolder) {
            return (Session)((EntityManagerHolder)((Object)value)).getEntityManager().unwrap(Session.class);
        }
        if (this.transactionManager != null && this.jtaSessionContext != null) {
            try {
                if (this.transactionManager.getStatus() == 0) {
                    Session session = this.jtaSessionContext.currentSession();
                    if (TransactionSynchronizationManager.isSynchronizationActive()) {
                        TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new SpringFlushSynchronization(session));
                    }
                    return session;
                }
            }
            catch (SystemException ex) {
                throw new HibernateException("JTA TransactionManager found but status check failed", (Throwable)ex);
            }
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            Session session = this.sessionFactory.openSession();
            if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                session.setHibernateFlushMode(FlushMode.MANUAL);
            }
            SessionHolder sessionHolder = new SessionHolder(session);
            TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new SpringSessionSynchronization(sessionHolder, (SessionFactory)this.sessionFactory, true));
            TransactionSynchronizationManager.bindResource((Object)this.sessionFactory, (Object)((Object)sessionHolder));
            sessionHolder.setSynchronizedWithTransaction(true);
            return session;
        }
        throw new HibernateException("Could not obtain transaction-synchronized Session for current thread");
    }
}

