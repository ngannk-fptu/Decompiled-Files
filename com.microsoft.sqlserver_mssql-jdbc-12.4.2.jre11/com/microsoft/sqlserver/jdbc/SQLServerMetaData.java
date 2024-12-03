/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLCollation;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.SQLServerSortOrder;
import java.text.MessageFormat;

public class SQLServerMetaData {
    String columnName = null;
    int javaSqlType;
    int precision = 0;
    int scale = 0;
    boolean useServerDefault = false;
    boolean isUniqueKey = false;
    SQLServerSortOrder sortOrder = SQLServerSortOrder.UNSPECIFIED;
    int sortOrdinal;
    private SQLCollation collation;
    static final int DEFAULT_SORT_ORDINAL = -1;

    public SQLServerMetaData(String columnName, int sqlType) {
        this.columnName = columnName;
        this.javaSqlType = sqlType;
    }

    public SQLServerMetaData(String columnName, int sqlType, int precision, int scale) {
        this.columnName = columnName;
        this.javaSqlType = sqlType;
        this.precision = precision;
        this.scale = scale;
    }

    public SQLServerMetaData(String columnName, int sqlType, int length) {
        this.columnName = columnName;
        this.javaSqlType = sqlType;
        this.precision = length;
    }

    public SQLServerMetaData(String columnName, int sqlType, int precision, int scale, boolean useServerDefault, boolean isUniqueKey, SQLServerSortOrder sortOrder, int sortOrdinal) throws SQLServerException {
        this.columnName = columnName;
        this.javaSqlType = sqlType;
        this.precision = precision;
        this.scale = scale;
        this.useServerDefault = useServerDefault;
        this.isUniqueKey = isUniqueKey;
        this.sortOrder = sortOrder;
        this.sortOrdinal = sortOrdinal;
        this.validateSortOrder();
    }

    public SQLServerMetaData(SQLServerMetaData sqlServerMetaData) {
        this.columnName = sqlServerMetaData.columnName;
        this.javaSqlType = sqlServerMetaData.javaSqlType;
        this.precision = sqlServerMetaData.precision;
        this.scale = sqlServerMetaData.scale;
        this.useServerDefault = sqlServerMetaData.useServerDefault;
        this.isUniqueKey = sqlServerMetaData.isUniqueKey;
        this.sortOrder = sqlServerMetaData.sortOrder;
        this.sortOrdinal = sqlServerMetaData.sortOrdinal;
    }

    public String getColumName() {
        return this.columnName;
    }

    public int getSqlType() {
        return this.javaSqlType;
    }

    public int getPrecision() {
        return this.precision;
    }

    public int getScale() {
        return this.scale;
    }

    public boolean useServerDefault() {
        return this.useServerDefault;
    }

    public boolean isUniqueKey() {
        return this.isUniqueKey;
    }

    public SQLServerSortOrder getSortOrder() {
        return this.sortOrder;
    }

    public int getSortOrdinal() {
        return this.sortOrdinal;
    }

    SQLCollation getCollation() {
        return this.collation;
    }

    void validateSortOrder() throws SQLServerException {
        if (SQLServerSortOrder.UNSPECIFIED == this.sortOrder != (-1 == this.sortOrdinal)) {
            MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_TVPMissingSortOrderOrOrdinal"));
            throw new SQLServerException(form.format(new Object[]{this.sortOrder, this.sortOrdinal}), null, 0, null);
        }
    }
}

