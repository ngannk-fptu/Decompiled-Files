/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractDateTimeType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class UtilDateType
extends AbstractDateTimeType<Date> {
    public UtilDateType() {
        super(93);
    }

    public UtilDateType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(Date value) {
        return dateTimeFormatter.print(value.getTime());
    }

    @Override
    public Date getValue(ResultSet rs, int startIndex) throws SQLException {
        return rs.getTimestamp(startIndex);
    }

    @Override
    public Class<Date> getReturnedClass() {
        return Date.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Date value) throws SQLException {
        st.setTimestamp(startIndex, new Timestamp(value.getTime()));
    }
}

