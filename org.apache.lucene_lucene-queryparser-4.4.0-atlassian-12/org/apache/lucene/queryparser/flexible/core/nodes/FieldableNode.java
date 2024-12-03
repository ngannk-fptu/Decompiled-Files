/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public interface FieldableNode
extends QueryNode {
    public CharSequence getField();

    public void setField(CharSequence var1);
}

