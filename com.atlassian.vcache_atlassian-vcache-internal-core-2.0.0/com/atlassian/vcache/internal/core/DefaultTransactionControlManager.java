/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.BegunTransactionalActivityHandler
 *  com.atlassian.vcache.internal.RequestContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.vcache.internal.BegunTransactionalActivityHandler;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.core.Instrumentor;
import com.atlassian.vcache.internal.core.TransactionControl;
import com.atlassian.vcache.internal.core.TransactionControlManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTransactionControlManager
implements TransactionControlManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultTransactionControlManager.class);
    private final Object transactionControllersKey = new Object();
    private final Object callbackKey = new Object();
    private final Instrumentor instrumentor;
    private final BegunTransactionalActivityHandler begunTransactionalActivityHandler;

    public DefaultTransactionControlManager(Instrumentor instrumentor, BegunTransactionalActivityHandler begunTransactionalActivityHandler) {
        this.instrumentor = Objects.requireNonNull(instrumentor);
        this.begunTransactionalActivityHandler = Objects.requireNonNull(begunTransactionalActivityHandler);
    }

    @Override
    public void registerTransactionalExternalCache(RequestContext requestContext, String cacheName, TransactionControl control) {
        ((HashMap)requestContext.computeIfAbsent(this.transactionControllersKey, HashMap::new)).computeIfAbsent(cacheName, x -> {
            log.trace("Registering {}", (Object)cacheName);
            return this.instrumentor.wrap(control, cacheName);
        });
        this.invokeCallbackIfNecessary(requestContext);
    }

    @Override
    public void syncAll(RequestContext requestContext) {
        log.trace("Synchronising all caches");
        requestContext.get(this.transactionControllersKey).ifPresent(txControls -> txControls.forEach((cacheName, transactionControl) -> {
            log.trace("Syncing {}", cacheName);
            transactionControl.transactionSync();
        }));
        this.resetShouldInvokeCallback(requestContext);
    }

    @Override
    public Set<String> discardAll(RequestContext requestContext) {
        log.trace("Discarding all caches");
        HashSet<String> discardedCacheNames = new HashSet<String>();
        requestContext.get(this.transactionControllersKey).ifPresent(txControls -> txControls.forEach((cacheName, transactionControl) -> {
            log.trace("Discarding {}", cacheName);
            if (transactionControl.transactionDiscard()) {
                discardedCacheNames.add((String)cacheName);
            }
        }));
        this.resetShouldInvokeCallback(requestContext);
        return discardedCacheNames;
    }

    private void invokeCallbackIfNecessary(RequestContext requestContext) {
        if (this.getCallbackInvokedFlag(requestContext).compareAndSet(false, true)) {
            this.begunTransactionalActivityHandler.onRequest(requestContext);
        }
    }

    private void resetShouldInvokeCallback(RequestContext requestContext) {
        this.getCallbackInvokedFlag(requestContext).set(false);
    }

    private AtomicBoolean getCallbackInvokedFlag(RequestContext requestContext) {
        return (AtomicBoolean)requestContext.computeIfAbsent(this.callbackKey, () -> new AtomicBoolean(false));
    }
}

