/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import java.util.Locale;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;

public class Informix10LimitHandler
extends AbstractLimitHandler {
    public static final Informix10LimitHandler INSTANCE = new Informix10LimitHandler();

    private Informix10LimitHandler() {
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        boolean hasOffset = LimitHelper.hasFirstRow(selection);
        String sqlOffset = hasOffset ? " skip " + selection.getFirstRow() : "";
        String sqlLimit = " first " + this.getMaxOrLimit(selection);
        String sqlOffsetLimit = sqlOffset + sqlLimit;
        String result = new StringBuilder(sql.length() + 10).append(sql).insert(sql.toLowerCase(Locale.ROOT).indexOf("select") + 6, sqlOffsetLimit).toString();
        return result;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean bindLimitParametersFirst() {
        return true;
    }

    @Override
    public boolean useMaxForLimit() {
        return false;
    }

    @Override
    public boolean supportsLimitOffset() {
        return true;
    }

    @Override
    public boolean supportsVariableLimit() {
        return false;
    }
}

