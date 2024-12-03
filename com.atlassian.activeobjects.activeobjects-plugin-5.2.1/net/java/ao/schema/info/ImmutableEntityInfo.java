/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 */
package net.java.ao.schema.info;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.java.ao.RawEntity;
import net.java.ao.schema.info.EntityInfo;
import net.java.ao.schema.info.FieldInfo;

class ImmutableEntityInfo<T extends RawEntity<K>, K>
implements EntityInfo<T, K> {
    private final Class<T> entityType;
    private final String tableName;
    private final FieldInfo<K> primaryKey;
    private final Map<String, FieldInfo> fieldByName;
    private final Map<Method, FieldInfo> fieldByMethod;

    ImmutableEntityInfo(Class<T> entityType, String tableName, Set<FieldInfo> fields) {
        this.entityType = Objects.requireNonNull(entityType, "entityType");
        this.tableName = Objects.requireNonNull(tableName, "tableName");
        ImmutableMap.Builder fieldByNameBuilder = ImmutableMap.builder();
        ImmutableMap.Builder fieldByMethodBuilder = ImmutableMap.builder();
        FieldInfo primaryKey = null;
        for (FieldInfo field : fields) {
            fieldByNameBuilder.put((Object)field.getName(), (Object)field);
            if (field.getPolymorphicName() != null) {
                fieldByNameBuilder.put((Object)field.getPolymorphicName(), (Object)field);
            }
            if (field.isPrimary()) {
                primaryKey = field;
            }
            if (field.hasAccessor()) {
                fieldByMethodBuilder.put((Object)field.getAccessor(), (Object)field);
            }
            if (!field.hasMutator()) continue;
            fieldByMethodBuilder.put((Object)field.getMutator(), (Object)field);
        }
        this.fieldByName = fieldByNameBuilder.build();
        this.fieldByMethod = fieldByMethodBuilder.build();
        this.primaryKey = Objects.requireNonNull(primaryKey, "primaryKey");
    }

    @Override
    public Class<T> getEntityType() {
        return this.entityType;
    }

    @Override
    public String getName() {
        return this.tableName;
    }

    @Override
    public FieldInfo<K> getPrimaryKey() {
        return this.primaryKey;
    }

    @Override
    public Set<FieldInfo> getFields() {
        return ImmutableSet.copyOf(this.fieldByName.values());
    }

    @Override
    public Set<String> getFieldNames() {
        return ImmutableSet.copyOf((Collection)Collections2.transform(this.getFields(), FieldInfo.PLUCK_NAME));
    }

    @Override
    public FieldInfo getField(Method method) {
        return this.fieldByMethod.get(method);
    }

    @Override
    public FieldInfo getField(String fieldName) {
        return this.fieldByName.get(fieldName);
    }

    @Override
    public boolean hasAccessor(Method method) {
        FieldInfo field = this.fieldByMethod.get(method);
        return field != null && method.equals(field.getAccessor());
    }

    @Override
    public boolean hasMutator(Method method) {
        FieldInfo field = this.fieldByMethod.get(method);
        return field != null && method.equals(field.getMutator());
    }

    @Override
    public boolean hasField(String fieldName) {
        return this.fieldByName.containsKey(fieldName);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImmutableEntityInfo that = (ImmutableEntityInfo)o;
        return !(this.entityType == null ? that.entityType != null : !this.entityType.equals(that.entityType));
    }

    public int hashCode() {
        return this.entityType != null ? this.entityType.hashCode() : 0;
    }
}

