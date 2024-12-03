/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.upgrade.ddl.DdlCommand;

public class DropIndexCommand
implements DdlCommand {
    private final HibernateDatabaseCapabilities databaseCapabilities;
    private final String indexName;
    private final String tableName;

    public DropIndexCommand(HibernateDatabaseCapabilities databaseCapabilities, String indexName, String tableName) {
        this.databaseCapabilities = databaseCapabilities;
        this.indexName = indexName;
        this.tableName = tableName;
    }

    @Override
    public String getStatement() {
        String sql = "drop index " + this.indexName;
        if (this.databaseCapabilities.isMySql() || this.databaseCapabilities.isSqlServer()) {
            sql = sql + " on " + this.tableName;
        }
        return sql;
    }
}

