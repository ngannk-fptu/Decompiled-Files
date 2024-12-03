/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.java.ao.ActiveObjectsException;
import net.java.ao.EntityManager;
import net.java.ao.types.AbstractLogicalType;
import net.java.ao.util.StringUtils;

final class EnumType
extends AbstractLogicalType<Enum<?>> {
    public EnumType() {
        super("Enum", new Class[]{Enum.class}, 12, new Integer[0]);
    }

    @Override
    public boolean isAllowedAsPrimaryKey() {
        return true;
    }

    @Override
    public Enum<?> pullFromDatabase(EntityManager manager, ResultSet res, Class<Enum<?>> type, String columnName) throws SQLException {
        String dbValue = res.getString(columnName);
        if (StringUtils.isBlank(dbValue)) {
            return null;
        }
        try {
            return Enum.valueOf(type, dbValue);
        }
        catch (IllegalArgumentException e) {
            throw new ActiveObjectsException("Could not find enum value for '" + type + "' corresponding to database value '" + dbValue + "'");
        }
    }

    @Override
    public void putToDatabase(EntityManager manager, PreparedStatement stmt, int index, Enum<?> value, int jdbcType) throws SQLException {
        stmt.setString(index, value.name());
    }
}

