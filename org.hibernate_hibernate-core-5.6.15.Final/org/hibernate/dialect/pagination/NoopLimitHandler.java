/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;

public class NoopLimitHandler
extends AbstractLimitHandler {
    public static final NoopLimitHandler INSTANCE = new NoopLimitHandler();

    private NoopLimitHandler() {
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        return sql;
    }

    @Override
    public int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index) {
        return 0;
    }

    @Override
    public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index) {
        return 0;
    }

    @Override
    public void setMaxRows(RowSelection selection, PreparedStatement statement) throws SQLException {
        if (LimitHelper.hasMaxRows(selection)) {
            int maxRows = selection.getMaxRows() + this.convertToFirstRowValue(LimitHelper.getFirstRow(selection));
            if (maxRows < 0) {
                statement.setMaxRows(Integer.MAX_VALUE);
            } else {
                statement.setMaxRows(maxRows);
            }
        }
    }
}

