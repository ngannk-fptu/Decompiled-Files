/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl.mappings;

import com.atlassian.confluence.extra.calendar3.querydsl.DatabaseNameHelper;
import com.querydsl.sql.RelationalPathBase;

public abstract class AbstractRelationalPathBase<T>
extends RelationalPathBase<T> {
    private final DatabaseNameHelper databaseNameHelper;

    public AbstractRelationalPathBase(Class<? extends T> type, String alias, String schema, String table, DatabaseNameHelper databaseNameHelper) {
        super(type, alias != null ? alias : databaseNameHelper.getCaseSensitiveTableName(table), schema, databaseNameHelper.getCaseSensitiveTableName(table));
        this.databaseNameHelper = databaseNameHelper;
    }

    protected String columnName(String columnName) {
        return this.databaseNameHelper.getCaseSensitiveColumnName(this.getTableName(), columnName);
    }
}

