/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClobType
extends AbstractType<Clob> {
    public ClobType() {
        super(2005);
    }

    public ClobType(int type) {
        super(type);
    }

    @Override
    public Clob getValue(ResultSet rs, int startIndex) throws SQLException {
        return rs.getClob(startIndex);
    }

    @Override
    public Class<Clob> getReturnedClass() {
        return Clob.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Clob value) throws SQLException {
        st.setClob(startIndex, value);
    }
}

