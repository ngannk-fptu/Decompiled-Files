/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTimeZone
 *  org.joda.time.LocalDate
 *  org.joda.time.ReadablePartial
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractDateTimeType;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.ReadablePartial;

public class LocalDateType
extends AbstractDateTimeType<LocalDate> {
    public LocalDateType() {
        super(91);
    }

    public LocalDateType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(LocalDate value) {
        return dateFormatter.print((ReadablePartial)value);
    }

    @Override
    public Class<LocalDate> getReturnedClass() {
        return LocalDate.class;
    }

    @Override
    public LocalDate getValue(ResultSet rs, int startIndex) throws SQLException {
        Date date = rs.getDate(startIndex, LocalDateType.utc());
        return date != null ? new LocalDate(date.getTime(), DateTimeZone.UTC) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, LocalDate value) throws SQLException {
        st.setDate(startIndex, new Date(value.toDateTimeAtStartOfDay(DateTimeZone.UTC).getMillis()), LocalDateType.utc());
    }
}

