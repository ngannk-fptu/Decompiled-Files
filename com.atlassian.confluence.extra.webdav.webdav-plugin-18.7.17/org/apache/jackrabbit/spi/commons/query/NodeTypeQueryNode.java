/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.query.ExactQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public class NodeTypeQueryNode
extends ExactQueryNode {
    protected NodeTypeQueryNode(QueryNode parent, Name nodeType) {
        super(parent, NameConstants.JCR_PRIMARYTYPE, nodeType);
    }

    @Override
    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        return visitor.visit(this, data);
    }

    @Override
    public int getType() {
        return 6;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NodeTypeQueryNode) {
            return super.equals(obj);
        }
        return false;
    }
}

