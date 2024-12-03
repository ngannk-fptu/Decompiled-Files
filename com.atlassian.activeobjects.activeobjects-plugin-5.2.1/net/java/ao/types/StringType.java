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

final class StringType
extends AbstractLogicalType<String> {
    public static final int DEFAULT_LENGTH = 255;

    public StringType() {
        super("String", new Class[]{String.class}, 12, new Integer[]{12, -9, -1, -16, 2005, 2011});
    }

    @Override
    public boolean isAllowedAsPrimaryKey() {
        return true;
    }

    @Override
    public void putToDatabase(EntityManager manager, PreparedStatement stmt, int index, String value, int jdbcType) throws SQLException {
        stmt.setString(index, value);
    }

    @Override
    public String pullFromDatabase(EntityManager manager, ResultSet res, Class<String> type, String columnName) throws SQLException {
        return res.getString(columnName);
    }

    @Override
    public String parse(String input) {
        return input;
    }

    @Override
    public String parseDefault(String input) {
        if (StringUtils.isBlank(input)) {
            throw new IllegalArgumentException("Empty strings are not supported on all databases. Therefore is not supported by Active Objects.");
        }
        return input;
    }
}

