/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.types;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.java.ao.EntityManager;
import net.java.ao.types.AbstractLogicalType;
import net.java.ao.util.StringUtils;

final class BooleanType
extends AbstractLogicalType<Boolean> {
    public BooleanType() {
        super("Boolean", new Class[]{Boolean.class, Boolean.TYPE}, 16, new Integer[]{16, -7, 2});
    }

    @Override
    public void putToDatabase(EntityManager manager, PreparedStatement stmt, int index, Boolean value, int jdbcType) throws SQLException {
        manager.getProvider().putBoolean(stmt, index, value);
    }

    @Override
    public Boolean pullFromDatabase(EntityManager manager, ResultSet res, Class<Boolean> type, String columnName) throws SQLException {
        return BooleanType.preserveNull(res, res.getBoolean(columnName));
    }

    @Override
    public Boolean parse(String input) {
        return StringUtils.isBlank(input) ? null : Boolean.valueOf(Boolean.parseBoolean(input));
    }

    @Override
    public boolean valueEquals(Object a, Object b) {
        if (a instanceof Number) {
            if (b instanceof Boolean) {
                return ((Number)a).intValue() == 1 == (Boolean)b;
            }
        } else if (a instanceof Boolean && b instanceof Number) {
            return ((Number)b).intValue() == 1 == (Boolean)a;
        }
        return a.equals(b);
    }
}

