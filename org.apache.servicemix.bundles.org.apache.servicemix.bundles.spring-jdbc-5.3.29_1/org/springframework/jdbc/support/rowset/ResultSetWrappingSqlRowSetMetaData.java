/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support.rowset;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.lang.Nullable;

public class ResultSetWrappingSqlRowSetMetaData
implements SqlRowSetMetaData {
    private final ResultSetMetaData resultSetMetaData;
    @Nullable
    private String[] columnNames;

    public ResultSetWrappingSqlRowSetMetaData(ResultSetMetaData resultSetMetaData) {
        this.resultSetMetaData = resultSetMetaData;
    }

    @Override
    public String getCatalogName(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.getCatalogName(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public String getColumnClassName(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.getColumnClassName(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public int getColumnCount() throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.getColumnCount();
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public String[] getColumnNames() throws InvalidResultSetAccessException {
        if (this.columnNames == null) {
            this.columnNames = new String[this.getColumnCount()];
            for (int i = 0; i < this.getColumnCount(); ++i) {
                this.columnNames[i] = this.getColumnName(i + 1);
            }
        }
        return this.columnNames;
    }

    @Override
    public int getColumnDisplaySize(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.getColumnDisplaySize(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public String getColumnLabel(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.getColumnLabel(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public String getColumnName(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.getColumnName(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public int getColumnType(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.getColumnType(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public String getColumnTypeName(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.getColumnTypeName(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public int getPrecision(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.getPrecision(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public int getScale(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.getScale(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public String getSchemaName(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.getSchemaName(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public String getTableName(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.getTableName(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean isCaseSensitive(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.isCaseSensitive(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean isCurrency(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.isCurrency(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }

    @Override
    public boolean isSigned(int column) throws InvalidResultSetAccessException {
        try {
            return this.resultSetMetaData.isSigned(column);
        }
        catch (SQLException se) {
            throw new InvalidResultSetAccessException(se);
        }
    }
}

