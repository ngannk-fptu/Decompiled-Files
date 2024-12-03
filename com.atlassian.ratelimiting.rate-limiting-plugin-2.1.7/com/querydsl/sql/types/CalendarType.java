/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractDateTimeType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

public class CalendarType
extends AbstractDateTimeType<Calendar> {
    public CalendarType() {
        super(93);
    }

    public CalendarType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(Calendar value) {
        return dateTimeFormatter.print(value.getTimeInMillis());
    }

    @Override
    public Calendar getValue(ResultSet rs, int startIndex) throws SQLException {
        Timestamp ts = rs.getTimestamp(startIndex);
        if (ts != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(ts.getTime());
            return cal;
        }
        return null;
    }

    @Override
    public Class<Calendar> getReturnedClass() {
        return Calendar.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Calendar value) throws SQLException {
        st.setTimestamp(startIndex, new Timestamp(value.getTimeInMillis()));
    }
}

