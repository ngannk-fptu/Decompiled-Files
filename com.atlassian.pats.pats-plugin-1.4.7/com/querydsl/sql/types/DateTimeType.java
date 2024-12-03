/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractDateTimeType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.joda.time.DateTime;

public class DateTimeType
extends AbstractDateTimeType<DateTime> {
    public DateTimeType() {
        super(93);
    }

    public DateTimeType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(DateTime value) {
        return dateTimeFormatter.print(value);
    }

    @Override
    public Class<DateTime> getReturnedClass() {
        return DateTime.class;
    }

    @Override
    public DateTime getValue(ResultSet rs, int startIndex) throws SQLException {
        Timestamp ts = rs.getTimestamp(startIndex);
        return ts != null ? new DateTime(ts.getTime()) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, DateTime value) throws SQLException {
        st.setTimestamp(startIndex, new Timestamp(value.getMillis()));
    }
}

