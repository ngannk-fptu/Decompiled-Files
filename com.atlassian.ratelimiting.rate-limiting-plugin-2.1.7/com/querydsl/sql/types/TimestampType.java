/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractDateTimeType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class TimestampType
extends AbstractDateTimeType<Timestamp> {
    public TimestampType() {
        super(93);
    }

    public TimestampType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(Timestamp value) {
        return dateTimeFormatter.print(value.getTime());
    }

    @Override
    public Timestamp getValue(ResultSet rs, int startIndex) throws SQLException {
        return rs.getTimestamp(startIndex);
    }

    @Override
    public Class<Timestamp> getReturnedClass() {
        return Timestamp.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Timestamp value) throws SQLException {
        st.setTimestamp(startIndex, value);
    }
}

