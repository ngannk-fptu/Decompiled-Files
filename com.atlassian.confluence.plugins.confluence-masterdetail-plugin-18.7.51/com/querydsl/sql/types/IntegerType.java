/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IntegerType
extends AbstractType<Integer> {
    public IntegerType() {
        super(4);
    }

    public IntegerType(int type) {
        super(type);
    }

    @Override
    public Class<Integer> getReturnedClass() {
        return Integer.class;
    }

    @Override
    public Integer getValue(ResultSet rs, int startIndex) throws SQLException {
        int val = rs.getInt(startIndex);
        return rs.wasNull() ? null : Integer.valueOf(val);
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Integer value) throws SQLException {
        st.setInt(startIndex, value);
    }
}

