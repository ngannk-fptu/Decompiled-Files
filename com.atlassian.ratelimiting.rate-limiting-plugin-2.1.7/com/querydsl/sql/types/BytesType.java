/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BytesType
extends AbstractType<byte[]> {
    public BytesType() {
        super(2004);
    }

    public BytesType(int type) {
        super(type);
    }

    @Override
    public byte[] getValue(ResultSet rs, int startIndex) throws SQLException {
        return rs.getBytes(startIndex);
    }

    @Override
    public Class<byte[]> getReturnedClass() {
        return byte[].class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, byte[] value) throws SQLException {
        st.setBytes(startIndex, value);
    }
}

