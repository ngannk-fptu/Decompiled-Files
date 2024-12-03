/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.List;
import java.util.Map;
import org.apache.commons.configuration2.tree.NodeAddData;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.NodeUpdateData;
import org.apache.commons.configuration2.tree.QueryResult;

public interface NodeKeyResolver<T> {
    public List<QueryResult<T>> resolveKey(T var1, String var2, NodeHandler<T> var3);

    public List<T> resolveNodeKey(T var1, String var2, NodeHandler<T> var3);

    public NodeAddData<T> resolveAddKey(T var1, String var2, NodeHandler<T> var3);

    public NodeUpdateData<T> resolveUpdateKey(T var1, String var2, Object var3, NodeHandler<T> var4);

    public String nodeKey(T var1, Map<T, String> var2, NodeHandler<T> var3);
}

