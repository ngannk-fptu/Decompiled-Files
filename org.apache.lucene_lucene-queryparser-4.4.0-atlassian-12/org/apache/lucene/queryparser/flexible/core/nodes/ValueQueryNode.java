/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public interface ValueQueryNode<T>
extends QueryNode {
    public void setValue(T var1);

    public T getValue();
}

