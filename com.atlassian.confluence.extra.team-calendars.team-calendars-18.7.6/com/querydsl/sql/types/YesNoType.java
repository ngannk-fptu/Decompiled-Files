/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nullable;

public class YesNoType
extends AbstractType<Boolean> {
    public YesNoType() {
        super(12);
    }

    public YesNoType(int type) {
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
        return str != null ? Boolean.valueOf(str.equalsIgnoreCase("Y")) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Boolean value) throws SQLException {
        st.setString(startIndex, value != false ? "Y" : "N");
    }
}

