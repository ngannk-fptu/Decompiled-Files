/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DataSourceProvider
 *  com.atlassian.activeobjects.spi.PluginInformation
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 */
package com.atlassian.activeobjects.admin.tables;

import com.atlassian.activeobjects.admin.tables.RowCounter;
import com.atlassian.activeobjects.backup.ActiveObjectsBackup;
import com.atlassian.activeobjects.backup.ActiveObjectsTableReader;
import com.atlassian.activeobjects.backup.PluginInformationFactory;
import com.atlassian.activeobjects.internal.DatabaseProviderFactory;
import com.atlassian.activeobjects.spi.DataSourceProvider;
import com.atlassian.activeobjects.spi.PluginInformation;
import com.atlassian.dbexporter.DatabaseInformation;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.Table;
import com.atlassian.dbexporter.exporter.TableReader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collections;
import java.util.Objects;
import net.java.ao.DatabaseProvider;
import net.java.ao.schema.NameConverters;

public final class TablesController {
    private final DatabaseProviderFactory databaseProviderFactory;
    private final ImportExportErrorService errorService;
    private final NameConverters nameConverters;
    private final PluginInformationFactory pluginInformationFactory;
    private final DataSourceProvider dataSourceProvider;

    public TablesController(DatabaseProviderFactory databaseProviderFactory, NameConverters nameConverters, DataSourceProvider dataSourceProvider, ImportExportErrorService errorService, PluginInformationFactory pluginInformationFactory) {
        this.databaseProviderFactory = Objects.requireNonNull(databaseProviderFactory);
        this.errorService = Objects.requireNonNull(errorService);
        this.nameConverters = Objects.requireNonNull(nameConverters);
        this.pluginInformationFactory = Objects.requireNonNull(pluginInformationFactory);
        this.dataSourceProvider = Objects.requireNonNull(dataSourceProvider);
    }

    public Multimap<PluginInformation, TableInformation> list() {
        DatabaseProvider databaseProvider = this.getDatabaseProvider();
        Iterable<Table> tables = this.readTables(this.newTableReader(databaseProvider));
        RowCounter rowCounter = RowCounter.from(databaseProvider);
        return this.tablesPerPlugin(tables, rowCounter);
    }

    private Iterable<Table> readTables(TableReader tableReader) {
        return tableReader.read(new DatabaseInformation(Collections.emptyMap()), new ActiveObjectsBackup.UpperCaseEntityNameProcessor());
    }

    private ActiveObjectsTableReader newTableReader(DatabaseProvider databaseProvider) {
        return new ActiveObjectsTableReader(this.errorService, this.nameConverters, databaseProvider, ActiveObjectsBackup.schemaConfiguration());
    }

    private DatabaseProvider getDatabaseProvider() {
        return this.databaseProviderFactory.getDatabaseProvider(this.dataSourceProvider.getDataSource(), this.dataSourceProvider.getDatabaseType(), this.dataSourceProvider.getSchema());
    }

    private Multimap<PluginInformation, TableInformation> tablesPerPlugin(Iterable<Table> tables, RowCounter rowCounter) {
        HashMultimap tablesPerPlugin = HashMultimap.create();
        for (Table table : tables) {
            String tableName = table.getName();
            tablesPerPlugin.put((Object)this.newPluginInformation(tableName), (Object)this.newTableInformation(tableName, rowCounter));
        }
        return tablesPerPlugin;
    }

    private PluginInformation newPluginInformation(String tableName) {
        return this.pluginInformationFactory.getPluginInformation(tableName);
    }

    private TableInformation newTableInformation(String tableName, RowCounter rowCounter) {
        return new TableInformation(tableName, rowCounter.count(tableName));
    }

    public static final class TableInformation {
        private final String table;
        private final String rows;

        public TableInformation(String table, int rows) {
            this.table = Objects.requireNonNull(table);
            this.rows = String.valueOf(rows);
        }

        public String getTable() {
            return this.table;
        }

        public String getRows() {
            return this.rows;
        }
    }
}

