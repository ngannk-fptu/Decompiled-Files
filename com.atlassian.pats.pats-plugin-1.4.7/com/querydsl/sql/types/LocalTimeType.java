/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractDateTimeType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

public class LocalTimeType
extends AbstractDateTimeType<LocalTime> {
    public LocalTimeType() {
        super(92);
    }

    public LocalTimeType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(LocalTime value) {
        return timeFormatter.print(value);
    }

    @Override
    public Class<LocalTime> getReturnedClass() {
        return LocalTime.class;
    }

    @Override
    public LocalTime getValue(ResultSet rs, int startIndex) throws SQLException {
        Time time = rs.getTime(startIndex, LocalTimeType.utc());
        return time != null ? new LocalTime(time.getTime(), DateTimeZone.UTC) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, LocalTime value) throws SQLException {
        st.setTime(startIndex, new Time(value.getMillisOfDay()), LocalTimeType.utc());
    }
}

