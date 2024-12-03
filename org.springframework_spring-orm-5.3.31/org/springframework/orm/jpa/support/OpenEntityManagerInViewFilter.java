/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.PersistenceException
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletContext
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.util.StringUtils
 *  org.springframework.web.context.WebApplicationContext
 *  org.springframework.web.context.request.async.CallableProcessingInterceptor
 *  org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
 *  org.springframework.web.context.request.async.WebAsyncManager
 *  org.springframework.web.context.request.async.WebAsyncUtils
 *  org.springframework.web.context.support.WebApplicationContextUtils
 *  org.springframework.web.filter.OncePerRequestFilter
 */
package org.springframework.orm.jpa.support;

import java.io.IOException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.support.AsyncRequestInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class OpenEntityManagerInViewFilter
extends OncePerRequestFilter {
    public static final String DEFAULT_ENTITY_MANAGER_FACTORY_BEAN_NAME = "entityManagerFactory";
    @Nullable
    private String entityManagerFactoryBeanName;
    @Nullable
    private String persistenceUnitName;
    @Nullable
    private volatile EntityManagerFactory entityManagerFactory;

    public void setEntityManagerFactoryBeanName(@Nullable String entityManagerFactoryBeanName) {
        this.entityManagerFactoryBeanName = entityManagerFactoryBeanName;
    }

    @Nullable
    protected String getEntityManagerFactoryBeanName() {
        return this.entityManagerFactoryBeanName;
    }

    public void setPersistenceUnitName(@Nullable String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    @Nullable
    protected String getPersistenceUnitName() {
        return this.persistenceUnitName;
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
        EntityManagerFactory emf = this.lookupEntityManagerFactory(request);
        boolean participate = false;
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager((ServletRequest)request);
        String key = this.getAlreadyFilteredAttributeName();
        if (TransactionSynchronizationManager.hasResource((Object)emf)) {
            participate = true;
        } else {
            boolean isFirstRequest;
            boolean bl = isFirstRequest = !this.isAsyncDispatch(request);
            if (isFirstRequest || !this.applyEntityManagerBindingInterceptor(asyncManager, key)) {
                this.logger.debug((Object)"Opening JPA EntityManager in OpenEntityManagerInViewFilter");
                try {
                    EntityManager em = this.createEntityManager(emf);
                    EntityManagerHolder emHolder = new EntityManagerHolder(em);
                    TransactionSynchronizationManager.bindResource((Object)emf, (Object)((Object)emHolder));
                    AsyncRequestInterceptor interceptor = new AsyncRequestInterceptor(emf, emHolder);
                    asyncManager.registerCallableInterceptor((Object)key, (CallableProcessingInterceptor)interceptor);
                    asyncManager.registerDeferredResultInterceptor((Object)key, (DeferredResultProcessingInterceptor)interceptor);
                }
                catch (PersistenceException ex) {
                    throw new DataAccessResourceFailureException("Could not create JPA EntityManager", (Throwable)ex);
                }
            }
        }
        try {
            filterChain.doFilter((ServletRequest)request, (ServletResponse)response);
        }
        finally {
            if (!participate) {
                EntityManagerHolder emHolder = (EntityManagerHolder)((Object)TransactionSynchronizationManager.unbindResource((Object)emf));
                if (!this.isAsyncStarted(request)) {
                    this.logger.debug((Object)"Closing JPA EntityManager in OpenEntityManagerInViewFilter");
                    EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
                }
            }
        }
    }

    protected EntityManagerFactory lookupEntityManagerFactory(HttpServletRequest request) {
        EntityManagerFactory emf = this.entityManagerFactory;
        if (emf == null) {
            this.entityManagerFactory = emf = this.lookupEntityManagerFactory();
        }
        return emf;
    }

    protected EntityManagerFactory lookupEntityManagerFactory() {
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext((ServletContext)this.getServletContext());
        String emfBeanName = this.getEntityManagerFactoryBeanName();
        String puName = this.getPersistenceUnitName();
        if (StringUtils.hasLength((String)emfBeanName)) {
            return (EntityManagerFactory)wac.getBean(emfBeanName, EntityManagerFactory.class);
        }
        if (!StringUtils.hasLength((String)puName) && wac.containsBean(DEFAULT_ENTITY_MANAGER_FACTORY_BEAN_NAME)) {
            return (EntityManagerFactory)wac.getBean(DEFAULT_ENTITY_MANAGER_FACTORY_BEAN_NAME, EntityManagerFactory.class);
        }
        return EntityManagerFactoryUtils.findEntityManagerFactory((ListableBeanFactory)wac, puName);
    }

    protected EntityManager createEntityManager(EntityManagerFactory emf) {
        return emf.createEntityManager();
    }

    private boolean applyEntityManagerBindingInterceptor(WebAsyncManager asyncManager, String key) {
        CallableProcessingInterceptor cpi = asyncManager.getCallableInterceptor((Object)key);
        if (cpi == null) {
            return false;
        }
        ((AsyncRequestInterceptor)cpi).bindEntityManager();
        return true;
    }
}

