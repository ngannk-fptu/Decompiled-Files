/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import javax.jcr.RepositoryException;
import org.apache.jackrabbit.spi.commons.query.AndQueryNode;
import org.apache.jackrabbit.spi.commons.query.DerefQueryNode;
import org.apache.jackrabbit.spi.commons.query.ExactQueryNode;
import org.apache.jackrabbit.spi.commons.query.LocationStepQueryNode;
import org.apache.jackrabbit.spi.commons.query.NodeTypeQueryNode;
import org.apache.jackrabbit.spi.commons.query.NotQueryNode;
import org.apache.jackrabbit.spi.commons.query.OrQueryNode;
import org.apache.jackrabbit.spi.commons.query.OrderQueryNode;
import org.apache.jackrabbit.spi.commons.query.PathQueryNode;
import org.apache.jackrabbit.spi.commons.query.PropertyFunctionQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNodeVisitor;
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;
import org.apache.jackrabbit.spi.commons.query.RelationQueryNode;
import org.apache.jackrabbit.spi.commons.query.TextsearchQueryNode;

public class DefaultQueryNodeVisitor
implements QueryNodeVisitor {
    @Override
    public Object visit(QueryRootNode node, Object data) throws RepositoryException {
        return data;
    }

    @Override
    public Object visit(OrQueryNode node, Object data) throws RepositoryException {
        return data;
    }

    @Override
    public Object visit(AndQueryNode node, Object data) throws RepositoryException {
        return data;
    }

    @Override
    public Object visit(NotQueryNode node, Object data) throws RepositoryException {
        return data;
    }

    @Override
    public Object visit(ExactQueryNode node, Object data) throws RepositoryException {
        return data;
    }

    @Override
    public Object visit(NodeTypeQueryNode node, Object data) throws RepositoryException {
        return data;
    }

    @Override
    public Object visit(TextsearchQueryNode node, Object data) throws RepositoryException {
        return data;
    }

    @Override
    public Object visit(PathQueryNode node, Object data) throws RepositoryException {
        return data;
    }

    @Override
    public Object visit(LocationStepQueryNode node, Object data) throws RepositoryException {
        return data;
    }

    @Override
    public Object visit(RelationQueryNode node, Object data) throws RepositoryException {
        return data;
    }

    @Override
    public Object visit(OrderQueryNode node, Object data) throws RepositoryException {
        return data;
    }

    @Override
    public Object visit(DerefQueryNode node, Object data) throws RepositoryException {
        return data;
    }

    @Override
    public Object visit(PropertyFunctionQueryNode node, Object data) throws RepositoryException {
        return data;
    }
}

