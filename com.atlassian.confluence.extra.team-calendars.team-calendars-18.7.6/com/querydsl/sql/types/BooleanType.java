/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanType
extends AbstractType<Boolean> {
    public BooleanType() {
        super(16);
    }

    public BooleanType(int type) {
        super(type);
    }

    @Override
    public Boolean getValue(ResultSet rs, int startIndex) throws SQLException {
        boolean val = rs.getBoolean(startIndex);
        return rs.wasNull() ? null : Boolean.valueOf(val);
    }

    @Override
    public Class<Boolean> getReturnedClass() {
        return Boolean.class;
    }

    @Override
    public String getLiteral(Boolean value) {
        return value != false ? "1" : "0";
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Boolean value) throws SQLException {
        st.setBoolean(startIndex, value);
    }
}

