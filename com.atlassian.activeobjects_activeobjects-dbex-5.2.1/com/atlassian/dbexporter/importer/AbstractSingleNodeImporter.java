/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.importer;

import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.importer.AbstractImporter;
import com.atlassian.dbexporter.importer.AroundImporter;
import com.atlassian.dbexporter.node.NodeParser;
import java.util.List;
import java.util.Objects;

public abstract class AbstractSingleNodeImporter
extends AbstractImporter {
    protected AbstractSingleNodeImporter(ImportExportErrorService errorService) {
        super(errorService);
    }

    protected AbstractSingleNodeImporter(ImportExportErrorService errorService, List<AroundImporter> arounds) {
        super(errorService, arounds);
    }

    @Override
    public final boolean supports(NodeParser node) {
        return Objects.requireNonNull(node).getName().equals(this.getNodeName());
    }

    protected abstract String getNodeName();
}

