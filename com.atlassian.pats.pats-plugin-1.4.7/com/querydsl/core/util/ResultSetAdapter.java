/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.util;

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

public class ResultSetAdapter
implements ResultSet {
    private final ResultSet rs;

    public ResultSetAdapter(ResultSet rs) {
        this.rs = rs;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        return this.rs.absolute(row);
    }

    @Override
    public void afterLast() throws SQLException {
        this.rs.afterLast();
    }

    @Override
    public void beforeFirst() throws SQLException {
        this.rs.beforeFirst();
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        this.rs.cancelRowUpdates();
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.rs.clearWarnings();
    }

    @Override
    public void close() throws SQLException {
        this.rs.close();
    }

    @Override
    public void deleteRow() throws SQLException {
        this.rs.deleteRow();
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        return this.rs.findColumn(columnLabel);
    }

    @Override
    public boolean first() throws SQLException {
        return this.rs.first();
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        return this.rs.getArray(columnIndex);
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        return this.rs.getArray(columnLabel);
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return this.rs.getAsciiStream(columnIndex);
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return this.rs.getAsciiStream(columnLabel);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return this.rs.getBigDecimal(columnIndex, scale);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return this.rs.getBigDecimal(columnIndex);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return this.rs.getBigDecimal(columnLabel, scale);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return this.rs.getBigDecimal(columnLabel);
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return this.rs.getBinaryStream(columnIndex);
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return this.rs.getBinaryStream(columnLabel);
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        return this.rs.getBlob(columnIndex);
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return this.rs.getBlob(columnLabel);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return this.rs.getBoolean(columnIndex);
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return this.rs.getBoolean(columnLabel);
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return this.rs.getByte(columnIndex);
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return this.rs.getByte(columnLabel);
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return this.rs.getBytes(columnIndex);
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return this.rs.getBytes(columnLabel);
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return this.rs.getCharacterStream(columnIndex);
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return this.rs.getCharacterStream(columnLabel);
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        return this.rs.getClob(columnIndex);
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return this.rs.getClob(columnLabel);
    }

    @Override
    public int getConcurrency() throws SQLException {
        return this.rs.getConcurrency();
    }

    @Override
    public String getCursorName() throws SQLException {
        return this.rs.getCursorName();
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return this.rs.getDate(columnIndex, cal);
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return this.rs.getDate(columnIndex);
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return this.rs.getDate(columnLabel, cal);
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return this.rs.getDate(columnLabel);
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return this.rs.getDouble(columnIndex);
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return this.rs.getDouble(columnLabel);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return this.rs.getFetchDirection();
    }

    @Override
    public int getFetchSize() throws SQLException {
        return this.rs.getFetchSize();
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return this.rs.getFloat(columnIndex);
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return this.rs.getFloat(columnLabel);
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.rs.getHoldability();
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return this.rs.getInt(columnIndex);
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return this.rs.getInt(columnLabel);
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return this.rs.getLong(columnIndex);
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return this.rs.getLong(columnLabel);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.rs.getMetaData();
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return this.rs.getNCharacterStream(columnIndex);
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return this.rs.getNCharacterStream(columnLabel);
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        return this.rs.getNClob(columnIndex);
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return this.rs.getNClob(columnLabel);
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return this.rs.getNString(columnIndex);
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return this.rs.getNString(columnLabel);
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return this.rs.getObject(columnIndex, map);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return this.rs.getObject(columnIndex);
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        return (T)this.rs.getObject(columnIndex);
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return this.rs.getObject(columnLabel, map);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return this.rs.getObject(columnLabel);
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        return (T)this.rs.getObject(columnLabel);
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        return this.rs.getRef(columnIndex);
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        return this.rs.getRef(columnLabel);
    }

    @Override
    public int getRow() throws SQLException {
        return this.rs.getRow();
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        return this.rs.getRowId(columnIndex);
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        return this.rs.getRowId(columnLabel);
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return this.rs.getShort(columnIndex);
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return this.rs.getShort(columnLabel);
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return this.rs.getSQLXML(columnIndex);
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return this.rs.getSQLXML(columnLabel);
    }

    @Override
    public Statement getStatement() throws SQLException {
        return this.rs.getStatement();
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return this.rs.getString(columnIndex);
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return this.rs.getString(columnLabel);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return this.rs.getTime(columnIndex, cal);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return this.rs.getTime(columnIndex);
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return this.rs.getTime(columnLabel, cal);
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return this.rs.getTime(columnLabel);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return this.rs.getTimestamp(columnIndex, cal);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return this.rs.getTimestamp(columnIndex);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return this.rs.getTimestamp(columnLabel, cal);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return this.rs.getTimestamp(columnLabel);
    }

    @Override
    public int getType() throws SQLException {
        return this.rs.getType();
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return this.rs.getUnicodeStream(columnIndex);
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return this.rs.getUnicodeStream(columnLabel);
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        return this.rs.getURL(columnIndex);
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return this.rs.getURL(columnLabel);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.rs.getWarnings();
    }

    @Override
    public void insertRow() throws SQLException {
        this.rs.insertRow();
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return this.rs.isAfterLast();
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return this.rs.isBeforeFirst();
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.rs.isClosed();
    }

    @Override
    public boolean isFirst() throws SQLException {
        return this.rs.isFirst();
    }

    @Override
    public boolean isLast() throws SQLException {
        return this.rs.isLast();
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.rs.isWrapperFor(iface);
    }

    @Override
    public boolean last() throws SQLException {
        return this.rs.last();
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        this.rs.moveToCurrentRow();
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        this.rs.moveToInsertRow();
    }

    @Override
    public boolean next() throws SQLException {
        return this.rs.next();
    }

    @Override
    public boolean previous() throws SQLException {
        return this.rs.previous();
    }

    @Override
    public void refreshRow() throws SQLException {
        this.rs.refreshRow();
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        return this.rs.relative(rows);
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return this.rs.rowDeleted();
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return this.rs.rowInserted();
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return this.rs.rowUpdated();
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        this.rs.setFetchDirection(direction);
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        this.rs.setFetchSize(rows);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return this.rs.unwrap(iface);
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        this.rs.updateArray(columnIndex, x);
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        this.rs.updateArray(columnLabel, x);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        this.rs.updateAsciiStream(columnIndex, x, length);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        this.rs.updateAsciiStream(columnIndex, x, length);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        this.rs.updateAsciiStream(columnIndex, x);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        this.rs.updateAsciiStream(columnLabel, x, length);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        this.rs.updateAsciiStream(columnLabel, x, length);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        this.rs.updateAsciiStream(columnLabel, x);
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        this.rs.updateBigDecimal(columnIndex, x);
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        this.rs.updateBigDecimal(columnLabel, x);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        this.rs.updateBinaryStream(columnIndex, x, length);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        this.rs.updateBinaryStream(columnIndex, x, length);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        this.rs.updateBinaryStream(columnIndex, x);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        this.rs.updateBinaryStream(columnLabel, x, length);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        this.rs.updateBinaryStream(columnLabel, x, length);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        this.rs.updateBinaryStream(columnLabel, x);
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        this.rs.updateBlob(columnIndex, x);
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        this.rs.updateBlob(columnIndex, inputStream, length);
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        this.rs.updateBlob(columnIndex, inputStream);
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        this.rs.updateBlob(columnLabel, x);
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        this.rs.updateBlob(columnLabel, inputStream, length);
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        this.rs.updateBlob(columnLabel, inputStream);
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        this.rs.updateBoolean(columnIndex, x);
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        this.rs.updateBoolean(columnLabel, x);
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        this.rs.updateByte(columnIndex, x);
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        this.rs.updateByte(columnLabel, x);
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        this.rs.updateBytes(columnIndex, x);
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        this.rs.updateBytes(columnLabel, x);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        this.rs.updateCharacterStream(columnIndex, x, length);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        this.rs.updateCharacterStream(columnIndex, x, length);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        this.rs.updateCharacterStream(columnIndex, x);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        this.rs.updateCharacterStream(columnLabel, reader, length);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        this.rs.updateCharacterStream(columnLabel, reader, length);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        this.rs.updateCharacterStream(columnLabel, reader);
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        this.rs.updateClob(columnIndex, x);
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        this.rs.updateClob(columnIndex, reader, length);
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        this.rs.updateClob(columnIndex, reader);
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        this.rs.updateClob(columnLabel, x);
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        this.rs.updateClob(columnLabel, reader, length);
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        this.rs.updateClob(columnLabel, reader);
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        this.rs.updateDate(columnIndex, x);
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        this.rs.updateDate(columnLabel, x);
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        this.rs.updateDouble(columnIndex, x);
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        this.rs.updateDouble(columnLabel, x);
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        this.rs.updateFloat(columnIndex, x);
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        this.rs.updateFloat(columnLabel, x);
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        this.rs.updateInt(columnIndex, x);
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        this.rs.updateInt(columnLabel, x);
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        this.rs.updateLong(columnIndex, x);
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        this.rs.updateLong(columnLabel, x);
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        this.rs.updateNCharacterStream(columnIndex, x, length);
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        this.rs.updateNCharacterStream(columnIndex, x);
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        this.rs.updateNCharacterStream(columnLabel, reader, length);
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        this.rs.updateNCharacterStream(columnLabel, reader);
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        this.rs.updateNClob(columnIndex, nClob);
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        this.rs.updateNClob(columnIndex, reader, length);
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        this.rs.updateNClob(columnIndex, reader);
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        this.rs.updateNClob(columnLabel, nClob);
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        this.rs.updateNClob(columnLabel, reader, length);
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        this.rs.updateNClob(columnLabel, reader);
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        this.rs.updateNString(columnIndex, nString);
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        this.rs.updateNString(columnLabel, nString);
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        this.rs.updateNull(columnIndex);
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        this.rs.updateNull(columnLabel);
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        this.rs.updateObject(columnIndex, x, scaleOrLength);
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        this.rs.updateObject(columnIndex, x);
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        this.rs.updateObject(columnLabel, x, scaleOrLength);
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        this.rs.updateObject(columnLabel, x);
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        this.rs.updateRef(columnIndex, x);
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        this.rs.updateRef(columnLabel, x);
    }

    @Override
    public void updateRow() throws SQLException {
        this.rs.updateRow();
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        this.rs.updateRowId(columnIndex, x);
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        this.rs.updateRowId(columnLabel, x);
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        this.rs.updateShort(columnIndex, x);
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        this.rs.updateShort(columnLabel, x);
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        this.rs.updateSQLXML(columnIndex, xmlObject);
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        this.rs.updateSQLXML(columnLabel, xmlObject);
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        this.rs.updateString(columnIndex, x);
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        this.rs.updateString(columnLabel, x);
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        this.rs.updateTime(columnIndex, x);
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        this.rs.updateTime(columnLabel, x);
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        this.rs.updateTimestamp(columnIndex, x);
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        this.rs.updateTimestamp(columnLabel, x);
    }

    @Override
    public boolean wasNull() throws SQLException {
        return this.rs.wasNull();
    }
}

