/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.core.metadata;

public class TableParameterMetaData {
    private final String parameterName;
    private final int sqlType;
    private final boolean nullable;

    public TableParameterMetaData(String columnName, int sqlType, boolean nullable) {
        this.parameterName = columnName;
        this.sqlType = sqlType;
        this.nullable = nullable;
    }

    public String getParameterName() {
        return this.parameterName;
    }

    public int getSqlType() {
        return this.sqlType;
    }

    public boolean isNullable() {
        return this.nullable;
    }
}

