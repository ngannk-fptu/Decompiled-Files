/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigDecimalType
extends AbstractType<BigDecimal> {
    public BigDecimalType() {
        super(3);
    }

    public BigDecimalType(int type) {
        super(type);
    }

    @Override
    public BigDecimal getValue(ResultSet rs, int startIndex) throws SQLException {
        return rs.getBigDecimal(startIndex);
    }

    @Override
    public Class<BigDecimal> getReturnedClass() {
        return BigDecimal.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, BigDecimal value) throws SQLException {
        st.setBigDecimal(startIndex, value);
    }
}

