/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.FieldValuePairQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;

public interface RangeQueryNode<T extends FieldValuePairQueryNode<?>>
extends FieldableNode {
    public T getLowerBound();

    public T getUpperBound();

    public boolean isLowerInclusive();

    public boolean isUpperInclusive();
}

