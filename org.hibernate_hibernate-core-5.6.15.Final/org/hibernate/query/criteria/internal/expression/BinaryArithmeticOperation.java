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
import org.hibernate.query.criteria.internal.expression.BinaryOperatorExpression;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.hibernate.query.criteria.internal.predicate.ImplicitNumericExpressionTypeDeterminer;

public class BinaryArithmeticOperation<N extends Number>
extends ExpressionImpl<N>
implements BinaryOperatorExpression<N>,
Serializable {
    private final Operation operator;
    private final Expression<? extends N> rhs;
    private final Expression<? extends N> lhs;

    public static Class<? extends Number> determineResultType(Class<? extends Number> argument1Type, Class<? extends Number> argument2Type) {
        return BinaryArithmeticOperation.determineResultType(argument1Type, argument2Type, false);
    }

    public static Class<? extends Number> determineResultType(Class<? extends Number> argument1Type, Class<? extends Number> argument2Type, boolean isQuotientOperation) {
        if (isQuotientOperation) {
            return Number.class;
        }
        return ImplicitNumericExpressionTypeDeterminer.determineResultType(argument1Type, argument2Type);
    }

    public static Class<? extends Number> determineReturnType(Class<? extends Number> defaultType, Expression<? extends Number> expression) {
        return expression == null || expression.getJavaType() == null ? defaultType : expression.getJavaType();
    }

    public static Class<? extends Number> determineReturnType(Class<? extends Number> defaultType, Number numberLiteral) {
        return numberLiteral == null ? defaultType : numberLiteral.getClass();
    }

    public BinaryArithmeticOperation(CriteriaBuilderImpl criteriaBuilder, Class<N> resultType, Operation operator, Expression<? extends N> lhs, Expression<? extends N> rhs) {
        super(criteriaBuilder, resultType);
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public BinaryArithmeticOperation(CriteriaBuilderImpl criteriaBuilder, Class<N> javaType, Operation operator, Expression<? extends N> lhs, N rhs) {
        super(criteriaBuilder, javaType);
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = new LiteralExpression<N>(criteriaBuilder, rhs);
    }

    public BinaryArithmeticOperation(CriteriaBuilderImpl criteriaBuilder, Class<N> javaType, Operation operator, N lhs, Expression<? extends N> rhs) {
        super(criteriaBuilder, javaType);
        this.operator = operator;
        this.lhs = new LiteralExpression<N>(criteriaBuilder, lhs);
        this.rhs = rhs;
    }

    public Operation getOperator() {
        return this.operator;
    }

    @Override
    public Expression<? extends N> getRightHandOperand() {
        return this.rhs;
    }

    @Override
    public Expression<? extends N> getLeftHandOperand() {
        return this.lhs;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getRightHandOperand(), registry);
        ParameterContainer.Helper.possibleParameter(this.getLeftHandOperand(), registry);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return this.getOperator().apply(((Renderable)this.getLeftHandOperand()).render(renderingContext), ((Renderable)this.getRightHandOperand()).render(renderingContext));
    }

    public static enum Operation {
        ADD{

            @Override
            String apply(String lhs, String rhs) {
                return Operation.applyPrimitive(lhs, '+', rhs);
            }
        }
        ,
        SUBTRACT{

            @Override
            String apply(String lhs, String rhs) {
                return Operation.applyPrimitive(lhs, '-', rhs);
            }
        }
        ,
        MULTIPLY{

            @Override
            String apply(String lhs, String rhs) {
                return Operation.applyPrimitive(lhs, '*', rhs);
            }
        }
        ,
        DIVIDE{

            @Override
            String apply(String lhs, String rhs) {
                return Operation.applyPrimitive(lhs, '/', rhs);
            }
        }
        ,
        QUOT{

            @Override
            String apply(String lhs, String rhs) {
                return Operation.applyPrimitive(lhs, '/', rhs);
            }
        }
        ,
        MOD{

            @Override
            String apply(String lhs, String rhs) {
                return "mod(" + lhs + "," + rhs + ")";
            }
        };

        private static final char LEFT_PAREN = '(';
        private static final char RIGHT_PAREN = ')';

        abstract String apply(String var1, String var2);

        private static String applyPrimitive(String lhs, char operator, String rhs) {
            return String.valueOf('(') + lhs + operator + rhs + ')';
        }
    }
}

