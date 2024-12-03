/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.internal.BegunTransactionalActivityHandler
 *  com.atlassian.vcache.internal.RequestContext
 *  com.atlassian.vcache.internal.VCacheLifecycleManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationAdapter
 */
package com.atlassian.confluence.impl.vcache;

import com.atlassian.confluence.core.SynchronizationManager;
import com.atlassian.vcache.internal.BegunTransactionalActivityHandler;
import com.atlassian.vcache.internal.RequestContext;
import com.atlassian.vcache.internal.VCacheLifecycleManager;
import java.util.Set;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

class VCacheTransactionSyncHandler
implements BegunTransactionalActivityHandler {
    private static final Logger log = LoggerFactory.getLogger(VCacheTransactionSyncHandler.class);
    private final Supplier<VCacheLifecycleManager> lifecycleManagerRef;
    private final SynchronizationManager synchronizationManager;

    public VCacheTransactionSyncHandler(SynchronizationManager synchronizationManager, Supplier<VCacheLifecycleManager> lifecycleManagerRef) {
        this.synchronizationManager = synchronizationManager;
        this.lifecycleManagerRef = lifecycleManagerRef;
    }

    public void onRequest(final RequestContext context) {
        final VCacheLifecycleManager lifecycleManager = this.lifecycleManagerRef.get();
        if (lifecycleManager != null) {
            log.debug("Registering sync for new transaction on {}", (Object)context);
            this.synchronizationManager.registerSynchronization((TransactionSynchronization)new TransactionSynchronizationAdapter(){

                public void afterCompletion(int status) {
                    if (status == 0) {
                        log.debug("Syncing tx on commit on {}", (Object)context);
                        lifecycleManager.transactionSync(context);
                    } else {
                        Set discardedCaches = lifecycleManager.transactionDiscard(context);
                        log.debug("Transaction completed with non-commit status {}, discarded pending operations on caches {}", (Object)status, (Object)discardedCaches);
                    }
                }
            });
        } else {
            log.error("No VCacheLifecycleManager is available, cannot initialise transaction sync.");
        }
    }

    public void onCleanUp(RequestContext requestContext) {
        VCacheLifecycleManager lifecycleManager = this.lifecycleManagerRef.get();
        if (lifecycleManager != null) {
            Set unsynchronizedCacheNames = lifecycleManager.transactionDiscard(requestContext);
            if (!unsynchronizedCacheNames.isEmpty()) {
                log.error("Unsynchronized transactional caches found whilst cleaning up VCache request context: {}", (Object)unsynchronizedCacheNames);
            }
        } else {
            log.warn("No VCache LifecycleManager available, cannot verify unsynchronized caches");
        }
    }
}

