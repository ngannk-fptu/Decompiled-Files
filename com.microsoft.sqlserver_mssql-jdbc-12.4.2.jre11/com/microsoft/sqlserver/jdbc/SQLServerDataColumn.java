/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

public final class SQLServerDataColumn {
    String columnName;
    int javaSqlType;
    int precision = 0;
    int scale = 0;
    int numberOfDigitsIntegerPart = 0;

    public SQLServerDataColumn(String columnName, int sqlType) {
        this.columnName = columnName;
        this.javaSqlType = sqlType;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public int getColumnType() {
        return this.javaSqlType;
    }

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.javaSqlType;
        hash = 31 * hash + this.precision;
        hash = 31 * hash + this.scale;
        hash = 31 * hash + this.numberOfDigitsIntegerPart;
        hash = 31 * hash + (null != this.columnName ? this.columnName.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (null != object && object.getClass() == SQLServerDataColumn.class) {
            SQLServerDataColumn aSQLServerDataColumn = (SQLServerDataColumn)object;
            if (this.hashCode() == aSQLServerDataColumn.hashCode()) {
                return (null == this.columnName ? null == aSQLServerDataColumn.columnName : this.columnName.equals(aSQLServerDataColumn.columnName)) && this.javaSqlType == aSQLServerDataColumn.javaSqlType && this.numberOfDigitsIntegerPart == aSQLServerDataColumn.numberOfDigitsIntegerPart && this.precision == aSQLServerDataColumn.precision && this.scale == aSQLServerDataColumn.scale;
            }
        }
        return false;
    }
}

