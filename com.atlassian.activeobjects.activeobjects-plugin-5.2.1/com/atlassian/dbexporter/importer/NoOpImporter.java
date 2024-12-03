/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.importer;

import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.importer.AbstractImporter;
import com.atlassian.dbexporter.importer.ImportConfiguration;
import com.atlassian.dbexporter.node.NodeParser;

public final class NoOpImporter
extends AbstractImporter {
    public NoOpImporter(ImportExportErrorService errorService) {
        super(errorService);
    }

    @Override
    protected final void doImportNode(NodeParser node, ImportConfiguration configuration, Context context) {
        if (node.isClosed()) {
            node.getNextNode();
            return;
        }
        String nodeName = node.getName();
        while (!node.getName().equals(nodeName) || !node.isClosed()) {
            node.getNextNode();
            if (!node.getName().equals(nodeName) || node.isClosed()) continue;
            this.doImportNode(node, configuration, context);
        }
    }

    @Override
    public final boolean supports(NodeParser node) {
        return true;
    }

    public String toString() {
        return "No Op Importer";
    }
}

