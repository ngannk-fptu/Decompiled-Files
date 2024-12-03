/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.backup;

import com.atlassian.activeobjects.backup.SqlUtils;
import com.atlassian.activeobjects.osgi.ActiveObjectsServiceFactory;
import com.atlassian.dbexporter.CleanupMode;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.importer.DatabaseCleaner;
import com.atlassian.dbexporter.jdbc.JdbcUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.java.ao.DatabaseProvider;
import net.java.ao.SchemaConfiguration;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.ddl.DDLAction;
import net.java.ao.schema.ddl.DDLTable;
import net.java.ao.schema.ddl.SQLAction;
import net.java.ao.schema.ddl.SchemaReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class ActiveObjectsDatabaseCleaner
implements DatabaseCleaner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ActiveObjectsServiceFactory aoServiceFactory;
    private final DatabaseProvider provider;
    private final ImportExportErrorService errorService;
    private final NameConverters converters;
    private final SchemaConfiguration schemaConfiguration;

    public ActiveObjectsDatabaseCleaner(DatabaseProvider provider, NameConverters converters, SchemaConfiguration schemaConfiguration, ImportExportErrorService errorService, ActiveObjectsServiceFactory aoServiceFactory) {
        this.aoServiceFactory = Objects.requireNonNull(aoServiceFactory);
        this.converters = Objects.requireNonNull(converters);
        this.errorService = Objects.requireNonNull(errorService);
        this.provider = Objects.requireNonNull(provider);
        this.schemaConfiguration = Objects.requireNonNull(schemaConfiguration);
    }

    @Override
    public void cleanup(CleanupMode cleanupMode) {
        if (cleanupMode.equals((Object)CleanupMode.CLEAN)) {
            this.doCleanup();
        } else {
            this.logger.debug("Not cleaning up database before import. Any existing entity with the same name of entity being imported will make the import fail.");
        }
    }

    private void doCleanup() {
        Connection conn = null;
        Statement stmt = null;
        try {
            this.aoServiceFactory.startCleaning();
            DDLTable[] readTables = SchemaReader.readSchema(this.provider, this.converters, this.schemaConfiguration);
            DDLAction[] actions = SchemaReader.sortTopologically(SchemaReader.diffSchema(this.provider.getTypeManager(), new DDLTable[0], readTables, this.provider.isCaseSensitive()));
            conn = this.provider.getConnection();
            stmt = conn.createStatement();
            for (DDLAction a : actions) {
                Iterable<SQLAction> sqlActions = this.provider.renderAction(this.converters, a);
                for (SQLAction sql : sqlActions) {
                    SqlUtils.executeUpdate(this.errorService, ActiveObjectsDatabaseCleaner.tableName(a), stmt, sql.getStatement());
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
                this.aoServiceFactory.stopCleaning();
                throw throwable;
            }
        }
        JdbcUtils.closeQuietly(new Statement[]{stmt});
        JdbcUtils.closeQuietly(conn);
        this.aoServiceFactory.stopCleaning();
    }

    @Nullable
    private static String tableName(DDLAction action) {
        return Optional.ofNullable(action).map(DDLAction::getTable).map(DDLTable::getName).orElse(null);
    }
}

