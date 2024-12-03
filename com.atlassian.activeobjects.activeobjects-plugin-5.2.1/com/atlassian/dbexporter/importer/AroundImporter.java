/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.importer;

import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.importer.ImportConfiguration;
import com.atlassian.dbexporter.node.NodeParser;

public interface AroundImporter {
    public void before(NodeParser var1, ImportConfiguration var2, Context var3);

    public void after(NodeParser var1, ImportConfiguration var2, Context var3);
}

