/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.info;

import java.lang.reflect.Method;
import java.util.Set;
import net.java.ao.RawEntity;
import net.java.ao.schema.info.FieldInfo;

public interface EntityInfo<T extends RawEntity<K>, K> {
    public Class<T> getEntityType();

    public String getName();

    public FieldInfo<K> getPrimaryKey();

    public Set<FieldInfo> getFields();

    public Set<String> getFieldNames();

    public FieldInfo getField(Method var1);

    public FieldInfo getField(String var1);

    public boolean hasAccessor(Method var1);

    public boolean hasMutator(Method var1);

    public boolean hasField(String var1);
}

