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
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import net.sourceforge.jtds.jdbc.JtdsPreparedStatement;
import net.sourceforge.jtds.jdbcx.proxy.ConnectionProxy;
import net.sourceforge.jtds.jdbcx.proxy.StatementProxy;

public class PreparedStatementProxy
extends StatementProxy
implements PreparedStatement {
    private JtdsPreparedStatement _preparedStatement;

    PreparedStatementProxy(ConnectionProxy connection, JtdsPreparedStatement preparedStatement) {
        super(connection, preparedStatement);
        this._preparedStatement = preparedStatement;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        this.validateConnection();
        try {
            return this._preparedStatement.executeQuery();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public int executeUpdate() throws SQLException {
        this.validateConnection();
        try {
            return this._preparedStatement.executeUpdate();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return Integer.MIN_VALUE;
        }
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setNull(parameterIndex, sqlType);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setBoolean(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setByte(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setShort(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setInt(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setLong(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setFloat(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setDouble(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setBigDecimal(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setString(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setBytes(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setDate(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setTime(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setTimestamp(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setAsciiStream(parameterIndex, x, length);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setUnicodeStream(parameterIndex, x, length);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setBinaryStream(parameterIndex, x, length);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void clearParameters() throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.clearParameters();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setObject(parameterIndex, x, targetSqlType, scale);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setObject(parameterIndex, x, targetSqlType);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setObject(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public boolean execute() throws SQLException {
        this.validateConnection();
        try {
            return this._preparedStatement.execute();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return false;
        }
    }

    @Override
    public void addBatch() throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.addBatch();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader x, int length) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setCharacterStream(parameterIndex, x, length);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setRef(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setBlob(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setClob(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setArray(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        this.validateConnection();
        try {
            return this._preparedStatement.getMetaData();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setDate(parameterIndex, x, cal);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setTime(parameterIndex, x, cal);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setTimestamp(parameterIndex, x, cal);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setNull(parameterIndex, sqlType, typeName);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        this.validateConnection();
        try {
            this._preparedStatement.setURL(parameterIndex, x);
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
        }
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        this.validateConnection();
        try {
            return this._preparedStatement.getParameterMetaData();
        }
        catch (SQLException sqlException) {
            this.processSQLException(sqlException);
            return null;
        }
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
}

