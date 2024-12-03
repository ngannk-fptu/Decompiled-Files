/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import javax.persistence.criteria.Expression;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.expression.UnaryOperatorExpression;

public class UnaryArithmeticOperation<T>
extends ExpressionImpl<T>
implements UnaryOperatorExpression<T>,
Serializable {
    private final Operation operation;
    private final Expression<T> operand;

    public UnaryArithmeticOperation(CriteriaBuilderImpl criteriaBuilder, Operation operation, Expression<T> operand) {
        super(criteriaBuilder, operand.getJavaType());
        this.operation = operation;
        this.operand = operand;
    }

    public Operation getOperation() {
        return this.operation;
    }

    @Override
    public Expression<T> getOperand() {
        return this.operand;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getOperand(), registry);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return (this.getOperation() == Operation.UNARY_MINUS ? (char)'-' : '+') + ((Renderable)this.getOperand()).render(renderingContext);
    }

    public static enum Operation {
        UNARY_PLUS,
        UNARY_MINUS;

    }
}

