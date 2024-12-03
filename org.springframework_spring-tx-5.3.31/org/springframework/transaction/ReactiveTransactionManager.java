/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  reactor.core.publisher.Mono
 */
package org.springframework.transaction;

import org.springframework.lang.Nullable;
import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionManager;
import reactor.core.publisher.Mono;

public interface ReactiveTransactionManager
extends TransactionManager {
    public Mono<ReactiveTransaction> getReactiveTransaction(@Nullable TransactionDefinition var1);

    public Mono<Void> commit(ReactiveTransaction var1);

    public Mono<Void> rollback(ReactiveTransaction var1);
}

