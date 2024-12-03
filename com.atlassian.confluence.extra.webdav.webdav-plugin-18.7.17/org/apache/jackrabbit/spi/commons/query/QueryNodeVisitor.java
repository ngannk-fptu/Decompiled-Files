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
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;
import org.apache.jackrabbit.spi.commons.query.RelationQueryNode;
import org.apache.jackrabbit.spi.commons.query.TextsearchQueryNode;

public interface QueryNodeVisitor {
    public Object visit(QueryRootNode var1, Object var2) throws RepositoryException;

    public Object visit(OrQueryNode var1, Object var2) throws RepositoryException;

    public Object visit(AndQueryNode var1, Object var2) throws RepositoryException;

    public Object visit(NotQueryNode var1, Object var2) throws RepositoryException;

    public Object visit(ExactQueryNode var1, Object var2) throws RepositoryException;

    public Object visit(NodeTypeQueryNode var1, Object var2) throws RepositoryException;

    public Object visit(TextsearchQueryNode var1, Object var2) throws RepositoryException;

    public Object visit(PathQueryNode var1, Object var2) throws RepositoryException;

    public Object visit(LocationStepQueryNode var1, Object var2) throws RepositoryException;

    public Object visit(RelationQueryNode var1, Object var2) throws RepositoryException;

    public Object visit(OrderQueryNode var1, Object var2) throws RepositoryException;

    public Object visit(DerefQueryNode var1, Object var2) throws RepositoryException;

    public Object visit(PropertyFunctionQueryNode var1, Object var2) throws RepositoryException;
}

