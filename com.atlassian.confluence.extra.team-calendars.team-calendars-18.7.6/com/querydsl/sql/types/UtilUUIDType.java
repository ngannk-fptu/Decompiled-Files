/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql.types;

import com.querydsl.sql.types.AbstractType;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UtilUUIDType
extends AbstractType<UUID> {
    private final boolean asString;

    public UtilUUIDType() {
        this(1111, true);
    }

    public UtilUUIDType(boolean asString) {
        this(1111, asString);
    }

    public UtilUUIDType(int type) {
        this(type, true);
    }

    public UtilUUIDType(int type, boolean asString) {
        super(type);
        this.asString = asString;
    }

    @Override
    public UUID getValue(ResultSet rs, int startIndex) throws SQLException {
        if (this.asString) {
            String str = rs.getString(startIndex);
            return str != null ? UUID.fromString(str) : null;
        }
        return (UUID)rs.getObject(startIndex);
    }

    @Override
    public Class<UUID> getReturnedClass() {
        return UUID.class;
    }

    @Override
    public void setValue(PreparedStatement st, int startIndex, UUID value) throws SQLException {
        if (this.asString) {
            st.setString(startIndex, value.toString());
        } else {
            st.setObject(startIndex, value);
        }
    }
}

