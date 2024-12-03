/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringAsObjectType
extends AbstractType<String> {
    public static final StringAsObjectType DEFAULT = new StringAsObjectType();

    public StringAsObjectType() {
        super(12);
    }

    public StringAsObjectType(int type) {
        super(type);
    }

    @Override
    public String getValue(ResultSet rs, int startIndex) throws SQLException {
        Object o = rs.getObject(startIndex);
        if (o instanceof String) {
            return (String)o;
        }
        if (o instanceof Clob) {
            Clob clob = (Clob)o;
            return clob.getSubString(1L, (int)clob.length());
        }
        if (o != null) {
            return o.toString();
        }
        return null;
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

