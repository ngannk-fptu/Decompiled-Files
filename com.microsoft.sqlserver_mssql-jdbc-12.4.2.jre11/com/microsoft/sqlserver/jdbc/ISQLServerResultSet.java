/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.Geography;
import com.microsoft.sqlserver.jdbc.Geometry;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.dataclassification.SensitivityClassification;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLType;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import microsoft.sql.DateTimeOffset;

public interface ISQLServerResultSet
extends ResultSet {
    public static final int TYPE_SS_DIRECT_FORWARD_ONLY = 2003;
    public static final int TYPE_SS_SERVER_CURSOR_FORWARD_ONLY = 2004;
    public static final int TYPE_SS_SCROLL_STATIC = 1004;
    public static final int TYPE_SS_SCROLL_KEYSET = 1005;
    public static final int TYPE_SS_SCROLL_DYNAMIC = 1006;
    public static final int CONCUR_SS_OPTIMISTIC_CC = 1008;
    public static final int CONCUR_SS_SCROLL_LOCKS = 1009;
    public static final int CONCUR_SS_OPTIMISTIC_CCVAL = 1010;

    public Geometry getGeometry(int var1) throws SQLServerException;

    public Geometry getGeometry(String var1) throws SQLServerException;

    public Geography getGeography(int var1) throws SQLServerException;

    public Geography getGeography(String var1) throws SQLServerException;

    public String getUniqueIdentifier(int var1) throws SQLServerException;

    public String getUniqueIdentifier(String var1) throws SQLServerException;

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

    public BigDecimal getMoney(int var1) throws SQLServerException;

    public BigDecimal getMoney(String var1) throws SQLServerException;

    public BigDecimal getSmallMoney(int var1) throws SQLServerException;

    public BigDecimal getSmallMoney(String var1) throws SQLServerException;

    public void updateDateTimeOffset(int var1, DateTimeOffset var2) throws SQLServerException;

    public void updateDateTimeOffset(String var1, DateTimeOffset var2) throws SQLServerException;

    public void updateObject(int var1, Object var2, int var3, int var4) throws SQLServerException;

    public void updateObject(int var1, Object var2, SQLType var3, int var4, boolean var5) throws SQLServerException;

    public void updateObject(String var1, Object var2, SQLType var3, int var4, boolean var5) throws SQLServerException;

    public void updateBoolean(int var1, boolean var2, boolean var3) throws SQLServerException;

    public void updateByte(int var1, byte var2, boolean var3) throws SQLServerException;

    public void updateShort(int var1, short var2, boolean var3) throws SQLServerException;

    public void updateInt(int var1, int var2, boolean var3) throws SQLServerException;

    public void updateLong(int var1, long var2, boolean var4) throws SQLServerException;

    public void updateFloat(int var1, float var2, boolean var3) throws SQLServerException;

    public void updateDouble(int var1, double var2, boolean var4) throws SQLServerException;

    public void updateMoney(int var1, BigDecimal var2) throws SQLServerException;

    public void updateMoney(int var1, BigDecimal var2, boolean var3) throws SQLServerException;

    public void updateMoney(String var1, BigDecimal var2) throws SQLServerException;

    public void updateMoney(String var1, BigDecimal var2, boolean var3) throws SQLServerException;

    public void updateSmallMoney(int var1, BigDecimal var2) throws SQLServerException;

    public void updateSmallMoney(int var1, BigDecimal var2, boolean var3) throws SQLServerException;

    public void updateSmallMoney(String var1, BigDecimal var2) throws SQLServerException;

    public void updateSmallMoney(String var1, BigDecimal var2, boolean var3) throws SQLServerException;

    public void updateBigDecimal(int var1, BigDecimal var2, Integer var3, Integer var4) throws SQLServerException;

    public void updateBigDecimal(int var1, BigDecimal var2, Integer var3, Integer var4, boolean var5) throws SQLServerException;

    public void updateString(int var1, String var2, boolean var3) throws SQLServerException;

    public void updateNString(int var1, String var2, boolean var3) throws SQLServerException;

    public void updateNString(String var1, String var2, boolean var3) throws SQLServerException;

    public void updateBytes(int var1, byte[] var2, boolean var3) throws SQLServerException;

    public void updateDate(int var1, Date var2, boolean var3) throws SQLServerException;

    public void updateTime(int var1, Time var2, Integer var3) throws SQLServerException;

    public void updateTime(int var1, Time var2, Integer var3, boolean var4) throws SQLServerException;

    public void updateTimestamp(int var1, Timestamp var2, int var3) throws SQLServerException;

    public void updateTimestamp(int var1, Timestamp var2, int var3, boolean var4) throws SQLServerException;

    public void updateDateTime(int var1, Timestamp var2) throws SQLServerException;

    public void updateDateTime(int var1, Timestamp var2, Integer var3) throws SQLServerException;

    public void updateDateTime(int var1, Timestamp var2, Integer var3, boolean var4) throws SQLServerException;

    public void updateSmallDateTime(int var1, Timestamp var2) throws SQLServerException;

    public void updateSmallDateTime(int var1, Timestamp var2, Integer var3) throws SQLServerException;

    public void updateSmallDateTime(int var1, Timestamp var2, Integer var3, boolean var4) throws SQLServerException;

    public void updateDateTimeOffset(int var1, DateTimeOffset var2, Integer var3) throws SQLServerException;

    public void updateDateTimeOffset(int var1, DateTimeOffset var2, Integer var3, boolean var4) throws SQLServerException;

    public void updateUniqueIdentifier(int var1, String var2) throws SQLServerException;

    public void updateUniqueIdentifier(int var1, String var2, boolean var3) throws SQLServerException;

    public void updateObject(int var1, Object var2, int var3, int var4, boolean var5) throws SQLServerException;

    public void updateBoolean(String var1, boolean var2, boolean var3) throws SQLServerException;

    public void updateByte(String var1, byte var2, boolean var3) throws SQLServerException;

    public void updateShort(String var1, short var2, boolean var3) throws SQLServerException;

    public void updateInt(String var1, int var2, boolean var3) throws SQLServerException;

    public void updateLong(String var1, long var2, boolean var4) throws SQLServerException;

    public void updateFloat(String var1, float var2, boolean var3) throws SQLServerException;

    public void updateDouble(String var1, double var2, boolean var4) throws SQLServerException;

    public void updateBigDecimal(String var1, BigDecimal var2, boolean var3) throws SQLServerException;

    public void updateBigDecimal(String var1, BigDecimal var2, Integer var3, Integer var4) throws SQLServerException;

    public void updateBigDecimal(String var1, BigDecimal var2, Integer var3, Integer var4, boolean var5) throws SQLServerException;

    public void updateString(String var1, String var2, boolean var3) throws SQLServerException;

    public void updateBytes(String var1, byte[] var2, boolean var3) throws SQLServerException;

    public void updateDate(String var1, Date var2, boolean var3) throws SQLServerException;

    public void updateTime(String var1, Time var2, int var3) throws SQLServerException;

    public void updateTime(String var1, Time var2, int var3, boolean var4) throws SQLServerException;

    public void updateTimestamp(String var1, Timestamp var2, int var3) throws SQLServerException;

    public void updateTimestamp(String var1, Timestamp var2, int var3, boolean var4) throws SQLServerException;

    public void updateDateTime(String var1, Timestamp var2) throws SQLServerException;

    public void updateDateTime(String var1, Timestamp var2, int var3) throws SQLServerException;

    public void updateDateTime(String var1, Timestamp var2, int var3, boolean var4) throws SQLServerException;

    public void updateSmallDateTime(String var1, Timestamp var2) throws SQLServerException;

    public void updateSmallDateTime(String var1, Timestamp var2, int var3) throws SQLServerException;

    public void updateSmallDateTime(String var1, Timestamp var2, int var3, boolean var4) throws SQLServerException;

    public void updateDateTimeOffset(String var1, DateTimeOffset var2, int var3) throws SQLServerException;

    public void updateDateTimeOffset(String var1, DateTimeOffset var2, int var3, boolean var4) throws SQLServerException;

    public void updateUniqueIdentifier(String var1, String var2) throws SQLServerException;

    public void updateUniqueIdentifier(String var1, String var2, boolean var3) throws SQLServerException;

    public void updateObject(String var1, Object var2, int var3, int var4) throws SQLServerException;

    public void updateObject(String var1, Object var2, int var3, int var4, boolean var5) throws SQLServerException;

    public SensitivityClassification getSensitivityClassification();
}

