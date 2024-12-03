/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotationAwareOrderComparator
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Mono
 */
package org.springframework.transaction.reactive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.transaction.reactive.TransactionContext;
import org.springframework.transaction.reactive.TransactionContextManager;
import org.springframework.transaction.reactive.TransactionSynchronization;
import org.springframework.transaction.reactive.TransactionSynchronizationUtils;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public class TransactionSynchronizationManager {
    private final TransactionContext transactionContext;

    public TransactionSynchronizationManager(TransactionContext transactionContext) {
        Assert.notNull((Object)transactionContext, (String)"TransactionContext must not be null");
        this.transactionContext = transactionContext;
    }

    public static Mono<TransactionSynchronizationManager> forCurrentTransaction() {
        return TransactionContextManager.currentContext().map(TransactionSynchronizationManager::new);
    }

    public boolean hasResource(Object key) {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Object value = this.doGetResource(actualKey);
        return value != null;
    }

    @Nullable
    public Object getResource(Object key) {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        return this.doGetResource(actualKey);
    }

    @Nullable
    private Object doGetResource(Object actualKey) {
        return this.transactionContext.getResources().get(actualKey);
    }

    public void bindResource(Object key, Object value) throws IllegalStateException {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Assert.notNull((Object)value, (String)"Value must not be null");
        Map<Object, Object> map = this.transactionContext.getResources();
        Object oldValue = map.put(actualKey, value);
        if (oldValue != null) {
            throw new IllegalStateException("Already value [" + oldValue + "] for key [" + actualKey + "] bound to context");
        }
    }

    public Object unbindResource(Object key) throws IllegalStateException {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Object value = this.doUnbindResource(actualKey);
        if (value == null) {
            throw new IllegalStateException("No value for key [" + actualKey + "] bound to context");
        }
        return value;
    }

    @Nullable
    public Object unbindResourceIfPossible(Object key) {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        return this.doUnbindResource(actualKey);
    }

    @Nullable
    private Object doUnbindResource(Object actualKey) {
        Map<Object, Object> map = this.transactionContext.getResources();
        return map.remove(actualKey);
    }

    public boolean isSynchronizationActive() {
        return this.transactionContext.getSynchronizations() != null;
    }

    public void initSynchronization() throws IllegalStateException {
        if (this.isSynchronizationActive()) {
            throw new IllegalStateException("Cannot activate transaction synchronization - already active");
        }
        this.transactionContext.setSynchronizations(new LinkedHashSet<TransactionSynchronization>());
    }

    public void registerSynchronization(TransactionSynchronization synchronization) throws IllegalStateException {
        Assert.notNull((Object)synchronization, (String)"TransactionSynchronization must not be null");
        Set<TransactionSynchronization> synchs = this.transactionContext.getSynchronizations();
        if (synchs == null) {
            throw new IllegalStateException("Transaction synchronization is not active");
        }
        synchs.add(synchronization);
    }

    public List<TransactionSynchronization> getSynchronizations() throws IllegalStateException {
        Set<TransactionSynchronization> synchs = this.transactionContext.getSynchronizations();
        if (synchs == null) {
            throw new IllegalStateException("Transaction synchronization is not active");
        }
        if (synchs.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<TransactionSynchronization> sortedSynchs = new ArrayList<TransactionSynchronization>(synchs);
        AnnotationAwareOrderComparator.sort(sortedSynchs);
        return Collections.unmodifiableList(sortedSynchs);
    }

    public void clearSynchronization() throws IllegalStateException {
        if (!this.isSynchronizationActive()) {
            throw new IllegalStateException("Cannot deactivate transaction synchronization - not active");
        }
        this.transactionContext.setSynchronizations(null);
    }

    public void setCurrentTransactionName(@Nullable String name) {
        this.transactionContext.setCurrentTransactionName(name);
    }

    @Nullable
    public String getCurrentTransactionName() {
        return this.transactionContext.getCurrentTransactionName();
    }

    public void setCurrentTransactionReadOnly(boolean readOnly) {
        this.transactionContext.setCurrentTransactionReadOnly(readOnly);
    }

    public boolean isCurrentTransactionReadOnly() {
        return this.transactionContext.isCurrentTransactionReadOnly();
    }

    public void setCurrentTransactionIsolationLevel(@Nullable Integer isolationLevel) {
        this.transactionContext.setCurrentTransactionIsolationLevel(isolationLevel);
    }

    @Nullable
    public Integer getCurrentTransactionIsolationLevel() {
        return this.transactionContext.getCurrentTransactionIsolationLevel();
    }

    public void setActualTransactionActive(boolean active) {
        this.transactionContext.setActualTransactionActive(active);
    }

    public boolean isActualTransactionActive() {
        return this.transactionContext.isActualTransactionActive();
    }

    public void clear() {
        this.transactionContext.clear();
    }
}

