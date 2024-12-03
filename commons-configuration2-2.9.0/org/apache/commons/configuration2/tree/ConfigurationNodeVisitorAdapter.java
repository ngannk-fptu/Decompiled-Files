/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import org.apache.commons.configuration2.tree.ConfigurationNodeVisitor;
import org.apache.commons.configuration2.tree.NodeHandler;

public class ConfigurationNodeVisitorAdapter<T>
implements ConfigurationNodeVisitor<T> {
    @Override
    public void visitBeforeChildren(T node, NodeHandler<T> handler) {
    }

    @Override
    public void visitAfterChildren(T node, NodeHandler<T> handler) {
    }

    @Override
    public boolean terminate() {
        return false;
    }
}

