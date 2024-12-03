/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLXML;

public class SQLXMLType
extends AbstractType<SQLXML> {
    public SQLXMLType() {
        super(2009);
    }

    public SQLXMLType(int type) {
        super(type);
    }

    @Override
    public SQLXML getValue(ResultSet rs, int startIndex) throws SQLException {
        return rs.getSQLXML(startIndex);
    }

    @Override
    public Class<SQLXML> getReturnedClass() {
        return SQLXML.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, SQLXML value) throws SQLException {
        st.setSQLXML(startIndex, value);
    }
}

