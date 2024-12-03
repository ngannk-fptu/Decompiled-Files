/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.reactivestreams.Publisher
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.transaction.reactive;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.springframework.lang.Nullable;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.reactive.TransactionCallback;
import org.springframework.transaction.reactive.TransactionContextManager;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

final class TransactionalOperatorImpl
implements TransactionalOperator {
    private static final Log logger = LogFactory.getLog(TransactionalOperatorImpl.class);
    private final ReactiveTransactionManager transactionManager;
    private final TransactionDefinition transactionDefinition;

    TransactionalOperatorImpl(ReactiveTransactionManager transactionManager, TransactionDefinition transactionDefinition) {
        Assert.notNull((Object)transactionManager, (String)"ReactiveTransactionManager must not be null");
        Assert.notNull((Object)transactionDefinition, (String)"TransactionDefinition must not be null");
        this.transactionManager = transactionManager;
        this.transactionDefinition = transactionDefinition;
    }

    public ReactiveTransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    @Override
    public <T> Mono<T> transactional(Mono<T> mono) {
        return TransactionContextManager.currentContext().flatMap(context -> {
            Mono<ReactiveTransaction> status = this.transactionManager.getReactiveTransaction(this.transactionDefinition);
            return status.flatMap(it -> Mono.usingWhen((Publisher)Mono.just((Object)it), ignore -> mono, this.transactionManager::commit, (res, err) -> Mono.empty(), this.transactionManager::rollback).onErrorResume(ex -> this.rollbackOnException((ReactiveTransaction)it, (Throwable)ex).then(Mono.error((Throwable)ex))));
        }).contextWrite(TransactionContextManager.getOrCreateContext()).contextWrite(TransactionContextManager.getOrCreateContextHolder());
    }

    @Override
    public <T> Flux<T> execute(TransactionCallback<T> action) throws TransactionException {
        return TransactionContextManager.currentContext().flatMapMany(context -> {
            Mono<ReactiveTransaction> status = this.transactionManager.getReactiveTransaction(this.transactionDefinition);
            return status.flatMapMany(it -> Flux.usingWhen((Publisher)Mono.just((Object)it), action::doInTransaction, this.transactionManager::commit, (tx, ex) -> Mono.empty(), this.transactionManager::rollback).onErrorResume(ex -> this.rollbackOnException((ReactiveTransaction)it, (Throwable)ex).then(Mono.error((Throwable)ex))));
        }).contextWrite(TransactionContextManager.getOrCreateContext()).contextWrite(TransactionContextManager.getOrCreateContextHolder());
    }

    private Mono<Void> rollbackOnException(ReactiveTransaction status, Throwable ex) throws TransactionException {
        logger.debug((Object)"Initiating transaction rollback on application exception", ex);
        return this.transactionManager.rollback(status).onErrorMap(ex2 -> {
            logger.error((Object)"Application exception overridden by rollback exception", ex);
            if (ex2 instanceof TransactionSystemException) {
                ((TransactionSystemException)((Object)((Object)ex2))).initApplicationException(ex);
            }
            return ex2;
        });
    }

    public boolean equals(@Nullable Object other) {
        return this == other || super.equals(other) && (!(other instanceof TransactionalOperatorImpl) || this.getTransactionManager() == ((TransactionalOperatorImpl)other).getTransactionManager());
    }

    public int hashCode() {
        return this.getTransactionManager().hashCode();
    }
}

