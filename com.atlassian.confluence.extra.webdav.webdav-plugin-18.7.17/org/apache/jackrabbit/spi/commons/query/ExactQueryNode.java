/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public class ExactQueryNode
extends QueryNode {
    private final Name property;
    private final Name value;

    public ExactQueryNode(QueryNode parent, Name property, Name value) {
        super(parent);
        if (parent == null) {
            throw new NullPointerException("parent");
        }
        this.property = property;
        this.value = value;
    }

    @Override
    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        return visitor.visit(this, data);
    }

    @Override
    public int getType() {
        return 5;
    }

    public Name getPropertyName() {
        return this.property;
    }

    public Name getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ExactQueryNode) {
            ExactQueryNode other = (ExactQueryNode)obj;
            return (this.value == null ? other.value == null : this.value.equals(other.value)) && (this.property == null ? other.property == null : this.property.equals(other.property));
        }
        return false;
    }

    @Override
    public boolean needsSystemTree() {
        return false;
    }
}

