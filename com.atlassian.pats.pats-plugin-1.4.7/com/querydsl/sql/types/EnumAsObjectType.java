/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnumAsObjectType<T extends Enum<T>>
extends AbstractType<T> {
    private final Class<T> type;

    public EnumAsObjectType(Class<T> type) {
        this(1111, type);
    }

    public EnumAsObjectType(int jdbcType, Class<T> type) {
        super(jdbcType);
        this.type = type;
    }

    @Override
    public Class<T> getReturnedClass() {
        return this.type;
    }

    @Override
    public T getValue(ResultSet rs, int startIndex) throws SQLException {
        String name = rs.getString(startIndex);
        return name != null ? (T)Enum.valueOf(this.type, name) : null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, T value) throws SQLException {
        st.setObject(startIndex, (Object)((Enum)value).name(), 1111);
    }
}

