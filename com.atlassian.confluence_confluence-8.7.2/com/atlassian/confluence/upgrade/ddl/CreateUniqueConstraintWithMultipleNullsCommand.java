/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.upgrade.ddl.DdlCommand;

public class CreateUniqueConstraintWithMultipleNullsCommand
implements DdlCommand {
    private final boolean isSqlServer;
    private final String constraintName;
    private final String tableName;
    private final String columnName;

    public CreateUniqueConstraintWithMultipleNullsCommand(HibernateDatabaseCapabilities databaseCapabilities, String constraintName, String tableName, String columnName) {
        this(databaseCapabilities.isSqlServer(), constraintName, tableName, columnName);
    }

    @Deprecated
    public CreateUniqueConstraintWithMultipleNullsCommand(HibernateConfig hibernateConfig, String constraintName, String tableName, String columnName) {
        this(hibernateConfig.isSqlServer(), constraintName, tableName, columnName);
    }

    private CreateUniqueConstraintWithMultipleNullsCommand(boolean isSqlServer, String constraintName, String tableName, String columnName) {
        this.isSqlServer = isSqlServer;
        this.constraintName = constraintName;
        this.tableName = tableName;
        this.columnName = columnName;
    }

    @Override
    public String getStatement() {
        if (this.isSqlServer) {
            return "create unique index " + this.tableName + "_" + this.constraintName + " on " + this.tableName + "(" + this.columnName + ") where " + this.columnName + " is not null";
        }
        return "alter table " + this.tableName + " add constraint " + this.constraintName + " unique (" + this.columnName + ")";
    }
}

