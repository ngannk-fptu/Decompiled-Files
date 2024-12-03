/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigIntegerType
extends AbstractType<BigInteger> {
    public BigIntegerType() {
        super(2);
    }

    public BigIntegerType(int type) {
        super(type);
    }

    @Override
    public BigInteger getValue(ResultSet rs, int startIndex) throws SQLException {
        BigDecimal bd = rs.getBigDecimal(startIndex);
        return bd != null ? bd.toBigInteger() : null;
    }

    @Override
    public Class<BigInteger> getReturnedClass() {
        return BigInteger.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, BigInteger value) throws SQLException {
        st.setBigDecimal(startIndex, new BigDecimal(value));
    }
}

