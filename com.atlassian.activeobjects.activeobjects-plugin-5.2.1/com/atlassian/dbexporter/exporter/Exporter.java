/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.exporter;

import com.atlassian.dbexporter.Context;
import com.atlassian.dbexporter.exporter.ExportConfiguration;
import com.atlassian.dbexporter.node.NodeCreator;

public interface Exporter {
    public void export(NodeCreator var1, ExportConfiguration var2, Context var3);
}

