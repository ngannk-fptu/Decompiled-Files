/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.aop.scope.ScopedObject
 *  org.springframework.core.InfrastructureProxy
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.transaction.support;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.core.InfrastructureProxy;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public abstract class TransactionSynchronizationUtils {
    private static final Log logger = LogFactory.getLog(TransactionSynchronizationUtils.class);
    private static final boolean aopAvailable = ClassUtils.isPresent((String)"org.springframework.aop.scope.ScopedObject", (ClassLoader)TransactionSynchronizationUtils.class.getClassLoader());

    public static boolean sameResourceFactory(ResourceTransactionManager tm, Object resourceFactory) {
        return TransactionSynchronizationUtils.unwrapResourceIfNecessary(tm.getResourceFactory()).equals(TransactionSynchronizationUtils.unwrapResourceIfNecessary(resourceFactory));
    }

    public static Object unwrapResourceIfNecessary(Object resource) {
        Assert.notNull((Object)resource, (String)"Resource must not be null");
        Object resourceRef = resource;
        if (resourceRef instanceof InfrastructureProxy) {
            resourceRef = ((InfrastructureProxy)resourceRef).getWrappedObject();
        }
        if (aopAvailable) {
            resourceRef = ScopedProxyUnwrapper.unwrapIfNecessary(resourceRef);
        }
        return resourceRef;
    }

    public static void triggerFlush() {
        for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.flush();
        }
    }

    public static void triggerBeforeCommit(boolean readOnly) {
        for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.beforeCommit(readOnly);
        }
    }

    public static void triggerBeforeCompletion() {
        for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            try {
                synchronization.beforeCompletion();
            }
            catch (Throwable ex) {
                logger.error((Object)"TransactionSynchronization.beforeCompletion threw exception", ex);
            }
        }
    }

    public static void triggerAfterCommit() {
        TransactionSynchronizationUtils.invokeAfterCommit(TransactionSynchronizationManager.getSynchronizations());
    }

    public static void invokeAfterCommit(@Nullable List<TransactionSynchronization> synchronizations) {
        if (synchronizations != null) {
            for (TransactionSynchronization synchronization : synchronizations) {
                synchronization.afterCommit();
            }
        }
    }

    public static void triggerAfterCompletion(int completionStatus) {
        List<TransactionSynchronization> synchronizations = TransactionSynchronizationManager.getSynchronizations();
        TransactionSynchronizationUtils.invokeAfterCompletion(synchronizations, completionStatus);
    }

    public static void invokeAfterCompletion(@Nullable List<TransactionSynchronization> synchronizations, int completionStatus) {
        if (synchronizations != null) {
            for (TransactionSynchronization synchronization : synchronizations) {
                try {
                    synchronization.afterCompletion(completionStatus);
                }
                catch (Throwable ex) {
                    logger.error((Object)"TransactionSynchronization.afterCompletion threw exception", ex);
                }
            }
        }
    }

    private static class ScopedProxyUnwrapper {
        private ScopedProxyUnwrapper() {
        }

        public static Object unwrapIfNecessary(Object resource) {
            if (resource instanceof ScopedObject) {
                return ((ScopedObject)resource).getTargetObject();
            }
            return resource;
        }
    }
}

