/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShortType
extends AbstractType<Short> {
    public ShortType() {
        super(5);
    }

    public ShortType(int type) {
        super(type);
    }

    @Override
    public Class<Short> getReturnedClass() {
        return Short.class;
    }

    @Override
    public Short getValue(ResultSet rs, int startIndex) throws SQLException {
        short val = rs.getShort(startIndex);
        return rs.wasNull() ? null : Short.valueOf(val);
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Short value) throws SQLException {
        st.setShort(startIndex, value);
    }
}

