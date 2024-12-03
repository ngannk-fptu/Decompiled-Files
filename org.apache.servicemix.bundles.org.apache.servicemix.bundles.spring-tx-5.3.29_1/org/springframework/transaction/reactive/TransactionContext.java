/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 *  org.springframework.util.function.SingletonSupplier
 */
package org.springframework.transaction.reactive;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.lang.Nullable;
import org.springframework.transaction.reactive.TransactionSynchronization;
import org.springframework.util.StringUtils;
import org.springframework.util.function.SingletonSupplier;

public class TransactionContext {
    @Nullable
    private final TransactionContext parent;
    private final SingletonSupplier<UUID> contextId = SingletonSupplier.of(UUID::randomUUID);
    private final Map<Object, Object> resources = new LinkedHashMap<Object, Object>();
    @Nullable
    private Set<TransactionSynchronization> synchronizations;
    @Nullable
    private volatile String currentTransactionName;
    private volatile boolean currentTransactionReadOnly;
    @Nullable
    private volatile Integer currentTransactionIsolationLevel;
    private volatile boolean actualTransactionActive;

    TransactionContext() {
        this(null);
    }

    TransactionContext(@Nullable TransactionContext parent) {
        this.parent = parent;
    }

    @Nullable
    public TransactionContext getParent() {
        return this.parent;
    }

    @Deprecated
    public String getName() {
        String name = this.getCurrentTransactionName();
        if (StringUtils.hasText((String)name)) {
            return this.getContextId() + ": " + name;
        }
        return this.getContextId().toString();
    }

    @Deprecated
    public UUID getContextId() {
        return (UUID)this.contextId.obtain();
    }

    public Map<Object, Object> getResources() {
        return this.resources;
    }

    public void setSynchronizations(@Nullable Set<TransactionSynchronization> synchronizations) {
        this.synchronizations = synchronizations;
    }

    @Nullable
    public Set<TransactionSynchronization> getSynchronizations() {
        return this.synchronizations;
    }

    public void setCurrentTransactionName(@Nullable String currentTransactionName) {
        this.currentTransactionName = currentTransactionName;
    }

    @Nullable
    public String getCurrentTransactionName() {
        return this.currentTransactionName;
    }

    public void setCurrentTransactionReadOnly(boolean currentTransactionReadOnly) {
        this.currentTransactionReadOnly = currentTransactionReadOnly;
    }

    public boolean isCurrentTransactionReadOnly() {
        return this.currentTransactionReadOnly;
    }

    public void setCurrentTransactionIsolationLevel(@Nullable Integer currentTransactionIsolationLevel) {
        this.currentTransactionIsolationLevel = currentTransactionIsolationLevel;
    }

    @Nullable
    public Integer getCurrentTransactionIsolationLevel() {
        return this.currentTransactionIsolationLevel;
    }

    public void setActualTransactionActive(boolean actualTransactionActive) {
        this.actualTransactionActive = actualTransactionActive;
    }

    public boolean isActualTransactionActive() {
        return this.actualTransactionActive;
    }

    public void clear() {
        this.synchronizations = null;
        this.currentTransactionName = null;
        this.currentTransactionReadOnly = false;
        this.currentTransactionIsolationLevel = null;
        this.actualTransactionActive = false;
    }
}

