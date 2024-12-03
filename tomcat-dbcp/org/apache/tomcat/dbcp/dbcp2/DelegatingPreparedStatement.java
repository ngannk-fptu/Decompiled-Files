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
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.tomcat.dbcp.dbcp2.AbandonedTrace;
import org.apache.tomcat.dbcp.dbcp2.DelegatingConnection;
import org.apache.tomcat.dbcp.dbcp2.DelegatingResultSet;
import org.apache.tomcat.dbcp.dbcp2.DelegatingStatement;
import org.apache.tomcat.dbcp.dbcp2.SQLExceptionList;

public class DelegatingPreparedStatement
extends DelegatingStatement
implements PreparedStatement {
    public DelegatingPreparedStatement(DelegatingConnection<?> connection, PreparedStatement statement) {
        super(connection, statement);
    }

    @Override
    public void addBatch() throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().addBatch();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void clearParameters() throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().clearParameters();
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public boolean execute() throws SQLException {
        this.checkOpen();
        if (this.getConnectionInternal() != null) {
            this.getConnectionInternal().setLastUsed();
        }
        try {
            return this.getDelegatePreparedStatement().execute();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public long executeLargeUpdate() throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegatePreparedStatement().executeLargeUpdate();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0L;
        }
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        this.checkOpen();
        if (this.getConnectionInternal() != null) {
            this.getConnectionInternal().setLastUsed();
        }
        try {
            return DelegatingResultSet.wrapResultSet(this, this.getDelegatePreparedStatement().executeQuery());
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public int executeUpdate() throws SQLException {
        this.checkOpen();
        if (this.getConnectionInternal() != null) {
            this.getConnectionInternal().setLastUsed();
        }
        try {
            return this.getDelegatePreparedStatement().executeUpdate();
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    private PreparedStatement getDelegatePreparedStatement() {
        return (PreparedStatement)this.getDelegate();
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegatePreparedStatement().getMetaData();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegatePreparedStatement().getParameterMetaData();
        }
        catch (SQLException e) {
            this.handleException(e);
            throw new AssertionError();
        }
    }

    @Override
    public void setArray(int i, Array x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setArray(i, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setAsciiStream(parameterIndex, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setAsciiStream(parameterIndex, x, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setAsciiStream(parameterIndex, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBigDecimal(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBinaryStream(parameterIndex, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBinaryStream(parameterIndex, x, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBinaryStream(parameterIndex, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBlob(int i, Blob x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBlob(i, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBlob(parameterIndex, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBlob(parameterIndex, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBoolean(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setByte(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setBytes(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setCharacterStream(parameterIndex, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setCharacterStream(parameterIndex, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setCharacterStream(parameterIndex, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setClob(int i, Clob x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setClob(i, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setClob(parameterIndex, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setClob(parameterIndex, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setDate(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setDate(parameterIndex, x, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setDouble(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setFloat(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setInt(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setLong(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNCharacterStream(parameterIndex, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNCharacterStream(parameterIndex, value, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNClob(parameterIndex, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNClob(parameterIndex, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNClob(parameterIndex, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNString(parameterIndex, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNull(parameterIndex, sqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setNull(paramIndex, sqlType, typeName);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setObject(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setObject(parameterIndex, x, targetSqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setObject(parameterIndex, x, targetSqlType, scale);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setObject(parameterIndex, x, targetSqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setObject(parameterIndex, x, targetSqlType, scaleOrLength);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setRef(int i, Ref x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setRef(i, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setRowId(int parameterIndex, RowId value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setRowId(parameterIndex, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setShort(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setSQLXML(parameterIndex, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setString(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setTime(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setTime(parameterIndex, x, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setTimestamp(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setTimestamp(parameterIndex, x, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    @Deprecated
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setUnicodeStream(parameterIndex, x, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegatePreparedStatement().setURL(parameterIndex, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public synchronized String toString() {
        Statement statement = this.getDelegate();
        return statement == null ? "NULL" : statement.toString();
    }

    protected void prepareToReturn() throws SQLException {
        this.setClosedInternal(true);
        this.removeThisTrace(this.getConnectionInternal());
        List<AbandonedTrace> traceList = this.getTrace();
        if (traceList != null) {
            ArrayList thrownList = new ArrayList();
            traceList.forEach(trace -> trace.close(thrownList::add));
            this.clearTrace();
            if (!thrownList.isEmpty()) {
                throw new SQLExceptionList(thrownList);
            }
        }
        super.passivate();
    }
}

