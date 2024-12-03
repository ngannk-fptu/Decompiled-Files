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

public abstract class FilterResultSet
implements ResultSet {
    protected ResultSet inner;

    private void __setInner(ResultSet resultSet) {
        this.inner = resultSet;
    }

    public FilterResultSet(ResultSet resultSet) {
        this.__setInner(resultSet);
    }

    public FilterResultSet() {
    }

    public void setInner(ResultSet resultSet) {
        this.__setInner(resultSet);
    }

    public ResultSet getInner() {
        return this.inner;
    }

    @Override
    public void clearWarnings() throws SQLException {
        this.inner.clearWarnings();
    }

    @Override
    public int getHoldability() throws SQLException {
        return this.inner.getHoldability();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return this.inner.getMetaData();
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
    public void updateBigDecimal(int n, BigDecimal bigDecimal) throws SQLException {
        this.inner.updateBigDecimal(n, bigDecimal);
    }

    @Override
    public void updateBigDecimal(String string, BigDecimal bigDecimal) throws SQLException {
        this.inner.updateBigDecimal(string, bigDecimal);
    }

    @Override
    public boolean absolute(int n) throws SQLException {
        return this.inner.absolute(n);
    }

    @Override
    public void afterLast() throws SQLException {
        this.inner.afterLast();
    }

    @Override
    public void beforeFirst() throws SQLException {
        this.inner.beforeFirst();
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        this.inner.cancelRowUpdates();
    }

    @Override
    public void deleteRow() throws SQLException {
        this.inner.deleteRow();
    }

    @Override
    public int findColumn(String string) throws SQLException {
        return this.inner.findColumn(string);
    }

    @Override
    public boolean first() throws SQLException {
        return this.inner.first();
    }

    @Override
    public InputStream getAsciiStream(int n) throws SQLException {
        return this.inner.getAsciiStream(n);
    }

    @Override
    public InputStream getAsciiStream(String string) throws SQLException {
        return this.inner.getAsciiStream(string);
    }

    @Override
    public BigDecimal getBigDecimal(String string, int n) throws SQLException {
        return this.inner.getBigDecimal(string, n);
    }

    @Override
    public BigDecimal getBigDecimal(String string) throws SQLException {
        return this.inner.getBigDecimal(string);
    }

    @Override
    public BigDecimal getBigDecimal(int n) throws SQLException {
        return this.inner.getBigDecimal(n);
    }

    @Override
    public BigDecimal getBigDecimal(int n, int n2) throws SQLException {
        return this.inner.getBigDecimal(n, n2);
    }

    @Override
    public InputStream getBinaryStream(String string) throws SQLException {
        return this.inner.getBinaryStream(string);
    }

    @Override
    public InputStream getBinaryStream(int n) throws SQLException {
        return this.inner.getBinaryStream(n);
    }

    @Override
    public Blob getBlob(String string) throws SQLException {
        return this.inner.getBlob(string);
    }

    @Override
    public Blob getBlob(int n) throws SQLException {
        return this.inner.getBlob(n);
    }

    @Override
    public Reader getCharacterStream(int n) throws SQLException {
        return this.inner.getCharacterStream(n);
    }

    @Override
    public Reader getCharacterStream(String string) throws SQLException {
        return this.inner.getCharacterStream(string);
    }

    @Override
    public Clob getClob(int n) throws SQLException {
        return this.inner.getClob(n);
    }

    @Override
    public Clob getClob(String string) throws SQLException {
        return this.inner.getClob(string);
    }

    @Override
    public int getConcurrency() throws SQLException {
        return this.inner.getConcurrency();
    }

    @Override
    public String getCursorName() throws SQLException {
        return this.inner.getCursorName();
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
    public Reader getNCharacterStream(int n) throws SQLException {
        return this.inner.getNCharacterStream(n);
    }

    @Override
    public Reader getNCharacterStream(String string) throws SQLException {
        return this.inner.getNCharacterStream(string);
    }

    @Override
    public NClob getNClob(String string) throws SQLException {
        return this.inner.getNClob(string);
    }

    @Override
    public NClob getNClob(int n) throws SQLException {
        return this.inner.getNClob(n);
    }

    @Override
    public String getNString(int n) throws SQLException {
        return this.inner.getNString(n);
    }

    @Override
    public String getNString(String string) throws SQLException {
        return this.inner.getNString(string);
    }

    @Override
    public int getRow() throws SQLException {
        return this.inner.getRow();
    }

    @Override
    public RowId getRowId(int n) throws SQLException {
        return this.inner.getRowId(n);
    }

    @Override
    public RowId getRowId(String string) throws SQLException {
        return this.inner.getRowId(string);
    }

    @Override
    public SQLXML getSQLXML(String string) throws SQLException {
        return this.inner.getSQLXML(string);
    }

    @Override
    public SQLXML getSQLXML(int n) throws SQLException {
        return this.inner.getSQLXML(n);
    }

    @Override
    public Statement getStatement() throws SQLException {
        return this.inner.getStatement();
    }

    @Override
    public InputStream getUnicodeStream(int n) throws SQLException {
        return this.inner.getUnicodeStream(n);
    }

    @Override
    public InputStream getUnicodeStream(String string) throws SQLException {
        return this.inner.getUnicodeStream(string);
    }

    @Override
    public void insertRow() throws SQLException {
        this.inner.insertRow();
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return this.inner.isAfterLast();
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return this.inner.isBeforeFirst();
    }

    @Override
    public boolean isFirst() throws SQLException {
        return this.inner.isFirst();
    }

    @Override
    public boolean isLast() throws SQLException {
        return this.inner.isLast();
    }

    @Override
    public boolean last() throws SQLException {
        return this.inner.last();
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        this.inner.moveToCurrentRow();
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        this.inner.moveToInsertRow();
    }

    @Override
    public void refreshRow() throws SQLException {
        this.inner.refreshRow();
    }

    @Override
    public boolean relative(int n) throws SQLException {
        return this.inner.relative(n);
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return this.inner.rowDeleted();
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return this.inner.rowInserted();
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return this.inner.rowUpdated();
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
    public void updateArray(String string, Array array) throws SQLException {
        this.inner.updateArray(string, array);
    }

    @Override
    public void updateArray(int n, Array array) throws SQLException {
        this.inner.updateArray(n, array);
    }

    @Override
    public void updateAsciiStream(int n, InputStream inputStream) throws SQLException {
        this.inner.updateAsciiStream(n, inputStream);
    }

    @Override
    public void updateAsciiStream(int n, InputStream inputStream, int n2) throws SQLException {
        this.inner.updateAsciiStream(n, inputStream, n2);
    }

    @Override
    public void updateAsciiStream(String string, InputStream inputStream) throws SQLException {
        this.inner.updateAsciiStream(string, inputStream);
    }

    @Override
    public void updateAsciiStream(String string, InputStream inputStream, long l) throws SQLException {
        this.inner.updateAsciiStream(string, inputStream, l);
    }

    @Override
    public void updateAsciiStream(String string, InputStream inputStream, int n) throws SQLException {
        this.inner.updateAsciiStream(string, inputStream, n);
    }

    @Override
    public void updateAsciiStream(int n, InputStream inputStream, long l) throws SQLException {
        this.inner.updateAsciiStream(n, inputStream, l);
    }

    @Override
    public void updateBinaryStream(int n, InputStream inputStream, long l) throws SQLException {
        this.inner.updateBinaryStream(n, inputStream, l);
    }

    @Override
    public void updateBinaryStream(String string, InputStream inputStream) throws SQLException {
        this.inner.updateBinaryStream(string, inputStream);
    }

    @Override
    public void updateBinaryStream(int n, InputStream inputStream) throws SQLException {
        this.inner.updateBinaryStream(n, inputStream);
    }

    @Override
    public void updateBinaryStream(String string, InputStream inputStream, long l) throws SQLException {
        this.inner.updateBinaryStream(string, inputStream, l);
    }

    @Override
    public void updateBinaryStream(int n, InputStream inputStream, int n2) throws SQLException {
        this.inner.updateBinaryStream(n, inputStream, n2);
    }

    @Override
    public void updateBinaryStream(String string, InputStream inputStream, int n) throws SQLException {
        this.inner.updateBinaryStream(string, inputStream, n);
    }

    @Override
    public void updateBlob(int n, Blob blob) throws SQLException {
        this.inner.updateBlob(n, blob);
    }

    @Override
    public void updateBlob(String string, Blob blob) throws SQLException {
        this.inner.updateBlob(string, blob);
    }

    @Override
    public void updateBlob(String string, InputStream inputStream) throws SQLException {
        this.inner.updateBlob(string, inputStream);
    }

    @Override
    public void updateBlob(String string, InputStream inputStream, long l) throws SQLException {
        this.inner.updateBlob(string, inputStream, l);
    }

    @Override
    public void updateBlob(int n, InputStream inputStream, long l) throws SQLException {
        this.inner.updateBlob(n, inputStream, l);
    }

    @Override
    public void updateBlob(int n, InputStream inputStream) throws SQLException {
        this.inner.updateBlob(n, inputStream);
    }

    @Override
    public void updateBoolean(String string, boolean bl) throws SQLException {
        this.inner.updateBoolean(string, bl);
    }

    @Override
    public void updateBoolean(int n, boolean bl) throws SQLException {
        this.inner.updateBoolean(n, bl);
    }

    @Override
    public void updateByte(String string, byte by) throws SQLException {
        this.inner.updateByte(string, by);
    }

    @Override
    public void updateByte(int n, byte by) throws SQLException {
        this.inner.updateByte(n, by);
    }

    @Override
    public void updateBytes(String string, byte[] byArray) throws SQLException {
        this.inner.updateBytes(string, byArray);
    }

    @Override
    public void updateBytes(int n, byte[] byArray) throws SQLException {
        this.inner.updateBytes(n, byArray);
    }

    @Override
    public void updateCharacterStream(String string, Reader reader) throws SQLException {
        this.inner.updateCharacterStream(string, reader);
    }

    @Override
    public void updateCharacterStream(String string, Reader reader, int n) throws SQLException {
        this.inner.updateCharacterStream(string, reader, n);
    }

    @Override
    public void updateCharacterStream(int n, Reader reader, long l) throws SQLException {
        this.inner.updateCharacterStream(n, reader, l);
    }

    @Override
    public void updateCharacterStream(String string, Reader reader, long l) throws SQLException {
        this.inner.updateCharacterStream(string, reader, l);
    }

    @Override
    public void updateCharacterStream(int n, Reader reader) throws SQLException {
        this.inner.updateCharacterStream(n, reader);
    }

    @Override
    public void updateCharacterStream(int n, Reader reader, int n2) throws SQLException {
        this.inner.updateCharacterStream(n, reader, n2);
    }

    @Override
    public void updateClob(String string, Reader reader, long l) throws SQLException {
        this.inner.updateClob(string, reader, l);
    }

    @Override
    public void updateClob(int n, Reader reader, long l) throws SQLException {
        this.inner.updateClob(n, reader, l);
    }

    @Override
    public void updateClob(String string, Reader reader) throws SQLException {
        this.inner.updateClob(string, reader);
    }

    @Override
    public void updateClob(int n, Reader reader) throws SQLException {
        this.inner.updateClob(n, reader);
    }

    @Override
    public void updateClob(int n, Clob clob) throws SQLException {
        this.inner.updateClob(n, clob);
    }

    @Override
    public void updateClob(String string, Clob clob) throws SQLException {
        this.inner.updateClob(string, clob);
    }

    @Override
    public void updateDate(int n, Date date) throws SQLException {
        this.inner.updateDate(n, date);
    }

    @Override
    public void updateDate(String string, Date date) throws SQLException {
        this.inner.updateDate(string, date);
    }

    @Override
    public void updateDouble(int n, double d) throws SQLException {
        this.inner.updateDouble(n, d);
    }

    @Override
    public void updateDouble(String string, double d) throws SQLException {
        this.inner.updateDouble(string, d);
    }

    @Override
    public void updateFloat(String string, float f) throws SQLException {
        this.inner.updateFloat(string, f);
    }

    @Override
    public void updateFloat(int n, float f) throws SQLException {
        this.inner.updateFloat(n, f);
    }

    @Override
    public void updateInt(String string, int n) throws SQLException {
        this.inner.updateInt(string, n);
    }

    @Override
    public void updateInt(int n, int n2) throws SQLException {
        this.inner.updateInt(n, n2);
    }

    @Override
    public void updateLong(String string, long l) throws SQLException {
        this.inner.updateLong(string, l);
    }

    @Override
    public void updateLong(int n, long l) throws SQLException {
        this.inner.updateLong(n, l);
    }

    @Override
    public void updateNCharacterStream(int n, Reader reader) throws SQLException {
        this.inner.updateNCharacterStream(n, reader);
    }

    @Override
    public void updateNCharacterStream(String string, Reader reader) throws SQLException {
        this.inner.updateNCharacterStream(string, reader);
    }

    @Override
    public void updateNCharacterStream(String string, Reader reader, long l) throws SQLException {
        this.inner.updateNCharacterStream(string, reader, l);
    }

    @Override
    public void updateNCharacterStream(int n, Reader reader, long l) throws SQLException {
        this.inner.updateNCharacterStream(n, reader, l);
    }

    @Override
    public void updateNClob(int n, Reader reader) throws SQLException {
        this.inner.updateNClob(n, reader);
    }

    @Override
    public void updateNClob(String string, Reader reader) throws SQLException {
        this.inner.updateNClob(string, reader);
    }

    @Override
    public void updateNClob(int n, Reader reader, long l) throws SQLException {
        this.inner.updateNClob(n, reader, l);
    }

    @Override
    public void updateNClob(int n, NClob nClob) throws SQLException {
        this.inner.updateNClob(n, nClob);
    }

    @Override
    public void updateNClob(String string, Reader reader, long l) throws SQLException {
        this.inner.updateNClob(string, reader, l);
    }

    @Override
    public void updateNClob(String string, NClob nClob) throws SQLException {
        this.inner.updateNClob(string, nClob);
    }

    @Override
    public void updateNString(String string, String string2) throws SQLException {
        this.inner.updateNString(string, string2);
    }

    @Override
    public void updateNString(int n, String string) throws SQLException {
        this.inner.updateNString(n, string);
    }

    @Override
    public void updateNull(int n) throws SQLException {
        this.inner.updateNull(n);
    }

    @Override
    public void updateNull(String string) throws SQLException {
        this.inner.updateNull(string);
    }

    @Override
    public void updateObject(int n, Object object) throws SQLException {
        this.inner.updateObject(n, object);
    }

    @Override
    public void updateObject(String string, Object object) throws SQLException {
        this.inner.updateObject(string, object);
    }

    @Override
    public void updateObject(String string, Object object, int n) throws SQLException {
        this.inner.updateObject(string, object, n);
    }

    @Override
    public void updateObject(int n, Object object, int n2) throws SQLException {
        this.inner.updateObject(n, object, n2);
    }

    @Override
    public void updateRef(int n, Ref ref) throws SQLException {
        this.inner.updateRef(n, ref);
    }

    @Override
    public void updateRef(String string, Ref ref) throws SQLException {
        this.inner.updateRef(string, ref);
    }

    @Override
    public void updateRow() throws SQLException {
        this.inner.updateRow();
    }

    @Override
    public void updateRowId(int n, RowId rowId) throws SQLException {
        this.inner.updateRowId(n, rowId);
    }

    @Override
    public void updateRowId(String string, RowId rowId) throws SQLException {
        this.inner.updateRowId(string, rowId);
    }

    @Override
    public void updateSQLXML(int n, SQLXML sQLXML) throws SQLException {
        this.inner.updateSQLXML(n, sQLXML);
    }

    @Override
    public void updateSQLXML(String string, SQLXML sQLXML) throws SQLException {
        this.inner.updateSQLXML(string, sQLXML);
    }

    @Override
    public void updateShort(String string, short s) throws SQLException {
        this.inner.updateShort(string, s);
    }

    @Override
    public void updateShort(int n, short s) throws SQLException {
        this.inner.updateShort(n, s);
    }

    @Override
    public void updateString(String string, String string2) throws SQLException {
        this.inner.updateString(string, string2);
    }

    @Override
    public void updateString(int n, String string) throws SQLException {
        this.inner.updateString(n, string);
    }

    @Override
    public void updateTime(String string, Time time) throws SQLException {
        this.inner.updateTime(string, time);
    }

    @Override
    public void updateTime(int n, Time time) throws SQLException {
        this.inner.updateTime(n, time);
    }

    @Override
    public void updateTimestamp(String string, Timestamp timestamp) throws SQLException {
        this.inner.updateTimestamp(string, timestamp);
    }

    @Override
    public void updateTimestamp(int n, Timestamp timestamp) throws SQLException {
        this.inner.updateTimestamp(n, timestamp);
    }

    @Override
    public boolean wasNull() throws SQLException {
        return this.inner.wasNull();
    }

    public Object getObject(int n, Class clazz) throws SQLException {
        return this.inner.getObject(n, clazz);
    }

    @Override
    public Object getObject(String string) throws SQLException {
        return this.inner.getObject(string);
    }

    public Object getObject(String string, Class clazz) throws SQLException {
        return this.inner.getObject(string, clazz);
    }

    public Object getObject(int n, Map map) throws SQLException {
        return this.inner.getObject(n, map);
    }

    public Object getObject(String string, Map map) throws SQLException {
        return this.inner.getObject(string, map);
    }

    @Override
    public Object getObject(int n) throws SQLException {
        return this.inner.getObject(n);
    }

    @Override
    public boolean getBoolean(String string) throws SQLException {
        return this.inner.getBoolean(string);
    }

    @Override
    public boolean getBoolean(int n) throws SQLException {
        return this.inner.getBoolean(n);
    }

    @Override
    public byte getByte(int n) throws SQLException {
        return this.inner.getByte(n);
    }

    @Override
    public byte getByte(String string) throws SQLException {
        return this.inner.getByte(string);
    }

    @Override
    public short getShort(String string) throws SQLException {
        return this.inner.getShort(string);
    }

    @Override
    public short getShort(int n) throws SQLException {
        return this.inner.getShort(n);
    }

    @Override
    public int getInt(String string) throws SQLException {
        return this.inner.getInt(string);
    }

    @Override
    public int getInt(int n) throws SQLException {
        return this.inner.getInt(n);
    }

    @Override
    public long getLong(String string) throws SQLException {
        return this.inner.getLong(string);
    }

    @Override
    public long getLong(int n) throws SQLException {
        return this.inner.getLong(n);
    }

    @Override
    public float getFloat(int n) throws SQLException {
        return this.inner.getFloat(n);
    }

    @Override
    public float getFloat(String string) throws SQLException {
        return this.inner.getFloat(string);
    }

    @Override
    public double getDouble(int n) throws SQLException {
        return this.inner.getDouble(n);
    }

    @Override
    public double getDouble(String string) throws SQLException {
        return this.inner.getDouble(string);
    }

    @Override
    public byte[] getBytes(String string) throws SQLException {
        return this.inner.getBytes(string);
    }

    @Override
    public byte[] getBytes(int n) throws SQLException {
        return this.inner.getBytes(n);
    }

    @Override
    public Array getArray(int n) throws SQLException {
        return this.inner.getArray(n);
    }

    @Override
    public Array getArray(String string) throws SQLException {
        return this.inner.getArray(string);
    }

    @Override
    public boolean next() throws SQLException {
        return this.inner.next();
    }

    @Override
    public URL getURL(int n) throws SQLException {
        return this.inner.getURL(n);
    }

    @Override
    public URL getURL(String string) throws SQLException {
        return this.inner.getURL(string);
    }

    @Override
    public void close() throws SQLException {
        this.inner.close();
    }

    @Override
    public int getType() throws SQLException {
        return this.inner.getType();
    }

    @Override
    public boolean previous() throws SQLException {
        return this.inner.previous();
    }

    @Override
    public Ref getRef(String string) throws SQLException {
        return this.inner.getRef(string);
    }

    @Override
    public Ref getRef(int n) throws SQLException {
        return this.inner.getRef(n);
    }

    @Override
    public String getString(int n) throws SQLException {
        return this.inner.getString(n);
    }

    @Override
    public String getString(String string) throws SQLException {
        return this.inner.getString(string);
    }

    @Override
    public Date getDate(int n, Calendar calendar) throws SQLException {
        return this.inner.getDate(n, calendar);
    }

    @Override
    public Date getDate(String string, Calendar calendar) throws SQLException {
        return this.inner.getDate(string, calendar);
    }

    @Override
    public Date getDate(String string) throws SQLException {
        return this.inner.getDate(string);
    }

    @Override
    public Date getDate(int n) throws SQLException {
        return this.inner.getDate(n);
    }

    @Override
    public Time getTime(int n) throws SQLException {
        return this.inner.getTime(n);
    }

    @Override
    public Time getTime(String string, Calendar calendar) throws SQLException {
        return this.inner.getTime(string, calendar);
    }

    @Override
    public Time getTime(String string) throws SQLException {
        return this.inner.getTime(string);
    }

    @Override
    public Time getTime(int n, Calendar calendar) throws SQLException {
        return this.inner.getTime(n, calendar);
    }

    @Override
    public Timestamp getTimestamp(int n) throws SQLException {
        return this.inner.getTimestamp(n);
    }

    @Override
    public Timestamp getTimestamp(String string) throws SQLException {
        return this.inner.getTimestamp(string);
    }

    @Override
    public Timestamp getTimestamp(String string, Calendar calendar) throws SQLException {
        return this.inner.getTimestamp(string, calendar);
    }

    @Override
    public Timestamp getTimestamp(int n, Calendar calendar) throws SQLException {
        return this.inner.getTimestamp(n, calendar);
    }

    public boolean isWrapperFor(Class clazz) throws SQLException {
        return this.inner.isWrapperFor(clazz);
    }

    public Object unwrap(Class clazz) throws SQLException {
        return this.inner.unwrap(clazz);
    }
}

