/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.hibernate.FlushMode
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.util.Assert
 */
package org.springframework.orm.hibernate5.support;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

public class OpenSessionInterceptor
implements MethodInterceptor,
InitializingBean {
    @Nullable
    private SessionFactory sessionFactory;

    public void setSessionFactory(@Nullable SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Nullable
    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public void afterPropertiesSet() {
        if (this.getSessionFactory() == null) {
            throw new IllegalArgumentException("Property 'sessionFactory' is required");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        SessionFactory sf = this.getSessionFactory();
        Assert.state((sf != null ? 1 : 0) != 0, (String)"No SessionFactory set");
        if (!TransactionSynchronizationManager.hasResource((Object)sf)) {
            Session session = this.openSession(sf);
            try {
                TransactionSynchronizationManager.bindResource((Object)sf, (Object)((Object)new SessionHolder(session)));
                Object object = invocation.proceed();
                return object;
            }
            finally {
                SessionFactoryUtils.closeSession(session);
                TransactionSynchronizationManager.unbindResource((Object)sf);
            }
        }
        return invocation.proceed();
    }

    protected Session openSession(SessionFactory sessionFactory) throws DataAccessResourceFailureException {
        Session session = this.openSession();
        if (session == null) {
            try {
                session = sessionFactory.openSession();
                session.setHibernateFlushMode(FlushMode.MANUAL);
            }
            catch (HibernateException ex) {
                throw new DataAccessResourceFailureException("Could not open Hibernate Session", (Throwable)ex);
            }
        }
        return session;
    }

    @Deprecated
    @Nullable
    protected Session openSession() throws DataAccessResourceFailureException {
        return null;
    }
}

