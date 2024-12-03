/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query;

import java.util.Collection;
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
import org.apache.jackrabbit.spi.commons.query.QueryNodeFactory;
import org.apache.jackrabbit.spi.commons.query.QueryRootNode;
import org.apache.jackrabbit.spi.commons.query.RelationQueryNode;
import org.apache.jackrabbit.spi.commons.query.TextsearchQueryNode;

public class DefaultQueryNodeFactory
implements QueryNodeFactory {
    private final Collection<Name> validJcrSystemNodeTypeNames;

    public DefaultQueryNodeFactory(Collection<Name> validJcrSystemNodeTypeNames) {
        this.validJcrSystemNodeTypeNames = validJcrSystemNodeTypeNames;
    }

    @Override
    public NodeTypeQueryNode createNodeTypeQueryNode(QueryNode parent, Name nodeType) {
        return new NodeTypeQueryNode(parent, nodeType);
    }

    @Override
    public AndQueryNode createAndQueryNode(QueryNode parent) {
        return new AndQueryNode(parent);
    }

    @Override
    public LocationStepQueryNode createLocationStepQueryNode(QueryNode parent) {
        return new LocationStepQueryNode(parent);
    }

    @Override
    public DerefQueryNode createDerefQueryNode(QueryNode parent, Name nameTest, boolean descendants) {
        return new DerefQueryNode(parent, nameTest, descendants);
    }

    @Override
    public NotQueryNode createNotQueryNode(QueryNode parent) {
        return new NotQueryNode(parent);
    }

    @Override
    public OrQueryNode createOrQueryNode(QueryNode parent) {
        return new OrQueryNode(parent);
    }

    @Override
    public RelationQueryNode createRelationQueryNode(QueryNode parent, int operation) {
        return new RelationQueryNode(parent, operation, this);
    }

    @Override
    public PathQueryNode createPathQueryNode(QueryNode parent) {
        return new PathQueryNode(parent, this.validJcrSystemNodeTypeNames);
    }

    @Override
    public OrderQueryNode createOrderQueryNode(QueryNode parent) {
        return new OrderQueryNode(parent);
    }

    @Override
    public PropertyFunctionQueryNode createPropertyFunctionQueryNode(QueryNode parent, String functionName) {
        return new PropertyFunctionQueryNode(parent, functionName);
    }

    @Override
    public QueryRootNode createQueryRootNode() {
        return new QueryRootNode();
    }

    @Override
    public TextsearchQueryNode createTextsearchQueryNode(QueryNode parent, String query) {
        return new TextsearchQueryNode(parent, query);
    }
}

