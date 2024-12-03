/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.SavepointManager
 *  org.springframework.transaction.support.ResourceHolderSupport
 *  org.springframework.util.Assert
 */
package org.springframework.orm.jpa;

import javax.persistence.EntityManager;
import org.springframework.lang.Nullable;
import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.support.ResourceHolderSupport;
import org.springframework.util.Assert;

public class EntityManagerHolder
extends ResourceHolderSupport {
    @Nullable
    private final EntityManager entityManager;
    private boolean transactionActive;
    @Nullable
    private SavepointManager savepointManager;

    public EntityManagerHolder(@Nullable EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public EntityManager getEntityManager() {
        Assert.state((this.entityManager != null ? 1 : 0) != 0, (String)"No EntityManager available");
        return this.entityManager;
    }

    protected void setTransactionActive(boolean transactionActive) {
        this.transactionActive = transactionActive;
    }

    protected boolean isTransactionActive() {
        return this.transactionActive;
    }

    protected void setSavepointManager(@Nullable SavepointManager savepointManager) {
        this.savepointManager = savepointManager;
    }

    @Nullable
    protected SavepointManager getSavepointManager() {
        return this.savepointManager;
    }

    public void clear() {
        super.clear();
        this.transactionActive = false;
        this.savepointManager = null;
    }
}

