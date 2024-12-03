/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql.schemarep;

public interface ColumnRep {
    public String getColumnName();

    public int getColumnType();

    public int[] getColumnSize();

    public boolean acceptsNulls();

    public Object getDefaultValue();
}

