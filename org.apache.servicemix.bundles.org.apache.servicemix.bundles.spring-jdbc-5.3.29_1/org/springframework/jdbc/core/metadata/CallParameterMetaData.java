/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.core.metadata;

import org.springframework.lang.Nullable;

public class CallParameterMetaData {
    private final boolean function;
    @Nullable
    private final String parameterName;
    private final int parameterType;
    private final int sqlType;
    @Nullable
    private final String typeName;
    private final boolean nullable;

    @Deprecated
    public CallParameterMetaData(@Nullable String columnName, int columnType, int sqlType, @Nullable String typeName, boolean nullable) {
        this(false, columnName, columnType, sqlType, typeName, nullable);
    }

    public CallParameterMetaData(boolean function, @Nullable String columnName, int columnType, int sqlType, @Nullable String typeName, boolean nullable) {
        this.function = function;
        this.parameterName = columnName;
        this.parameterType = columnType;
        this.sqlType = sqlType;
        this.typeName = typeName;
        this.nullable = nullable;
    }

    public boolean isFunction() {
        return this.function;
    }

    @Nullable
    public String getParameterName() {
        return this.parameterName;
    }

    public int getParameterType() {
        return this.parameterType;
    }

    public boolean isReturnParameter() {
        return this.function ? this.parameterType == 4 : this.parameterType == 5 || this.parameterType == 3;
    }

    public int getSqlType() {
        return this.sqlType;
    }

    @Nullable
    public String getTypeName() {
        return this.typeName;
    }

    public boolean isNullable() {
        return this.nullable;
    }
}

