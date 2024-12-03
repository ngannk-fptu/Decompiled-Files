/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LongType
extends AbstractType<Long> {
    public LongType() {
        super(-5);
    }

    public LongType(int type) {
        super(type);
    }

    @Override
    public Class<Long> getReturnedClass() {
        return Long.class;
    }

    @Override
    public Long getValue(ResultSet rs, int startIndex) throws SQLException {
        long val = rs.getLong(startIndex);
        return rs.wasNull() ? null : Long.valueOf(val);
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Long value) throws SQLException {
        st.setLong(startIndex, value);
    }
}

