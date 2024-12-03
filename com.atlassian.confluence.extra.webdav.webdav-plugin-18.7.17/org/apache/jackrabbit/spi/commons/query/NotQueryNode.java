/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.commons.query.NAryQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public class NotQueryNode
extends NAryQueryNode<QueryNode> {
    protected NotQueryNode(QueryNode parent) {
        super(parent);
    }

    @Override
    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        return visitor.visit(this, data);
    }

    @Override
    public int getType() {
        return 9;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NotQueryNode) {
            return super.equals(obj);
        }
        return false;
    }
}

