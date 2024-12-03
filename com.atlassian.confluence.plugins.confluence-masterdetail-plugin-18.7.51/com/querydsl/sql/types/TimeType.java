/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractDateTimeType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

public class TimeType
extends AbstractDateTimeType<Time> {
    public TimeType() {
        super(92);
    }

    public TimeType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(Time value) {
        return timeFormatter.print(value.getTime());
    }

    @Override
    public Time getValue(ResultSet rs, int startIndex) throws SQLException {
        return rs.getTime(startIndex);
    }

    @Override
    public Class<Time> getReturnedClass() {
        return Time.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Time value) throws SQLException {
        st.setTime(startIndex, value);
    }
}

