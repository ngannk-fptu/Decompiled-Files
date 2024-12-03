/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.query.AndQueryNode;
import org.apache.jackrabbit.spi.commons.query.DerefQueryNode;
import org.apache.jackrabbit.spi.commons.query.LocationStepQueryNode;
import org.apache.jackrabbit.spi.commons.query.NodeTypeQueryNode;
import org.apache.jackrabbit.spi.commons.query.NotQueryNode;
import org.apache.jackrabbit.spi.commons.query.OrQueryNode;
import org.apache.jackrabbit.spi.commons.query.OrderQueryNode;
import org.apache.jackrabbit.spi.commons.query.PathQueryNode;
import org.apache.jackrabbit.spi.commons.query.PropertyFunctionQueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryNode;
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;
import org.apache.jackrabbit.spi.commons.query.RelationQueryNode;
import org.apache.jackrabbit.spi.commons.query.TextsearchQueryNode;

public interface QueryNodeFactory {
    public NodeTypeQueryNode createNodeTypeQueryNode(QueryNode var1, Name var2);

    public AndQueryNode createAndQueryNode(QueryNode var1);

    public LocationStepQueryNode createLocationStepQueryNode(QueryNode var1);

    public DerefQueryNode createDerefQueryNode(QueryNode var1, Name var2, boolean var3);

    public NotQueryNode createNotQueryNode(QueryNode var1);

    public OrQueryNode createOrQueryNode(QueryNode var1);

    public RelationQueryNode createRelationQueryNode(QueryNode var1, int var2);

    public PathQueryNode createPathQueryNode(QueryNode var1);

    public OrderQueryNode createOrderQueryNode(QueryNode var1);

    public PropertyFunctionQueryNode createPropertyFunctionQueryNode(QueryNode var1, String var2);

    public QueryRootNode createQueryRootNode();

    public TextsearchQueryNode createTextsearchQueryNode(QueryNode var1, String var2);
}

