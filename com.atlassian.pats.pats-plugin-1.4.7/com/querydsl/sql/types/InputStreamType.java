/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InputStreamType
extends AbstractType<InputStream> {
    public InputStreamType() {
        super(2004);
    }

    public InputStreamType(int type) {
        super(type);
    }

    @Override
    public Class<InputStream> getReturnedClass() {
        return InputStream.class;
    }

    @Override
    public InputStream getValue(ResultSet rs, int column) throws SQLException {
        return rs.getBinaryStream(column);
    }

    @Override
    public void setValue(PreparedStatement ps, int column, InputStream value) throws SQLException {
        ps.setBinaryStream(column, value);
    }
}

