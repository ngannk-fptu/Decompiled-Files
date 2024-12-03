/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.dbexporter;

import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.DatabaseInformations;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.importer.ImportConfiguration;
import com.atlassian.dbexporter.importer.Importer;
import com.atlassian.dbexporter.importer.ImporterUtils;
import com.atlassian.dbexporter.importer.NoOpImporter;
import com.atlassian.dbexporter.node.NodeBackup;
import com.atlassian.dbexporter.node.NodeParser;
import com.atlassian.dbexporter.node.NodeStreamReader;
import com.atlassian.dbexporter.progress.ProgressMonitor;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DbImporter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ImportExportErrorService errorService;
    private final List<Importer> importers;

    public DbImporter(ImportExportErrorService errorService, Importer ... importers) {
        this(errorService, Stream.of((Object[])Objects.requireNonNull(importers)).collect(Collectors.toList()));
    }

    public DbImporter(ImportExportErrorService errorService, List<Importer> importers) {
        this.errorService = Objects.requireNonNull(errorService);
        if (Objects.requireNonNull(importers).isEmpty()) {
            throw new IllegalArgumentException("DbImporter must be created with at least one importer!");
        }
        this.importers = importers;
    }

    public void importData(NodeStreamReader streamReader, ImportConfiguration configuration) {
        ProgressMonitor monitor = configuration.getProgressMonitor();
        DatabaseInformations.Database database = DatabaseInformations.database(configuration.getDatabaseInformation());
        monitor.begin(database);
        NodeParser node = NodeBackup.RootNode.get(streamReader);
        this.logger.debug("Root node is {}", (Object)node);
        ImporterUtils.checkStartNode(node, "backup");
        node.getNextNode();
        Context context = new Context(new Object[0]);
        this.logger.debug("Starting import from node {}", (Object)node);
        do {
            this.getImporter(node).importNode(node, configuration, context);
        } while (!node.getName().equals("backup") || !node.isClosed());
        monitor.end(database);
    }

    private Importer getImporter(NodeParser node) {
        for (Importer importer : this.importers) {
            if (!importer.supports(node)) continue;
            this.logger.debug("Found importer {} for node {}", (Object)importer, (Object)node);
            return importer;
        }
        NoOpImporter noOpImporter = new NoOpImporter(this.errorService);
        this.logger.debug("Didn't find any importer for node {}, using {}", (Object)node, (Object)noOpImporter);
        return noOpImporter;
    }
}

