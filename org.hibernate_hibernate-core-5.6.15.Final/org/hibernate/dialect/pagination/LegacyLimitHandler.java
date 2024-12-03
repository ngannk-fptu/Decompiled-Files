/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;

public class LegacyLimitHandler
extends AbstractLimitHandler {
    private final Dialect dialect;

    public LegacyLimitHandler(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public boolean supportsLimit() {
        return this.dialect.supportsLimit();
    }

    @Override
    public boolean supportsLimitOffset() {
        return this.dialect.supportsLimitOffset();
    }

    @Override
    public boolean supportsVariableLimit() {
        return this.dialect.supportsVariableLimit();
    }

    @Override
    public boolean bindLimitParametersInReverseOrder() {
        return this.dialect.bindLimitParametersInReverseOrder();
    }

    @Override
    public boolean bindLimitParametersFirst() {
        return this.dialect.bindLimitParametersFirst();
    }

    @Override
    public boolean useMaxForLimit() {
        return this.dialect.useMaxForLimit();
    }

    @Override
    public boolean forceLimitUsage() {
        return this.dialect.forceLimitUsage();
    }

    @Override
    public int convertToFirstRowValue(int zeroBasedFirstResult) {
        return this.dialect.convertToFirstRowValue(zeroBasedFirstResult);
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        boolean useLimitOffset = this.supportsLimit() && this.supportsLimitOffset() && LimitHelper.hasFirstRow(selection) && LimitHelper.hasMaxRows(selection);
        return this.dialect.getLimitString(sql, useLimitOffset ? LimitHelper.getFirstRow(selection) : 0, this.getMaxOrLimit(selection));
    }
}

