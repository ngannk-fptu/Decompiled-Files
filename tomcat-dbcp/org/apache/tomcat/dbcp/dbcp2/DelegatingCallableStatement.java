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
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import org.apache.tomcat.dbcp.dbcp2.DelegatingConnection;
import org.apache.tomcat.dbcp.dbcp2.DelegatingPreparedStatement;

public class DelegatingCallableStatement
extends DelegatingPreparedStatement
implements CallableStatement {
    public DelegatingCallableStatement(DelegatingConnection<?> connection, CallableStatement statement) {
        super(connection, statement);
    }

    @Override
    public Array getArray(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getArray(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Array getArray(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getArray(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBigDecimal(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    @Deprecated
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBigDecimal(parameterIndex, scale);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBigDecimal(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Blob getBlob(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBlob(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Blob getBlob(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBlob(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public boolean getBoolean(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBoolean(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public boolean getBoolean(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBoolean(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }

    @Override
    public byte getByte(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getByte(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public byte getByte(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getByte(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public byte[] getBytes(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBytes(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public byte[] getBytes(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getBytes(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getCharacterStream(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Reader getCharacterStream(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getCharacterStream(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Clob getClob(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getClob(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Clob getClob(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getClob(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Date getDate(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getDate(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getDate(parameterIndex, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Date getDate(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getDate(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getDate(parameterName, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    private CallableStatement getDelegateCallableStatement() {
        return (CallableStatement)this.getDelegate();
    }

    @Override
    public double getDouble(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getDouble(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0.0;
        }
    }

    @Override
    public double getDouble(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getDouble(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0.0;
        }
    }

    @Override
    public float getFloat(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getFloat(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0.0f;
        }
    }

    @Override
    public float getFloat(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getFloat(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0.0f;
        }
    }

    @Override
    public int getInt(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getInt(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public int getInt(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getInt(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public long getLong(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getLong(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0L;
        }
    }

    @Override
    public long getLong(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getLong(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0L;
        }
    }

    @Override
    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getNCharacterStream(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Reader getNCharacterStream(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getNCharacterStream(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public NClob getNClob(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getNClob(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public NClob getNClob(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getNClob(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public String getNString(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getNString(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public String getNString(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getNString(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Object getObject(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getObject(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getObject(parameterIndex, type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Object getObject(int i, Map<String, Class<?>> map) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getObject(i, map);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Object getObject(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getObject(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getObject(parameterName, type);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getObject(parameterName, map);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Ref getRef(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getRef(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Ref getRef(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getRef(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public RowId getRowId(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getRowId(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public RowId getRowId(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getRowId(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public short getShort(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getShort(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public short getShort(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getShort(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return 0;
        }
    }

    @Override
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getSQLXML(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getSQLXML(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public String getString(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getString(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public String getString(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getString(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Time getTime(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTime(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTime(parameterIndex, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Time getTime(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTime(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTime(parameterName, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTimestamp(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTimestamp(parameterIndex, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTimestamp(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getTimestamp(parameterName, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public URL getURL(int parameterIndex) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getURL(parameterIndex);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public URL getURL(String parameterName) throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().getURL(parameterName);
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterIndex, sqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterIndex, sqlType, scale);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void registerOutParameter(int paramIndex, int sqlType, String typeName) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(paramIndex, sqlType, typeName);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterIndex, sqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterIndex, sqlType, scale);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterIndex, sqlType, typeName);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterName, sqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterName, sqlType, scale);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterName, sqlType, typeName);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterName, sqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType, int scale) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterName, sqlType, scale);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, SQLType sqlType, String typeName) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().registerOutParameter(parameterName, sqlType, typeName);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setAsciiStream(parameterName, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setAsciiStream(parameterName, x, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream inputStream, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setAsciiStream(parameterName, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBigDecimal(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBinaryStream(parameterName, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBinaryStream(parameterName, x, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream inputStream, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBinaryStream(parameterName, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBlob(String parameterName, Blob blob) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBlob(parameterName, blob);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBlob(parameterName, inputStream);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBlob(parameterName, inputStream, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBoolean(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setByte(String parameterName, byte x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setByte(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setBytes(String parameterName, byte[] x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setBytes(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setCharacterStream(parameterName, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        this.checkOpen();
        this.getDelegateCallableStatement().setCharacterStream(parameterName, reader, length);
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setCharacterStream(parameterName, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setClob(String parameterName, Clob clob) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setClob(parameterName, clob);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setClob(String parameterName, Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setClob(parameterName, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setClob(parameterName, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setDate(String parameterName, Date x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setDate(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setDate(parameterName, x, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setDouble(String parameterName, double x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setDouble(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setFloat(String parameterName, float x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setFloat(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setInt(String parameterName, int x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setInt(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setLong(String parameterName, long x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setLong(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNCharacterStream(parameterName, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNCharacterStream(parameterName, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNClob(String parameterName, NClob value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNClob(parameterName, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNClob(String parameterName, Reader reader) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNClob(parameterName, reader);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNClob(parameterName, reader, length);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNString(String parameterName, String value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNString(parameterName, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNull(String parameterName, int sqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNull(parameterName, sqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setNull(parameterName, sqlType, typeName);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setObject(String parameterName, Object x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setObject(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setObject(parameterName, x, targetSqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setObject(parameterName, x, targetSqlType, scale);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setObject(String parameterName, Object x, SQLType targetSqlType) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setObject(parameterName, x, targetSqlType);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setObject(parameterName, x, targetSqlType, scaleOrLength);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setRowId(String parameterName, RowId value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setRowId(parameterName, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setShort(String parameterName, short x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setShort(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setSQLXML(String parameterName, SQLXML value) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setSQLXML(parameterName, value);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setString(String parameterName, String x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setString(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setTime(String parameterName, Time x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setTime(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setTime(parameterName, x, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setTimestamp(parameterName, x);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setTimestamp(parameterName, x, cal);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setURL(String parameterName, URL val) throws SQLException {
        this.checkOpen();
        try {
            this.getDelegateCallableStatement().setURL(parameterName, val);
        }
        catch (SQLException e) {
            this.handleException(e);
        }
    }

    @Override
    public boolean wasNull() throws SQLException {
        this.checkOpen();
        try {
            return this.getDelegateCallableStatement().wasNull();
        }
        catch (SQLException e) {
            this.handleException(e);
            return false;
        }
    }
}

