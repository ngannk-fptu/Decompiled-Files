/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

public final class NonNullableTransientDependencies {
    private Map<Object, Set<String>> propertyPathsByTransientEntity;

    public void add(String propertyName, Object transientEntity) {
        Set<String> propertyPaths;
        if (this.propertyPathsByTransientEntity == null) {
            this.propertyPathsByTransientEntity = new IdentityHashMap<Object, Set<String>>();
        }
        if ((propertyPaths = this.propertyPathsByTransientEntity.get(transientEntity)) == null) {
            propertyPaths = new HashSet<String>();
            this.propertyPathsByTransientEntity.put(transientEntity, propertyPaths);
        }
        propertyPaths.add(propertyName);
    }

    public Iterable<Object> getNonNullableTransientEntities() {
        if (this.propertyPathsByTransientEntity == null) {
            return Collections.emptyList();
        }
        return this.propertyPathsByTransientEntity.keySet();
    }

    public Iterable<String> getNonNullableTransientPropertyPaths(Object entity) {
        if (this.propertyPathsByTransientEntity == null) {
            return Collections.emptyList();
        }
        return this.propertyPathsByTransientEntity.get(entity);
    }

    public boolean isEmpty() {
        return this.propertyPathsByTransientEntity == null || this.propertyPathsByTransientEntity.isEmpty();
    }

    public void resolveNonNullableTransientEntity(Object entity) {
        if (this.propertyPathsByTransientEntity != null && this.propertyPathsByTransientEntity.remove(entity) == null) {
            throw new IllegalStateException("Attempt to resolve a non-nullable, transient entity that is not a dependency.");
        }
    }

    public String toLoggableString(SharedSessionContractImplementor session) {
        StringBuilder sb = new StringBuilder(this.getClass().getSimpleName()).append('[');
        if (this.propertyPathsByTransientEntity != null) {
            for (Map.Entry<Object, Set<String>> entry : this.propertyPathsByTransientEntity.entrySet()) {
                sb.append("transientEntityName=").append(session.bestGuessEntityName(entry.getKey()));
                sb.append(" requiredBy=").append(entry.getValue());
            }
        }
        sb.append(']');
        return sb.toString();
    }
}

