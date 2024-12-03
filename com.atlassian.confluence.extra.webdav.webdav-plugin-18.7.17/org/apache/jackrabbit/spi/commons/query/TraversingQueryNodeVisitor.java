/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.commons.query.AndQueryNode;
import org.apache.jackrabbit.spi.commons.query.DefaultQueryNodeVisitor;
import org.apache.jackrabbit.spi.commons.query.DerefQueryNode;
import org.apache.jackrabbit.spi.commons.query.LocationStepQueryNode;
import org.apache.jackrabbit.spi.commons.query.NotQueryNode;
import org.apache.jackrabbit.spi.commons.query.OrQueryNode;
import org.apache.jackrabbit.spi.commons.query.OrderQueryNode;
import org.apache.jackrabbit.spi.commons.query.PathQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;

public class TraversingQueryNodeVisitor
extends DefaultQueryNodeVisitor {
    @Override
    public Object visit(OrQueryNode node, Object data) throws RepositoryException {
        return node.acceptOperands(this, data);
    }

    @Override
    public Object visit(AndQueryNode node, Object data) throws RepositoryException {
        return node.acceptOperands(this, data);
    }

    @Override
    public Object visit(QueryRootNode node, Object data) throws RepositoryException {
        OrderQueryNode orderNode;
        PathQueryNode pathNode = node.getLocationNode();
        if (pathNode != null) {
            pathNode.accept(this, data);
        }
        if ((orderNode = node.getOrderNode()) != null) {
            orderNode.accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(NotQueryNode node, Object data) throws RepositoryException {
        return node.acceptOperands(this, data);
    }

    @Override
    public Object visit(PathQueryNode node, Object data) throws RepositoryException {
        return node.acceptOperands(this, data);
    }

    @Override
    public Object visit(LocationStepQueryNode node, Object data) throws RepositoryException {
        return node.acceptOperands(this, data);
    }

    @Override
    public Object visit(DerefQueryNode node, Object data) throws RepositoryException {
        return node.acceptOperands(this, data);
    }
}

