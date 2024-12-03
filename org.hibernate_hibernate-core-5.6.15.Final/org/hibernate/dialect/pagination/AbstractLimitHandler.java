/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.hibernate.dialect.pagination.LimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;

public abstract class AbstractLimitHandler
implements LimitHandler {
    protected AbstractLimitHandler() {
    }

    @Override
    public boolean supportsLimit() {
        return false;
    }

    @Override
    public boolean supportsLimitOffset() {
        return this.supportsLimit();
    }

    public boolean supportsVariableLimit() {
        return this.supportsLimit();
    }

    public boolean bindLimitParametersInReverseOrder() {
        return false;
    }

    public boolean bindLimitParametersFirst() {
        return false;
    }

    public boolean useMaxForLimit() {
        return false;
    }

    public boolean forceLimitUsage() {
        return false;
    }

    public int convertToFirstRowValue(int zeroBasedFirstResult) {
        return zeroBasedFirstResult;
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        throw new UnsupportedOperationException("Paged queries not supported by " + this.getClass().getName());
    }

    @Override
    public int bindLimitParametersAtStartOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException {
        return this.bindLimitParametersFirst() ? this.bindLimitParameters(selection, statement, index) : 0;
    }

    @Override
    public int bindLimitParametersAtEndOfQuery(RowSelection selection, PreparedStatement statement, int index) throws SQLException {
        return !this.bindLimitParametersFirst() ? this.bindLimitParameters(selection, statement, index) : 0;
    }

    @Override
    public void setMaxRows(RowSelection selection, PreparedStatement statement) throws SQLException {
    }

    protected final int bindLimitParameters(RowSelection selection, PreparedStatement statement, int index) throws SQLException {
        if (!this.supportsVariableLimit() || !LimitHelper.hasMaxRows(selection)) {
            return 0;
        }
        int firstRow = this.convertToFirstRowValue(LimitHelper.getFirstRow(selection));
        int lastRow = this.getMaxOrLimit(selection);
        boolean hasFirstRow = this.supportsLimitOffset() && (firstRow > 0 || this.forceLimitUsage());
        boolean reverse = this.bindLimitParametersInReverseOrder();
        if (hasFirstRow) {
            statement.setInt(index + (reverse ? 1 : 0), firstRow);
        }
        statement.setInt(index + (reverse || !hasFirstRow ? 0 : 1), lastRow);
        return hasFirstRow ? 2 : 1;
    }

    protected final int getMaxOrLimit(RowSelection selection) {
        int maxRows;
        int firstRow = this.convertToFirstRowValue(LimitHelper.getFirstRow(selection));
        int lastRow = selection.getMaxRows();
        int n = maxRows = this.useMaxForLimit() ? lastRow + firstRow : lastRow;
        if (maxRows < 0) {
            return Integer.MAX_VALUE;
        }
        return maxRows;
    }
}

