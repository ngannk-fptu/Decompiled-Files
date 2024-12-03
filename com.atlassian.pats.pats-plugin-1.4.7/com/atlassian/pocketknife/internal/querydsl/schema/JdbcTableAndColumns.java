/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.atlassian.fugue.Option
 */
package com.atlassian.pocketknife.internal.querydsl.schema;

import io.atlassian.fugue.Option;
import java.util.LinkedHashSet;

public class JdbcTableAndColumns {
    private final Option<String> tableName;
    private final LinkedHashSet<String> tableColumns;

    public JdbcTableAndColumns(Option<String> tableName, LinkedHashSet<String> tableColumns) {
        this.tableName = tableName;
        this.tableColumns = tableColumns;
    }

    public LinkedHashSet<String> getColumnNames() {
        return this.tableColumns;
    }

    public Option<String> getTableName() {
        return this.tableName;
    }
}

