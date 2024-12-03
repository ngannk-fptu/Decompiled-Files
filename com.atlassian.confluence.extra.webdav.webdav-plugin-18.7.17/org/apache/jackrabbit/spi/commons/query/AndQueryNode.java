/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.commons.query.NAryQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public class AndQueryNode
extends NAryQueryNode<QueryNode> {
    protected AndQueryNode(QueryNode parent) {
        super(parent);
    }

    @Override
    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        return visitor.visit(this, data);
    }

    @Override
    public int getType() {
        return 7;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AndQueryNode) {
            return super.equals(obj);
        }
        return false;
    }
}

