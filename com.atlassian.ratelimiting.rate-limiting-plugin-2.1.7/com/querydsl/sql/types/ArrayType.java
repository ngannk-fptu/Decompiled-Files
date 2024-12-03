/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Primitives
 *  javax.annotation.Nullable
 */
package com.querydsl.sql.types;

import com.google.common.primitives.Primitives;
import com.querydsl.sql.types.AbstractType;
import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nullable;

public class ArrayType<T>
extends AbstractType<T> {
    private final Class<T> type;
    private final String typeName;
    private final boolean convertPrimitives;

    private static void copy(Object source, Object target, int length) {
        for (int i = 0; i < length; ++i) {
            Object val = Array.get(source, i);
            Array.set(target, i, val);
        }
    }

    public ArrayType(Class<T> type, String typeName) {
        super(2003);
        this.type = type;
        this.typeName = typeName;
        this.convertPrimitives = type.getComponentType().isPrimitive();
    }

    @Override
    public Class<T> getReturnedClass() {
        return this.type;
    }

    @Override
    @Nullable
    public T getValue(ResultSet rs, int startIndex) throws SQLException {
        java.sql.Array arr = rs.getArray(startIndex);
        if (arr != null) {
            Object[] rv = (Object[])arr.getArray();
            if (this.convertPrimitives) {
                Object rv2 = Array.newInstance(this.type.getComponentType(), rv.length);
                ArrayType.copy(rv, rv2, rv.length);
                return (T)rv2;
            }
            return (T)rv;
        }
        return null;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, T value) throws SQLException {
        if (this.convertPrimitives) {
            int length = Array.getLength(value);
            Object value2 = Array.newInstance(Primitives.wrap(this.type.getComponentType()), length);
            ArrayType.copy(value, value2, length);
            value = value2;
        }
        java.sql.Array arr = st.getConnection().createArrayOf(this.typeName, (Object[])value);
        st.setArray(startIndex, arr);
    }
}

