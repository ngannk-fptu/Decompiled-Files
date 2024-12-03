/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import org.apache.commons.configuration2.tree.AbstractImmutableNodeHandler;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeHandler;

class TrackedNodeHandler
extends AbstractImmutableNodeHandler {
    private final ImmutableNode rootNode;
    private final NodeHandler<ImmutableNode> parentHandler;

    public TrackedNodeHandler(ImmutableNode root, NodeHandler<ImmutableNode> handler) {
        this.rootNode = root;
        this.parentHandler = handler;
    }

    public NodeHandler<ImmutableNode> getParentHandler() {
        return this.parentHandler;
    }

    @Override
    public ImmutableNode getParent(ImmutableNode node) {
        return this.getParentHandler().getParent(node);
    }

    @Override
    public ImmutableNode getRootNode() {
        return this.rootNode;
    }
}

