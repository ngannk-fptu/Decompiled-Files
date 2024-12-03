/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter;

import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.exporter.ExportConfiguration;
import com.atlassian.dbexporter.exporter.Exporter;
import com.atlassian.dbexporter.node.NodeBackup;
import com.atlassian.dbexporter.node.NodeCreator;
import com.atlassian.dbexporter.node.NodeStreamWriter;
import com.atlassian.dbexporter.progress.ProgressMonitor;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class DbExporter {
    private final List<Exporter> exporters;

    public DbExporter(Exporter ... exporters) {
        this(Arrays.asList((Object[])Objects.requireNonNull(exporters)));
    }

    public DbExporter(List<Exporter> exporters) {
        if (Objects.requireNonNull(exporters).isEmpty()) {
            throw new IllegalArgumentException("DbExporter must be created with at least one Exporter!");
        }
        this.exporters = exporters;
    }

    public void exportData(NodeStreamWriter streamWriter, ExportConfiguration configuration) {
        ProgressMonitor monitor = configuration.getProgressMonitor();
        monitor.begin(new Object[0]);
        NodeCreator node = NodeBackup.RootNode.add(streamWriter);
        Context context = new Context(new Object[0]);
        for (Exporter exporter : this.exporters) {
            exporter.export(node, configuration, context);
        }
        monitor.end(new Object[0]);
    }
}

