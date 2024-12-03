/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FloatType
extends AbstractType<Float> {
    public FloatType() {
        super(6);
    }

    public FloatType(int type) {
        super(type);
    }

    @Override
    public Class<Float> getReturnedClass() {
        return Float.class;
    }

    @Override
    public Float getValue(ResultSet rs, int startIndex) throws SQLException {
        float val = rs.getFloat(startIndex);
        return rs.wasNull() ? null : Float.valueOf(val);
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Float value) throws SQLException {
        st.setFloat(startIndex, value.floatValue());
    }
}

