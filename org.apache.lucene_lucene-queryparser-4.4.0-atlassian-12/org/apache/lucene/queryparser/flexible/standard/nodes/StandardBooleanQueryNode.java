/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.nodes;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public class StandardBooleanQueryNode
extends BooleanQueryNode {
    private boolean disableCoord;

    public StandardBooleanQueryNode(List<QueryNode> clauses, boolean disableCoord) {
        super(clauses);
        this.disableCoord = disableCoord;
    }

    public boolean isDisableCoord() {
        return this.disableCoord;
    }
}

