/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.helper;

import net.java.ao.types.TypeInfo;

public interface Field {
    public String getName();

    public TypeInfo<?> getDatabaseType();

    public int getJdbcType();

    public boolean isAutoIncrement();

    public boolean isNotNull();

    public Object getDefaultValue();

    public boolean isPrimaryKey();

    public boolean isUnique();
}

