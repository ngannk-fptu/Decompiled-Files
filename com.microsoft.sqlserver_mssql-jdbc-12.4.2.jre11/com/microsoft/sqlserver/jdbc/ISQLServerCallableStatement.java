/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.ISQLServerDataRecord;
import com.microsoft.sqlserver.jdbc.ISQLServerPreparedStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLType;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import microsoft.sql.DateTimeOffset;

public interface ISQLServerCallableStatement
extends CallableStatement,
ISQLServerPreparedStatement {
    @Deprecated(since="6.5.4")
    public BigDecimal getBigDecimal(String var1, int var2) throws SQLServerException;

    public Timestamp getDateTime(int var1) throws SQLServerException;

    public Timestamp getDateTime(String var1) throws SQLServerException;

    public Timestamp getDateTime(int var1, Calendar var2) throws SQLServerException;

    public Timestamp getDateTime(String var1, Calendar var2) throws SQLServerException;

    public Timestamp getSmallDateTime(int var1) throws SQLServerException;

    public Timestamp getSmallDateTime(String var1) throws SQLServerException;

    public Timestamp getSmallDateTime(int var1, Calendar var2) throws SQLServerException;

    public Timestamp getSmallDateTime(String var1, Calendar var2) throws SQLServerException;

    public DateTimeOffset getDateTimeOffset(int var1) throws SQLServerException;

    public DateTimeOffset getDateTimeOffset(String var1) throws SQLServerException;

    public InputStream getAsciiStream(int var1) throws SQLServerException;

    public InputStream getAsciiStream(String var1) throws SQLServerException;

    public BigDecimal getMoney(int var1) throws SQLServerException;

    public BigDecimal getMoney(String var1) throws SQLServerException;

    public BigDecimal getSmallMoney(int var1) throws SQLServerException;

    public BigDecimal getSmallMoney(String var1) throws SQLServerException;

    public InputStream getBinaryStream(int var1) throws SQLServerException;

    public InputStream getBinaryStream(String var1) throws SQLServerException;

    public void setTimestamp(String var1, Timestamp var2, Calendar var3, boolean var4) throws SQLServerException;

    public void setTime(String var1, Time var2, Calendar var3, boolean var4) throws SQLServerException;

    public void setDate(String var1, Date var2, Calendar var3, boolean var4) throws SQLServerException;

    public void setNString(String var1, String var2, boolean var3) throws SQLServerException;

    public void setObject(String var1, Object var2, int var3, int var4, boolean var5) throws SQLServerException;

    public void setObject(String var1, Object var2, int var3, Integer var4, int var5) throws SQLServerException;

    public void setTimestamp(String var1, Timestamp var2, int var3) throws SQLServerException;

    public void setTimestamp(String var1, Timestamp var2, int var3, boolean var4) throws SQLServerException;

    public void setDateTimeOffset(String var1, DateTimeOffset var2) throws SQLServerException;

    public void setDateTimeOffset(String var1, DateTimeOffset var2, int var3) throws SQLServerException;

    public void setDateTimeOffset(String var1, DateTimeOffset var2, int var3, boolean var4) throws SQLServerException;

    public void setTime(String var1, Time var2, int var3) throws SQLServerException;

    public void setTime(String var1, Time var2, int var3, boolean var4) throws SQLServerException;

    public void setDateTime(String var1, Timestamp var2) throws SQLServerException;

    public void setDateTime(String var1, Timestamp var2, boolean var3) throws SQLServerException;

    public void setSmallDateTime(String var1, Timestamp var2) throws SQLServerException;

    public void setSmallDateTime(String var1, Timestamp var2, boolean var3) throws SQLServerException;

    public void setUniqueIdentifier(String var1, String var2) throws SQLServerException;

    public void setUniqueIdentifier(String var1, String var2, boolean var3) throws SQLServerException;

    public void setBytes(String var1, byte[] var2, boolean var3) throws SQLServerException;

    public void setByte(String var1, byte var2, boolean var3) throws SQLServerException;

    public void setString(String var1, String var2, boolean var3) throws SQLServerException;

    public void setMoney(String var1, BigDecimal var2) throws SQLServerException;

    public void setMoney(String var1, BigDecimal var2, boolean var3) throws SQLServerException;

    public void setSmallMoney(String var1, BigDecimal var2) throws SQLServerException;

    public void setSmallMoney(String var1, BigDecimal var2, boolean var3) throws SQLServerException;

    public void setBigDecimal(String var1, BigDecimal var2, int var3, int var4) throws SQLServerException;

    public void setBigDecimal(String var1, BigDecimal var2, int var3, int var4, boolean var5) throws SQLServerException;

    public void setDouble(String var1, double var2, boolean var4) throws SQLServerException;

    public void setFloat(String var1, float var2, boolean var3) throws SQLServerException;

    public void setInt(String var1, int var2, boolean var3) throws SQLServerException;

    public void setLong(String var1, long var2, boolean var4) throws SQLServerException;

    public void setShort(String var1, short var2, boolean var3) throws SQLServerException;

    public void setBoolean(String var1, boolean var2, boolean var3) throws SQLServerException;

    public void setStructured(String var1, String var2, SQLServerDataTable var3) throws SQLServerException;

    public void setStructured(String var1, String var2, ResultSet var3) throws SQLServerException;

    public void setStructured(String var1, String var2, ISQLServerDataRecord var3) throws SQLServerException;

    public void registerOutParameter(String var1, SQLType var2, int var3, int var4) throws SQLServerException;

    public void registerOutParameter(int var1, SQLType var2, int var3, int var4) throws SQLServerException;

    public void registerOutParameter(int var1, int var2, int var3, int var4) throws SQLServerException;

    public void registerOutParameter(String var1, int var2, int var3, int var4) throws SQLServerException;

    public void setObject(String var1, Object var2, SQLType var3, int var4, boolean var5) throws SQLServerException;
}

