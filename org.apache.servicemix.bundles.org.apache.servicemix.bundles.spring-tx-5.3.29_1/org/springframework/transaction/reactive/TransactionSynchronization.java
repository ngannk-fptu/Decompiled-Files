/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.transaction.reactive;

import reactor.core.publisher.Mono;

public interface TransactionSynchronization {
    public static final int STATUS_COMMITTED = 0;
    public static final int STATUS_ROLLED_BACK = 1;
    public static final int STATUS_UNKNOWN = 2;

    default public Mono<Void> suspend() {
        return Mono.empty();
    }

    default public Mono<Void> resume() {
        return Mono.empty();
    }

    default public Mono<Void> beforeCommit(boolean readOnly) {
        return Mono.empty();
    }

    default public Mono<Void> beforeCompletion() {
        return Mono.empty();
    }

    default public Mono<Void> afterCommit() {
        return Mono.empty();
    }

    default public Mono<Void> afterCompletion(int status) {
        return Mono.empty();
    }
}

