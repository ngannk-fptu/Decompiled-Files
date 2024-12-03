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

public final class RenameTableCommand
implements DdlCommand {
    private final HibernateDatabaseCapabilities databaseCapabilities;
    private final String oldTableName;
    private final String newTableName;
    private final boolean oldTableNameQuoted;
    private final boolean newTableNameQuoted;

    public RenameTableCommand(HibernateDatabaseCapabilities databaseCapabilities, String oldTableName, String newTableName, boolean oldTableNameQuoted, boolean newTableNameQuoted) {
        this.databaseCapabilities = databaseCapabilities;
        this.oldTableName = oldTableName;
        this.newTableName = newTableName;
        this.oldTableNameQuoted = oldTableNameQuoted;
        this.newTableNameQuoted = newTableNameQuoted;
    }

    public RenameTableCommand(HibernateDatabaseCapabilities databaseCapabilities, String oldTableName, String newTableName) {
        this(databaseCapabilities, oldTableName, newTableName, false, false);
    }

    @Deprecated
    public RenameTableCommand(HibernateConfig hibernateConfig, String oldTableName, String newTableName, boolean oldTableNameQuoted, boolean newTableNameQuoted) {
        this.databaseCapabilities = HibernateDatabaseCapabilities.from(hibernateConfig);
        this.oldTableName = oldTableName;
        this.newTableName = newTableName;
        this.oldTableNameQuoted = oldTableNameQuoted;
        this.newTableNameQuoted = newTableNameQuoted;
    }

    @Deprecated
    public RenameTableCommand(HibernateConfig hibernateConfig, String oldTableName, String newTableName) {
        this(hibernateConfig, oldTableName, newTableName, false, false);
    }

    @Override
    public String getStatement() {
        if (this.databaseCapabilities.isSqlServer()) {
            return "sp_rename " + RenameTableCommand.escapeWithSingleQuotes(this.oldTableName) + ", " + RenameTableCommand.escapeWithSingleQuotes(this.newTableName);
        }
        return "alter table " + this.escapeTableName(this.oldTableName, this.oldTableNameQuoted) + " rename to " + this.escapeTableName(this.newTableName, this.newTableNameQuoted);
    }

    private String escapeTableName(String tableName, boolean quoted) {
        if (this.databaseCapabilities.isHSQL() || this.databaseCapabilities.isOracle() || this.databaseCapabilities.isPostgreSql() && quoted || this.databaseCapabilities.isH2()) {
            return RenameTableCommand.escapeWithDoubleQuotes(tableName);
        }
        if (this.databaseCapabilities.isMySql()) {
            return RenameTableCommand.escapeWithBackquotes(tableName);
        }
        if (this.databaseCapabilities.isSqlServer()) {
            return RenameTableCommand.escapeWithSingleQuotes(tableName);
        }
        return tableName;
    }

    private static String escapeWithSingleQuotes(String tableName) {
        return "'" + tableName.replaceAll("'", "''") + "'";
    }

    private static String escapeWithBackquotes(String tableName) {
        return "`" + tableName.replaceAll("`", "``") + "`";
    }

    private static String escapeWithDoubleQuotes(String tableName) {
        return "\"" + tableName.replaceAll("\"", "\"\"") + "\"";
    }
}

