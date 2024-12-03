/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NamedThreadLocal
 *  org.springframework.core.OrderComparator
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.transaction.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.OrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.ResourceHolder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationUtils;
import org.springframework.util.Assert;

public abstract class TransactionSynchronizationManager {
    private static final ThreadLocal<Map<Object, Object>> resources = new NamedThreadLocal("Transactional resources");
    private static final ThreadLocal<Set<TransactionSynchronization>> synchronizations = new NamedThreadLocal("Transaction synchronizations");
    private static final ThreadLocal<String> currentTransactionName = new NamedThreadLocal("Current transaction name");
    private static final ThreadLocal<Boolean> currentTransactionReadOnly = new NamedThreadLocal("Current transaction read-only status");
    private static final ThreadLocal<Integer> currentTransactionIsolationLevel = new NamedThreadLocal("Current transaction isolation level");
    private static final ThreadLocal<Boolean> actualTransactionActive = new NamedThreadLocal("Actual transaction active");

    public static Map<Object, Object> getResourceMap() {
        Map<Object, Object> map = resources.get();
        return map != null ? Collections.unmodifiableMap(map) : Collections.emptyMap();
    }

    public static boolean hasResource(Object key) {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Object value = TransactionSynchronizationManager.doGetResource(actualKey);
        return value != null;
    }

    @Nullable
    public static Object getResource(Object key) {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        return TransactionSynchronizationManager.doGetResource(actualKey);
    }

    @Nullable
    private static Object doGetResource(Object actualKey) {
        Map<Object, Object> map = resources.get();
        if (map == null) {
            return null;
        }
        Object value = map.get(actualKey);
        if (value instanceof ResourceHolder && ((ResourceHolder)value).isVoid()) {
            map.remove(actualKey);
            if (map.isEmpty()) {
                resources.remove();
            }
            value = null;
        }
        return value;
    }

    public static void bindResource(Object key, Object value) throws IllegalStateException {
        Object oldValue;
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Assert.notNull((Object)value, (String)"Value must not be null");
        Map<Object, Object> map = resources.get();
        if (map == null) {
            map = new HashMap<Object, Object>();
            resources.set(map);
        }
        if ((oldValue = map.put(actualKey, value)) instanceof ResourceHolder && ((ResourceHolder)oldValue).isVoid()) {
            oldValue = null;
        }
        if (oldValue != null) {
            throw new IllegalStateException("Already value [" + oldValue + "] for key [" + actualKey + "] bound to thread");
        }
    }

    public static Object unbindResource(Object key) throws IllegalStateException {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        Object value = TransactionSynchronizationManager.doUnbindResource(actualKey);
        if (value == null) {
            throw new IllegalStateException("No value for key [" + actualKey + "] bound to thread");
        }
        return value;
    }

    @Nullable
    public static Object unbindResourceIfPossible(Object key) {
        Object actualKey = TransactionSynchronizationUtils.unwrapResourceIfNecessary(key);
        return TransactionSynchronizationManager.doUnbindResource(actualKey);
    }

    @Nullable
    private static Object doUnbindResource(Object actualKey) {
        Map<Object, Object> map = resources.get();
        if (map == null) {
            return null;
        }
        Object value = map.remove(actualKey);
        if (map.isEmpty()) {
            resources.remove();
        }
        if (value instanceof ResourceHolder && ((ResourceHolder)value).isVoid()) {
            value = null;
        }
        return value;
    }

    public static boolean isSynchronizationActive() {
        return synchronizations.get() != null;
    }

    public static void initSynchronization() throws IllegalStateException {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Cannot activate transaction synchronization - already active");
        }
        synchronizations.set(new LinkedHashSet());
    }

    public static void registerSynchronization(TransactionSynchronization synchronization) throws IllegalStateException {
        Assert.notNull((Object)synchronization, (String)"TransactionSynchronization must not be null");
        Set<TransactionSynchronization> synchs = synchronizations.get();
        if (synchs == null) {
            throw new IllegalStateException("Transaction synchronization is not active");
        }
        synchs.add(synchronization);
    }

    public static List<TransactionSynchronization> getSynchronizations() throws IllegalStateException {
        Set<TransactionSynchronization> synchs = synchronizations.get();
        if (synchs == null) {
            throw new IllegalStateException("Transaction synchronization is not active");
        }
        if (synchs.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<TransactionSynchronization> sortedSynchs = new ArrayList<TransactionSynchronization>(synchs);
        OrderComparator.sort(sortedSynchs);
        return Collections.unmodifiableList(sortedSynchs);
    }

    public static void clearSynchronization() throws IllegalStateException {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("Cannot deactivate transaction synchronization - not active");
        }
        synchronizations.remove();
    }

    public static void setCurrentTransactionName(@Nullable String name) {
        currentTransactionName.set(name);
    }

    @Nullable
    public static String getCurrentTransactionName() {
        return currentTransactionName.get();
    }

    public static void setCurrentTransactionReadOnly(boolean readOnly) {
        currentTransactionReadOnly.set(readOnly ? Boolean.TRUE : null);
    }

    public static boolean isCurrentTransactionReadOnly() {
        return currentTransactionReadOnly.get() != null;
    }

    public static void setCurrentTransactionIsolationLevel(@Nullable Integer isolationLevel) {
        currentTransactionIsolationLevel.set(isolationLevel);
    }

    @Nullable
    public static Integer getCurrentTransactionIsolationLevel() {
        return currentTransactionIsolationLevel.get();
    }

    public static void setActualTransactionActive(boolean active) {
        actualTransactionActive.set(active ? Boolean.TRUE : null);
    }

    public static boolean isActualTransactionActive() {
        return actualTransactionActive.get() != null;
    }

    public static void clear() {
        synchronizations.remove();
        currentTransactionName.remove();
        currentTransactionReadOnly.remove();
        currentTransactionIsolationLevel.remove();
        actualTransactionActive.remove();
    }
}

