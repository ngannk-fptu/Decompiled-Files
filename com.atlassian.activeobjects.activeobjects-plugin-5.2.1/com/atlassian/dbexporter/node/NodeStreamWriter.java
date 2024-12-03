/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dbexporter.node;

import com.atlassian.dbexporter.node.NodeCreator;
import java.io.Closeable;

public interface NodeStreamWriter
extends Closeable {
    public NodeCreator addRootNode(String var1) throws IllegalStateException;

    public void flush();

    @Override
    public void close();
}

