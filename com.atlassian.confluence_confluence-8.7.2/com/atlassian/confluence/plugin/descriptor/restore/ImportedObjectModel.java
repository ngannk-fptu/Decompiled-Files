/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.restore;

import java.util.Map;
import java.util.Objects;

public class ImportedObjectModel {
    private final Map<String, Object> originalPropertyValueMap;
    private final Class<?> entityClass;
    private final Object id;

    public ImportedObjectModel(Object id, Class<?> entityClass, Map<String, Object> originalPropertyValueMap) {
        this.id = id;
        this.entityClass = entityClass;
        this.originalPropertyValueMap = originalPropertyValueMap;
    }

    public Object getId() {
        return this.id;
    }

    public Map<String, Object> getOriginalPropertyValueMap() {
        return this.originalPropertyValueMap;
    }

    public Class<?> getEntityClass() {
        return this.entityClass;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        ImportedObjectModel that = (ImportedObjectModel)obj;
        return Objects.equals(this.entityClass, that.entityClass) && Objects.equals(this.id, that.id) && Objects.equals(this.originalPropertyValueMap, that.originalPropertyValueMap);
    }

    public int hashCode() {
        return Objects.hash(this.id, this.entityClass, this.originalPropertyValueMap);
    }
}

