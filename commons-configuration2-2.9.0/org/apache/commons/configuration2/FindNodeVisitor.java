/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import org.apache.commons.configuration2.tree.ConfigurationNodeVisitorAdapter;
import org.apache.commons.configuration2.tree.NodeHandler;

class FindNodeVisitor<T>
extends ConfigurationNodeVisitorAdapter<T> {
    private final T searchNode;
    private boolean found;

    public FindNodeVisitor(T node) {
        this.searchNode = node;
    }

    @Override
    public void visitBeforeChildren(T node, NodeHandler<T> handler) {
        if (node.equals(this.searchNode)) {
            this.found = true;
        }
    }

    @Override
    public boolean terminate() {
        return this.found;
    }

    public boolean isFound() {
        return this.found;
    }

    public void reset() {
        this.found = false;
    }
}

