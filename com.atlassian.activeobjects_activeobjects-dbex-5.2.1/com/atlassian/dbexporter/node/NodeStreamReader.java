/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.node;

import com.atlassian.dbexporter.node.NodeParser;
import java.io.Closeable;

public interface NodeStreamReader
extends Closeable {
    public NodeParser getRootNode() throws IllegalStateException;

    @Override
    public void close();
}

