/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.exporter;

import com.atlassian.dbexporter.Column;
import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.DatabaseInformation;
import com.atlassian.dbexporter.ForeignKey;
import com.atlassian.dbexporter.Table;
import com.atlassian.dbexporter.exporter.ExportConfiguration;
import com.atlassian.dbexporter.exporter.Exporter;
import com.atlassian.dbexporter.exporter.TableReader;
import com.atlassian.dbexporter.node.NodeBackup;
import com.atlassian.dbexporter.node.NodeCreator;
import com.atlassian.dbexporter.progress.ProgressMonitor;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class TableDefinitionExporter
implements Exporter {
    private final TableReader tableReader;

    public TableDefinitionExporter(TableReader tableReader) {
        this.tableReader = Objects.requireNonNull(tableReader);
    }

    @Override
    public void export(NodeCreator node, ExportConfiguration configuration, Context context) {
        ProgressMonitor monitor = configuration.getProgressMonitor();
        monitor.begin(ProgressMonitor.Task.TABLE_DEFINITION, new Object[0]);
        int tableCount = 0;
        Iterable<Table> tables = this.tableReader.read(this.getDatabaseInformation(context), configuration.getEntityNameProcessor());
        for (Table table : tables) {
            this.export(node, table);
            ++tableCount;
        }
        monitor.end(ProgressMonitor.Task.TABLE_DEFINITION, new Object[0]);
        monitor.totalNumberOfTables(tableCount);
        Collection tablesToInsert = StreamSupport.stream(tables.spliterator(), false).collect(Collectors.toList());
        context.putAll(tablesToInsert);
    }

    private DatabaseInformation getDatabaseInformation(Context context) {
        return Objects.requireNonNull(context.get(DatabaseInformation.class));
    }

    private void export(NodeCreator node, Table table) {
        NodeBackup.TableDefinitionNode.add(node);
        NodeBackup.TableDefinitionNode.setName(node, table.getName());
        for (Column column : table.getColumns()) {
            this.export(node, column);
        }
        for (ForeignKey foreignKey : table.getForeignKeys()) {
            this.export(node, foreignKey);
        }
        node.closeEntity();
    }

    private void export(NodeCreator node, Column column) {
        NodeBackup.ColumnDefinitionNode.add(node);
        NodeBackup.ColumnDefinitionNode.setName(node, column.getName());
        NodeBackup.ColumnDefinitionNode.setPrimaryKey(node, column.isPrimaryKey());
        NodeBackup.ColumnDefinitionNode.setAutoIncrement(node, column.isAutoIncrement());
        NodeBackup.ColumnDefinitionNode.setSqlType(node, column.getSqlType());
        NodeBackup.ColumnDefinitionNode.setPrecision(node, column.getPrecision());
        NodeBackup.ColumnDefinitionNode.setScale(node, column.getScale());
        node.closeEntity();
    }

    private void export(NodeCreator node, ForeignKey foreignKey) {
        NodeBackup.ForeignKeyDefinitionNode.add(node);
        NodeBackup.ForeignKeyDefinitionNode.setFromTable(node, foreignKey.getFromTable());
        NodeBackup.ForeignKeyDefinitionNode.setFromColumn(node, foreignKey.getFromField());
        NodeBackup.ForeignKeyDefinitionNode.setToTable(node, foreignKey.getToTable());
        NodeBackup.ForeignKeyDefinitionNode.setToColumn(node, foreignKey.getToField());
        node.closeEntity();
    }
}

