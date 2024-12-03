/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter;

import com.atlassian.dbexporter.EntityNameProcessor;

public final class NoOpEntityNameProcessor
implements EntityNameProcessor {
    @Override
    public String tableName(String table) {
        return table;
    }

    @Override
    public String columnName(String column) {
        return column;
    }
}

