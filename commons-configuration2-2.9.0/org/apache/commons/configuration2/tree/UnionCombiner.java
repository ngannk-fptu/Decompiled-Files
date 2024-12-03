/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.LinkedList;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeCombiner;

public class UnionCombiner
extends NodeCombiner {
    @Override
    public ImmutableNode combine(ImmutableNode node1, ImmutableNode node2) {
        ImmutableNode.Builder result = new ImmutableNode.Builder();
        result.name(node1.getNodeName());
        result.addAttributes(node2.getAttributes());
        result.addAttributes(node1.getAttributes());
        LinkedList<ImmutableNode> children2 = new LinkedList<ImmutableNode>(node2.getChildren());
        node1.forEach(child1 -> {
            ImmutableNode child2 = this.findCombineNode(node1, node2, (ImmutableNode)child1);
            if (child2 != null) {
                result.addChild(this.combine((ImmutableNode)child1, child2));
                children2.remove(child2);
            } else {
                result.addChild((ImmutableNode)child1);
            }
        });
        children2.forEach(result::addChild);
        return result.create();
    }

    protected ImmutableNode findCombineNode(ImmutableNode node1, ImmutableNode node2, ImmutableNode child) {
        ImmutableNode child2;
        if (child.getValue() == null && !this.isListNode(child) && HANDLER.getChildrenCount(node1, child.getNodeName()) == 1 && HANDLER.getChildrenCount(node2, child.getNodeName()) == 1 && (child2 = HANDLER.getChildren(node2, child.getNodeName()).get(0)).getValue() == null) {
            return child2;
        }
        return null;
    }
}

