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

public abstract class SynchronizedFilterPreparedStatement
implements PreparedStatement {
    protected PreparedStatement inner;

    private void __setInner(PreparedStatement preparedStatement) {
        this.inner = preparedStatement;
    }

    public SynchronizedFilterPreparedStatement(PreparedStatement preparedStatement) {
        this.__setInner(preparedStatement);
    }

    public SynchronizedFilterPreparedStatement() {
    }

    public synchronized void setInner(PreparedStatement preparedStatement) {
        this.__setInner(preparedStatement);
    }

    public synchronized PreparedStatement getInner() {
        return this.inner;
    }

    @Override
    public synchronized boolean execute() throws SQLException {
        return this.inner.execute();
    }

    @Override
    public synchronized ResultSetMetaData getMetaData() throws SQLException {
        return this.inner.getMetaData();
    }

    @Override
    public synchronized void setArray(int n, Array array) throws SQLException {
        this.inner.setArray(n, array);
    }

    @Override
    public synchronized void addBatch() throws SQLException {
        this.inner.addBatch();
    }

    @Override
    public synchronized ResultSet executeQuery() throws SQLException {
        return this.inner.executeQuery();
    }

    @Override
    public synchronized int executeUpdate() throws SQLException {
        return this.inner.executeUpdate();
    }

    @Override
    public synchronized void clearParameters() throws SQLException {
        this.inner.clearParameters();
    }

    @Override
    public synchronized ParameterMetaData getParameterMetaData() throws SQLException {
        return this.inner.getParameterMetaData();
    }

    @Override
    public synchronized void setAsciiStream(int n, InputStream inputStream, long l) throws SQLException {
        this.inner.setAsciiStream(n, inputStream, l);
    }

    @Override
    public synchronized void setAsciiStream(int n, InputStream inputStream) throws SQLException {
        this.inner.setAsciiStream(n, inputStream);
    }

    @Override
    public synchronized void setAsciiStream(int n, InputStream inputStream, int n2) throws SQLException {
        this.inner.setAsciiStream(n, inputStream, n2);
    }

    @Override
    public synchronized void setBigDecimal(int n, BigDecimal bigDecimal) throws SQLException {
        this.inner.setBigDecimal(n, bigDecimal);
    }

    @Override
    public synchronized void setBinaryStream(int n, InputStream inputStream, int n2) throws SQLException {
        this.inner.setBinaryStream(n, inputStream, n2);
    }

    @Override
    public synchronized void setBinaryStream(int n, InputStream inputStream, long l) throws SQLException {
        this.inner.setBinaryStream(n, inputStream, l);
    }

    @Override
    public synchronized void setBinaryStream(int n, InputStream inputStream) throws SQLException {
        this.inner.setBinaryStream(n, inputStream);
    }

    @Override
    public synchronized void setBlob(int n, InputStream inputStream, long l) throws SQLException {
        this.inner.setBlob(n, inputStream, l);
    }

    @Override
    public synchronized void setBlob(int n, Blob blob) throws SQLException {
        this.inner.setBlob(n, blob);
    }

    @Override
    public synchronized void setBlob(int n, InputStream inputStream) throws SQLException {
        this.inner.setBlob(n, inputStream);
    }

    @Override
    public synchronized void setBytes(int n, byte[] byArray) throws SQLException {
        this.inner.setBytes(n, byArray);
    }

    @Override
    public synchronized void setCharacterStream(int n, Reader reader) throws SQLException {
        this.inner.setCharacterStream(n, reader);
    }

    @Override
    public synchronized void setCharacterStream(int n, Reader reader, int n2) throws SQLException {
        this.inner.setCharacterStream(n, reader, n2);
    }

    @Override
    public synchronized void setCharacterStream(int n, Reader reader, long l) throws SQLException {
        this.inner.setCharacterStream(n, reader, l);
    }

    @Override
    public synchronized void setClob(int n, Reader reader, long l) throws SQLException {
        this.inner.setClob(n, reader, l);
    }

    @Override
    public synchronized void setClob(int n, Reader reader) throws SQLException {
        this.inner.setClob(n, reader);
    }

    @Override
    public synchronized void setClob(int n, Clob clob) throws SQLException {
        this.inner.setClob(n, clob);
    }

    @Override
    public synchronized void setDate(int n, Date date) throws SQLException {
        this.inner.setDate(n, date);
    }

    @Override
    public synchronized void setDate(int n, Date date, Calendar calendar) throws SQLException {
        this.inner.setDate(n, date, calendar);
    }

    @Override
    public synchronized void setNCharacterStream(int n, Reader reader, long l) throws SQLException {
        this.inner.setNCharacterStream(n, reader, l);
    }

    @Override
    public synchronized void setNCharacterStream(int n, Reader reader) throws SQLException {
        this.inner.setNCharacterStream(n, reader);
    }

    @Override
    public synchronized void setNClob(int n, Reader reader) throws SQLException {
        this.inner.setNClob(n, reader);
    }

    @Override
    public synchronized void setNClob(int n, Reader reader, long l) throws SQLException {
        this.inner.setNClob(n, reader, l);
    }

    @Override
    public synchronized void setNClob(int n, NClob nClob) throws SQLException {
        this.inner.setNClob(n, nClob);
    }

    @Override
    public synchronized void setNString(int n, String string) throws SQLException {
        this.inner.setNString(n, string);
    }

    @Override
    public synchronized void setNull(int n, int n2) throws SQLException {
        this.inner.setNull(n, n2);
    }

    @Override
    public synchronized void setNull(int n, int n2, String string) throws SQLException {
        this.inner.setNull(n, n2, string);
    }

    @Override
    public synchronized void setObject(int n, Object object, int n2) throws SQLException {
        this.inner.setObject(n, object, n2);
    }

    @Override
    public synchronized void setObject(int n, Object object, int n2, int n3) throws SQLException {
        this.inner.setObject(n, object, n2, n3);
    }

    @Override
    public synchronized void setObject(int n, Object object) throws SQLException {
        this.inner.setObject(n, object);
    }

    @Override
    public synchronized void setRef(int n, Ref ref) throws SQLException {
        this.inner.setRef(n, ref);
    }

    @Override
    public synchronized void setRowId(int n, RowId rowId) throws SQLException {
        this.inner.setRowId(n, rowId);
    }

    @Override
    public synchronized void setSQLXML(int n, SQLXML sQLXML) throws SQLException {
        this.inner.setSQLXML(n, sQLXML);
    }

    @Override
    public synchronized void setString(int n, String string) throws SQLException {
        this.inner.setString(n, string);
    }

    @Override
    public synchronized void setUnicodeStream(int n, InputStream inputStream, int n2) throws SQLException {
        this.inner.setUnicodeStream(n, inputStream, n2);
    }

    @Override
    public synchronized void setBoolean(int n, boolean bl) throws SQLException {
        this.inner.setBoolean(n, bl);
    }

    @Override
    public synchronized void setByte(int n, byte by) throws SQLException {
        this.inner.setByte(n, by);
    }

    @Override
    public synchronized void setDouble(int n, double d) throws SQLException {
        this.inner.setDouble(n, d);
    }

    @Override
    public synchronized void setFloat(int n, float f) throws SQLException {
        this.inner.setFloat(n, f);
    }

    @Override
    public synchronized void setInt(int n, int n2) throws SQLException {
        this.inner.setInt(n, n2);
    }

    @Override
    public synchronized void setLong(int n, long l) throws SQLException {
        this.inner.setLong(n, l);
    }

    @Override
    public synchronized void setShort(int n, short s) throws SQLException {
        this.inner.setShort(n, s);
    }

    @Override
    public synchronized void setTimestamp(int n, Timestamp timestamp, Calendar calendar) throws SQLException {
        this.inner.setTimestamp(n, timestamp, calendar);
    }

    @Override
    public synchronized void setTimestamp(int n, Timestamp timestamp) throws SQLException {
        this.inner.setTimestamp(n, timestamp);
    }

    @Override
    public synchronized void setURL(int n, URL uRL) throws SQLException {
        this.inner.setURL(n, uRL);
    }

    @Override
    public synchronized void setTime(int n, Time time, Calendar calendar) throws SQLException {
        this.inner.setTime(n, time, calendar);
    }

    @Override
    public synchronized void setTime(int n, Time time) throws SQLException {
        this.inner.setTime(n, time);
    }

    @Override
    public synchronized boolean execute(String string, int n) throws SQLException {
        return this.inner.execute(string, n);
    }

    @Override
    public synchronized boolean execute(String string, String[] stringArray) throws SQLException {
        return this.inner.execute(string, stringArray);
    }

    @Override
    public synchronized boolean execute(String string) throws SQLException {
        return this.inner.execute(string);
    }

    @Override
    public synchronized boolean execute(String string, int[] nArray) throws SQLException {
        return this.inner.execute(string, nArray);
    }

    @Override
    public synchronized void clearWarnings() throws SQLException {
        this.inner.clearWarnings();
    }

    @Override
    public synchronized SQLWarning getWarnings() throws SQLException {
        return this.inner.getWarnings();
    }

    @Override
    public synchronized boolean isClosed() throws SQLException {
        return this.inner.isClosed();
    }

    @Override
    public synchronized int getFetchDirection() throws SQLException {
        return this.inner.getFetchDirection();
    }

    @Override
    public synchronized int getFetchSize() throws SQLException {
        return this.inner.getFetchSize();
    }

    @Override
    public synchronized void setFetchDirection(int n) throws SQLException {
        this.inner.setFetchDirection(n);
    }

    @Override
    public synchronized void setFetchSize(int n) throws SQLException {
        this.inner.setFetchSize(n);
    }

    @Override
    public synchronized Connection getConnection() throws SQLException {
        return this.inner.getConnection();
    }

    @Override
    public synchronized int getResultSetHoldability() throws SQLException {
        return this.inner.getResultSetHoldability();
    }

    @Override
    public synchronized void addBatch(String string) throws SQLException {
        this.inner.addBatch(string);
    }

    @Override
    public synchronized void cancel() throws SQLException {
        this.inner.cancel();
    }

    @Override
    public synchronized void clearBatch() throws SQLException {
        this.inner.clearBatch();
    }

    @Override
    public synchronized void closeOnCompletion() throws SQLException {
        this.inner.closeOnCompletion();
    }

    @Override
    public synchronized int[] executeBatch() throws SQLException {
        return this.inner.executeBatch();
    }

    @Override
    public synchronized ResultSet executeQuery(String string) throws SQLException {
        return this.inner.executeQuery(string);
    }

    @Override
    public synchronized int executeUpdate(String string, int[] nArray) throws SQLException {
        return this.inner.executeUpdate(string, nArray);
    }

    @Override
    public synchronized int executeUpdate(String string, String[] stringArray) throws SQLException {
        return this.inner.executeUpdate(string, stringArray);
    }

    @Override
    public synchronized int executeUpdate(String string) throws SQLException {
        return this.inner.executeUpdate(string);
    }

    @Override
    public synchronized int executeUpdate(String string, int n) throws SQLException {
        return this.inner.executeUpdate(string, n);
    }

    @Override
    public synchronized ResultSet getGeneratedKeys() throws SQLException {
        return this.inner.getGeneratedKeys();
    }

    @Override
    public synchronized int getMaxFieldSize() throws SQLException {
        return this.inner.getMaxFieldSize();
    }

    @Override
    public synchronized int getMaxRows() throws SQLException {
        return this.inner.getMaxRows();
    }

    @Override
    public synchronized boolean getMoreResults() throws SQLException {
        return this.inner.getMoreResults();
    }

    @Override
    public synchronized boolean getMoreResults(int n) throws SQLException {
        return this.inner.getMoreResults(n);
    }

    @Override
    public synchronized int getQueryTimeout() throws SQLException {
        return this.inner.getQueryTimeout();
    }

    @Override
    public synchronized ResultSet getResultSet() throws SQLException {
        return this.inner.getResultSet();
    }

    @Override
    public synchronized int getResultSetConcurrency() throws SQLException {
        return this.inner.getResultSetConcurrency();
    }

    @Override
    public synchronized int getResultSetType() throws SQLException {
        return this.inner.getResultSetType();
    }

    @Override
    public synchronized int getUpdateCount() throws SQLException {
        return this.inner.getUpdateCount();
    }

    @Override
    public synchronized boolean isCloseOnCompletion() throws SQLException {
        return this.inner.isCloseOnCompletion();
    }

    @Override
    public synchronized boolean isPoolable() throws SQLException {
        return this.inner.isPoolable();
    }

    @Override
    public synchronized void setCursorName(String string) throws SQLException {
        this.inner.setCursorName(string);
    }

    @Override
    public synchronized void setEscapeProcessing(boolean bl) throws SQLException {
        this.inner.setEscapeProcessing(bl);
    }

    @Override
    public synchronized void setMaxFieldSize(int n) throws SQLException {
        this.inner.setMaxFieldSize(n);
    }

    @Override
    public synchronized void setMaxRows(int n) throws SQLException {
        this.inner.setMaxRows(n);
    }

    @Override
    public synchronized void setPoolable(boolean bl) throws SQLException {
        this.inner.setPoolable(bl);
    }

    @Override
    public synchronized void setQueryTimeout(int n) throws SQLException {
        this.inner.setQueryTimeout(n);
    }

    @Override
    public synchronized void close() throws SQLException {
        this.inner.close();
    }

    public synchronized boolean isWrapperFor(Class clazz) throws SQLException {
        return this.inner.isWrapperFor(clazz);
    }

    public synchronized Object unwrap(Class clazz) throws SQLException {
        return this.inner.unwrap(clazz);
    }
}

