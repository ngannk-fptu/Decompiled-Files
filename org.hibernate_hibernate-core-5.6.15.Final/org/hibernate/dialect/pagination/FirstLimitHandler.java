/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import org.hibernate.dialect.pagination.LegacyFirstLimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;

public class FirstLimitHandler
extends LegacyFirstLimitHandler {
    public static final FirstLimitHandler INSTANCE = new FirstLimitHandler();

    private FirstLimitHandler() {
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        boolean hasOffset = LimitHelper.hasFirstRow(selection);
        if (hasOffset) {
            throw new UnsupportedOperationException("query result offset is not supported");
        }
        return super.processSql(sql, selection);
    }
}

