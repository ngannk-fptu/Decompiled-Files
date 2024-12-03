/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.support;

import org.springframework.transaction.support.ResourceHolder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public abstract class ResourceHolderSynchronization<H extends ResourceHolder, K>
implements TransactionSynchronization {
    private final H resourceHolder;
    private final K resourceKey;
    private volatile boolean holderActive = true;

    public ResourceHolderSynchronization(H resourceHolder, K resourceKey) {
        this.resourceHolder = resourceHolder;
        this.resourceKey = resourceKey;
    }

    @Override
    public void suspend() {
        if (this.holderActive) {
            TransactionSynchronizationManager.unbindResource(this.resourceKey);
        }
    }

    @Override
    public void resume() {
        if (this.holderActive) {
            TransactionSynchronizationManager.bindResource(this.resourceKey, this.resourceHolder);
        }
    }

    @Override
    public void flush() {
        this.flushResource(this.resourceHolder);
    }

    @Override
    public void beforeCommit(boolean readOnly) {
    }

    @Override
    public void beforeCompletion() {
        if (this.shouldUnbindAtCompletion()) {
            TransactionSynchronizationManager.unbindResource(this.resourceKey);
            this.holderActive = false;
            if (this.shouldReleaseBeforeCompletion()) {
                this.releaseResource(this.resourceHolder, this.resourceKey);
            }
        }
    }

    @Override
    public void afterCommit() {
        if (!this.shouldReleaseBeforeCompletion()) {
            this.processResourceAfterCommit(this.resourceHolder);
        }
    }

    @Override
    public void afterCompletion(int status) {
        if (this.shouldUnbindAtCompletion()) {
            boolean releaseNecessary = false;
            if (this.holderActive) {
                this.holderActive = false;
                TransactionSynchronizationManager.unbindResourceIfPossible(this.resourceKey);
                this.resourceHolder.unbound();
                releaseNecessary = true;
            } else {
                releaseNecessary = this.shouldReleaseAfterCompletion(this.resourceHolder);
            }
            if (releaseNecessary) {
                this.releaseResource(this.resourceHolder, this.resourceKey);
            }
        } else {
            this.cleanupResource(this.resourceHolder, this.resourceKey, status == 0);
        }
        this.resourceHolder.reset();
    }

    protected boolean shouldUnbindAtCompletion() {
        return true;
    }

    protected boolean shouldReleaseBeforeCompletion() {
        return true;
    }

    protected boolean shouldReleaseAfterCompletion(H resourceHolder) {
        return !this.shouldReleaseBeforeCompletion();
    }

    protected void flushResource(H resourceHolder) {
    }

    protected void processResourceAfterCommit(H resourceHolder) {
    }

    protected void releaseResource(H resourceHolder, K resourceKey) {
    }

    protected void cleanupResource(H resourceHolder, K resourceKey, boolean committed) {
    }
}

