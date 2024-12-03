/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.LinkedList;
import org.apache.commons.configuration2.tree.ConfigurationNodeVisitor;
import org.apache.commons.configuration2.tree.NodeHandler;

public class NodeTreeWalker {
    public static final NodeTreeWalker INSTANCE = new NodeTreeWalker();

    public <T> void walkDFS(T root, ConfigurationNodeVisitor<T> visitor, NodeHandler<T> handler) {
        if (NodeTreeWalker.checkParameters(root, visitor, handler)) {
            NodeTreeWalker.dfs(root, visitor, handler);
        }
    }

    public <T> void walkBFS(T root, ConfigurationNodeVisitor<T> visitor, NodeHandler<T> handler) {
        if (NodeTreeWalker.checkParameters(root, visitor, handler)) {
            NodeTreeWalker.bfs(root, visitor, handler);
        }
    }

    private static <T> void dfs(T node, ConfigurationNodeVisitor<T> visitor, NodeHandler<T> handler) {
        if (!visitor.terminate()) {
            visitor.visitBeforeChildren(node, handler);
            handler.getChildren(node).forEach(c -> NodeTreeWalker.dfs(c, visitor, handler));
            if (!visitor.terminate()) {
                visitor.visitAfterChildren(node, handler);
            }
        }
    }

    private static <T> void bfs(T root, ConfigurationNodeVisitor<T> visitor, NodeHandler<T> handler) {
        LinkedList<T> pendingNodes = new LinkedList<T>();
        pendingNodes.add(root);
        boolean cancel = false;
        while (!pendingNodes.isEmpty() && !cancel) {
            Object node = pendingNodes.remove(0);
            visitor.visitBeforeChildren(node, handler);
            cancel = visitor.terminate();
            pendingNodes.addAll(handler.getChildren(node));
        }
    }

    private static <T> boolean checkParameters(T root, ConfigurationNodeVisitor<T> visitor, NodeHandler<T> handler) {
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor must not be null!");
        }
        if (handler == null) {
            throw new IllegalArgumentException("NodeHandler must not be null!");
        }
        return root != null;
    }
}

