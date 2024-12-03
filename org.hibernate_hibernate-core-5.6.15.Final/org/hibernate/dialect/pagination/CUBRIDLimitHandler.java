/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;

public class CUBRIDLimitHandler
extends AbstractLimitHandler {
    public static final CUBRIDLimitHandler INSTANCE = new CUBRIDLimitHandler();

    private CUBRIDLimitHandler() {
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        if (LimitHelper.useLimit(this, selection)) {
            boolean useLimitOffset = LimitHelper.hasFirstRow(selection);
            return sql + (useLimitOffset ? " limit ?, ?" : " limit ?");
        }
        return sql;
    }
}

