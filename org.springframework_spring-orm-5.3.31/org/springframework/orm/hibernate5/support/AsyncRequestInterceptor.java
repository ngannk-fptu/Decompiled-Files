/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.hibernate.SessionFactory
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.web.context.request.NativeWebRequest
 *  org.springframework.web.context.request.async.CallableProcessingInterceptor
 *  org.springframework.web.context.request.async.DeferredResult
 *  org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
 */
package org.springframework.orm.hibernate5.support;

import java.util.concurrent.Callable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;

class AsyncRequestInterceptor
implements CallableProcessingInterceptor,
DeferredResultProcessingInterceptor {
    private static final Log logger = LogFactory.getLog(AsyncRequestInterceptor.class);
    private final SessionFactory sessionFactory;
    private final SessionHolder sessionHolder;
    private volatile boolean timeoutInProgress;
    private volatile boolean errorInProgress;

    public AsyncRequestInterceptor(SessionFactory sessionFactory, SessionHolder sessionHolder) {
        this.sessionFactory = sessionFactory;
        this.sessionHolder = sessionHolder;
    }

    public <T> void preProcess(NativeWebRequest request, Callable<T> task) {
        this.bindSession();
    }

    public void bindSession() {
        this.timeoutInProgress = false;
        this.errorInProgress = false;
        TransactionSynchronizationManager.bindResource((Object)this.sessionFactory, (Object)((Object)this.sessionHolder));
    }

    public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) {
        TransactionSynchronizationManager.unbindResource((Object)this.sessionFactory);
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
        this.closeSession();
    }

    private void closeSession() {
        if (this.timeoutInProgress || this.errorInProgress) {
            logger.debug((Object)"Closing Hibernate Session after async request timeout/error");
            SessionFactoryUtils.closeSession(this.sessionHolder.getSession());
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
        this.closeSession();
    }
}

