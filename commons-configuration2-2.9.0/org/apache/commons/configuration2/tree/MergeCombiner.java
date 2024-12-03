/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeCombiner;

public class MergeCombiner
extends NodeCombiner {
    @Override
    public ImmutableNode combine(ImmutableNode node1, ImmutableNode node2) {
        ImmutableNode.Builder result = new ImmutableNode.Builder();
        result.name(node1.getNodeName());
        result.value(node1.getValue());
        this.addAttributes(result, node1, node2);
        LinkedList<ImmutableNode> children2 = new LinkedList<ImmutableNode>(node2.getChildren());
        node1.forEach(child1 -> {
            ImmutableNode child2 = this.canCombine(node2, (ImmutableNode)child1, (List<ImmutableNode>)children2);
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

    protected void addAttributes(ImmutableNode.Builder result, ImmutableNode node1, ImmutableNode node2) {
        HashMap<String, Object> attributes = new HashMap<String, Object>(node1.getAttributes());
        node2.getAttributes().forEach(attributes::putIfAbsent);
        result.addAttributes(attributes);
    }

    protected ImmutableNode canCombine(ImmutableNode node2, ImmutableNode child, List<ImmutableNode> children2) {
        Map<String, Object> attrs1 = child.getAttributes();
        ArrayList nodes = new ArrayList();
        List<ImmutableNode> children = HANDLER.getChildren(node2, child.getNodeName());
        children.forEach(node -> {
            if (MergeCombiner.matchAttributes(attrs1, node)) {
                nodes.add(node);
            }
        });
        if (nodes.size() == 1) {
            return (ImmutableNode)nodes.get(0);
        }
        if (nodes.size() > 1 && !this.isListNode(child)) {
            nodes.forEach(children2::remove);
        }
        return null;
    }

    private static boolean matchAttributes(Map<String, Object> attrs1, ImmutableNode node) {
        Map<String, Object> attrs2 = node.getAttributes();
        for (Map.Entry<String, Object> e : attrs1.entrySet()) {
            if (!attrs2.containsKey(e.getKey()) || Objects.equals(e.getValue(), attrs2.get(e.getKey()))) continue;
            return false;
        }
        return true;
    }
}

