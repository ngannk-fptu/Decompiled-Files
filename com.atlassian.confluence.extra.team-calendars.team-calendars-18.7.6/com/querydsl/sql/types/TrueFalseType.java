/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nullable;

public class TrueFalseType
extends AbstractType<Boolean> {
    public TrueFalseType() {
        super(12);
    }

    public TrueFalseType(int type) {
        super(type);
    }

    @Override
    public Class<Boolean> getReturnedClass() {
        return Boolean.class;
    }

    @Override
    @Nullable
    public Boolean getValue(ResultSet rs, int startIndex) throws SQLException {
        String str = rs.getString(startIndex);
        return str != null ? Boolean.valueOf(str.equalsIgnoreCase("T")) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Boolean value) throws SQLException {
        st.setString(startIndex, value != false ? "T" : "F");
    }
}

