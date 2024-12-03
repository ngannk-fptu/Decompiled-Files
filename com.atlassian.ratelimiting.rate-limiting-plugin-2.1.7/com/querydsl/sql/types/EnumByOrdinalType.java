/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnumByOrdinalType<T extends Enum<T>>
extends AbstractType<T> {
    private final Class<T> type;

    public EnumByOrdinalType(Class<T> type) {
        this(4, type);
    }

    public EnumByOrdinalType(int jdbcType, Class<T> type) {
        super(jdbcType);
        this.type = type;
    }

    @Override
    public Class<T> getReturnedClass() {
        return this.type;
    }

    @Override
    public T getValue(ResultSet rs, int startIndex) throws SQLException {
        int ordinal = rs.getInt(startIndex);
        return (T)(rs.wasNull() ? null : ((Enum[])this.type.getEnumConstants())[ordinal]);
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, T value) throws SQLException {
        st.setInt(startIndex, ((Enum)value).ordinal());
    }
}

