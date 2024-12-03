/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigDecimalAsDoubleType
extends AbstractType<BigDecimal> {
    public static final BigDecimalAsDoubleType DEFAULT = new BigDecimalAsDoubleType();

    public BigDecimalAsDoubleType() {
        super(8);
    }

    public BigDecimalAsDoubleType(int type) {
        super(type);
    }

    @Override
    public BigDecimal getValue(ResultSet rs, int startIndex) throws SQLException {
        double val = rs.getDouble(startIndex);
        return rs.wasNull() ? null : BigDecimal.valueOf(val);
    }

    @Override
    public Class<BigDecimal> getReturnedClass() {
        return BigDecimal.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, BigDecimal value) throws SQLException {
        st.setDouble(startIndex, value.doubleValue());
    }
}

