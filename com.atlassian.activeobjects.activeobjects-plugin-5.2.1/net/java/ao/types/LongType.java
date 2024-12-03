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

final class LongType
extends AbstractLogicalType<Long> {
    public LongType() {
        super("Long", new Class[]{Long.class, Long.TYPE}, -5, new Integer[]{-5, 2});
    }

    @Override
    public boolean isAllowedAsPrimaryKey() {
        return true;
    }

    @Override
    public void putToDatabase(EntityManager manager, PreparedStatement stmt, int index, Long value, int jdbcType) throws SQLException {
        stmt.setLong(index, value);
    }

    @Override
    public Long pullFromDatabase(EntityManager manager, ResultSet res, Class<Long> type, int columnIndex) throws SQLException {
        return LongType.preserveNull(res, res.getLong(columnIndex));
    }

    @Override
    public Long pullFromDatabase(EntityManager manager, ResultSet res, Class<Long> type, String columnName) throws SQLException {
        return LongType.preserveNull(res, res.getLong(columnName));
    }

    @Override
    public Long parse(String input) {
        return StringUtils.isBlank(input) ? null : Long.valueOf(Long.parseLong(input));
    }
}

