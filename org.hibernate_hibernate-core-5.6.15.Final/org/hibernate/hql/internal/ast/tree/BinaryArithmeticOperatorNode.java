/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  antlr.SemanticException
 */
package org.hibernate.hql.internal.ast.tree;

import antlr.SemanticException;
import java.util.Calendar;
import java.util.Date;
import org.hibernate.hql.internal.ast.tree.AbstractSelectExpression;
import org.hibernate.hql.internal.ast.tree.BinaryOperatorNode;
import org.hibernate.hql.internal.ast.tree.DisplayableNode;
import org.hibernate.hql.internal.ast.tree.ExpectedTypeAwareNode;
import org.hibernate.hql.internal.ast.tree.Node;
import org.hibernate.hql.internal.ast.tree.ParameterNode;
import org.hibernate.hql.internal.ast.tree.SqlNode;
import org.hibernate.hql.internal.ast.util.ColumnHelper;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

public class BinaryArithmeticOperatorNode
extends AbstractSelectExpression
implements BinaryOperatorNode,
DisplayableNode {
    @Override
    public void initialize() throws SemanticException {
        Type rhType;
        Node lhs = this.getLeftHandOperand();
        if (lhs == null) {
            throw new SemanticException("left-hand operand of a binary operator was null");
        }
        Node rhs = this.getRightHandOperand();
        if (rhs == null) {
            throw new SemanticException("right-hand operand of a binary operator was null");
        }
        Type lhType = lhs instanceof SqlNode ? ((SqlNode)lhs).getDataType() : null;
        Type type = rhType = rhs instanceof SqlNode ? ((SqlNode)rhs).getDataType() : null;
        if (ExpectedTypeAwareNode.class.isAssignableFrom(((Object)((Object)lhs)).getClass()) && rhType != null) {
            Type expectedType = this.isDateTimeType(rhType) ? (this.getType() == 122 ? StandardBasicTypes.DOUBLE : rhType) : rhType;
            ((ExpectedTypeAwareNode)((Object)lhs)).setExpectedType(expectedType);
        } else if (ParameterNode.class.isAssignableFrom(((Object)((Object)rhs)).getClass()) && lhType != null) {
            Type expectedType = null;
            if (this.isDateTimeType(lhType)) {
                if (this.getType() == 122) {
                    expectedType = StandardBasicTypes.DOUBLE;
                }
            } else {
                expectedType = lhType;
            }
            ((ExpectedTypeAwareNode)((Object)rhs)).setExpectedType(expectedType);
        }
    }

    @Override
    public Type getDataType() {
        if (super.getDataType() == null) {
            super.setDataType(this.resolveDataType());
        }
        return super.getDataType();
    }

    private Type resolveDataType() {
        Type rhType;
        Node lhs = this.getLeftHandOperand();
        Node rhs = this.getRightHandOperand();
        Type lhType = lhs instanceof SqlNode ? ((SqlNode)lhs).getDataType() : null;
        Type type = rhType = rhs instanceof SqlNode ? ((SqlNode)rhs).getDataType() : null;
        if (this.isDateTimeType(lhType) || this.isDateTimeType(rhType)) {
            return this.resolveDateTimeArithmeticResultType(lhType, rhType);
        }
        if (lhType == null) {
            if (rhType == null) {
                return StandardBasicTypes.DOUBLE;
            }
            return rhType;
        }
        if (rhType == null) {
            return lhType;
        }
        if (lhType == StandardBasicTypes.DOUBLE || rhType == StandardBasicTypes.DOUBLE) {
            return StandardBasicTypes.DOUBLE;
        }
        if (lhType == StandardBasicTypes.FLOAT || rhType == StandardBasicTypes.FLOAT) {
            return StandardBasicTypes.FLOAT;
        }
        if (lhType == StandardBasicTypes.BIG_DECIMAL || rhType == StandardBasicTypes.BIG_DECIMAL) {
            return StandardBasicTypes.BIG_DECIMAL;
        }
        if (lhType == StandardBasicTypes.BIG_INTEGER || rhType == StandardBasicTypes.BIG_INTEGER) {
            return StandardBasicTypes.BIG_INTEGER;
        }
        if (lhType == StandardBasicTypes.LONG || rhType == StandardBasicTypes.LONG) {
            return StandardBasicTypes.LONG;
        }
        if (lhType == StandardBasicTypes.INTEGER || rhType == StandardBasicTypes.INTEGER) {
            return StandardBasicTypes.INTEGER;
        }
        return lhType;
    }

    private boolean isDateTimeType(Type type) {
        return type != null && (Date.class.isAssignableFrom(type.getReturnedClass()) || Calendar.class.isAssignableFrom(type.getReturnedClass()));
    }

    private Type resolveDateTimeArithmeticResultType(Type lhType, Type rhType) {
        boolean lhsIsDateTime = this.isDateTimeType(lhType);
        boolean rhsIsDateTime = this.isDateTimeType(rhType);
        if (this.getType() == 122) {
            return lhsIsDateTime ? lhType : rhType;
        }
        if (this.getType() == 123) {
            if (lhsIsDateTime && !rhsIsDateTime) {
                return lhType;
            }
            if (lhsIsDateTime && rhsIsDateTime) {
                return StandardBasicTypes.DOUBLE;
            }
        }
        return null;
    }

    @Override
    public void setScalarColumnText(int i) throws SemanticException {
        ColumnHelper.generateSingleScalarColumn(this, i);
    }

    @Override
    public Node getLeftHandOperand() {
        return (Node)this.getFirstChild();
    }

    @Override
    public Node getRightHandOperand() {
        return (Node)this.getFirstChild().getNextSibling();
    }

    @Override
    public String getDisplayText() {
        return "{dataType=" + this.getDataType() + "}";
    }
}

