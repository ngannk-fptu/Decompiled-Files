/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.importer;

import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.importer.AroundImporter;
import com.atlassian.dbexporter.importer.ImportConfiguration;
import com.atlassian.dbexporter.node.NodeParser;

public class NoOpAroundImporter
implements AroundImporter {
    @Override
    public void before(NodeParser node, ImportConfiguration configuration, Context context) {
    }

    @Override
    public void after(NodeParser node, ImportConfiguration configuration, Context context) {
    }
}

