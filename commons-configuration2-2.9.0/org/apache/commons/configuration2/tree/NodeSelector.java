/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package org.apache.commons.configuration2.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeKeyResolver;
import org.apache.commons.configuration2.tree.QueryResult;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class NodeSelector {
    private final List<String> nodeKeys;

    public NodeSelector(String key) {
        this(Collections.singletonList(key));
    }

    private NodeSelector(List<String> keys) {
        this.nodeKeys = keys;
    }

    public ImmutableNode select(ImmutableNode root, NodeKeyResolver<ImmutableNode> resolver, NodeHandler<ImmutableNode> handler) {
        LinkedList<ImmutableNode> nodes = new LinkedList<ImmutableNode>();
        Iterator<String> itKeys = this.nodeKeys.iterator();
        this.getFilteredResults(root, resolver, handler, itKeys.next(), nodes);
        while (itKeys.hasNext()) {
            String currentKey = itKeys.next();
            LinkedList currentResults = new LinkedList();
            nodes.forEach(currentRoot -> this.getFilteredResults((ImmutableNode)currentRoot, resolver, handler, currentKey, currentResults));
            nodes = currentResults;
        }
        return nodes.size() == 1 ? (ImmutableNode)nodes.get(0) : null;
    }

    public NodeSelector subSelector(String subKey) {
        ArrayList<String> keys = new ArrayList<String>(this.nodeKeys.size() + 1);
        keys.addAll(this.nodeKeys);
        keys.add(subKey);
        return new NodeSelector(keys);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof NodeSelector)) {
            return false;
        }
        NodeSelector c = (NodeSelector)obj;
        return this.nodeKeys.equals(c.nodeKeys);
    }

    public int hashCode() {
        return this.nodeKeys.hashCode();
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("keys", this.nodeKeys).toString();
    }

    private void getFilteredResults(ImmutableNode root, NodeKeyResolver<ImmutableNode> resolver, NodeHandler<ImmutableNode> handler, String key, List<ImmutableNode> nodes) {
        List<QueryResult<ImmutableNode>> results = resolver.resolveKey(root, key, handler);
        results.forEach(result -> {
            if (!result.isAttributeResult()) {
                nodes.add((ImmutableNode)result.getNode());
            }
        });
    }
}

