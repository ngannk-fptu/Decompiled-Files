/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.dbexporter.importer;

import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.ImportExportErrorService;
import com.atlassian.dbexporter.importer.AroundImporter;
import com.atlassian.dbexporter.importer.ImportConfiguration;
import com.atlassian.dbexporter.importer.Importer;
import com.atlassian.dbexporter.node.NodeParser;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractImporter
implements Importer {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final ImportExportErrorService errorService;
    private final List<AroundImporter> arounds;

    protected AbstractImporter(ImportExportErrorService errorService) {
        this(errorService, Collections.emptyList());
    }

    protected AbstractImporter(ImportExportErrorService errorService, List<AroundImporter> arounds) {
        this.errorService = Objects.requireNonNull(errorService);
        this.arounds = Objects.requireNonNull(arounds);
    }

    @Override
    public final void importNode(NodeParser node, ImportConfiguration configuration, Context context) {
        Objects.requireNonNull(node);
        if (node.isClosed()) {
            throw new IllegalArgumentException("Node must not be closed to be imported! " + node);
        }
        if (!this.supports(node)) {
            throw new IllegalArgumentException("Importer called on unsupported node! " + node);
        }
        Objects.requireNonNull(context);
        this.logger.debug("Importing node {}", (Object)node);
        for (AroundImporter around : this.arounds) {
            around.before(node, configuration, context);
        }
        this.doImportNode(node, configuration, context);
        ListIterator<AroundImporter> iterator = this.arounds.listIterator(this.arounds.size());
        while (iterator.hasPrevious()) {
            iterator.previous().after(node, configuration, context);
        }
    }

    protected abstract void doImportNode(NodeParser var1, ImportConfiguration var2, Context var3);
}

