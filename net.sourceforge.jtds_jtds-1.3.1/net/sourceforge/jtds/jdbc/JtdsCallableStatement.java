/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import net.sourceforge.jtds.jdbc.BlobImpl;
import net.sourceforge.jtds.jdbc.ClobImpl;
import net.sourceforge.jtds.jdbc.JtdsConnection;
import net.sourceforge.jtds.jdbc.JtdsPreparedStatement;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbc.ParamInfo;
import net.sourceforge.jtds.jdbc.Support;
import net.sourceforge.jtds.jdbc.UniqueIdentifier;

public class JtdsCallableStatement
extends JtdsPreparedStatement
implements CallableStatement {
    protected boolean paramWasNull;

    JtdsCallableStatement(JtdsConnection connection, String sql, int resultSetType, int concurrency) throws SQLException {
        super(connection, sql, resultSetType, concurrency, false);
    }

    final int findParameter(String name, boolean set) throws SQLException {
        int i;
        this.checkOpen();
        if (!name.startsWith("@")) {
            name = "@" + name;
        }
        for (i = 0; i < this.parameters.length; ++i) {
            if (this.parameters[i].name == null || !this.parameters[i].name.equalsIgnoreCase(name)) continue;
            return i + 1;
        }
        if (set && !name.equalsIgnoreCase("@return_status")) {
            for (i = 0; i < this.parameters.length; ++i) {
                if (this.parameters[i].name != null) continue;
                this.parameters[i].name = name;
                return i + 1;
            }
        }
        throw new SQLException(Messages.get("error.callable.noparam", name), "07000");
    }

    protected Object getOutputValue(int parameterIndex) throws SQLException {
        this.checkOpen();
        ParamInfo parameter = this.getParameter(parameterIndex);
        if (!parameter.isOutput) {
            throw new SQLException(Messages.get("error.callable.notoutput", new Integer(parameterIndex)), "07000");
        }
        Object value = parameter.getOutValue();
        this.paramWasNull = value == null;
        return value;
    }

    @Override
    protected void checkOpen() throws SQLException {
        if (this.isClosed()) {
            throw new SQLException(Messages.get("error.generic.closed", "CallableStatement"), "HY010");
        }
    }

    @Override
    protected SQLException executeMSBatch(int size, int executeSize, ArrayList counts) throws SQLException {
        if (this.parameters.length == 0) {
            return super.executeMSBatch(size, executeSize, counts);
        }
        SQLException sqlEx = null;
        int i = 0;
        while (i < size) {
            Object value = this.batchValues.get(i);
            boolean executeNow = ++i % executeSize == 0 || i == size;
            this.tds.startBatch();
            this.tds.executeSQL(this.sql, this.procName, (ParamInfo[])value, false, 0, -1, -1, executeNow);
            if (!executeNow || (sqlEx = this.tds.getBatchCounts(counts, sqlEx)) == null || counts.size() == i) continue;
            break;
        }
        return sqlEx;
    }

    @Override
    protected SQLException executeSybaseBatch(int size, int executeSize, ArrayList counts) throws SQLException {
        if (this.parameters.length == 0) {
            return super.executeSybaseBatch(size, executeSize, counts);
        }
        SQLException sqlEx = null;
        int i = 0;
        while (i < size) {
            Object value = this.batchValues.get(i);
            this.tds.executeSQL(this.sql, this.procName, (ParamInfo[])value, false, 0, -1, -1, true);
            if ((sqlEx = this.tds.getBatchCounts(counts, sqlEx)) == null || counts.size() == ++i) continue;
            break;
        }
        return sqlEx;
    }

    @Override
    public boolean wasNull() throws SQLException {
        this.checkOpen();
        return this.paramWasNull;
    }

    @Override
    public byte getByte(int parameterIndex) throws SQLException {
        return ((Integer)Support.convert(this, this.getOutputValue(parameterIndex), -6, null)).byteValue();
    }

    @Override
    public double getDouble(int parameterIndex) throws SQLException {
        return (Double)Support.convert(this, this.getOutputValue(parameterIndex), 8, null);
    }

    @Override
    public float getFloat(int parameterIndex) throws SQLException {
        return ((Float)Support.convert(this, this.getOutputValue(parameterIndex), 7, null)).floatValue();
    }

    @Override
    public int getInt(int parameterIndex) throws SQLException {
        return (Integer)Support.convert(this, this.getOutputValue(parameterIndex), 4, null);
    }

    @Override
    public long getLong(int parameterIndex) throws SQLException {
        return (Long)Support.convert(this, this.getOutputValue(parameterIndex), -5, null);
    }

    @Override
    public short getShort(int parameterIndex) throws SQLException {
        return ((Integer)Support.convert(this, this.getOutputValue(parameterIndex), 5, null)).shortValue();
    }

    @Override
    public boolean getBoolean(int parameterIndex) throws SQLException {
        return (Boolean)Support.convert(this, this.getOutputValue(parameterIndex), 16, null);
    }

    @Override
    public byte[] getBytes(int parameterIndex) throws SQLException {
        this.checkOpen();
        return (byte[])Support.convert(this, this.getOutputValue(parameterIndex), -3, this.connection.getCharset());
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        if (sqlType == 3 || sqlType == 2) {
            this.registerOutParameter(parameterIndex, sqlType, 10);
        } else {
            this.registerOutParameter(parameterIndex, sqlType, 0);
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        this.checkOpen();
        if (scale < 0 || scale > this.connection.getMaxPrecision()) {
            throw new SQLException(Messages.get("error.generic.badscale"), "HY092");
        }
        ParamInfo pi = this.getParameter(parameterIndex);
        pi.isOutput = true;
        if ("ERROR".equals(Support.getJdbcTypeName(sqlType))) {
            throw new SQLException(Messages.get("error.generic.badtype", Integer.toString(sqlType)), "HY092");
        }
        pi.jdbcType = sqlType == 2005 ? -1 : (sqlType == 2004 ? -4 : sqlType);
        pi.scale = scale;
    }

    @Override
    public Object getObject(int parameterIndex) throws SQLException {
        Object value = this.getOutputValue(parameterIndex);
        if (value instanceof UniqueIdentifier) {
            return value.toString();
        }
        if (!this.connection.getUseLOBs()) {
            value = Support.convertLOB(value);
        }
        return value;
    }

    @Override
    public String getString(int parameterIndex) throws SQLException {
        this.checkOpen();
        return (String)Support.convert(this, this.getOutputValue(parameterIndex), 12, this.connection.getCharset());
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        JtdsCallableStatement.notImplemented("CallableStatement.registerOutParameter(int, int, String");
    }

    @Override
    public byte getByte(String parameterName) throws SQLException {
        return this.getByte(this.findParameter(parameterName, false));
    }

    @Override
    public double getDouble(String parameterName) throws SQLException {
        return this.getDouble(this.findParameter(parameterName, false));
    }

    @Override
    public float getFloat(String parameterName) throws SQLException {
        return this.getFloat(this.findParameter(parameterName, false));
    }

    @Override
    public int getInt(String parameterName) throws SQLException {
        return this.getInt(this.findParameter(parameterName, false));
    }

    @Override
    public long getLong(String parameterName) throws SQLException {
        return this.getLong(this.findParameter(parameterName, false));
    }

    @Override
    public short getShort(String parameterName) throws SQLException {
        return this.getShort(this.findParameter(parameterName, false));
    }

    @Override
    public boolean getBoolean(String parameterName) throws SQLException {
        return this.getBoolean(this.findParameter(parameterName, false));
    }

    @Override
    public byte[] getBytes(String parameterName) throws SQLException {
        return this.getBytes(this.findParameter(parameterName, false));
    }

    @Override
    public void setByte(String parameterName, byte x) throws SQLException {
        this.setByte(this.findParameter(parameterName, true), x);
    }

    @Override
    public void setDouble(String parameterName, double x) throws SQLException {
        this.setDouble(this.findParameter(parameterName, true), x);
    }

    @Override
    public void setFloat(String parameterName, float x) throws SQLException {
        this.setFloat(this.findParameter(parameterName, true), x);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        this.registerOutParameter(this.findParameter(parameterName, true), sqlType);
    }

    @Override
    public void setInt(String parameterName, int x) throws SQLException {
        this.setInt(this.findParameter(parameterName, true), x);
    }

    @Override
    public void setNull(String parameterName, int sqlType) throws SQLException {
        this.setNull(this.findParameter(parameterName, true), sqlType);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        this.registerOutParameter(this.findParameter(parameterName, true), sqlType, scale);
    }

    @Override
    public void setLong(String parameterName, long x) throws SQLException {
        this.setLong(this.findParameter(parameterName, true), x);
    }

    @Override
    public void setShort(String parameterName, short x) throws SQLException {
        this.setShort(this.findParameter(parameterName, true), x);
    }

    @Override
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        this.setBoolean(this.findParameter(parameterName, true), x);
    }

    @Override
    public void setBytes(String parameterName, byte[] x) throws SQLException {
        this.setBytes(this.findParameter(parameterName, true), x);
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return (BigDecimal)Support.convert(this, this.getOutputValue(parameterIndex), 3, null);
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        BigDecimal bd = (BigDecimal)Support.convert(this, this.getOutputValue(parameterIndex), 3, null);
        return bd.setScale(scale);
    }

    @Override
    public URL getURL(int parameterIndex) throws SQLException {
        this.checkOpen();
        String url = (String)Support.convert(this, this.getOutputValue(parameterIndex), 12, this.connection.getCharset());
        try {
            return new URL(url);
        }
        catch (MalformedURLException e) {
            throw new SQLException(Messages.get("error.resultset.badurl", url), "22000");
        }
    }

    @Override
    public Array getArray(int parameterIndex) throws SQLException {
        JtdsCallableStatement.notImplemented("CallableStatement.getArray");
        return null;
    }

    @Override
    public Blob getBlob(int parameterIndex) throws SQLException {
        byte[] value = this.getBytes(parameterIndex);
        if (value == null) {
            return null;
        }
        return new BlobImpl(this.connection, value);
    }

    @Override
    public Clob getClob(int parameterIndex) throws SQLException {
        String value = this.getString(parameterIndex);
        if (value == null) {
            return null;
        }
        return new ClobImpl(this.connection, value);
    }

    @Override
    public Date getDate(int parameterIndex) throws SQLException {
        return (Date)Support.convert(this, this.getOutputValue(parameterIndex), 91, null);
    }

    @Override
    public Ref getRef(int parameterIndex) throws SQLException {
        JtdsCallableStatement.notImplemented("CallableStatement.getRef");
        return null;
    }

    @Override
    public Time getTime(int parameterIndex) throws SQLException {
        return (Time)Support.convert(this, this.getOutputValue(parameterIndex), 92, null);
    }

    @Override
    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        return (Timestamp)Support.convert(this, this.getOutputValue(parameterIndex), 93, null);
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        this.setAsciiStream(this.findParameter(parameterName, true), x, length);
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        this.setBinaryStream(this.findParameter(parameterName, true), x, length);
    }

    @Override
    public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
        this.setCharacterStream(this.findParameter(parameterName, true), reader, length);
    }

    @Override
    public Object getObject(String parameterName) throws SQLException {
        return this.getObject(this.findParameter(parameterName, false));
    }

    @Override
    public void setObject(String parameterName, Object x) throws SQLException {
        this.setObject(this.findParameter(parameterName, true), x);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        this.setObject(this.findParameter(parameterName, true), x, targetSqlType);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        this.setObject(this.findParameter(parameterName, true), x, targetSqlType, scale);
    }

    public Object getObject(int parameterIndex, Map map) throws SQLException {
        JtdsCallableStatement.notImplemented("CallableStatement.getObject(int, Map)");
        return null;
    }

    @Override
    public String getString(String parameterName) throws SQLException {
        return this.getString(this.findParameter(parameterName, false));
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        JtdsCallableStatement.notImplemented("CallableStatement.registerOutParameter(String, int, String");
    }

    @Override
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        JtdsCallableStatement.notImplemented("CallableStatement.setNull(String, int, String");
    }

    @Override
    public void setString(String parameterName, String x) throws SQLException {
        this.setString(this.findParameter(parameterName, true), x);
    }

    @Override
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return this.getBigDecimal(this.findParameter(parameterName, false));
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        this.setBigDecimal(this.findParameter(parameterName, true), x);
    }

    @Override
    public URL getURL(String parameterName) throws SQLException {
        return this.getURL(this.findParameter(parameterName, false));
    }

    @Override
    public void setURL(String parameterName, URL x) throws SQLException {
        this.setObject(this.findParameter(parameterName, true), (Object)x);
    }

    @Override
    public Array getArray(String parameterName) throws SQLException {
        return this.getArray(this.findParameter(parameterName, false));
    }

    @Override
    public Blob getBlob(String parameterName) throws SQLException {
        return this.getBlob(this.findParameter(parameterName, false));
    }

    @Override
    public Clob getClob(String parameterName) throws SQLException {
        return this.getClob(this.findParameter(parameterName, false));
    }

    @Override
    public Date getDate(String parameterName) throws SQLException {
        return this.getDate(this.findParameter(parameterName, false));
    }

    @Override
    public void setDate(String parameterName, Date x) throws SQLException {
        this.setDate(this.findParameter(parameterName, true), x);
    }

    @Override
    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        Date date = this.getDate(parameterIndex);
        if (date != null && cal != null) {
            date = new Date(Support.timeToZone(date, cal));
        }
        return date;
    }

    @Override
    public Ref getRef(String parameterName) throws SQLException {
        return this.getRef(this.findParameter(parameterName, false));
    }

    @Override
    public Time getTime(String parameterName) throws SQLException {
        return this.getTime(this.findParameter(parameterName, false));
    }

    @Override
    public void setTime(String parameterName, Time x) throws SQLException {
        this.setTime(this.findParameter(parameterName, true), x);
    }

    @Override
    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        Time time = this.getTime(parameterIndex);
        if (time != null && cal != null) {
            time = new Time(Support.timeToZone(time, cal));
        }
        return time;
    }

    @Override
    public Timestamp getTimestamp(String parameterName) throws SQLException {
        return this.getTimestamp(this.findParameter(parameterName, false));
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        this.setTimestamp(this.findParameter(parameterName, true), x);
    }

    @Override
    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        Timestamp timestamp = this.getTimestamp(parameterIndex);
        if (timestamp != null && cal != null) {
            timestamp = new Timestamp(Support.timeToZone(timestamp, cal));
        }
        return timestamp;
    }

    public Object getObject(String parameterName, Map map) throws SQLException {
        return this.getObject(this.findParameter(parameterName, false), map);
    }

    @Override
    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        return this.getDate(this.findParameter(parameterName, false), cal);
    }

    @Override
    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        return this.getTime(this.findParameter(parameterName, false), cal);
    }

    @Override
    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        return this.getTimestamp(this.findParameter(parameterName, false), cal);
    }

    @Override
    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        this.setDate(this.findParameter(parameterName, true), x, cal);
    }

    @Override
    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        this.setTime(this.findParameter(parameterName, true), x, cal);
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        this.setTimestamp(this.findParameter(parameterName, true), x, cal);
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
    public void closeOnCompletion() throws SQLException {
        throw new AbstractMethodError();
    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        throw new AbstractMethodError();
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

