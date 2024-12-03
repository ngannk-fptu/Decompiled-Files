/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nullable;

public class NumericBooleanType
extends AbstractType<Boolean> {
    public static final NumericBooleanType DEFAULT = new NumericBooleanType();

    public NumericBooleanType() {
        super(4);
    }

    public NumericBooleanType(int type) {
        super(type);
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
    @Nullable
    public Boolean getValue(ResultSet rs, int startIndex) throws SQLException {
        Number num = (Number)rs.getObject(startIndex);
        return num != null ? Boolean.valueOf(num.intValue() == 1) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Boolean value) throws SQLException {
        st.setInt(startIndex, value != false ? 1 : 0);
    }
}

