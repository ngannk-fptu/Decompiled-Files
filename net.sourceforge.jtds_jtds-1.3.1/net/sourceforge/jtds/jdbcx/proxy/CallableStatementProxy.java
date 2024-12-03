/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbcx.proxy;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import net.sourceforge.jtds.jdbc.JtdsCallableStatement;
import net.sourceforge.jtds.jdbcx.proxy.ConnectionProxy;
import net.sourceforge.jtds.jdbcx.proxy.PreparedStatementProxy;

public class CallableStatementProxy
extends PreparedStatementProxy
implements CallableStatement {
    private JtdsCallableStatement _callableStatement;

    CallableStatementProxy(ConnectionProxy connection, JtdsCallableStatement callableStatement) {
        super(connection, callableStatement);
        this._callableStatement = callableStatement;
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.registerOutParameter(parameterIndex, sqlType);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.registerOutParameter(parameterIndex, sqlType, scale);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public boolean wasNull() throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.wasNull();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return false;
        }
    }

    @Override
    public String getString(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getString(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public boolean getBoolean(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getBoolean(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return false;
        }
    }

    @Override
    public byte getByte(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getByte(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return -128;
        }
    }

    @Override
    public short getShort(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getShort(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Short.MIN_VALUE;
        }
    }

    @Override
    public int getInt(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getInt(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public long getLong(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getLong(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Long.MIN_VALUE;
        }
    }

    @Override
    public float getFloat(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getFloat(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Float.MIN_VALUE;
        }
    }

    @Override
    public double getDouble(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getDouble(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Double.MIN_VALUE;
        }
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getBigDecimal(parameterIndex, scale);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public byte[] getBytes(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getBytes(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Date getDate(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getDate(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Time getTime(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getTime(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getTimestamp(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Object getObject(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getObject(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getBigDecimal(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    public Object getObject(int parameterIndex, Map map) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getObject(parameterIndex, map);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Ref getRef(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getRef(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Blob getBlob(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getBlob(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Clob getClob(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getClob(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Array getArray(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getArray(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getDate(parameterIndex, cal);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getTime(parameterIndex, cal);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getTimestamp(parameterIndex, cal);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.registerOutParameter(parameterIndex, sqlType, typeName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.registerOutParameter(parameterName, sqlType);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.registerOutParameter(parameterName, sqlType, scale);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.registerOutParameter(parameterName, sqlType, typeName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public URL getURL(int parameterIndex) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getURL(parameterIndex);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public void setURL(String parameterName, URL val) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setURL(parameterName, val);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setNull(String parameterName, int sqlType) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setNull(parameterName, sqlType);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setBoolean(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setByte(String parameterName, byte x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setByte(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setShort(String parameterName, short x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setShort(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setInt(String parameterName, int x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setInt(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setLong(String parameterName, long x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setLong(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setFloat(String parameterName, float x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setFloat(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setDouble(String parameterName, double x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setDouble(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setBigDecimal(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setString(String parameterName, String x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setString(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setBytes(String parameterName, byte[] x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setBytes(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setDate(String parameterName, Date x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setDate(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setTime(String parameterName, Time x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setTime(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setTimestamp(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setAsciiStream(parameterName, x, length);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setBinaryStream(parameterName, x, length);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setObject(parameterName, x, targetSqlType, scale);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setObject(parameterName, x, targetSqlType);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setObject(String parameterName, Object x) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setObject(parameterName, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setCharacterStream(String parameterName, Reader x, int length) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setCharacterStream(parameterName, x, length);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setDate(parameterName, x, cal);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setTime(parameterName, x, cal);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setTimestamp(parameterName, x, cal);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        this.validateConnection();
        try {
            this._callableStatement.setNull(parameterName, sqlType, typeName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public String getString(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getString(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public boolean getBoolean(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getBoolean(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return false;
        }
    }

    @Override
    public byte getByte(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getByte(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return -128;
        }
    }

    @Override
    public short getShort(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getShort(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Short.MIN_VALUE;
        }
    }

    @Override
    public int getInt(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getInt(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public long getLong(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getLong(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Long.MIN_VALUE;
        }
    }

    @Override
    public float getFloat(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getFloat(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Float.MIN_VALUE;
        }
    }

    @Override
    public double getDouble(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getDouble(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Double.MIN_VALUE;
        }
    }

    @Override
    public byte[] getBytes(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getBytes(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Date getDate(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getDate(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Time getTime(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getTime(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getTimestamp(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Object getObject(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getObject(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getBigDecimal(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    public Object getObject(String parameterName, Map map) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getObject(parameterName, map);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Ref getRef(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getRef(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Blob getBlob(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getBlob(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Clob getClob(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getClob(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Array getArray(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getArray(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getDate(parameterName, cal);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getTime(parameterName, cal);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getTimestamp(parameterName, cal);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public URL getURL(String parameterName) throws SQLException {
        this.validateConnection();
        try {
            return this._callableStatement.getURL(parameterName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public Reader getCharacterStream(String parameterName) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public Reader getNCharacterStream(String parameterName) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public NClob getNClob(int parameterIndex) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public NClob getNClob(String parameterName) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public String getNString(int parameterIndex) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public String getNString(String parameterName) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public RowId getRowId(int parameterIndex) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public RowId getRowId(String parameterName) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setBlob(String parameterName, Blob x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setClob(String parameterName, Clob x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setClob(String parameterName, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNClob(String parameterName, NClob value) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNClob(String parameterName, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNString(String parameterName, String value) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setRowId(String parameterName, RowId x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public boolean isClosed() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public boolean isPoolable() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public boolean isWrapperFor(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public Object unwrap(Class arg0) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public void closeOnCompletion() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    @Override
    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        throw new AbstractMethodError();
    }
}

