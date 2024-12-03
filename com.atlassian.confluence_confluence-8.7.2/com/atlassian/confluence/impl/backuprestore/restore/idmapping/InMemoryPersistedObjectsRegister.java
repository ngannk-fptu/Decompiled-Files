/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.idmapping;

import com.atlassian.confluence.impl.backuprestore.restore.idmapping.PersistedObjectsRegister;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InMemoryPersistedObjectsRegister
implements PersistedObjectsRegister {
    private static final Object DUMMY = new Object();
    private final Map<Class<?>, EntityClassStorage> storageMap = new ConcurrentHashMap();

    @Override
    public boolean isPersistedDatabaseId(Class<?> entityClass, Object databaseId) {
        return this.storageMap.computeIfAbsent(entityClass, eClass -> new EntityClassStorage()).isPersistedDatabaseId(databaseId);
    }

    @Override
    public void markIdsAsPersisted(Class<?> entityClass, List<Object> ids) {
        this.storageMap.computeIfAbsent(entityClass, eClass -> new EntityClassStorage()).markDatabaseIdsAsPersisted(ids);
    }

    private static class EntityClassStorage {
        final Map<Object, Object> persistedDatabaseIds = new ConcurrentHashMap<Object, Object>();

        private EntityClassStorage() {
        }

        void markDatabaseIdsAsPersisted(List<Object> databaseIds) {
            this.persistedDatabaseIds.putAll(databaseIds.stream().collect(Collectors.toMap(Function.identity(), value -> DUMMY)));
        }

        boolean isPersistedDatabaseId(Object databaseId) {
            return this.persistedDatabaseIds.containsKey(databaseId);
        }
    }
}

