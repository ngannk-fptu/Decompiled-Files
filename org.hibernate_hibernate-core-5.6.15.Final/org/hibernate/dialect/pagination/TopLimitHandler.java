/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import java.util.Locale;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;

public class TopLimitHandler
extends AbstractLimitHandler {
    private final boolean supportsVariableLimit;
    private final boolean bindLimitParametersFirst;

    public TopLimitHandler(boolean supportsVariableLimit, boolean bindLimitParametersFirst) {
        this.supportsVariableLimit = supportsVariableLimit;
        this.bindLimitParametersFirst = bindLimitParametersFirst;
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public boolean useMaxForLimit() {
        return true;
    }

    @Override
    public boolean supportsLimitOffset() {
        return this.supportsVariableLimit;
    }

    @Override
    public boolean supportsVariableLimit() {
        return this.supportsVariableLimit;
    }

    @Override
    public boolean bindLimitParametersFirst() {
        return this.bindLimitParametersFirst;
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        if (LimitHelper.hasFirstRow(selection)) {
            throw new UnsupportedOperationException("query result offset is not supported");
        }
        int selectIndex = sql.toLowerCase(Locale.ROOT).indexOf("select");
        int selectDistinctIndex = sql.toLowerCase(Locale.ROOT).indexOf("select distinct");
        int insertionPoint = selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
        StringBuilder sb = new StringBuilder(sql.length() + 8).append(sql);
        if (this.supportsVariableLimit) {
            sb.insert(insertionPoint, " TOP ? ");
        } else {
            sb.insert(insertionPoint, " TOP " + this.getMaxOrLimit(selection) + " ");
        }
        return sb.toString();
    }
}

