/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractDateTimeType;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DateType
extends AbstractDateTimeType<Date> {
    public DateType() {
        super(91);
    }

    public DateType(int type) {
        super(type);
    }

    @Override
    public String getLiteral(Date value) {
        return dateFormatter.print(value.getTime());
    }

    @Override
    public Date getValue(ResultSet rs, int startIndex) throws SQLException {
        return rs.getDate(startIndex);
    }

    @Override
    public Class<Date> getReturnedClass() {
        return Date.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Date value) throws SQLException {
        st.setDate(startIndex, value);
    }
}

