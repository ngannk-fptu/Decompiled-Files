/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.pagination;

import java.util.Locale;
import org.hibernate.dialect.pagination.AbstractLimitHandler;
import org.hibernate.engine.spi.RowSelection;

public class LegacyFirstLimitHandler
extends AbstractLimitHandler {
    public static final LegacyFirstLimitHandler INSTANCE = new LegacyFirstLimitHandler();

    LegacyFirstLimitHandler() {
    }

    @Override
    public String processSql(String sql, RowSelection selection) {
        return new StringBuilder(sql.length() + 16).append(sql).insert(sql.toLowerCase(Locale.ROOT).indexOf("select") + 6, " first " + this.getMaxOrLimit(selection)).toString();
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
        return false;
    }

    @Override
    public boolean supportsVariableLimit() {
        return false;
    }
}

