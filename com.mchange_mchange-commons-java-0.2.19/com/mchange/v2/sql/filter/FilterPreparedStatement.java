/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.sql.filter;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public abstract class FilterPreparedStatement
implements PreparedStatement {
    protected PreparedStatement inner;

    private void __setInner(PreparedStatement preparedStatement) {
        this.inner = preparedStatement;
    }

    public FilterPreparedStatement(PreparedStatement preparedStatement) {
        this.__setInner(preparedStatement);
    }

    public FilterPreparedStatement() {
    }

    public void setInner(PreparedStatement preparedStatement) {
        this.__setInner(preparedStatement);
    }

    public PreparedStatement getInner() {
        return this.inner;
    }

    @Override
    public boolean execute() throws SQLException {
        return this.inner.execute();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.inner.getMetaData();
    }

    @Override
    public void setArray(int n, Array array) throws SQLException {
        this.inner.setArray(n, array);
    }

    @Override
    public void addBatch() throws SQLException {
        this.inner.addBatch();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        return this.inner.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException {
        return this.inner.executeUpdate();
    }

    @Override
    public void clearParameters() throws SQLException {
        this.inner.clearParameters();
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return this.inner.getParameterMetaData();
    }

    @Override
    public void setAsciiStream(int n, InputStream inputStream, long l) throws SQLException {
        this.inner.setAsciiStream(n, inputStream, l);
    }

    @Override
    public void setAsciiStream(int n, InputStream inputStream) throws SQLException {
        this.inner.setAsciiStream(n, inputStream);
    }

    @Override
    public void setAsciiStream(int n, InputStream inputStream, int n2) throws SQLException {
        this.inner.setAsciiStream(n, inputStream, n2);
    }

    @Override
    public void setBigDecimal(int n, BigDecimal bigDecimal) throws SQLException {
        this.inner.setBigDecimal(n, bigDecimal);
    }

    @Override
    public void setBinaryStream(int n, InputStream inputStream, int n2) throws SQLException {
        this.inner.setBinaryStream(n, inputStream, n2);
    }

    @Override
    public void setBinaryStream(int n, InputStream inputStream, long l) throws SQLException {
        this.inner.setBinaryStream(n, inputStream, l);
    }

    @Override
    public void setBinaryStream(int n, InputStream inputStream) throws SQLException {
        this.inner.setBinaryStream(n, inputStream);
    }

    @Override
    public void setBlob(int n, InputStream inputStream, long l) throws SQLException {
        this.inner.setBlob(n, inputStream, l);
    }

    @Override
    public void setBlob(int n, Blob blob) throws SQLException {
        this.inner.setBlob(n, blob);
    }

    @Override
    public void setBlob(int n, InputStream inputStream) throws SQLException {
        this.inner.setBlob(n, inputStream);
    }

    @Override
    public void setBytes(int n, byte[] byArray) throws SQLException {
        this.inner.setBytes(n, byArray);
    }

    @Override
    public void setCharacterStream(int n, Reader reader) throws SQLException {
        this.inner.setCharacterStream(n, reader);
    }

    @Override
    public void setCharacterStream(int n, Reader reader, int n2) throws SQLException {
        this.inner.setCharacterStream(n, reader, n2);
    }

    @Override
    public void setCharacterStream(int n, Reader reader, long l) throws SQLException {
        this.inner.setCharacterStream(n, reader, l);
    }

    @Override
    public void setClob(int n, Reader reader, long l) throws SQLException {
        this.inner.setClob(n, reader, l);
    }

    @Override
    public void setClob(int n, Reader reader) throws SQLException {
        this.inner.setClob(n, reader);
    }

    @Override
    public void setClob(int n, Clob clob) throws SQLException {
        this.inner.setClob(n, clob);
    }

    @Override
    public void setDate(int n, Date date) throws SQLException {
        this.inner.setDate(n, date);
    }

    @Override
    public void setDate(int n, Date date, Calendar calendar) throws SQLException {
        this.inner.setDate(n, date, calendar);
    }

    @Override
    public void setNCharacterStream(int n, Reader reader, long l) throws SQLException {
        this.inner.setNCharacterStream(n, reader, l);
    }

    @Override
    public void setNCharacterStream(int n, Reader reader) throws SQLException {
        this.inner.setNCharacterStream(n, reader);
    }

    @Override
    public void setNClob(int n, Reader reader) throws SQLException {
        this.inner.setNClob(n, reader);
    }

    @Override
    public void setNClob(int n, Reader reader, long l) throws SQLException {
        this.inner.setNClob(n, reader, l);
    }

    @Override
    public void setNClob(int n, NClob nClob) throws SQLException {
        this.inner.setNClob(n, nClob);
    }

    @Override
    public void setNString(int n, String string) throws SQLException {
        this.inner.setNString(n, string);
    }

    @Override
    public void setNull(int n, int n2) throws SQLException {
        this.inner.setNull(n, n2);
    }

    @Override
    public void setNull(int n, int n2, String string) throws SQLException {
        this.inner.setNull(n, n2, string);
    }

    @Override
    public void setObject(int n, Object object, int n2) throws SQLException {
        this.inner.setObject(n, object, n2);
    }

    @Override
    public void setObject(int n, Object object, int n2, int n3) throws SQLException {
        this.inner.setObject(n, object, n2, n3);
    }

    @Override
    public void setObject(int n, Object object) throws SQLException {
        this.inner.setObject(n, object);
    }

    @Override
    public void setRef(int n, Ref ref) throws SQLException {
        this.inner.setRef(n, ref);
    }

    @Override
    public void setRowId(int n, RowId rowId) throws SQLException {
        this.inner.setRowId(n, rowId);
    }

    @Override
    public void setSQLXML(int n, SQLXML sQLXML) throws SQLException {
        this.inner.setSQLXML(n, sQLXML);
    }

    @Override
    public void setString(int n, String string) throws SQLException {
        this.inner.setString(n, string);
    }

    @Override
    public void setUnicodeStream(int n, InputStream inputStream, int n2) throws SQLException {
        this.inner.setUnicodeStream(n, inputStream, n2);
    }

    @Override
    public void setBoolean(int n, boolean bl) throws SQLException {
        this.inner.setBoolean(n, bl);
    }

    @Override
    public void setByte(int n, byte by) throws SQLException {
        this.inner.setByte(n, by);
    }

    @Override
    public void setDouble(int n, double d) throws SQLException {
        this.inner.setDouble(n, d);
    }

    @Override
    public void setFloat(int n, float f) throws SQLException {
        this.inner.setFloat(n, f);
    }

    @Override
    public void setInt(int n, int n2) throws SQLException {
        this.inner.setInt(n, n2);
    }

    @Override
    public void setLong(int n, long l) throws SQLException {
        this.inner.setLong(n, l);
    }

    @Override
    public void setShort(int n, short s) throws SQLException {
        this.inner.setShort(n, s);
    }

    @Override
    public void setTimestamp(int n, Timestamp timestamp, Calendar calendar) throws SQLException {
        this.inner.setTimestamp(n, timestamp, calendar);
    }

    @Override
    public void setTimestamp(int n, Timestamp timestamp) throws SQLException {
        this.inner.setTimestamp(n, timestamp);
    }

    @Override
    public void setURL(int n, URL uRL) throws SQLException {
        this.inner.setURL(n, uRL);
    }

    @Override
    public void setTime(int n, Time time, Calendar calendar) throws SQLException {
        this.inner.setTime(n, time, calendar);
    }

    @Override
    public void setTime(int n, Time time) throws SQLException {
        this.inner.setTime(n, time);
    }

    @Override
    public boolean execute(String string, int n) throws SQLException {
        return this.inner.execute(string, n);
    }

    @Override
    public boolean execute(String string, String[] stringArray) throws SQLException {
        return this.inner.execute(string, stringArray);
    }

    @Override
    public boolean execute(String string) throws SQLException {
        return this.inner.execute(string);
    }

    @Override
    public boolean execute(String string, int[] nArray) throws SQLException {
        return this.inner.execute(string, nArray);
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.inner.clearWarnings();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.inner.getWarnings();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.inner.isClosed();
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return this.inner.getFetchDirection();
    }

    @Override
    public int getFetchSize() throws SQLException {
        return this.inner.getFetchSize();
    }

    @Override
    public void setFetchDirection(int n) throws SQLException {
        this.inner.setFetchDirection(n);
    }

    @Override
    public void setFetchSize(int n) throws SQLException {
        this.inner.setFetchSize(n);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.inner.getConnection();
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return this.inner.getResultSetHoldability();
    }

    @Override
    public void addBatch(String string) throws SQLException {
        this.inner.addBatch(string);
    }

    @Override
    public void cancel() throws SQLException {
        this.inner.cancel();
    }

    @Override
    public void clearBatch() throws SQLException {
        this.inner.clearBatch();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        this.inner.closeOnCompletion();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        return this.inner.executeBatch();
    }

    @Override
    public ResultSet executeQuery(String string) throws SQLException {
        return this.inner.executeQuery(string);
    }

    @Override
    public int executeUpdate(String string, int[] nArray) throws SQLException {
        return this.inner.executeUpdate(string, nArray);
    }

    @Override
    public int executeUpdate(String string, String[] stringArray) throws SQLException {
        return this.inner.executeUpdate(string, stringArray);
    }

    @Override
    public int executeUpdate(String string) throws SQLException {
        return this.inner.executeUpdate(string);
    }

    @Override
    public int executeUpdate(String string, int n) throws SQLException {
        return this.inner.executeUpdate(string, n);
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return this.inner.getGeneratedKeys();
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return this.inner.getMaxFieldSize();
    }

    @Override
    public int getMaxRows() throws SQLException {
        return this.inner.getMaxRows();
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return this.inner.getMoreResults();
    }

    @Override
    public boolean getMoreResults(int n) throws SQLException {
        return this.inner.getMoreResults(n);
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return this.inner.getQueryTimeout();
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return this.inner.getResultSet();
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return this.inner.getResultSetConcurrency();
    }

    @Override
    public int getResultSetType() throws SQLException {
        return this.inner.getResultSetType();
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return this.inner.getUpdateCount();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return this.inner.isCloseOnCompletion();
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return this.inner.isPoolable();
    }

    @Override
    public void setCursorName(String string) throws SQLException {
        this.inner.setCursorName(string);
    }

    @Override
    public void setEscapeProcessing(boolean bl) throws SQLException {
        this.inner.setEscapeProcessing(bl);
    }

    @Override
    public void setMaxFieldSize(int n) throws SQLException {
        this.inner.setMaxFieldSize(n);
    }

    @Override
    public void setMaxRows(int n) throws SQLException {
        this.inner.setMaxRows(n);
    }

    @Override
    public void setPoolable(boolean bl) throws SQLException {
        this.inner.setPoolable(bl);
    }

    @Override
    public void setQueryTimeout(int n) throws SQLException {
        this.inner.setQueryTimeout(n);
    }

    @Override
    public void close() throws SQLException {
        this.inner.close();
    }

    public boolean isWrapperFor(Class clazz) throws SQLException {
        return this.inner.isWrapperFor(clazz);
    }

    public Object unwrap(Class clazz) throws SQLException {
        return this.inner.unwrap(clazz);
    }
}

