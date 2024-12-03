/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.commons.query.NAryQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public class OrQueryNode
extends NAryQueryNode<QueryNode> {
    protected OrQueryNode(QueryNode parent) {
        super(parent);
    }

    @Override
    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        return visitor.visit(this, data);
    }

    @Override
    public int getType() {
        return 8;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OrQueryNode) {
            return super.equals(obj);
        }
        return false;
    }
}

