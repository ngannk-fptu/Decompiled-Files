/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.dialect.pagination.LimitHelper;
import org.hibernate.engine.spi.RowSelection;

public class SQL2008StandardLimitHandler
extends AbstractLimitHandler {
    public static final SQL2008StandardLimitHandler INSTANCE = new SQL2008StandardLimitHandler();

    private SQL2008StandardLimitHandler() {
    }

    @Override
    public boolean supportsLimit() {
        return true;
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        if (LimitHelper.useLimit(this, selection)) {
            return sql + (LimitHelper.hasFirstRow(selection) ? " offset ? rows fetch next ? rows only" : " fetch first ? rows only");
        }
        return sql;
    }
}

