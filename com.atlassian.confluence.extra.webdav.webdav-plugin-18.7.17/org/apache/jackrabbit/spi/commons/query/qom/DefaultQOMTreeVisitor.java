/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import org.apache.jackrabbit.spi.commons.query.qom.AndImpl;
import org.apache.jackrabbit.spi.commons.query.qom.BindVariableValueImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ChildNodeImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ChildNodeJoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ColumnImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ComparisonImpl;
import org.apache.jackrabbit.spi.commons.query.qom.DescendantNodeImpl;
import org.apache.jackrabbit.spi.commons.query.qom.DescendantNodeJoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.EquiJoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.FullTextSearchImpl;
import org.apache.jackrabbit.spi.commons.query.qom.FullTextSearchScoreImpl;
import org.apache.jackrabbit.spi.commons.query.qom.JoinImpl;
import org.apache.jackrabbit.spi.commons.query.qom.LengthImpl;
import org.apache.jackrabbit.spi.commons.query.qom.LiteralImpl;
import org.apache.jackrabbit.spi.commons.query.qom.LowerCaseImpl;
import org.apache.jackrabbit.spi.commons.query.qom.NodeLocalNameImpl;
import org.apache.jackrabbit.spi.commons.query.qom.NodeNameImpl;
import org.apache.jackrabbit.spi.commons.query.qom.NotImpl;
import org.apache.jackrabbit.spi.commons.query.qom.OrImpl;
import org.apache.jackrabbit.spi.commons.query.qom.OrderingImpl;
import org.apache.jackrabbit.spi.commons.query.qom.PropertyExistenceImpl;
import org.apache.jackrabbit.spi.commons.query.qom.PropertyValueImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;
import org.apache.jackrabbit.spi.commons.query.qom.QueryObjectModelTree;
import org.apache.jackrabbit.spi.commons.query.qom.SameNodeImpl;
import org.apache.jackrabbit.spi.commons.query.qom.SameNodeJoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.SelectorImpl;
import org.apache.jackrabbit.spi.commons.query.qom.UpperCaseImpl;

public class DefaultQOMTreeVisitor
implements QOMTreeVisitor {
    @Override
    public Object visit(AndImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(BindVariableValueImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(ChildNodeImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(ChildNodeJoinConditionImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(ColumnImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(ComparisonImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(DescendantNodeImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(DescendantNodeJoinConditionImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(EquiJoinConditionImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(FullTextSearchImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(FullTextSearchScoreImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(JoinImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(LengthImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(LiteralImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(LowerCaseImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(NodeLocalNameImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(NodeNameImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(NotImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(OrderingImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(OrImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(PropertyExistenceImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(PropertyValueImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(QueryObjectModelTree node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(SameNodeImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(SameNodeJoinConditionImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(SelectorImpl node, Object data) throws Exception {
        return data;
    }

    @Override
    public Object visit(UpperCaseImpl node, Object data) throws Exception {
        return data;
    }
}

