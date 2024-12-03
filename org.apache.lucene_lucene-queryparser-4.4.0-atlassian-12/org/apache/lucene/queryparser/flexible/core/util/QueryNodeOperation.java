/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.util;

import java.util.ArrayList;
import org.apache.lucene.queryparser.flexible.core.QueryNodeError;
import org.apache.lucene.queryparser.flexible.core.nodes.AndQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public final class QueryNodeOperation {
    private QueryNodeOperation() {
    }

    public static final QueryNode logicalAnd(QueryNode q1, QueryNode q2) {
        if (q1 == null) {
            return q2;
        }
        if (q2 == null) {
            return q1;
        }
        ANDOperation op = null;
        op = q1 instanceof AndQueryNode && q2 instanceof AndQueryNode ? ANDOperation.BOTH : (q1 instanceof AndQueryNode ? ANDOperation.Q1 : (q1 instanceof AndQueryNode ? ANDOperation.Q2 : ANDOperation.NONE));
        try {
            QueryNode result = null;
            switch (op) {
                case NONE: {
                    ArrayList<QueryNode> children = new ArrayList<QueryNode>();
                    children.add(q1.cloneTree());
                    children.add(q2.cloneTree());
                    result = new AndQueryNode(children);
                    return result;
                }
                case Q1: {
                    result = q1.cloneTree();
                    result.add(q2.cloneTree());
                    return result;
                }
                case Q2: {
                    result = q2.cloneTree();
                    result.add(q1.cloneTree());
                    return result;
                }
                case BOTH: {
                    result = q1.cloneTree();
                    result.add(q2.cloneTree().getChildren());
                    return result;
                }
            }
        }
        catch (CloneNotSupportedException e) {
            throw new QueryNodeError(e);
        }
        return null;
    }

    private static enum ANDOperation {
        BOTH,
        Q1,
        Q2,
        NONE;

    }
}

