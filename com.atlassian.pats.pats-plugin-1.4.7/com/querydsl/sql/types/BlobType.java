/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlobType
extends AbstractType<Blob> {
    public BlobType() {
        super(2004);
    }

    public BlobType(int type) {
        super(type);
    }

    @Override
    public Blob getValue(ResultSet rs, int startIndex) throws SQLException {
        return rs.getBlob(startIndex);
    }

    @Override
    public Class<Blob> getReturnedClass() {
        return Blob.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, Blob value) throws SQLException {
        st.setBlob(startIndex, value);
    }
}

