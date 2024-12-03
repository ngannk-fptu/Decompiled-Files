/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.PersistenceException
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.ui.ModelMap
 *  org.springframework.web.context.request.AsyncWebRequestInterceptor
 *  org.springframework.web.context.request.WebRequest
 *  org.springframework.web.context.request.async.CallableProcessingInterceptor
 *  org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
 *  org.springframework.web.context.request.async.WebAsyncManager
 *  org.springframework.web.context.request.async.WebAsyncUtils
 */
package org.springframework.orm.jpa.support;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryAccessor;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.support.AsyncRequestInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.AsyncWebRequestInterceptor;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;

public class OpenEntityManagerInViewInterceptor
extends EntityManagerFactoryAccessor
implements AsyncWebRequestInterceptor {
    public static final String PARTICIPATE_SUFFIX = ".PARTICIPATE";

    public void preHandle(WebRequest request) throws DataAccessException {
        String key = this.getParticipateAttributeName();
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager((WebRequest)request);
        if (asyncManager.hasConcurrentResult() && this.applyEntityManagerBindingInterceptor(asyncManager, key)) {
            return;
        }
        EntityManagerFactory emf = this.obtainEntityManagerFactory();
        if (TransactionSynchronizationManager.hasResource((Object)emf)) {
            Integer count = (Integer)request.getAttribute(key, 0);
            int newCount = count != null ? count + 1 : 1;
            request.setAttribute(this.getParticipateAttributeName(), (Object)newCount, 0);
        } else {
            this.logger.debug((Object)"Opening JPA EntityManager in OpenEntityManagerInViewInterceptor");
            try {
                EntityManager em = this.createEntityManager();
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

    public void postHandle(WebRequest request, @Nullable ModelMap model) {
    }

    public void afterCompletion(WebRequest request, @Nullable Exception ex) throws DataAccessException {
        if (!this.decrementParticipateCount(request)) {
            EntityManagerHolder emHolder = (EntityManagerHolder)((Object)TransactionSynchronizationManager.unbindResource((Object)this.obtainEntityManagerFactory()));
            this.logger.debug((Object)"Closing JPA EntityManager in OpenEntityManagerInViewInterceptor");
            EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
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
            TransactionSynchronizationManager.unbindResource((Object)this.obtainEntityManagerFactory());
        }
    }

    protected String getParticipateAttributeName() {
        return this.obtainEntityManagerFactory().toString() + PARTICIPATE_SUFFIX;
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

