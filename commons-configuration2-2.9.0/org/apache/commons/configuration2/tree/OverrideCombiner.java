/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeCombiner;

public class OverrideCombiner
extends NodeCombiner {
    @Override
    public ImmutableNode combine(ImmutableNode node1, ImmutableNode node2) {
        ImmutableNode.Builder result = new ImmutableNode.Builder();
        result.name(node1.getNodeName());
        node1.forEach(child -> {
            ImmutableNode child2 = this.canCombine(node1, node2, (ImmutableNode)child);
            result.addChild(child2 != null ? this.combine((ImmutableNode)child, child2) : child);
        });
        node2.stream().filter(child -> HANDLER.getChildrenCount(node1, child.getNodeName()) < 1).forEach(result::addChild);
        this.addAttributes(result, node1, node2);
        result.value(node1.getValue() != null ? node1.getValue() : node2.getValue());
        return result.create();
    }

    protected void addAttributes(ImmutableNode.Builder result, ImmutableNode node1, ImmutableNode node2) {
        result.addAttributes(node1.getAttributes());
        node2.getAttributes().keySet().forEach(attr -> {
            if (!node1.getAttributes().containsKey(attr)) {
                result.addAttribute((String)attr, HANDLER.getAttributeValue(node2, (String)attr));
            }
        });
    }

    protected ImmutableNode canCombine(ImmutableNode node1, ImmutableNode node2, ImmutableNode child) {
        if (HANDLER.getChildrenCount(node2, child.getNodeName()) == 1 && HANDLER.getChildrenCount(node1, child.getNodeName()) == 1 && !this.isListNode(child)) {
            return HANDLER.getChildren(node2, child.getNodeName()).get(0);
        }
        return null;
    }
}

