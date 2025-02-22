/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package org.postgresql.core;

import java.util.Arrays;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.postgresql.core.BaseQueryKey;

class QueryWithReturningColumnsKey
extends BaseQueryKey {
    public final String[] columnNames;
    private int size;

    QueryWithReturningColumnsKey(String sql, boolean isParameterized, boolean escapeProcessing, String @Nullable [] columnNames) {
        super(sql, isParameterized, escapeProcessing);
        if (columnNames == null) {
            columnNames = new String[]{"*"};
        }
        this.columnNames = columnNames;
    }

    @Override
    public long getSize() {
        int size = this.size;
        if (size != 0) {
            return size;
        }
        size = (int)super.getSize();
        if (this.columnNames != null) {
            size = (int)((long)size + 16L);
            for (String columnName : this.columnNames) {
                size = (int)((long)size + (long)columnName.length() * 2L);
            }
        }
        this.size = size;
        return size;
    }

    @Override
    public String toString() {
        return "QueryWithReturningColumnsKey{sql='" + this.sql + '\'' + ", isParameterized=" + this.isParameterized + ", escapeProcessing=" + this.escapeProcessing + ", columnNames=" + Arrays.toString(this.columnNames) + '}';
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        QueryWithReturningColumnsKey that = (QueryWithReturningColumnsKey)o;
        return Arrays.equals(this.columnNames, that.columnNames);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(this.columnNames);
        return result;
    }
}

