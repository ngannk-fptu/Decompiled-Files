/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoubleType
extends AbstractType<Double> {
    public DoubleType() {
        super(8);
    }

    public DoubleType(int type) {
        super(type);
    }

    @Override
    public Class<Double> getReturnedClass() {
        return Double.class;
    }

    @Override
    public Double getValue(ResultSet rs, int startIndex) throws SQLException {
        double val = rs.getDouble(startIndex);
        return rs.wasNull() ? null : Double.valueOf(val);
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Double value) throws SQLException {
        st.setDouble(startIndex, value);
    }
}

