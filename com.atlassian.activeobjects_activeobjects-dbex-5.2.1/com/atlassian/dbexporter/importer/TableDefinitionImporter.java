/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.importer;

import com.atlassian.dbexporter.Column;
import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.DatabaseInformation;
import com.atlassian.dbexporter.EntityNameProcessor;
import com.atlassian.dbexporter.ForeignKey;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.Table;
import com.atlassian.dbexporter.importer.AbstractSingleNodeImporter;
import com.atlassian.dbexporter.importer.DatabaseCleaner;
import com.atlassian.dbexporter.importer.ImportConfiguration;
import com.atlassian.dbexporter.importer.ImporterUtils;
import com.atlassian.dbexporter.importer.NoOpAroundImporter;
import com.atlassian.dbexporter.importer.TableCreator;
import com.atlassian.dbexporter.node.NodeBackup;
import com.atlassian.dbexporter.node.NodeParser;
import com.atlassian.dbexporter.progress.ProgressMonitor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TableDefinitionImporter
extends AbstractSingleNodeImporter {
    private final TableCreator tableCreator;

    public TableDefinitionImporter(ImportExportErrorService errorService, TableCreator tableCreator, DatabaseCleaner databaseCleaner) {
        super(errorService, Stream.of(DatabaseCleanerAroundImporter.newCleaner(databaseCleaner)).collect(Collectors.toList()));
        this.tableCreator = Objects.requireNonNull(tableCreator);
    }

    @Override
    protected void doImportNode(NodeParser node, ImportConfiguration configuration, Context context) {
        ProgressMonitor monitor = configuration.getProgressMonitor();
        monitor.begin(ProgressMonitor.Task.TABLE_DEFINITION, new Object[0]);
        ArrayList<Table> tables = new ArrayList<Table>();
        while (ImporterUtils.isNodeNotClosed(node, this.getNodeName())) {
            tables.add(this.readTable(node, configuration.getEntityNameProcessor()));
        }
        monitor.end(ProgressMonitor.Task.TABLE_DEFINITION, new Object[0]);
        monitor.totalNumberOfTables(tables.size());
        DatabaseInformation databaseInformation = context.get(DatabaseInformation.class);
        this.tableCreator.create(databaseInformation, tables, configuration.getEntityNameProcessor(), monitor);
        context.putAll(tables);
    }

    private Table readTable(NodeParser node, EntityNameProcessor entityNameProcessor) {
        ImporterUtils.checkStartNode(node, "table");
        String tableName = entityNameProcessor.tableName(NodeBackup.TableDefinitionNode.getName(node));
        node.getNextNode();
        List<Column> columns = this.readColumns(node, entityNameProcessor);
        Collection<ForeignKey> foreignKeys = this.readForeignKeys(node);
        ImporterUtils.checkEndNode(node, "table");
        node.getNextNode();
        return new Table(tableName, columns, foreignKeys);
    }

    private List<Column> readColumns(NodeParser node, EntityNameProcessor entityNameProcessor) {
        ArrayList<Column> columns = new ArrayList<Column>();
        while (node.getName().equals("column")) {
            columns.add(this.readColumn(node, entityNameProcessor));
        }
        return columns;
    }

    private Column readColumn(NodeParser node, EntityNameProcessor entityNameProcessor) {
        ImporterUtils.checkStartNode(node, "column");
        String columnName = entityNameProcessor.columnName(NodeBackup.ColumnDefinitionNode.getName(node));
        boolean isPk = NodeBackup.ColumnDefinitionNode.isPrimaryKey(node);
        boolean isAi = NodeBackup.ColumnDefinitionNode.isAutoIncrement(node);
        int sqlType = NodeBackup.ColumnDefinitionNode.getSqlType(node);
        Integer precision = NodeBackup.ColumnDefinitionNode.getPrecision(node);
        Integer scale = NodeBackup.ColumnDefinitionNode.getScale(node);
        ImporterUtils.checkEndNode(node.getNextNode(), "column");
        node.getNextNode();
        return new Column(columnName, sqlType, isPk, isAi, precision, scale);
    }

    private Collection<ForeignKey> readForeignKeys(NodeParser node) {
        ArrayList<ForeignKey> fks = new ArrayList<ForeignKey>();
        while (node.getName().equals("foreignKey")) {
            fks.add(this.readForeignKey(node));
        }
        return fks;
    }

    private ForeignKey readForeignKey(NodeParser node) {
        ImporterUtils.checkStartNode(node, "foreignKey");
        String fromTable = NodeBackup.ForeignKeyDefinitionNode.getFromTable(node);
        String fromColumn = NodeBackup.ForeignKeyDefinitionNode.getFromColumn(node);
        String toTable = NodeBackup.ForeignKeyDefinitionNode.getToTable(node);
        String toColumn = NodeBackup.ForeignKeyDefinitionNode.getToColumn(node);
        ImporterUtils.checkEndNode(node.getNextNode(), "foreignKey");
        node.getNextNode();
        return new ForeignKey(fromTable, fromColumn, toTable, toColumn);
    }

    @Override
    protected String getNodeName() {
        return "table";
    }

    static final class DatabaseCleanerAroundImporter
    extends NoOpAroundImporter {
        private final DatabaseCleaner databaseCleaner;

        private DatabaseCleanerAroundImporter(DatabaseCleaner databaseCleaner) {
            this.databaseCleaner = Objects.requireNonNull(databaseCleaner);
        }

        static DatabaseCleanerAroundImporter newCleaner(DatabaseCleaner cleaner) {
            return new DatabaseCleanerAroundImporter(cleaner);
        }

        @Override
        public void before(NodeParser node, ImportConfiguration configuration, Context context) {
            this.databaseCleaner.cleanup(configuration.getCleanupMode());
        }
    }
}

