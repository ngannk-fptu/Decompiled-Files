/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.query.LocationStepQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public class DerefQueryNode
extends LocationStepQueryNode {
    private Name refProperty;

    protected DerefQueryNode(QueryNode parent, Name nameTest, boolean descendants) {
        super(parent);
        this.setNameTest(nameTest);
        this.setIncludeDescendants(descendants);
    }

    public void setRefProperty(Name propertyName) {
        this.refProperty = propertyName;
    }

    public Name getRefProperty() {
        return this.refProperty;
    }

    @Override
    public int getType() {
        return 12;
    }

    @Override
    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        return visitor.visit(this, data);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DerefQueryNode) {
            DerefQueryNode other = (DerefQueryNode)obj;
            return super.equals(obj) && this.refProperty == null ? other.refProperty == null : this.refProperty.equals(other.refProperty);
        }
        return false;
    }

    @Override
    public boolean needsSystemTree() {
        return true;
    }
}

