/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObjectType
extends AbstractType<Object> {
    public ObjectType() {
        super(1111);
    }

    public ObjectType(int type) {
        super(type);
    }

    @Override
    public Object getValue(ResultSet rs, int startIndex) throws SQLException {
        return rs.getObject(startIndex);
    }

    @Override
    public Class<Object> getReturnedClass() {
        return Object.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Object value) throws SQLException {
        st.setObject(startIndex, value);
    }
}

