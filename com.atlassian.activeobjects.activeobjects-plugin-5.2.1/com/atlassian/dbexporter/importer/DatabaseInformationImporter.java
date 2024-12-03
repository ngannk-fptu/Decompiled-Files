/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.importer;

import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.DatabaseInformation;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.importer.AbstractSingleNodeImporter;
import com.atlassian.dbexporter.importer.DatabaseInformationChecker;
import com.atlassian.dbexporter.importer.ImportConfiguration;
import com.atlassian.dbexporter.importer.ImporterUtils;
import com.atlassian.dbexporter.node.NodeBackup;
import com.atlassian.dbexporter.node.NodeParser;
import com.atlassian.dbexporter.progress.ProgressMonitor;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class DatabaseInformationImporter
extends AbstractSingleNodeImporter {
    private final DatabaseInformationChecker infoChecker;

    public DatabaseInformationImporter(ImportExportErrorService errorService) {
        this(errorService, NoOpDatabaseInformationChecker.INSTANCE);
    }

    public DatabaseInformationImporter(ImportExportErrorService errorService, DatabaseInformationChecker infoChecker) {
        super(errorService);
        this.infoChecker = Objects.requireNonNull(infoChecker);
    }

    @Override
    protected void doImportNode(NodeParser node, ImportConfiguration configuration, Context context) {
        ProgressMonitor monitor = configuration.getProgressMonitor();
        monitor.begin(ProgressMonitor.Task.DATABASE_INFORMATION, new Object[0]);
        DatabaseInformation info = this.doImportDatabaseInformation(node);
        this.infoChecker.check(info);
        context.put(info);
        monitor.end(ProgressMonitor.Task.DATABASE_INFORMATION, new Object[0]);
    }

    private DatabaseInformation doImportDatabaseInformation(NodeParser node) {
        HashMap<String, String> meta = new HashMap<String, String>();
        ImporterUtils.checkStartNode(node, "database");
        this.doImportMetas(node.getNextNode(), meta);
        ImporterUtils.checkEndNode(node, "database");
        node.getNextNode();
        return new DatabaseInformation(meta);
    }

    private void doImportMetas(NodeParser node, Map<String, String> meta) {
        while (node.getName().equals("meta")) {
            this.doImportMeta(node, meta);
        }
    }

    private void doImportMeta(NodeParser node, Map<String, String> meta) {
        ImporterUtils.checkStartNode(node, "meta");
        meta.put(NodeBackup.DatabaseInformationNode.getMetaKey(node), NodeBackup.DatabaseInformationNode.getMetaValue(node));
        ImporterUtils.checkEndNode(node.getNextNode(), "meta");
        node.getNextNode();
    }

    @Override
    protected String getNodeName() {
        return "database";
    }

    private static final class NoOpDatabaseInformationChecker
    implements DatabaseInformationChecker {
        private static final DatabaseInformationChecker INSTANCE = new NoOpDatabaseInformationChecker();

        private NoOpDatabaseInformationChecker() {
        }

        @Override
        public void check(DatabaseInformation information) {
        }
    }
}

