/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.Collection;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeKeyResolver;

public interface NodeModel<T> {
    public void setRootNode(T var1);

    public NodeHandler<T> getNodeHandler();

    public void addProperty(String var1, Iterable<?> var2, NodeKeyResolver<T> var3);

    public void addNodes(String var1, Collection<? extends T> var2, NodeKeyResolver<T> var3);

    public void setProperty(String var1, Object var2, NodeKeyResolver<T> var3);

    public Object clearTree(String var1, NodeKeyResolver<T> var2);

    public void clearProperty(String var1, NodeKeyResolver<T> var2);

    public void clear(NodeKeyResolver<T> var1);

    public ImmutableNode getInMemoryRepresentation();
}

