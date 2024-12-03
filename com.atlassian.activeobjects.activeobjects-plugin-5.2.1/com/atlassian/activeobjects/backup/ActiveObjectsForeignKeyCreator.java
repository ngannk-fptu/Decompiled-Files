/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.activeobjects.backup.ForeignKeyCreator;
import com.atlassian.activeobjects.backup.SqlUtils;
import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.ForeignKey;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.jdbc.JdbcUtils;
import com.google.common.base.Preconditions;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import net.java.ao.DatabaseProvider;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.ddl.DDLAction;
import net.java.ao.schema.ddl.DDLActionType;
import net.java.ao.schema.ddl.DDLForeignKey;
import net.java.ao.schema.ddl.SQLAction;

final class ActiveObjectsForeignKeyCreator
implements ForeignKeyCreator {
    private final ImportExportErrorService errorService;
    private final NameConverters converters;
    private final DatabaseProvider provider;

    public ActiveObjectsForeignKeyCreator(ImportExportErrorService errorService, NameConverters converters, DatabaseProvider provider) {
        this.errorService = (ImportExportErrorService)Preconditions.checkNotNull((Object)errorService);
        this.converters = (NameConverters)Preconditions.checkNotNull((Object)converters);
        this.provider = (DatabaseProvider)Preconditions.checkNotNull((Object)provider);
    }

    @Override
    public void create(Iterable<ForeignKey> foreignKeys, EntityNameProcessor entityNameProcessor) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = this.provider.getConnection();
            stmt = conn.createStatement();
            for (ForeignKey foreignKey : foreignKeys) {
                DDLAction a = new DDLAction(DDLActionType.ALTER_ADD_KEY);
                a.setKey(this.toDdlForeignKey(foreignKey, entityNameProcessor));
                Iterable<SQLAction> sqlActions = this.provider.renderAction(this.converters, a);
                for (SQLAction sql : sqlActions) {
                    SqlUtils.executeUpdate(this.errorService, this.tableName(a), stmt, sql.getStatement());
                }
            }
        }
        catch (SQLException e) {
            try {
                throw this.errorService.newImportExportSqlException(null, "", e);
            }
            catch (Throwable throwable) {
                JdbcUtils.closeQuietly(new Statement[]{stmt});
                JdbcUtils.closeQuietly(conn);
                throw throwable;
            }
        }
        JdbcUtils.closeQuietly(new Statement[]{stmt});
        JdbcUtils.closeQuietly(conn);
    }

    private String tableName(DDLAction a) {
        if (a == null) {
            return null;
        }
        if (a.getTable() == null) {
            return null;
        }
        return a.getTable().getName();
    }

    private DDLForeignKey toDdlForeignKey(ForeignKey foreignKey, EntityNameProcessor entityNameProcessor) {
        DDLForeignKey ddlForeignKey = new DDLForeignKey();
        ddlForeignKey.setDomesticTable(entityNameProcessor.tableName(foreignKey.getFromTable()));
        ddlForeignKey.setField(entityNameProcessor.columnName(foreignKey.getFromField()));
        ddlForeignKey.setTable(entityNameProcessor.tableName(foreignKey.getToTable()));
        ddlForeignKey.setForeignField(entityNameProcessor.columnName(foreignKey.getToField()));
        return ddlForeignKey;
    }
}

