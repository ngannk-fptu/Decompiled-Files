/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.db.sql.schemarep;

import com.mchange.lang.ArrayUtils;
import com.mchange.v1.db.sql.schemarep.ColumnRep;
import java.util.Arrays;

public class ColumnRepImpl
implements ColumnRep {
    String colName;
    int col_type;
    int[] colSize;
    boolean accepts_nulls;
    Object defaultValue;

    public ColumnRepImpl(String string, int n) {
        this(string, n, null);
    }

    public ColumnRepImpl(String string, int n, int[] nArray) {
        this(string, n, nArray, false, null);
    }

    public ColumnRepImpl(String string, int n, int[] nArray, boolean bl, Object object) {
        this.colName = string;
        this.col_type = n;
        this.colSize = nArray;
        this.accepts_nulls = bl;
        this.defaultValue = object;
    }

    @Override
    public String getColumnName() {
        return this.colName;
    }

    @Override
    public int getColumnType() {
        return this.col_type;
    }

    @Override
    public int[] getColumnSize() {
        return this.colSize;
    }

    @Override
    public boolean acceptsNulls() {
        return this.accepts_nulls;
    }

    @Override
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public boolean equals(Object object) {
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ColumnRepImpl columnRepImpl = (ColumnRepImpl)object;
        if (!this.colName.equals(columnRepImpl.colName) || this.col_type != columnRepImpl.col_type || this.accepts_nulls != columnRepImpl.accepts_nulls) {
            return false;
        }
        if (this.colSize != columnRepImpl.colSize && !Arrays.equals(this.colSize, columnRepImpl.colSize)) {
            return false;
        }
        return this.defaultValue == columnRepImpl.defaultValue || this.defaultValue == null || this.defaultValue.equals(columnRepImpl.defaultValue);
    }

    public int hashCode() {
        int n = this.colName.hashCode() ^ this.col_type;
        if (!this.accepts_nulls) {
            n ^= 0xFFFFFFFF;
        }
        if (this.colSize != null) {
            n ^= ArrayUtils.hashAll(this.colSize);
        }
        if (this.defaultValue != null) {
            n ^= this.defaultValue.hashCode();
        }
        return n;
    }
}

