/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigIntegerAsLongType
extends AbstractType<BigInteger> {
    public static final BigIntegerAsLongType DEFAULT = new BigIntegerAsLongType();

    public BigIntegerAsLongType() {
        super(2);
    }

    public BigIntegerAsLongType(int type) {
        super(type);
    }

    @Override
    public BigInteger getValue(ResultSet rs, int startIndex) throws SQLException {
        long val = rs.getLong(startIndex);
        return rs.wasNull() ? null : BigInteger.valueOf(val);
    }

    @Override
    public Class<BigInteger> getReturnedClass() {
        return BigInteger.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, BigInteger value) throws SQLException {
        st.setLong(startIndex, value.longValue());
    }
}

