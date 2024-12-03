/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringType
extends AbstractType<String> {
    public StringType() {
        super(12);
    }

    public StringType(int type) {
        super(type);
    }

    @Override
    public String getValue(ResultSet rs, int startIndex) throws SQLException {
        return rs.getString(startIndex);
    }

    @Override
    public Class<String> getReturnedClass() {
        return String.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, String value) throws SQLException {
        st.setString(startIndex, value);
    }
}

