/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.tree;

import java.util.List;
import org.apache.commons.configuration2.tree.NodeAddData;
import org.apache.commons.configuration2.tree.NodeHandler;
import org.apache.commons.configuration2.tree.QueryResult;

public interface ExpressionEngine {
    public <T> List<QueryResult<T>> query(T var1, String var2, NodeHandler<T> var3);

    public <T> String nodeKey(T var1, String var2, NodeHandler<T> var3);

    public String attributeKey(String var1, String var2);

    public <T> String canonicalKey(T var1, String var2, NodeHandler<T> var3);

    public <T> NodeAddData<T> prepareAdd(T var1, String var2, NodeHandler<T> var3);
}

