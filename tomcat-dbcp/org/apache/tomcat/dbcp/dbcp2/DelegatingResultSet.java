/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2;

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
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import org.apache.tomcat.dbcp.dbcp2.AbandonedTrace;
import org.apache.tomcat.dbcp.dbcp2.DelegatingConnection;
import org.apache.tomcat.dbcp.dbcp2.DelegatingStatement;
import org.apache.tomcat.dbcp.dbcp2.Jdbc41Bridge;

public final class DelegatingResultSet
extends AbandonedTrace
implements ResultSet {
    private final ResultSet resultSet;
    private Statement statement;
    private Connection connection;

    public static ResultSet wrapResultSet(Connection connection, ResultSet resultSet) {
        if (null == resultSet) {
            return null;
        }
        return new DelegatingResultSet(connection, resultSet);
    }

    public static ResultSet wrapResultSet(Statement statement, ResultSet resultSet) {
        if (null == resultSet) {
            return null;
        }
        return new DelegatingResultSet(statement, resultSet);
    }

    private DelegatingResultSet(Connection connection, ResultSet resultSet) {
        super((AbandonedTrace)((Object)connection));
        this.connection = connection;
        this.resultSet = resultSet;
    }

    private DelegatingResultSet(Statement statement, ResultSet resultSet) {
        super((AbandonedTrace)((Object)statement));
        this.statement = statement;
        this.resultSet = resultSet;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        try {
            return this.resultSet.absolute(row);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public void afterLast() throws SQLException {
        try {
            this.resultSet.afterLast();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void beforeFirst() throws SQLException {
        try {
            this.resultSet.beforeFirst();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        try {
            this.resultSet.cancelRowUpdates();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        try {
            this.resultSet.clearWarnings();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void close() throws SQLException {
        try {
            if (this.statement != null) {
                this.removeThisTrace(this.statement);
                this.statement = null;
            }
            if (this.connection != null) {
                this.removeThisTrace(this.connection);
                this.connection = null;
            }
            this.resultSet.close();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void deleteRow() throws SQLException {
        try {
            this.resultSet.deleteRow();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public int findColumn(String columnName) throws SQLException {
        try {
            return this.resultSet.findColumn(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public boolean first() throws SQLException {
        try {
            return this.resultSet.first();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public Array getArray(int i) throws SQLException {
        try {
            return this.resultSet.getArray(i);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Array getArray(String colName) throws SQLException {
        try {
            return this.resultSet.getArray(colName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getAsciiStream(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public InputStream getAsciiStream(String columnName) throws SQLException {
        try {
            return this.resultSet.getAsciiStream(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getBigDecimal(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    @Deprecated
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        try {
            return this.resultSet.getBigDecimal(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        try {
            return this.resultSet.getBigDecimal(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    @Deprecated
    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        try {
            return this.resultSet.getBigDecimal(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getBinaryStream(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public InputStream getBinaryStream(String columnName) throws SQLException {
        try {
            return this.resultSet.getBinaryStream(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Blob getBlob(int i) throws SQLException {
        try {
            return this.resultSet.getBlob(i);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Blob getBlob(String colName) throws SQLException {
        try {
            return this.resultSet.getBlob(colName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getBoolean(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean getBoolean(String columnName) throws SQLException {
        try {
            return this.resultSet.getBoolean(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getByte(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public byte getByte(String columnName) throws SQLException {
        try {
            return this.resultSet.getByte(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getBytes(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public byte[] getBytes(String columnName) throws SQLException {
        try {
            return this.resultSet.getBytes(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getCharacterStream(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Reader getCharacterStream(String columnName) throws SQLException {
        try {
            return this.resultSet.getCharacterStream(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Clob getClob(int i) throws SQLException {
        try {
            return this.resultSet.getClob(i);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Clob getClob(String colName) throws SQLException {
        try {
            return this.resultSet.getClob(colName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public int getConcurrency() throws SQLException {
        try {
            return this.resultSet.getConcurrency();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public String getCursorName() throws SQLException {
        try {
            return this.resultSet.getCursorName();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getDate(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        try {
            return this.resultSet.getDate(columnIndex, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Date getDate(String columnName) throws SQLException {
        try {
            return this.resultSet.getDate(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Date getDate(String columnName, Calendar cal) throws SQLException {
        try {
            return this.resultSet.getDate(columnName, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    public ResultSet getDelegate() {
        return this.resultSet;
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getDouble(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0.0;
        }
    }

    @Override
    public double getDouble(String columnName) throws SQLException {
        try {
            return this.resultSet.getDouble(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0.0;
        }
    }

    @Override
    public int getFetchDirection() throws SQLException {
        try {
            return this.resultSet.getFetchDirection();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getFetchSize() throws SQLException {
        try {
            return this.resultSet.getFetchSize();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getFloat(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0.0f;
        }
    }

    @Override
    public float getFloat(String columnName) throws SQLException {
        try {
            return this.resultSet.getFloat(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0.0f;
        }
    }

    @Override
    public int getHoldability() throws SQLException {
        try {
            return this.resultSet.getHoldability();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    public ResultSet getInnermostDelegate() {
        ResultSet r = this.resultSet;
        while (r instanceof DelegatingResultSet) {
            if (this != (r = ((DelegatingResultSet)r).getDelegate())) continue;
            return null;
        }
        return r;
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getInt(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getInt(String columnName) throws SQLException {
        try {
            return this.resultSet.getInt(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getLong(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0L;
        }
    }

    @Override
    public long getLong(String columnName) throws SQLException {
        try {
            return this.resultSet.getLong(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0L;
        }
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return this.resultSet.getMetaData();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getNCharacterStream(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        try {
            return this.resultSet.getNCharacterStream(columnLabel);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getNClob(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        try {
            return this.resultSet.getNClob(columnLabel);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getNString(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        try {
            return this.resultSet.getNString(columnLabel);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getObject(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        try {
            return Jdbc41Bridge.getObject(this.resultSet, columnIndex, type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        try {
            return this.resultSet.getObject(i, map);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Object getObject(String columnName) throws SQLException {
        try {
            return this.resultSet.getObject(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        try {
            return Jdbc41Bridge.getObject(this.resultSet, columnLabel, type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Object getObject(String colName, Map<String, Class<?>> map) throws SQLException {
        try {
            return this.resultSet.getObject(colName, map);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Ref getRef(int i) throws SQLException {
        try {
            return this.resultSet.getRef(i);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Ref getRef(String colName) throws SQLException {
        try {
            return this.resultSet.getRef(colName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public int getRow() throws SQLException {
        try {
            return this.resultSet.getRow();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getRowId(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        try {
            return this.resultSet.getRowId(columnLabel);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getShort(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public short getShort(String columnName) throws SQLException {
        try {
            return this.resultSet.getShort(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getSQLXML(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        try {
            return this.resultSet.getSQLXML(columnLabel);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Statement getStatement() throws SQLException {
        return this.statement;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getString(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public String getString(String columnName) throws SQLException {
        try {
            return this.resultSet.getString(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getTime(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        try {
            return this.resultSet.getTime(columnIndex, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Time getTime(String columnName) throws SQLException {
        try {
            return this.resultSet.getTime(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Time getTime(String columnName, Calendar cal) throws SQLException {
        try {
            return this.resultSet.getTime(columnName, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getTimestamp(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        try {
            return this.resultSet.getTimestamp(columnIndex, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(String columnName) throws SQLException {
        try {
            return this.resultSet.getTimestamp(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        try {
            return this.resultSet.getTimestamp(columnName, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public int getType() throws SQLException {
        try {
            return this.resultSet.getType();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    @Deprecated
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getUnicodeStream(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    @Deprecated
    public InputStream getUnicodeStream(String columnName) throws SQLException {
        try {
            return this.resultSet.getUnicodeStream(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        try {
            return this.resultSet.getURL(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public URL getURL(String columnName) throws SQLException {
        try {
            return this.resultSet.getURL(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        try {
            return this.resultSet.getWarnings();
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    protected void handleException(SQLException e) throws SQLException {
        if (this.statement instanceof DelegatingStatement) {
            ((DelegatingStatement)this.statement).handleException(e);
        } else if (this.connection instanceof DelegatingConnection) {
            ((DelegatingConnection)this.connection).handleException(e);
        } else {
            throw e;
        }
    }

    @Override
    public void insertRow() throws SQLException {
        try {
            this.resultSet.insertRow();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        try {
            return this.resultSet.isAfterLast();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        try {
            return this.resultSet.isBeforeFirst();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        try {
            return this.resultSet.isClosed();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean isFirst() throws SQLException {
        try {
            return this.resultSet.isFirst();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean isLast() throws SQLException {
        try {
            return this.resultSet.isLast();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return true;
        }
        if (iface.isAssignableFrom(this.resultSet.getClass())) {
            return true;
        }
        return this.resultSet.isWrapperFor(iface);
    }

    @Override
    public boolean last() throws SQLException {
        try {
            return this.resultSet.last();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        try {
            this.resultSet.moveToCurrentRow();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        try {
            this.resultSet.moveToInsertRow();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public boolean next() throws SQLException {
        try {
            return this.resultSet.next();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean previous() throws SQLException {
        try {
            return this.resultSet.previous();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public void refreshRow() throws SQLException {
        try {
            this.resultSet.refreshRow();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        try {
            return this.resultSet.relative(rows);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        try {
            return this.resultSet.rowDeleted();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean rowInserted() throws SQLException {
        try {
            return this.resultSet.rowInserted();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        try {
            return this.resultSet.rowUpdated();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        try {
            this.resultSet.setFetchDirection(direction);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        try {
            this.resultSet.setFetchSize(rows);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    public synchronized String toString() {
        return super.toString() + "[resultSet=" + this.resultSet + ", statement=" + this.statement + ", connection=" + this.connection + "]";
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isAssignableFrom(this.getClass())) {
            return iface.cast(this);
        }
        if (iface.isAssignableFrom(this.resultSet.getClass())) {
            return iface.cast(this.resultSet);
        }
        return this.resultSet.unwrap(iface);
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        try {
            this.resultSet.updateArray(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateArray(String columnName, Array x) throws SQLException {
        try {
            this.resultSet.updateArray(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream inputStream) throws SQLException {
        try {
            this.resultSet.updateAsciiStream(columnIndex, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        try {
            this.resultSet.updateAsciiStream(columnIndex, x, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream inputStream, long length) throws SQLException {
        try {
            this.resultSet.updateAsciiStream(columnIndex, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream inputStream) throws SQLException {
        try {
            this.resultSet.updateAsciiStream(columnLabel, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
        try {
            this.resultSet.updateAsciiStream(columnName, x, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream inputStream, long length) throws SQLException {
        try {
            this.resultSet.updateAsciiStream(columnLabel, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        try {
            this.resultSet.updateBigDecimal(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        try {
            this.resultSet.updateBigDecimal(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream inputStream) throws SQLException {
        try {
            this.resultSet.updateBinaryStream(columnIndex, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        try {
            this.resultSet.updateBinaryStream(columnIndex, x, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream inputStream, long length) throws SQLException {
        try {
            this.resultSet.updateBinaryStream(columnIndex, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream inputStream) throws SQLException {
        try {
            this.resultSet.updateBinaryStream(columnLabel, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
        try {
            this.resultSet.updateBinaryStream(columnName, x, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream inputStream, long length) throws SQLException {
        try {
            this.resultSet.updateBinaryStream(columnLabel, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        try {
            this.resultSet.updateBlob(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        try {
            this.resultSet.updateBlob(columnIndex, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        try {
            this.resultSet.updateBlob(columnIndex, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBlob(String columnName, Blob x) throws SQLException {
        try {
            this.resultSet.updateBlob(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        try {
            this.resultSet.updateBlob(columnLabel, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        try {
            this.resultSet.updateBlob(columnLabel, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        try {
            this.resultSet.updateBoolean(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBoolean(String columnName, boolean x) throws SQLException {
        try {
            this.resultSet.updateBoolean(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        try {
            this.resultSet.updateByte(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateByte(String columnName, byte x) throws SQLException {
        try {
            this.resultSet.updateByte(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        try {
            this.resultSet.updateBytes(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateBytes(String columnName, byte[] x) throws SQLException {
        try {
            this.resultSet.updateBytes(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader reader) throws SQLException {
        try {
            this.resultSet.updateCharacterStream(columnIndex, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        try {
            this.resultSet.updateCharacterStream(columnIndex, x, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader reader, long length) throws SQLException {
        try {
            this.resultSet.updateCharacterStream(columnIndex, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
        try {
            this.resultSet.updateCharacterStream(columnLabel, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
        try {
            this.resultSet.updateCharacterStream(columnName, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        try {
            this.resultSet.updateCharacterStream(columnLabel, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        try {
            this.resultSet.updateClob(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        try {
            this.resultSet.updateClob(columnIndex, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        try {
            this.resultSet.updateClob(columnIndex, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateClob(String columnName, Clob x) throws SQLException {
        try {
            this.resultSet.updateClob(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        try {
            this.resultSet.updateClob(columnLabel, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        try {
            this.resultSet.updateClob(columnLabel, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        try {
            this.resultSet.updateDate(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateDate(String columnName, Date x) throws SQLException {
        try {
            this.resultSet.updateDate(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        try {
            this.resultSet.updateDouble(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateDouble(String columnName, double x) throws SQLException {
        try {
            this.resultSet.updateDouble(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        try {
            this.resultSet.updateFloat(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateFloat(String columnName, float x) throws SQLException {
        try {
            this.resultSet.updateFloat(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        try {
            this.resultSet.updateInt(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateInt(String columnName, int x) throws SQLException {
        try {
            this.resultSet.updateInt(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        try {
            this.resultSet.updateLong(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateLong(String columnName, long x) throws SQLException {
        try {
            this.resultSet.updateLong(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader reader) throws SQLException {
        try {
            this.resultSet.updateNCharacterStream(columnIndex, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader reader, long length) throws SQLException {
        try {
            this.resultSet.updateNCharacterStream(columnIndex, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
        try {
            this.resultSet.updateNCharacterStream(columnLabel, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
        try {
            this.resultSet.updateNCharacterStream(columnLabel, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNClob(int columnIndex, NClob value) throws SQLException {
        try {
            this.resultSet.updateNClob(columnIndex, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        try {
            this.resultSet.updateNClob(columnIndex, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        try {
            this.resultSet.updateNClob(columnIndex, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNClob(String columnLabel, NClob value) throws SQLException {
        try {
            this.resultSet.updateNClob(columnLabel, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        try {
            this.resultSet.updateNClob(columnLabel, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        try {
            this.resultSet.updateNClob(columnLabel, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNString(int columnIndex, String value) throws SQLException {
        try {
            this.resultSet.updateNString(columnIndex, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNString(String columnLabel, String value) throws SQLException {
        try {
            this.resultSet.updateNString(columnLabel, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        try {
            this.resultSet.updateNull(columnIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateNull(String columnName) throws SQLException {
        try {
            this.resultSet.updateNull(columnName);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        try {
            this.resultSet.updateObject(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        try {
            this.resultSet.updateObject(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType) throws SQLException {
        try {
            this.resultSet.updateObject(columnIndex, x, targetSqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateObject(int columnIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        try {
            this.resultSet.updateObject(columnIndex, x, targetSqlType, scaleOrLength);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateObject(String columnName, Object x) throws SQLException {
        try {
            this.resultSet.updateObject(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateObject(String columnName, Object x, int scale) throws SQLException {
        try {
            this.resultSet.updateObject(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType) throws SQLException {
        try {
            this.resultSet.updateObject(columnLabel, x, targetSqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateObject(String columnLabel, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        try {
            this.resultSet.updateObject(columnLabel, x, targetSqlType, scaleOrLength);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        try {
            this.resultSet.updateRef(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateRef(String columnName, Ref x) throws SQLException {
        try {
            this.resultSet.updateRef(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateRow() throws SQLException {
        try {
            this.resultSet.updateRow();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateRowId(int columnIndex, RowId value) throws SQLException {
        try {
            this.resultSet.updateRowId(columnIndex, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateRowId(String columnLabel, RowId value) throws SQLException {
        try {
            this.resultSet.updateRowId(columnLabel, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        try {
            this.resultSet.updateShort(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateShort(String columnName, short x) throws SQLException {
        try {
            this.resultSet.updateShort(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML value) throws SQLException {
        try {
            this.resultSet.updateSQLXML(columnIndex, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML value) throws SQLException {
        try {
            this.resultSet.updateSQLXML(columnLabel, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        try {
            this.resultSet.updateString(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateString(String columnName, String x) throws SQLException {
        try {
            this.resultSet.updateString(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        try {
            this.resultSet.updateTime(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateTime(String columnName, Time x) throws SQLException {
        try {
            this.resultSet.updateTime(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        try {
            this.resultSet.updateTimestamp(columnIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        try {
            this.resultSet.updateTimestamp(columnName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public boolean wasNull() throws SQLException {
        try {
            return this.resultSet.wasNull();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }
}

