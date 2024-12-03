/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.exporter;

import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.DatabaseInformation;
import com.atlassian.dbexporter.exporter.DatabaseInformationReader;
import com.atlassian.dbexporter.exporter.ExportConfiguration;
import com.atlassian.dbexporter.exporter.Exporter;
import com.atlassian.dbexporter.node.NodeBackup;
import com.atlassian.dbexporter.node.NodeCreator;
import com.atlassian.dbexporter.progress.ProgressMonitor;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class DatabaseInformationExporter
implements Exporter {
    private final DatabaseInformationReader databaseInformationReader;

    public DatabaseInformationExporter() {
        this(Collections::emptyMap);
    }

    public DatabaseInformationExporter(DatabaseInformationReader databaseInformationReader) {
        this.databaseInformationReader = Objects.requireNonNull(databaseInformationReader);
    }

    @Override
    public void export(NodeCreator node, ExportConfiguration configuration, Context context) {
        ProgressMonitor monitor = configuration.getProgressMonitor();
        monitor.begin(ProgressMonitor.Task.DATABASE_INFORMATION, new Object[0]);
        Map<String, String> properties = this.databaseInformationReader.get();
        if (!properties.isEmpty()) {
            this.export(node, properties);
        }
        context.put(new DatabaseInformation(properties));
        monitor.end(ProgressMonitor.Task.DATABASE_INFORMATION, new Object[0]);
    }

    private void export(NodeCreator node, Map<String, String> properties) {
        node.addNode("database");
        for (Map.Entry<String, String> property : properties.entrySet()) {
            NodeBackup.DatabaseInformationNode.addMeta(node, property.getKey(), property.getValue());
        }
        node.closeEntity();
    }
}

