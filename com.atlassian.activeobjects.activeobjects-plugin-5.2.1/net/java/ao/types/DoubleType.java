/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.types;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.java.ao.EntityManager;
import net.java.ao.types.AbstractLogicalType;
import net.java.ao.util.DoubleUtils;
import net.java.ao.util.StringUtils;

final class DoubleType
extends AbstractLogicalType<Double> {
    public DoubleType() {
        super("Double", new Class[]{Double.class, Double.TYPE}, 8, new Integer[]{8, 2, 3});
    }

    @Override
    public Double pullFromDatabase(EntityManager manager, ResultSet res, Class<Double> type, String columnName) throws SQLException {
        return DoubleType.preserveNull(res, res.getDouble(columnName));
    }

    @Override
    protected Double validateInternal(Double value) {
        DoubleUtils.checkDouble(value);
        return value;
    }

    @Override
    public Double parse(String input) {
        return StringUtils.isBlank(input) ? null : Double.valueOf(Double.parseDouble(input));
    }
}

