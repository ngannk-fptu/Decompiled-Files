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
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public abstract class SynchronizedFilterResultSet
implements ResultSet {
    protected ResultSet inner;

    private void __setInner(ResultSet resultSet) {
        this.inner = resultSet;
    }

    public SynchronizedFilterResultSet(ResultSet resultSet) {
        this.__setInner(resultSet);
    }

    public SynchronizedFilterResultSet() {
    }

    public synchronized void setInner(ResultSet resultSet) {
        this.__setInner(resultSet);
    }

    public synchronized ResultSet getInner() {
        return this.inner;
    }

    @Override
    public synchronized void clearWarnings() throws SQLException {
        this.inner.clearWarnings();
    }

    @Override
    public synchronized int getHoldability() throws SQLException {
        return this.inner.getHoldability();
    }

    @Override
    public synchronized ResultSetMetaData getMetaData() throws SQLException {
        return this.inner.getMetaData();
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
    public synchronized void updateBigDecimal(int n, BigDecimal bigDecimal) throws SQLException {
        this.inner.updateBigDecimal(n, bigDecimal);
    }

    @Override
    public synchronized void updateBigDecimal(String string, BigDecimal bigDecimal) throws SQLException {
        this.inner.updateBigDecimal(string, bigDecimal);
    }

    @Override
    public synchronized boolean absolute(int n) throws SQLException {
        return this.inner.absolute(n);
    }

    @Override
    public synchronized void afterLast() throws SQLException {
        this.inner.afterLast();
    }

    @Override
    public synchronized void beforeFirst() throws SQLException {
        this.inner.beforeFirst();
    }

    @Override
    public synchronized void cancelRowUpdates() throws SQLException {
        this.inner.cancelRowUpdates();
    }

    @Override
    public synchronized void deleteRow() throws SQLException {
        this.inner.deleteRow();
    }

    @Override
    public synchronized int findColumn(String string) throws SQLException {
        return this.inner.findColumn(string);
    }

    @Override
    public synchronized boolean first() throws SQLException {
        return this.inner.first();
    }

    @Override
    public synchronized InputStream getAsciiStream(int n) throws SQLException {
        return this.inner.getAsciiStream(n);
    }

    @Override
    public synchronized InputStream getAsciiStream(String string) throws SQLException {
        return this.inner.getAsciiStream(string);
    }

    @Override
    public synchronized BigDecimal getBigDecimal(String string, int n) throws SQLException {
        return this.inner.getBigDecimal(string, n);
    }

    @Override
    public synchronized BigDecimal getBigDecimal(String string) throws SQLException {
        return this.inner.getBigDecimal(string);
    }

    @Override
    public synchronized BigDecimal getBigDecimal(int n) throws SQLException {
        return this.inner.getBigDecimal(n);
    }

    @Override
    public synchronized BigDecimal getBigDecimal(int n, int n2) throws SQLException {
        return this.inner.getBigDecimal(n, n2);
    }

    @Override
    public synchronized InputStream getBinaryStream(String string) throws SQLException {
        return this.inner.getBinaryStream(string);
    }

    @Override
    public synchronized InputStream getBinaryStream(int n) throws SQLException {
        return this.inner.getBinaryStream(n);
    }

    @Override
    public synchronized Blob getBlob(String string) throws SQLException {
        return this.inner.getBlob(string);
    }

    @Override
    public synchronized Blob getBlob(int n) throws SQLException {
        return this.inner.getBlob(n);
    }

    @Override
    public synchronized Reader getCharacterStream(int n) throws SQLException {
        return this.inner.getCharacterStream(n);
    }

    @Override
    public synchronized Reader getCharacterStream(String string) throws SQLException {
        return this.inner.getCharacterStream(string);
    }

    @Override
    public synchronized Clob getClob(int n) throws SQLException {
        return this.inner.getClob(n);
    }

    @Override
    public synchronized Clob getClob(String string) throws SQLException {
        return this.inner.getClob(string);
    }

    @Override
    public synchronized int getConcurrency() throws SQLException {
        return this.inner.getConcurrency();
    }

    @Override
    public synchronized String getCursorName() throws SQLException {
        return this.inner.getCursorName();
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
    public synchronized Reader getNCharacterStream(int n) throws SQLException {
        return this.inner.getNCharacterStream(n);
    }

    @Override
    public synchronized Reader getNCharacterStream(String string) throws SQLException {
        return this.inner.getNCharacterStream(string);
    }

    @Override
    public synchronized NClob getNClob(String string) throws SQLException {
        return this.inner.getNClob(string);
    }

    @Override
    public synchronized NClob getNClob(int n) throws SQLException {
        return this.inner.getNClob(n);
    }

    @Override
    public synchronized String getNString(int n) throws SQLException {
        return this.inner.getNString(n);
    }

    @Override
    public synchronized String getNString(String string) throws SQLException {
        return this.inner.getNString(string);
    }

    @Override
    public synchronized int getRow() throws SQLException {
        return this.inner.getRow();
    }

    @Override
    public synchronized RowId getRowId(int n) throws SQLException {
        return this.inner.getRowId(n);
    }

    @Override
    public synchronized RowId getRowId(String string) throws SQLException {
        return this.inner.getRowId(string);
    }

    @Override
    public synchronized SQLXML getSQLXML(String string) throws SQLException {
        return this.inner.getSQLXML(string);
    }

    @Override
    public synchronized SQLXML getSQLXML(int n) throws SQLException {
        return this.inner.getSQLXML(n);
    }

    @Override
    public synchronized Statement getStatement() throws SQLException {
        return this.inner.getStatement();
    }

    @Override
    public synchronized InputStream getUnicodeStream(int n) throws SQLException {
        return this.inner.getUnicodeStream(n);
    }

    @Override
    public synchronized InputStream getUnicodeStream(String string) throws SQLException {
        return this.inner.getUnicodeStream(string);
    }

    @Override
    public synchronized void insertRow() throws SQLException {
        this.inner.insertRow();
    }

    @Override
    public synchronized boolean isAfterLast() throws SQLException {
        return this.inner.isAfterLast();
    }

    @Override
    public synchronized boolean isBeforeFirst() throws SQLException {
        return this.inner.isBeforeFirst();
    }

    @Override
    public synchronized boolean isFirst() throws SQLException {
        return this.inner.isFirst();
    }

    @Override
    public synchronized boolean isLast() throws SQLException {
        return this.inner.isLast();
    }

    @Override
    public synchronized boolean last() throws SQLException {
        return this.inner.last();
    }

    @Override
    public synchronized void moveToCurrentRow() throws SQLException {
        this.inner.moveToCurrentRow();
    }

    @Override
    public synchronized void moveToInsertRow() throws SQLException {
        this.inner.moveToInsertRow();
    }

    @Override
    public synchronized void refreshRow() throws SQLException {
        this.inner.refreshRow();
    }

    @Override
    public synchronized boolean relative(int n) throws SQLException {
        return this.inner.relative(n);
    }

    @Override
    public synchronized boolean rowDeleted() throws SQLException {
        return this.inner.rowDeleted();
    }

    @Override
    public synchronized boolean rowInserted() throws SQLException {
        return this.inner.rowInserted();
    }

    @Override
    public synchronized boolean rowUpdated() throws SQLException {
        return this.inner.rowUpdated();
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
    public synchronized void updateArray(String string, Array array) throws SQLException {
        this.inner.updateArray(string, array);
    }

    @Override
    public synchronized void updateArray(int n, Array array) throws SQLException {
        this.inner.updateArray(n, array);
    }

    @Override
    public synchronized void updateAsciiStream(int n, InputStream inputStream) throws SQLException {
        this.inner.updateAsciiStream(n, inputStream);
    }

    @Override
    public synchronized void updateAsciiStream(int n, InputStream inputStream, int n2) throws SQLException {
        this.inner.updateAsciiStream(n, inputStream, n2);
    }

    @Override
    public synchronized void updateAsciiStream(String string, InputStream inputStream) throws SQLException {
        this.inner.updateAsciiStream(string, inputStream);
    }

    @Override
    public synchronized void updateAsciiStream(String string, InputStream inputStream, long l) throws SQLException {
        this.inner.updateAsciiStream(string, inputStream, l);
    }

    @Override
    public synchronized void updateAsciiStream(String string, InputStream inputStream, int n) throws SQLException {
        this.inner.updateAsciiStream(string, inputStream, n);
    }

    @Override
    public synchronized void updateAsciiStream(int n, InputStream inputStream, long l) throws SQLException {
        this.inner.updateAsciiStream(n, inputStream, l);
    }

    @Override
    public synchronized void updateBinaryStream(int n, InputStream inputStream, long l) throws SQLException {
        this.inner.updateBinaryStream(n, inputStream, l);
    }

    @Override
    public synchronized void updateBinaryStream(String string, InputStream inputStream) throws SQLException {
        this.inner.updateBinaryStream(string, inputStream);
    }

    @Override
    public synchronized void updateBinaryStream(int n, InputStream inputStream) throws SQLException {
        this.inner.updateBinaryStream(n, inputStream);
    }

    @Override
    public synchronized void updateBinaryStream(String string, InputStream inputStream, long l) throws SQLException {
        this.inner.updateBinaryStream(string, inputStream, l);
    }

    @Override
    public synchronized void updateBinaryStream(int n, InputStream inputStream, int n2) throws SQLException {
        this.inner.updateBinaryStream(n, inputStream, n2);
    }

    @Override
    public synchronized void updateBinaryStream(String string, InputStream inputStream, int n) throws SQLException {
        this.inner.updateBinaryStream(string, inputStream, n);
    }

    @Override
    public synchronized void updateBlob(int n, Blob blob) throws SQLException {
        this.inner.updateBlob(n, blob);
    }

    @Override
    public synchronized void updateBlob(String string, Blob blob) throws SQLException {
        this.inner.updateBlob(string, blob);
    }

    @Override
    public synchronized void updateBlob(String string, InputStream inputStream) throws SQLException {
        this.inner.updateBlob(string, inputStream);
    }

    @Override
    public synchronized void updateBlob(String string, InputStream inputStream, long l) throws SQLException {
        this.inner.updateBlob(string, inputStream, l);
    }

    @Override
    public synchronized void updateBlob(int n, InputStream inputStream, long l) throws SQLException {
        this.inner.updateBlob(n, inputStream, l);
    }

    @Override
    public synchronized void updateBlob(int n, InputStream inputStream) throws SQLException {
        this.inner.updateBlob(n, inputStream);
    }

    @Override
    public synchronized void updateBoolean(String string, boolean bl) throws SQLException {
        this.inner.updateBoolean(string, bl);
    }

    @Override
    public synchronized void updateBoolean(int n, boolean bl) throws SQLException {
        this.inner.updateBoolean(n, bl);
    }

    @Override
    public synchronized void updateByte(String string, byte by) throws SQLException {
        this.inner.updateByte(string, by);
    }

    @Override
    public synchronized void updateByte(int n, byte by) throws SQLException {
        this.inner.updateByte(n, by);
    }

    @Override
    public synchronized void updateBytes(String string, byte[] byArray) throws SQLException {
        this.inner.updateBytes(string, byArray);
    }

    @Override
    public synchronized void updateBytes(int n, byte[] byArray) throws SQLException {
        this.inner.updateBytes(n, byArray);
    }

    @Override
    public synchronized void updateCharacterStream(String string, Reader reader) throws SQLException {
        this.inner.updateCharacterStream(string, reader);
    }

    @Override
    public synchronized void updateCharacterStream(String string, Reader reader, int n) throws SQLException {
        this.inner.updateCharacterStream(string, reader, n);
    }

    @Override
    public synchronized void updateCharacterStream(int n, Reader reader, long l) throws SQLException {
        this.inner.updateCharacterStream(n, reader, l);
    }

    @Override
    public synchronized void updateCharacterStream(String string, Reader reader, long l) throws SQLException {
        this.inner.updateCharacterStream(string, reader, l);
    }

    @Override
    public synchronized void updateCharacterStream(int n, Reader reader) throws SQLException {
        this.inner.updateCharacterStream(n, reader);
    }

    @Override
    public synchronized void updateCharacterStream(int n, Reader reader, int n2) throws SQLException {
        this.inner.updateCharacterStream(n, reader, n2);
    }

    @Override
    public synchronized void updateClob(String string, Reader reader, long l) throws SQLException {
        this.inner.updateClob(string, reader, l);
    }

    @Override
    public synchronized void updateClob(int n, Reader reader, long l) throws SQLException {
        this.inner.updateClob(n, reader, l);
    }

    @Override
    public synchronized void updateClob(String string, Reader reader) throws SQLException {
        this.inner.updateClob(string, reader);
    }

    @Override
    public synchronized void updateClob(int n, Reader reader) throws SQLException {
        this.inner.updateClob(n, reader);
    }

    @Override
    public synchronized void updateClob(int n, Clob clob) throws SQLException {
        this.inner.updateClob(n, clob);
    }

    @Override
    public synchronized void updateClob(String string, Clob clob) throws SQLException {
        this.inner.updateClob(string, clob);
    }

    @Override
    public synchronized void updateDate(int n, Date date) throws SQLException {
        this.inner.updateDate(n, date);
    }

    @Override
    public synchronized void updateDate(String string, Date date) throws SQLException {
        this.inner.updateDate(string, date);
    }

    @Override
    public synchronized void updateDouble(int n, double d) throws SQLException {
        this.inner.updateDouble(n, d);
    }

    @Override
    public synchronized void updateDouble(String string, double d) throws SQLException {
        this.inner.updateDouble(string, d);
    }

    @Override
    public synchronized void updateFloat(String string, float f) throws SQLException {
        this.inner.updateFloat(string, f);
    }

    @Override
    public synchronized void updateFloat(int n, float f) throws SQLException {
        this.inner.updateFloat(n, f);
    }

    @Override
    public synchronized void updateInt(String string, int n) throws SQLException {
        this.inner.updateInt(string, n);
    }

    @Override
    public synchronized void updateInt(int n, int n2) throws SQLException {
        this.inner.updateInt(n, n2);
    }

    @Override
    public synchronized void updateLong(String string, long l) throws SQLException {
        this.inner.updateLong(string, l);
    }

    @Override
    public synchronized void updateLong(int n, long l) throws SQLException {
        this.inner.updateLong(n, l);
    }

    @Override
    public synchronized void updateNCharacterStream(int n, Reader reader) throws SQLException {
        this.inner.updateNCharacterStream(n, reader);
    }

    @Override
    public synchronized void updateNCharacterStream(String string, Reader reader) throws SQLException {
        this.inner.updateNCharacterStream(string, reader);
    }

    @Override
    public synchronized void updateNCharacterStream(String string, Reader reader, long l) throws SQLException {
        this.inner.updateNCharacterStream(string, reader, l);
    }

    @Override
    public synchronized void updateNCharacterStream(int n, Reader reader, long l) throws SQLException {
        this.inner.updateNCharacterStream(n, reader, l);
    }

    @Override
    public synchronized void updateNClob(int n, Reader reader) throws SQLException {
        this.inner.updateNClob(n, reader);
    }

    @Override
    public synchronized void updateNClob(String string, Reader reader) throws SQLException {
        this.inner.updateNClob(string, reader);
    }

    @Override
    public synchronized void updateNClob(int n, Reader reader, long l) throws SQLException {
        this.inner.updateNClob(n, reader, l);
    }

    @Override
    public synchronized void updateNClob(int n, NClob nClob) throws SQLException {
        this.inner.updateNClob(n, nClob);
    }

    @Override
    public synchronized void updateNClob(String string, Reader reader, long l) throws SQLException {
        this.inner.updateNClob(string, reader, l);
    }

    @Override
    public synchronized void updateNClob(String string, NClob nClob) throws SQLException {
        this.inner.updateNClob(string, nClob);
    }

    @Override
    public synchronized void updateNString(String string, String string2) throws SQLException {
        this.inner.updateNString(string, string2);
    }

    @Override
    public synchronized void updateNString(int n, String string) throws SQLException {
        this.inner.updateNString(n, string);
    }

    @Override
    public synchronized void updateNull(int n) throws SQLException {
        this.inner.updateNull(n);
    }

    @Override
    public synchronized void updateNull(String string) throws SQLException {
        this.inner.updateNull(string);
    }

    @Override
    public synchronized void updateObject(int n, Object object) throws SQLException {
        this.inner.updateObject(n, object);
    }

    @Override
    public synchronized void updateObject(String string, Object object) throws SQLException {
        this.inner.updateObject(string, object);
    }

    @Override
    public synchronized void updateObject(String string, Object object, int n) throws SQLException {
        this.inner.updateObject(string, object, n);
    }

    @Override
    public synchronized void updateObject(int n, Object object, int n2) throws SQLException {
        this.inner.updateObject(n, object, n2);
    }

    @Override
    public synchronized void updateRef(int n, Ref ref) throws SQLException {
        this.inner.updateRef(n, ref);
    }

    @Override
    public synchronized void updateRef(String string, Ref ref) throws SQLException {
        this.inner.updateRef(string, ref);
    }

    @Override
    public synchronized void updateRow() throws SQLException {
        this.inner.updateRow();
    }

    @Override
    public synchronized void updateRowId(int n, RowId rowId) throws SQLException {
        this.inner.updateRowId(n, rowId);
    }

    @Override
    public synchronized void updateRowId(String string, RowId rowId) throws SQLException {
        this.inner.updateRowId(string, rowId);
    }

    @Override
    public synchronized void updateSQLXML(int n, SQLXML sQLXML) throws SQLException {
        this.inner.updateSQLXML(n, sQLXML);
    }

    @Override
    public synchronized void updateSQLXML(String string, SQLXML sQLXML) throws SQLException {
        this.inner.updateSQLXML(string, sQLXML);
    }

    @Override
    public synchronized void updateShort(String string, short s) throws SQLException {
        this.inner.updateShort(string, s);
    }

    @Override
    public synchronized void updateShort(int n, short s) throws SQLException {
        this.inner.updateShort(n, s);
    }

    @Override
    public synchronized void updateString(String string, String string2) throws SQLException {
        this.inner.updateString(string, string2);
    }

    @Override
    public synchronized void updateString(int n, String string) throws SQLException {
        this.inner.updateString(n, string);
    }

    @Override
    public synchronized void updateTime(String string, Time time) throws SQLException {
        this.inner.updateTime(string, time);
    }

    @Override
    public synchronized void updateTime(int n, Time time) throws SQLException {
        this.inner.updateTime(n, time);
    }

    @Override
    public synchronized void updateTimestamp(String string, Timestamp timestamp) throws SQLException {
        this.inner.updateTimestamp(string, timestamp);
    }

    @Override
    public synchronized void updateTimestamp(int n, Timestamp timestamp) throws SQLException {
        this.inner.updateTimestamp(n, timestamp);
    }

    @Override
    public synchronized boolean wasNull() throws SQLException {
        return this.inner.wasNull();
    }

    public synchronized Object getObject(int n, Class clazz) throws SQLException {
        return this.inner.getObject(n, clazz);
    }

    @Override
    public synchronized Object getObject(String string) throws SQLException {
        return this.inner.getObject(string);
    }

    public synchronized Object getObject(String string, Class clazz) throws SQLException {
        return this.inner.getObject(string, clazz);
    }

    public synchronized Object getObject(int n, Map map) throws SQLException {
        return this.inner.getObject(n, map);
    }

    public synchronized Object getObject(String string, Map map) throws SQLException {
        return this.inner.getObject(string, map);
    }

    @Override
    public synchronized Object getObject(int n) throws SQLException {
        return this.inner.getObject(n);
    }

    @Override
    public synchronized boolean getBoolean(String string) throws SQLException {
        return this.inner.getBoolean(string);
    }

    @Override
    public synchronized boolean getBoolean(int n) throws SQLException {
        return this.inner.getBoolean(n);
    }

    @Override
    public synchronized byte getByte(int n) throws SQLException {
        return this.inner.getByte(n);
    }

    @Override
    public synchronized byte getByte(String string) throws SQLException {
        return this.inner.getByte(string);
    }

    @Override
    public synchronized short getShort(String string) throws SQLException {
        return this.inner.getShort(string);
    }

    @Override
    public synchronized short getShort(int n) throws SQLException {
        return this.inner.getShort(n);
    }

    @Override
    public synchronized int getInt(String string) throws SQLException {
        return this.inner.getInt(string);
    }

    @Override
    public synchronized int getInt(int n) throws SQLException {
        return this.inner.getInt(n);
    }

    @Override
    public synchronized long getLong(String string) throws SQLException {
        return this.inner.getLong(string);
    }

    @Override
    public synchronized long getLong(int n) throws SQLException {
        return this.inner.getLong(n);
    }

    @Override
    public synchronized float getFloat(int n) throws SQLException {
        return this.inner.getFloat(n);
    }

    @Override
    public synchronized float getFloat(String string) throws SQLException {
        return this.inner.getFloat(string);
    }

    @Override
    public synchronized double getDouble(int n) throws SQLException {
        return this.inner.getDouble(n);
    }

    @Override
    public synchronized double getDouble(String string) throws SQLException {
        return this.inner.getDouble(string);
    }

    @Override
    public synchronized byte[] getBytes(String string) throws SQLException {
        return this.inner.getBytes(string);
    }

    @Override
    public synchronized byte[] getBytes(int n) throws SQLException {
        return this.inner.getBytes(n);
    }

    @Override
    public synchronized Array getArray(int n) throws SQLException {
        return this.inner.getArray(n);
    }

    @Override
    public synchronized Array getArray(String string) throws SQLException {
        return this.inner.getArray(string);
    }

    @Override
    public synchronized boolean next() throws SQLException {
        return this.inner.next();
    }

    @Override
    public synchronized URL getURL(int n) throws SQLException {
        return this.inner.getURL(n);
    }

    @Override
    public synchronized URL getURL(String string) throws SQLException {
        return this.inner.getURL(string);
    }

    @Override
    public synchronized void close() throws SQLException {
        this.inner.close();
    }

    @Override
    public synchronized int getType() throws SQLException {
        return this.inner.getType();
    }

    @Override
    public synchronized boolean previous() throws SQLException {
        return this.inner.previous();
    }

    @Override
    public synchronized Ref getRef(String string) throws SQLException {
        return this.inner.getRef(string);
    }

    @Override
    public synchronized Ref getRef(int n) throws SQLException {
        return this.inner.getRef(n);
    }

    @Override
    public synchronized String getString(int n) throws SQLException {
        return this.inner.getString(n);
    }

    @Override
    public synchronized String getString(String string) throws SQLException {
        return this.inner.getString(string);
    }

    @Override
    public synchronized Date getDate(int n, Calendar calendar) throws SQLException {
        return this.inner.getDate(n, calendar);
    }

    @Override
    public synchronized Date getDate(String string, Calendar calendar) throws SQLException {
        return this.inner.getDate(string, calendar);
    }

    @Override
    public synchronized Date getDate(String string) throws SQLException {
        return this.inner.getDate(string);
    }

    @Override
    public synchronized Date getDate(int n) throws SQLException {
        return this.inner.getDate(n);
    }

    @Override
    public synchronized Time getTime(int n) throws SQLException {
        return this.inner.getTime(n);
    }

    @Override
    public synchronized Time getTime(String string, Calendar calendar) throws SQLException {
        return this.inner.getTime(string, calendar);
    }

    @Override
    public synchronized Time getTime(String string) throws SQLException {
        return this.inner.getTime(string);
    }

    @Override
    public synchronized Time getTime(int n, Calendar calendar) throws SQLException {
        return this.inner.getTime(n, calendar);
    }

    @Override
    public synchronized Timestamp getTimestamp(int n) throws SQLException {
        return this.inner.getTimestamp(n);
    }

    @Override
    public synchronized Timestamp getTimestamp(String string) throws SQLException {
        return this.inner.getTimestamp(string);
    }

    @Override
    public synchronized Timestamp getTimestamp(String string, Calendar calendar) throws SQLException {
        return this.inner.getTimestamp(string, calendar);
    }

    @Override
    public synchronized Timestamp getTimestamp(int n, Calendar calendar) throws SQLException {
        return this.inner.getTimestamp(n, calendar);
    }

    public synchronized boolean isWrapperFor(Class clazz) throws SQLException {
        return this.inner.isWrapperFor(clazz);
    }

    public synchronized Object unwrap(Class clazz) throws SQLException {
        return this.inner.unwrap(clazz);
    }
}

