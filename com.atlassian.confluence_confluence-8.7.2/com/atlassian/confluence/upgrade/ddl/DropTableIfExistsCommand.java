/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 */
package com.atlassian.confluence.upgrade.ddl;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.pages.persistence.dao.DbEscapeHelper;
import com.atlassian.confluence.upgrade.ddl.DdlCommand;

public class DropTableIfExistsCommand
implements DdlCommand {
    private final HibernateConfig config;
    private final DbEscapeHelper escapeHelper;
    private final String tableName;
    private boolean escapeTableName;

    public static DropTableIfExistsCommand withEscapingTableName(HibernateConfig config, String tableName) {
        return new DropTableIfExistsCommand(config, tableName, true);
    }

    public static DropTableIfExistsCommand withoutEscapingTableName(HibernateConfig config, String tableName) {
        return new DropTableIfExistsCommand(config, tableName, false);
    }

    private DropTableIfExistsCommand(HibernateConfig config, String tableName, boolean escapeTableName) {
        this.config = config;
        this.escapeHelper = new DbEscapeHelper(config);
        this.tableName = tableName;
        this.escapeTableName = escapeTableName;
    }

    @Override
    public String getStatement() {
        if (this.config.isPostgreSql() || this.config.isMySql() || this.config.isH2()) {
            String maybeEscapedTable = this.escapeTableName ? this.escapeHelper.escapeIdentifier(this.tableName) : this.tableName;
            return "DROP TABLE IF EXISTS " + maybeEscapedTable;
        }
        if (this.config.isSqlServer()) {
            return "IF OBJECT_ID('" + this.tableName + "', 'U') IS NOT NULL   DROP TABLE " + this.tableName + ";";
        }
        if (this.config.isOracle()) {
            return "BEGIN       EXECUTE IMMEDIATE 'DROP TABLE " + this.tableName + "';   EXCEPTION       WHEN OTHERS THEN NULL;   END;";
        }
        throw new IllegalStateException("Unknown database provider");
    }
}

