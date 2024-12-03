/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jdbc.support.rowset;

import org.springframework.jdbc.InvalidResultSetAccessException;

public interface SqlRowSetMetaData {
    public String getCatalogName(int var1) throws InvalidResultSetAccessException;

    public String getColumnClassName(int var1) throws InvalidResultSetAccessException;

    public int getColumnCount() throws InvalidResultSetAccessException;

    public String[] getColumnNames() throws InvalidResultSetAccessException;

    public int getColumnDisplaySize(int var1) throws InvalidResultSetAccessException;

    public String getColumnLabel(int var1) throws InvalidResultSetAccessException;

    public String getColumnName(int var1) throws InvalidResultSetAccessException;

    public int getColumnType(int var1) throws InvalidResultSetAccessException;

    public String getColumnTypeName(int var1) throws InvalidResultSetAccessException;

    public int getPrecision(int var1) throws InvalidResultSetAccessException;

    public int getScale(int var1) throws InvalidResultSetAccessException;

    public String getSchemaName(int var1) throws InvalidResultSetAccessException;

    public String getTableName(int var1) throws InvalidResultSetAccessException;

    public boolean isCaseSensitive(int var1) throws InvalidResultSetAccessException;

    public boolean isCurrency(int var1) throws InvalidResultSetAccessException;

    public boolean isSigned(int var1) throws InvalidResultSetAccessException;
}

