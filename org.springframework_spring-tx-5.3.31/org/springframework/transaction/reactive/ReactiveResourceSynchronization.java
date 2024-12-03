/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Mono
 */
package org.springframework.transaction.reactive;

import org.springframework.transaction.reactive.TransactionSynchronization;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import reactor.core.publisher.Mono;

public abstract class ReactiveResourceSynchronization<O, K>
implements TransactionSynchronization {
    private final O resourceObject;
    private final K resourceKey;
    private final TransactionSynchronizationManager synchronizationManager;
    private volatile boolean holderActive = true;

    public ReactiveResourceSynchronization(O resourceObject, K resourceKey, TransactionSynchronizationManager synchronizationManager) {
        this.resourceObject = resourceObject;
        this.resourceKey = resourceKey;
        this.synchronizationManager = synchronizationManager;
    }

    @Override
    public Mono<Void> suspend() {
        if (this.holderActive) {
            this.synchronizationManager.unbindResource(this.resourceKey);
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> resume() {
        if (this.holderActive) {
            this.synchronizationManager.bindResource(this.resourceKey, this.resourceObject);
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> beforeCommit(boolean readOnly) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> beforeCompletion() {
        if (this.shouldUnbindAtCompletion()) {
            this.synchronizationManager.unbindResource(this.resourceKey);
            this.holderActive = false;
            if (this.shouldReleaseBeforeCompletion()) {
                return this.releaseResource(this.resourceObject, this.resourceKey);
            }
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> afterCommit() {
        if (!this.shouldReleaseBeforeCompletion()) {
            return this.processResourceAfterCommit(this.resourceObject);
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> afterCompletion(int status) {
        return Mono.defer(() -> {
            Mono<Void> sync = Mono.empty();
            if (this.shouldUnbindAtCompletion()) {
                boolean releaseNecessary = false;
                if (this.holderActive) {
                    this.holderActive = false;
                    this.synchronizationManager.unbindResourceIfPossible(this.resourceKey);
                    releaseNecessary = true;
                } else {
                    releaseNecessary = this.shouldReleaseAfterCompletion(this.resourceObject);
                }
                if (releaseNecessary) {
                    sync = this.releaseResource(this.resourceObject, this.resourceKey);
                }
            } else {
                sync = this.cleanupResource(this.resourceObject, this.resourceKey, status == 0);
            }
            return sync;
        });
    }

    protected boolean shouldUnbindAtCompletion() {
        return true;
    }

    protected boolean shouldReleaseBeforeCompletion() {
        return true;
    }

    protected boolean shouldReleaseAfterCompletion(O resourceHolder) {
        return !this.shouldReleaseBeforeCompletion();
    }

    protected Mono<Void> processResourceAfterCommit(O resourceHolder) {
        return Mono.empty();
    }

    protected Mono<Void> releaseResource(O resourceHolder, K resourceKey) {
        return Mono.empty();
    }

    protected Mono<Void> cleanupResource(O resourceHolder, K resourceKey, boolean committed) {
        return Mono.empty();
    }
}

