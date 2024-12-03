/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.AbstractRangeQueryNode;

public class TermRangeQueryNode
extends AbstractRangeQueryNode<FieldQueryNode> {
    public TermRangeQueryNode(FieldQueryNode lower, FieldQueryNode upper, boolean lowerInclusive, boolean upperInclusive) {
        this.setBounds(lower, upper, lowerInclusive, upperInclusive);
    }
}

