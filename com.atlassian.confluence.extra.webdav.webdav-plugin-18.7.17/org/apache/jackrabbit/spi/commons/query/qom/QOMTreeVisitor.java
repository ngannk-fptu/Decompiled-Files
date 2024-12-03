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
import org.apache.jackrabbit.spi.commons.query.qom.QueryObjectModelTree;
import org.apache.jackrabbit.spi.commons.query.qom.SameNodeImpl;
import org.apache.jackrabbit.spi.commons.query.qom.SameNodeJoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.SelectorImpl;
import org.apache.jackrabbit.spi.commons.query.qom.UpperCaseImpl;

public interface QOMTreeVisitor {
    public Object visit(AndImpl var1, Object var2) throws Exception;

    public Object visit(BindVariableValueImpl var1, Object var2) throws Exception;

    public Object visit(ChildNodeImpl var1, Object var2) throws Exception;

    public Object visit(ChildNodeJoinConditionImpl var1, Object var2) throws Exception;

    public Object visit(ColumnImpl var1, Object var2) throws Exception;

    public Object visit(ComparisonImpl var1, Object var2) throws Exception;

    public Object visit(DescendantNodeImpl var1, Object var2) throws Exception;

    public Object visit(DescendantNodeJoinConditionImpl var1, Object var2) throws Exception;

    public Object visit(EquiJoinConditionImpl var1, Object var2) throws Exception;

    public Object visit(FullTextSearchImpl var1, Object var2) throws Exception;

    public Object visit(FullTextSearchScoreImpl var1, Object var2) throws Exception;

    public Object visit(JoinImpl var1, Object var2) throws Exception;

    public Object visit(LengthImpl var1, Object var2) throws Exception;

    public Object visit(LiteralImpl var1, Object var2) throws Exception;

    public Object visit(LowerCaseImpl var1, Object var2) throws Exception;

    public Object visit(NodeLocalNameImpl var1, Object var2) throws Exception;

    public Object visit(NodeNameImpl var1, Object var2) throws Exception;

    public Object visit(NotImpl var1, Object var2) throws Exception;

    public Object visit(OrderingImpl var1, Object var2) throws Exception;

    public Object visit(OrImpl var1, Object var2) throws Exception;

    public Object visit(PropertyExistenceImpl var1, Object var2) throws Exception;

    public Object visit(PropertyValueImpl var1, Object var2) throws Exception;

    public Object visit(QueryObjectModelTree var1, Object var2) throws Exception;

    public Object visit(SameNodeImpl var1, Object var2) throws Exception;

    public Object visit(SameNodeJoinConditionImpl var1, Object var2) throws Exception;

    public Object visit(SelectorImpl var1, Object var2) throws Exception;

    public Object visit(UpperCaseImpl var1, Object var2) throws Exception;
}

