/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.aop.scope.ScopedObject
 *  org.springframework.core.InfrastructureProxy
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.transaction.reactive;

import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.core.InfrastructureProxy;
import org.springframework.transaction.reactive.TransactionSynchronization;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

abstract class TransactionSynchronizationUtils {
    private static final Log logger = LogFactory.getLog(TransactionSynchronizationUtils.class);
    private static final boolean aopAvailable = ClassUtils.isPresent((String)"org.springframework.aop.scope.ScopedObject", (ClassLoader)TransactionSynchronizationUtils.class.getClassLoader());

    TransactionSynchronizationUtils() {
    }

    static Object unwrapResourceIfNecessary(Object resource) {
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

    public static Mono<Void> triggerBeforeCommit(Collection<TransactionSynchronization> synchronizations, boolean readOnly) {
        return Flux.fromIterable(synchronizations).concatMap(it -> it.beforeCommit(readOnly)).then();
    }

    public static Mono<Void> triggerBeforeCompletion(Collection<TransactionSynchronization> synchronizations) {
        return Flux.fromIterable(synchronizations).concatMap(TransactionSynchronization::beforeCompletion).onErrorContinue((t, o) -> logger.error((Object)"TransactionSynchronization.beforeCompletion threw exception", t)).then();
    }

    public static Mono<Void> invokeAfterCommit(Collection<TransactionSynchronization> synchronizations) {
        return Flux.fromIterable(synchronizations).concatMap(TransactionSynchronization::afterCommit).then();
    }

    public static Mono<Void> invokeAfterCompletion(Collection<TransactionSynchronization> synchronizations, int completionStatus) {
        return Flux.fromIterable(synchronizations).concatMap(it -> it.afterCompletion(completionStatus)).onErrorContinue((t, o) -> logger.error((Object)"TransactionSynchronization.afterCompletion threw exception", t)).then();
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

