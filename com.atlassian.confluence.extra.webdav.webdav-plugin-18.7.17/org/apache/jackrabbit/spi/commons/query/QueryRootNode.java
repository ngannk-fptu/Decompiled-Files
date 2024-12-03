/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import java.util.ArrayList;
import java.util.List;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.query.OrderQueryNode;
import org.apache.jackrabbit.spi.commons.query.PathQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;

public class QueryRootNode
extends QueryNode {
    private PathQueryNode locationNode;
    private final List selectProperties = new ArrayList();
    private OrderQueryNode orderNode;

    protected QueryRootNode() {
        super(null);
    }

    public PathQueryNode getLocationNode() {
        return this.locationNode;
    }

    public void setLocationNode(PathQueryNode locationNode) {
        this.locationNode = locationNode;
    }

    public void addSelectProperty(Name propName) {
        this.selectProperties.add(propName);
    }

    public Name[] getSelectProperties() {
        return this.selectProperties.toArray(new Name[this.selectProperties.size()]);
    }

    public OrderQueryNode getOrderNode() {
        return this.orderNode;
    }

    public void setOrderNode(OrderQueryNode orderNode) {
        this.orderNode = orderNode;
    }

    @Override
    public Object accept(QueryNodeVisitor visitor, Object data) throws RepositoryException {
        return visitor.visit(this, data);
    }

    @Override
    public int getType() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QueryRootNode) {
            QueryRootNode other = (QueryRootNode)obj;
            return (this.locationNode == null ? other.locationNode == null : this.locationNode.equals(other.locationNode)) && this.selectProperties.equals(other.selectProperties) && (this.orderNode == null ? other.orderNode == null : this.orderNode.equals(other.orderNode));
        }
        return false;
    }

    @Override
    public boolean needsSystemTree() {
        return this.locationNode != null && this.locationNode.needsSystemTree() || this.orderNode != null && this.orderNode.needsSystemTree();
    }
}

