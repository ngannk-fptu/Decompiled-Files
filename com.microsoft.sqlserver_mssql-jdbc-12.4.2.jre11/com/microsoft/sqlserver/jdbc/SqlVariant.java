/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLCollation;

class SqlVariant {
    private int baseType;
    private int precision;
    private int scale;
    private int maxLength;
    private SQLCollation collation;
    private boolean isBaseTypeTime = false;
    private JDBCType baseJDBCType;

    SqlVariant(int baseType) {
        this.baseType = baseType;
    }

    boolean isBaseTypeTimeValue() {
        return this.isBaseTypeTime;
    }

    void setIsBaseTypeTimeValue(boolean isBaseTypeTime) {
        this.isBaseTypeTime = isBaseTypeTime;
    }

    void setBaseType(int baseType) {
        this.baseType = baseType;
    }

    int getBaseType() {
        return this.baseType;
    }

    void setBaseJDBCType(JDBCType baseJDBCType) {
        this.baseJDBCType = baseJDBCType;
    }

    JDBCType getBaseJDBCType() {
        return this.baseJDBCType;
    }

    void setScale(int scale) {
        this.scale = scale;
    }

    int getScale() {
        return this.scale;
    }

    void setPrecision(int precision) {
        this.precision = precision;
    }

    int getPrecision() {
        return this.precision;
    }

    void setCollation(SQLCollation collation) {
        this.collation = collation;
    }

    SQLCollation getCollation() {
        return this.collation;
    }

    void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    int getMaxLength() {
        return this.maxLength;
    }
}

