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

final class IntegerType
extends AbstractLogicalType<Integer> {
    public IntegerType() {
        super("Integer", new Class[]{Integer.class, Integer.TYPE}, 4, new Integer[]{4, 2});
    }

    @Override
    public boolean isAllowedAsPrimaryKey() {
        return true;
    }

    @Override
    public void putToDatabase(EntityManager manager, PreparedStatement stmt, int index, Integer value, int jdbcType) throws SQLException {
        stmt.setInt(index, value);
    }

    @Override
    public Integer pullFromDatabase(EntityManager manager, ResultSet res, Class<Integer> type, int columnIndex) throws SQLException {
        return IntegerType.preserveNull(res, res.getInt(columnIndex));
    }

    @Override
    public Integer pullFromDatabase(EntityManager manager, ResultSet res, Class<Integer> type, String columnName) throws SQLException {
        return IntegerType.preserveNull(res, res.getInt(columnName));
    }

    @Override
    public Integer parse(String input) {
        return StringUtils.isBlank(input) ? null : Integer.valueOf(Integer.parseInt(input));
    }
}

