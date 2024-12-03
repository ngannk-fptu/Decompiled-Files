/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.configuration2.tree.AbstractImmutableNodeHandler;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeHandler;

public abstract class NodeCombiner {
    protected static final NodeHandler<ImmutableNode> HANDLER = NodeCombiner.createNodeHandler();
    private final Set<String> listNodes = new HashSet<String>();

    public void addListNode(String nodeName) {
        this.listNodes.add(nodeName);
    }

    public Set<String> getListNodes() {
        return Collections.unmodifiableSet(this.listNodes);
    }

    public boolean isListNode(ImmutableNode node) {
        return this.listNodes.contains(node.getNodeName());
    }

    public abstract ImmutableNode combine(ImmutableNode var1, ImmutableNode var2);

    private static NodeHandler<ImmutableNode> createNodeHandler() {
        return new AbstractImmutableNodeHandler(){

            @Override
            public ImmutableNode getParent(ImmutableNode node) {
                return null;
            }

            @Override
            public ImmutableNode getRootNode() {
                return null;
            }
        };
    }
}

