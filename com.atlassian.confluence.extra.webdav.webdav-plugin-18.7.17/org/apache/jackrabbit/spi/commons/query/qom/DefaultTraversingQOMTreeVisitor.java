/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import org.apache.jackrabbit.spi.commons.query.qom.AndImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ColumnImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ComparisonImpl;
import org.apache.jackrabbit.spi.commons.query.qom.ConstraintImpl;
import org.apache.jackrabbit.spi.commons.query.qom.DefaultQOMTreeVisitor;
import org.apache.jackrabbit.spi.commons.query.qom.DynamicOperandImpl;
import org.apache.jackrabbit.spi.commons.query.qom.FullTextSearchImpl;
import org.apache.jackrabbit.spi.commons.query.qom.JoinConditionImpl;
import org.apache.jackrabbit.spi.commons.query.qom.JoinImpl;
import org.apache.jackrabbit.spi.commons.query.qom.LengthImpl;
import org.apache.jackrabbit.spi.commons.query.qom.LowerCaseImpl;
import org.apache.jackrabbit.spi.commons.query.qom.NotImpl;
import org.apache.jackrabbit.spi.commons.query.qom.OrImpl;
import org.apache.jackrabbit.spi.commons.query.qom.OrderingImpl;
import org.apache.jackrabbit.spi.commons.query.qom.PropertyValueImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QueryObjectModelTree;
import org.apache.jackrabbit.spi.commons.query.qom.SourceImpl;
import org.apache.jackrabbit.spi.commons.query.qom.StaticOperandImpl;
import org.apache.jackrabbit.spi.commons.query.qom.UpperCaseImpl;

public class DefaultTraversingQOMTreeVisitor
extends DefaultQOMTreeVisitor {
    @Override
    public final Object visit(AndImpl node, Object data) throws Exception {
        ((ConstraintImpl)node.getConstraint1()).accept(this, data);
        ((ConstraintImpl)node.getConstraint2()).accept(this, data);
        return data;
    }

    @Override
    public Object visit(ComparisonImpl node, Object data) throws Exception {
        ((DynamicOperandImpl)node.getOperand1()).accept(this, data);
        ((StaticOperandImpl)node.getOperand2()).accept(this, data);
        return data;
    }

    @Override
    public Object visit(FullTextSearchImpl node, Object data) throws Exception {
        ((StaticOperandImpl)node.getFullTextSearchExpression()).accept(this, data);
        return data;
    }

    @Override
    public Object visit(JoinImpl node, Object data) throws Exception {
        ((SourceImpl)node.getRight()).accept(this, data);
        ((SourceImpl)node.getLeft()).accept(this, data);
        ((JoinConditionImpl)node.getJoinCondition()).accept(this, data);
        return data;
    }

    @Override
    public Object visit(LengthImpl node, Object data) throws Exception {
        ((PropertyValueImpl)node.getPropertyValue()).accept(this, data);
        return data;
    }

    @Override
    public Object visit(LowerCaseImpl node, Object data) throws Exception {
        ((DynamicOperandImpl)node.getOperand()).accept(this, data);
        return data;
    }

    @Override
    public Object visit(NotImpl node, Object data) throws Exception {
        ((ConstraintImpl)node.getConstraint()).accept(this, data);
        return data;
    }

    @Override
    public Object visit(OrderingImpl node, Object data) throws Exception {
        ((DynamicOperandImpl)node.getOperand()).accept(this, data);
        return data;
    }

    @Override
    public Object visit(OrImpl node, Object data) throws Exception {
        ((ConstraintImpl)node.getConstraint1()).accept(this, data);
        ((ConstraintImpl)node.getConstraint2()).accept(this, data);
        return data;
    }

    @Override
    public Object visit(QueryObjectModelTree node, Object data) throws Exception {
        node.getSource().accept(this, data);
        ConstraintImpl constraint = node.getConstraint();
        if (constraint != null) {
            constraint.accept(this, data);
        }
        OrderingImpl[] orderings = node.getOrderings();
        for (int i = 0; i < orderings.length; ++i) {
            orderings[i].accept(this, data);
        }
        ColumnImpl[] columns = node.getColumns();
        for (int i = 0; i < columns.length; ++i) {
            columns[i].accept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(UpperCaseImpl node, Object data) throws Exception {
        ((DynamicOperandImpl)node.getOperand()).accept(this, data);
        return data;
    }
}

