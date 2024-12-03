/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.hibernate.FlushMode
 *  org.hibernate.HibernateException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.web.context.WebApplicationContext
 *  org.springframework.web.context.request.async.CallableProcessingInterceptor
 *  org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
 *  org.springframework.web.context.request.async.WebAsyncManager
 *  org.springframework.web.context.request.async.WebAsyncUtils
 *  org.springframework.web.context.support.WebApplicationContextUtils
 *  org.springframework.web.filter.OncePerRequestFilter
 */
package org.springframework.orm.hibernate5.support;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.orm.hibernate5.support.AsyncRequestInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class OpenSessionInViewFilter
extends OncePerRequestFilter {
    public static final String DEFAULT_SESSION_FACTORY_BEAN_NAME = "sessionFactory";
    private String sessionFactoryBeanName = "sessionFactory";

    public void setSessionFactoryBeanName(String sessionFactoryBeanName) {
        this.sessionFactoryBeanName = sessionFactoryBeanName;
    }

    protected String getSessionFactoryBeanName() {
        return this.sessionFactoryBeanName;
    }

    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        SessionFactory sessionFactory = this.lookupSessionFactory(request);
        boolean participate = false;
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager((ServletRequest)request);
        String key = this.getAlreadyFilteredAttributeName();
        if (TransactionSynchronizationManager.hasResource((Object)sessionFactory)) {
            participate = true;
        } else {
            boolean isFirstRequest;
            boolean bl = isFirstRequest = !this.isAsyncDispatch(request);
            if (isFirstRequest || !this.applySessionBindingInterceptor(asyncManager, key)) {
                this.logger.debug((Object)"Opening Hibernate Session in OpenSessionInViewFilter");
                Session session = this.openSession(sessionFactory);
                SessionHolder sessionHolder = new SessionHolder(session);
                TransactionSynchronizationManager.bindResource((Object)sessionFactory, (Object)((Object)sessionHolder));
                AsyncRequestInterceptor interceptor = new AsyncRequestInterceptor(sessionFactory, sessionHolder);
                asyncManager.registerCallableInterceptor((Object)key, (CallableProcessingInterceptor)interceptor);
                asyncManager.registerDeferredResultInterceptor((Object)key, (DeferredResultProcessingInterceptor)interceptor);
            }
        }
        try {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            if (!participate) {
                SessionHolder sessionHolder = (SessionHolder)((Object)TransactionSynchronizationManager.unbindResource((Object)sessionFactory));
                if (!this.isAsyncStarted(request)) {
                    this.logger.debug((Object)"Closing Hibernate Session in OpenSessionInViewFilter");
                    SessionFactoryUtils.closeSession(sessionHolder.getSession());
                }
            }
        }
    }

    protected SessionFactory lookupSessionFactory(HttpServletRequest request) {
        return this.lookupSessionFactory();
    }

    protected SessionFactory lookupSessionFactory() {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Using SessionFactory '" + this.getSessionFactoryBeanName() + "' for OpenSessionInViewFilter"));
        }
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext((ServletContext)this.getServletContext());
        return (SessionFactory)wac.getBean(this.getSessionFactoryBeanName(), SessionFactory.class);
    }

    protected Session openSession(SessionFactory sessionFactory) throws DataAccessResourceFailureException {
        try {
            Session session = sessionFactory.openSession();
            session.setHibernateFlushMode(FlushMode.MANUAL);
            return session;
        }
        catch (HibernateException ex) {
            throw new DataAccessResourceFailureException("Could not open Hibernate Session", (Throwable)ex);
        }
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

