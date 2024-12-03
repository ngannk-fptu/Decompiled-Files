/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class BooleanModifierNode
extends ModifierQueryNode {
    public BooleanModifierNode(QueryNode node, ModifierQueryNode.Modifier mod) {
        super(node, mod);
    }
}

