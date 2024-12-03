/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support.rowset;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import org.springframework.jdbc.InvalidResultSetAccessException;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.lang.Nullable;

public interface SqlRowSet
extends Serializable {
    public SqlRowSetMetaData getMetaData();

    public int findColumn(String var1) throws InvalidResultSetAccessException;

    @Nullable
    public BigDecimal getBigDecimal(int var1) throws InvalidResultSetAccessException;

    @Nullable
    public BigDecimal getBigDecimal(String var1) throws InvalidResultSetAccessException;

    public boolean getBoolean(int var1) throws InvalidResultSetAccessException;

    public boolean getBoolean(String var1) throws InvalidResultSetAccessException;

    public byte getByte(int var1) throws InvalidResultSetAccessException;

    public byte getByte(String var1) throws InvalidResultSetAccessException;

    @Nullable
    public Date getDate(int var1) throws InvalidResultSetAccessException;

    @Nullable
    public Date getDate(String var1) throws InvalidResultSetAccessException;

    @Nullable
    public Date getDate(int var1, Calendar var2) throws InvalidResultSetAccessException;

    @Nullable
    public Date getDate(String var1, Calendar var2) throws InvalidResultSetAccessException;

    public double getDouble(int var1) throws InvalidResultSetAccessException;

    public double getDouble(String var1) throws InvalidResultSetAccessException;

    public float getFloat(int var1) throws InvalidResultSetAccessException;

    public float getFloat(String var1) throws InvalidResultSetAccessException;

    public int getInt(int var1) throws InvalidResultSetAccessException;

    public int getInt(String var1) throws InvalidResultSetAccessException;

    public long getLong(int var1) throws InvalidResultSetAccessException;

    public long getLong(String var1) throws InvalidResultSetAccessException;

    @Nullable
    public String getNString(int var1) throws InvalidResultSetAccessException;

    @Nullable
    public String getNString(String var1) throws InvalidResultSetAccessException;

    @Nullable
    public Object getObject(int var1) throws InvalidResultSetAccessException;

    @Nullable
    public Object getObject(String var1) throws InvalidResultSetAccessException;

    @Nullable
    public Object getObject(int var1, Map<String, Class<?>> var2) throws InvalidResultSetAccessException;

    @Nullable
    public Object getObject(String var1, Map<String, Class<?>> var2) throws InvalidResultSetAccessException;

    @Nullable
    public <T> T getObject(int var1, Class<T> var2) throws InvalidResultSetAccessException;

    @Nullable
    public <T> T getObject(String var1, Class<T> var2) throws InvalidResultSetAccessException;

    public short getShort(int var1) throws InvalidResultSetAccessException;

    public short getShort(String var1) throws InvalidResultSetAccessException;

    @Nullable
    public String getString(int var1) throws InvalidResultSetAccessException;

    @Nullable
    public String getString(String var1) throws InvalidResultSetAccessException;

    @Nullable
    public Time getTime(int var1) throws InvalidResultSetAccessException;

    @Nullable
    public Time getTime(String var1) throws InvalidResultSetAccessException;

    @Nullable
    public Time getTime(int var1, Calendar var2) throws InvalidResultSetAccessException;

    @Nullable
    public Time getTime(String var1, Calendar var2) throws InvalidResultSetAccessException;

    @Nullable
    public Timestamp getTimestamp(int var1) throws InvalidResultSetAccessException;

    @Nullable
    public Timestamp getTimestamp(String var1) throws InvalidResultSetAccessException;

    @Nullable
    public Timestamp getTimestamp(int var1, Calendar var2) throws InvalidResultSetAccessException;

    @Nullable
    public Timestamp getTimestamp(String var1, Calendar var2) throws InvalidResultSetAccessException;

    public boolean absolute(int var1) throws InvalidResultSetAccessException;

    public void afterLast() throws InvalidResultSetAccessException;

    public void beforeFirst() throws InvalidResultSetAccessException;

    public boolean first() throws InvalidResultSetAccessException;

    public int getRow() throws InvalidResultSetAccessException;

    public boolean isAfterLast() throws InvalidResultSetAccessException;

    public boolean isBeforeFirst() throws InvalidResultSetAccessException;

    public boolean isFirst() throws InvalidResultSetAccessException;

    public boolean isLast() throws InvalidResultSetAccessException;

    public boolean last() throws InvalidResultSetAccessException;

    public boolean next() throws InvalidResultSetAccessException;

    public boolean previous() throws InvalidResultSetAccessException;

    public boolean relative(int var1) throws InvalidResultSetAccessException;

    public boolean wasNull() throws InvalidResultSetAccessException;
}

