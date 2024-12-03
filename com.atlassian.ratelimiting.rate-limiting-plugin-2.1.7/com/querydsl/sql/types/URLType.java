/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class URLType
extends AbstractType<URL> {
    public URLType() {
        super(12);
    }

    public URLType(int type) {
        super(type);
    }

    @Override
    public URL getValue(ResultSet rs, int startIndex) throws SQLException {
        return rs.getURL(startIndex);
    }

    @Override
    public Class<URL> getReturnedClass() {
        return URL.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, URL value) throws SQLException {
        st.setURL(startIndex, value);
    }
}

