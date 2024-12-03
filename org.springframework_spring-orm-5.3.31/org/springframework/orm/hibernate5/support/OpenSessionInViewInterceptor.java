/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.hibernate.FlushMode
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.ui.ModelMap
 *  org.springframework.util.Assert
 *  org.springframework.web.context.request.AsyncWebRequestInterceptor
 *  org.springframework.web.context.request.WebRequest
 *  org.springframework.web.context.request.async.CallableProcessingInterceptor
 *  org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
 *  org.springframework.web.context.request.async.WebAsyncManager
 *  org.springframework.web.context.request.async.WebAsyncUtils
 */
package org.springframework.orm.hibernate5.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.orm.hibernate5.support.AsyncRequestInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.context.request.AsyncWebRequestInterceptor;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;

public class OpenSessionInViewInterceptor
implements AsyncWebRequestInterceptor {
    public static final String PARTICIPATE_SUFFIX = ".PARTICIPATE";
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private SessionFactory sessionFactory;

    public void setSessionFactory(@Nullable SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Nullable
    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    private SessionFactory obtainSessionFactory() {
        SessionFactory sf = this.getSessionFactory();
        Assert.state((sf != null ? 1 : 0) != 0, (String)"No SessionFactory set");
        return sf;
    }

    public void preHandle(WebRequest request) throws DataAccessException {
        String key = this.getParticipateAttributeName();
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager((WebRequest)request);
        if (asyncManager.hasConcurrentResult() && this.applySessionBindingInterceptor(asyncManager, key)) {
            return;
        }
        if (TransactionSynchronizationManager.hasResource((Object)this.obtainSessionFactory())) {
            Integer count = (Integer)request.getAttribute(key, 0);
            int newCount = count != null ? count + 1 : 1;
            request.setAttribute(this.getParticipateAttributeName(), (Object)newCount, 0);
        } else {
            this.logger.debug((Object)"Opening Hibernate Session in OpenSessionInViewInterceptor");
            Session session = this.openSession();
            SessionHolder sessionHolder = new SessionHolder(session);
            TransactionSynchronizationManager.bindResource((Object)this.obtainSessionFactory(), (Object)((Object)sessionHolder));
            AsyncRequestInterceptor asyncRequestInterceptor = new AsyncRequestInterceptor(this.obtainSessionFactory(), sessionHolder);
            asyncManager.registerCallableInterceptor((Object)key, (CallableProcessingInterceptor)asyncRequestInterceptor);
            asyncManager.registerDeferredResultInterceptor((Object)key, (DeferredResultProcessingInterceptor)asyncRequestInterceptor);
        }
    }

    public void postHandle(WebRequest request, @Nullable ModelMap model) {
    }

    public void afterCompletion(WebRequest request, @Nullable Exception ex) throws DataAccessException {
        if (!this.decrementParticipateCount(request)) {
            SessionHolder sessionHolder = (SessionHolder)((Object)TransactionSynchronizationManager.unbindResource((Object)this.obtainSessionFactory()));
            this.logger.debug((Object)"Closing Hibernate Session in OpenSessionInViewInterceptor");
            SessionFactoryUtils.closeSession(sessionHolder.getSession());
        }
    }

    private boolean decrementParticipateCount(WebRequest request) {
        String participateAttributeName = this.getParticipateAttributeName();
        Integer count = (Integer)request.getAttribute(participateAttributeName, 0);
        if (count == null) {
            return false;
        }
        if (count > 1) {
            request.setAttribute(participateAttributeName, (Object)(count - 1), 0);
        } else {
            request.removeAttribute(participateAttributeName, 0);
        }
        return true;
    }

    public void afterConcurrentHandlingStarted(WebRequest request) {
        if (!this.decrementParticipateCount(request)) {
            TransactionSynchronizationManager.unbindResource((Object)this.obtainSessionFactory());
        }
    }

    protected Session openSession() throws DataAccessResourceFailureException {
        try {
            Session session = this.obtainSessionFactory().openSession();
            session.setHibernateFlushMode(FlushMode.MANUAL);
            return session;
        }
        catch (HibernateException ex) {
            throw new DataAccessResourceFailureException("Could not open Hibernate Session", (Throwable)ex);
        }
    }

    protected String getParticipateAttributeName() {
        return this.obtainSessionFactory().toString() + PARTICIPATE_SUFFIX;
    }

    private boolean applySessionBindingInterceptor(WebAsyncManager asyncManager, String key) {
        CallableProcessingInterceptor cpi = asyncManager.getCallableInterceptor((Object)key);
        if (cpi == null) {
            return false;
        }
        ((AsyncRequestInterceptor)cpi).bindSession();
        return true;
    }
}

