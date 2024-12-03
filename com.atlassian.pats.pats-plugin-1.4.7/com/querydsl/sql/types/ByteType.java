/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ByteType
extends AbstractType<Byte> {
    public ByteType() {
        super(-6);
    }

    public ByteType(int type) {
        super(type);
    }

    @Override
    public Class<Byte> getReturnedClass() {
        return Byte.class;
    }

    @Override
    public Byte getValue(ResultSet rs, int startIndex) throws SQLException {
        byte val = rs.getByte(startIndex);
        return rs.wasNull() ? null : Byte.valueOf(val);
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Byte value) throws SQLException {
        st.setByte(startIndex, value);
    }
}

