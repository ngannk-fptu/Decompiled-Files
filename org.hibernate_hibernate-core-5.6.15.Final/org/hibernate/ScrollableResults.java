/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.io.Closeable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.hibernate.type.Type;

public interface ScrollableResults
extends AutoCloseable,
Closeable {
    @Override
    public void close();

    public boolean next();

    public boolean previous();

    public boolean scroll(int var1);

    public boolean last();

    public boolean first();

    public void beforeFirst();

    public void afterLast();

    public boolean isFirst();

    public boolean isLast();

    public int getRowNumber();

    public boolean setRowNumber(int var1);

    public Object[] get();

    public Object get(int var1);

    public Type getType(int var1);

    public Integer getInteger(int var1);

    public Long getLong(int var1);

    public Float getFloat(int var1);

    public Boolean getBoolean(int var1);

    public Double getDouble(int var1);

    public Short getShort(int var1);

    public Byte getByte(int var1);

    public Character getCharacter(int var1);

    public byte[] getBinary(int var1);

    public String getText(int var1);

    public Blob getBlob(int var1);

    public Clob getClob(int var1);

    public String getString(int var1);

    public BigDecimal getBigDecimal(int var1);

    public BigInteger getBigInteger(int var1);

    public Date getDate(int var1);

    public Locale getLocale(int var1);

    public Calendar getCalendar(int var1);

    public TimeZone getTimeZone(int var1);
}

