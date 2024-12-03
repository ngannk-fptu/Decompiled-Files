/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.transaction.reactive;

import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.reactive.TransactionCallback;
import org.springframework.transaction.reactive.TransactionalOperatorImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionalOperator {
    default public <T> Flux<T> transactional(Flux<T> flux) {
        return this.execute(it -> flux);
    }

    public <T> Mono<T> transactional(Mono<T> var1);

    public <T> Flux<T> execute(TransactionCallback<T> var1) throws TransactionException;

    public static TransactionalOperator create(ReactiveTransactionManager transactionManager) {
        return TransactionalOperator.create(transactionManager, TransactionDefinition.withDefaults());
    }

    public static TransactionalOperator create(ReactiveTransactionManager transactionManager, TransactionDefinition transactionDefinition) {
        return new TransactionalOperatorImpl(transactionManager, transactionDefinition);
    }
}

