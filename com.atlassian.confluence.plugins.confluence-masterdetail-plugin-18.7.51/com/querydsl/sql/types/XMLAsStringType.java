/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;

public class XMLAsStringType
extends AbstractType<String> {
    public XMLAsStringType() {
        super(2009);
    }

    public XMLAsStringType(int type) {
        super(type);
    }

    @Override
    public String getValue(ResultSet rs, int startIndex) throws SQLException {
        SQLXML value = rs.getSQLXML(startIndex);
        return value != null ? value.getString() : null;
    }

    @Override
    public Class<String> getReturnedClass() {
        return String.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, String value) throws SQLException {
        SQLXML xml = st.getConnection().createSQLXML();
        xml.setString(value);
        st.setSQLXML(startIndex, xml);
    }
}

