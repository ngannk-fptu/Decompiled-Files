/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.Geometry;
import com.microsoft.sqlserver.jdbc.ISQLServerDataRecord;
import com.microsoft.sqlserver.jdbc.ISQLServerStatement;
import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLType;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import microsoft.sql.DateTimeOffset;

public interface ISQLServerPreparedStatement
extends PreparedStatement,
ISQLServerStatement {
    public void setDateTimeOffset(int var1, DateTimeOffset var2) throws SQLServerException;

    public void setObject(int var1, Object var2, SQLType var3, Integer var4, Integer var5) throws SQLServerException;

    public void setObject(int var1, Object var2, SQLType var3, Integer var4, Integer var5, boolean var6) throws SQLServerException;

    public int getPreparedStatementHandle() throws SQLServerException;

    public void setBigDecimal(int var1, BigDecimal var2, int var3, int var4) throws SQLServerException;

    public void setBigDecimal(int var1, BigDecimal var2, int var3, int var4, boolean var5) throws SQLServerException;

    public void setMoney(int var1, BigDecimal var2) throws SQLServerException;

    public void setMoney(int var1, BigDecimal var2, boolean var3) throws SQLServerException;

    public void setSmallMoney(int var1, BigDecimal var2) throws SQLServerException;

    public void setSmallMoney(int var1, BigDecimal var2, boolean var3) throws SQLServerException;

    public void setBoolean(int var1, boolean var2, boolean var3) throws SQLServerException;

    public void setByte(int var1, byte var2, boolean var3) throws SQLServerException;

    public void setBytes(int var1, byte[] var2, boolean var3) throws SQLServerException;

    public void setUniqueIdentifier(int var1, String var2) throws SQLServerException;

    public void setUniqueIdentifier(int var1, String var2, boolean var3) throws SQLServerException;

    public void setDouble(int var1, double var2, boolean var4) throws SQLServerException;

    public void setFloat(int var1, float var2, boolean var3) throws SQLServerException;

    public void setGeometry(int var1, Geometry var2) throws SQLServerException;

    public void setGeography(int var1, Geography var2) throws SQLServerException;

    public void setInt(int var1, int var2, boolean var3) throws SQLServerException;

    public void setLong(int var1, long var2, boolean var4) throws SQLServerException;

    public void setObject(int var1, Object var2, int var3, Integer var4, int var5) throws SQLServerException;

    public void setObject(int var1, Object var2, int var3, Integer var4, int var5, boolean var6) throws SQLServerException;

    public void setShort(int var1, short var2, boolean var3) throws SQLServerException;

    public void setString(int var1, String var2, boolean var3) throws SQLServerException;

    public void setNString(int var1, String var2, boolean var3) throws SQLServerException;

    public void setTime(int var1, Time var2, int var3) throws SQLServerException;

    public void setTime(int var1, Time var2, int var3, boolean var4) throws SQLServerException;

    public void setTimestamp(int var1, Timestamp var2, int var3) throws SQLServerException;

    public void setTimestamp(int var1, Timestamp var2, int var3, boolean var4) throws SQLServerException;

    public void setDateTimeOffset(int var1, DateTimeOffset var2, int var3) throws SQLServerException;

    public void setDateTimeOffset(int var1, DateTimeOffset var2, int var3, boolean var4) throws SQLServerException;

    public void setDateTime(int var1, Timestamp var2) throws SQLServerException;

    public void setDateTime(int var1, Timestamp var2, boolean var3) throws SQLServerException;

    public void setSmallDateTime(int var1, Timestamp var2) throws SQLServerException;

    public void setSmallDateTime(int var1, Timestamp var2, boolean var3) throws SQLServerException;

    public void setStructured(int var1, String var2, SQLServerDataTable var3) throws SQLServerException;

    public void setStructured(int var1, String var2, ResultSet var3) throws SQLServerException;

    public void setStructured(int var1, String var2, ISQLServerDataRecord var3) throws SQLServerException;

    public void setDate(int var1, Date var2, Calendar var3, boolean var4) throws SQLServerException;

    public void setTime(int var1, Time var2, Calendar var3, boolean var4) throws SQLServerException;

    public void setTimestamp(int var1, Timestamp var2, Calendar var3, boolean var4) throws SQLServerException;

    public ParameterMetaData getParameterMetaData(boolean var1) throws SQLServerException;

    public boolean getUseFmtOnly() throws SQLServerException;

    public void setUseFmtOnly(boolean var1) throws SQLServerException;
}

