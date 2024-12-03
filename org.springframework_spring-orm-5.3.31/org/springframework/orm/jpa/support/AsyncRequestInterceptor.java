/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManagerFactory
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.context.request.async.CallableProcessingInterceptor
 *  org.springframework.web.context.request.async.DeferredResult
 *  org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
 */
package org.springframework.orm.jpa.support;

import java.util.concurrent.Callable;
import javax.persistence.EntityManagerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;

class AsyncRequestInterceptor
implements CallableProcessingInterceptor,
DeferredResultProcessingInterceptor {
    private static final Log logger = LogFactory.getLog(AsyncRequestInterceptor.class);
    private final EntityManagerFactory emFactory;
    private final EntityManagerHolder emHolder;
    private volatile boolean timeoutInProgress;
    private volatile boolean errorInProgress;

    public AsyncRequestInterceptor(EntityManagerFactory emFactory, EntityManagerHolder emHolder) {
        this.emFactory = emFactory;
        this.emHolder = emHolder;
    }

    public <T> void preProcess(NativeWebRequest request, Callable<T> task) {
        this.bindEntityManager();
    }

    public void bindEntityManager() {
        this.timeoutInProgress = false;
        this.errorInProgress = false;
        TransactionSynchronizationManager.bindResource((Object)this.emFactory, (Object)((Object)this.emHolder));
    }

    public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) {
        TransactionSynchronizationManager.unbindResource((Object)this.emFactory);
    }

    public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) {
        this.timeoutInProgress = true;
        return RESULT_NONE;
    }

    public <T> Object handleError(NativeWebRequest request, Callable<T> task, Throwable t) {
        this.errorInProgress = true;
        return RESULT_NONE;
    }

    public <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {
        this.closeEntityManager();
    }

    private void closeEntityManager() {
        if (this.timeoutInProgress || this.errorInProgress) {
            logger.debug((Object)"Closing JPA EntityManager after async request timeout/error");
            EntityManagerFactoryUtils.closeEntityManager(this.emHolder.getEntityManager());
        }
    }

    public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> deferredResult) {
        this.timeoutInProgress = true;
        return true;
    }

    public <T> boolean handleError(NativeWebRequest request, DeferredResult<T> deferredResult, Throwable t) {
        this.errorInProgress = true;
        return true;
    }

    public <T> void afterCompletion(NativeWebRequest request, DeferredResult<T> deferredResult) {
        this.closeEntityManager();
    }
}

