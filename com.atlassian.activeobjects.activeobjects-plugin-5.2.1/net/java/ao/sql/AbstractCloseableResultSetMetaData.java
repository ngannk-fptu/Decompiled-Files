/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.sql;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import net.java.ao.sql.CloseableResultSetMetaData;

public abstract class AbstractCloseableResultSetMetaData
implements CloseableResultSetMetaData {
    private final ResultSetMetaData delegate;

    public AbstractCloseableResultSetMetaData(ResultSetMetaData delegate) {
        this.delegate = delegate;
    }

    @Override
    public int getColumnCount() throws SQLException {
        return this.delegate.getColumnCount();
    }

    @Override
    public boolean isAutoIncrement(int column) throws SQLException {
        return this.delegate.isAutoIncrement(column);
    }

    @Override
    public boolean isCaseSensitive(int column) throws SQLException {
        return this.delegate.isCaseSensitive(column);
    }

    @Override
    public boolean isSearchable(int column) throws SQLException {
        return this.delegate.isSearchable(column);
    }

    @Override
    public boolean isCurrency(int column) throws SQLException {
        return this.delegate.isCurrency(column);
    }

    @Override
    public int isNullable(int column) throws SQLException {
        return this.delegate.isNullable(column);
    }

    @Override
    public boolean isSigned(int column) throws SQLException {
        return this.delegate.isSigned(column);
    }

    @Override
    public int getColumnDisplaySize(int column) throws SQLException {
        return this.delegate.getColumnDisplaySize(column);
    }

    @Override
    public String getColumnLabel(int column) throws SQLException {
        return this.delegate.getColumnLabel(column);
    }

    @Override
    public String getColumnName(int column) throws SQLException {
        return this.delegate.getColumnName(column);
    }

    @Override
    public String getSchemaName(int column) throws SQLException {
        return this.delegate.getSchemaName(column);
    }

    @Override
    public int getPrecision(int column) throws SQLException {
        return this.delegate.getPrecision(column);
    }

    @Override
    public int getScale(int column) throws SQLException {
        return this.delegate.getScale(column);
    }

    @Override
    public String getTableName(int column) throws SQLException {
        return this.delegate.getTableName(column);
    }

    @Override
    public String getCatalogName(int column) throws SQLException {
        return this.delegate.getCatalogName(column);
    }

    @Override
    public int getColumnType(int column) throws SQLException {
        return this.delegate.getColumnType(column);
    }

    @Override
    public String getColumnTypeName(int column) throws SQLException {
        return this.delegate.getColumnTypeName(column);
    }

    @Override
    public boolean isReadOnly(int column) throws SQLException {
        return this.delegate.isReadOnly(column);
    }

    @Override
    public boolean isWritable(int column) throws SQLException {
        return this.delegate.isWritable(column);
    }

    @Override
    public boolean isDefinitelyWritable(int column) throws SQLException {
        return this.delegate.isDefinitelyWritable(column);
    }

    @Override
    public String getColumnClassName(int column) throws SQLException {
        return this.delegate.getColumnClassName(column);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.delegate.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.delegate.isWrapperFor(iface);
    }
}

