/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Selection
 */
package org.hibernate.query.criteria.internal.predicate;

import java.io.Serializable;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Selection;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.BinaryOperatorExpression;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.hibernate.query.criteria.internal.predicate.AbstractSimplePredicate;

public class ComparisonPredicate
extends AbstractSimplePredicate
implements BinaryOperatorExpression<Boolean>,
Serializable {
    private final ComparisonOperator comparisonOperator;
    private final Expression<?> leftHandSide;
    private final Expression<?> rightHandSide;

    public ComparisonPredicate(CriteriaBuilderImpl criteriaBuilder, ComparisonOperator comparisonOperator, Expression<?> leftHandSide, Expression<?> rightHandSide) {
        super(criteriaBuilder);
        this.comparisonOperator = comparisonOperator;
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
    }

    public ComparisonPredicate(CriteriaBuilderImpl criteriaBuilder, ComparisonOperator comparisonOperator, Expression<?> leftHandSide, Object rightHandSide) {
        super(criteriaBuilder);
        this.comparisonOperator = comparisonOperator;
        this.leftHandSide = leftHandSide;
        this.rightHandSide = ValueHandlerFactory.isNumeric(leftHandSide.getJavaType()) ? new LiteralExpression(criteriaBuilder, ValueHandlerFactory.convert(rightHandSide, leftHandSide.getJavaType())) : new LiteralExpression<Object>(criteriaBuilder, rightHandSide);
    }

    public <N extends Number> ComparisonPredicate(CriteriaBuilderImpl criteriaBuilder, ComparisonOperator comparisonOperator, Expression<N> leftHandSide, Number rightHandSide) {
        super(criteriaBuilder);
        this.comparisonOperator = comparisonOperator;
        this.leftHandSide = leftHandSide;
        Class type = leftHandSide.getJavaType();
        if (Number.class.equals((Object)type)) {
            this.rightHandSide = new LiteralExpression<Number>(criteriaBuilder, rightHandSide);
        } else {
            Number converted = (Number)ValueHandlerFactory.convert(rightHandSide, type);
            this.rightHandSide = new LiteralExpression<Number>(criteriaBuilder, converted);
        }
    }

    public ComparisonOperator getComparisonOperator() {
        return this.getComparisonOperator(this.isNegated());
    }

    public ComparisonOperator getComparisonOperator(boolean isNegated) {
        return isNegated ? this.comparisonOperator.negated() : this.comparisonOperator;
    }

    @Override
    public Expression getLeftHandOperand() {
        return this.leftHandSide;
    }

    @Override
    public Expression getRightHandOperand() {
        return this.rightHandSide;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter((Selection)this.getLeftHandOperand(), registry);
        ParameterContainer.Helper.possibleParameter((Selection)this.getRightHandOperand(), registry);
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        return ((Renderable)this.getLeftHandOperand()).render(renderingContext) + this.getComparisonOperator(isNegated).rendered() + ((Renderable)this.getRightHandOperand()).render(renderingContext);
    }

    public static enum ComparisonOperator {
        EQUAL{

            @Override
            public ComparisonOperator negated() {
                return NOT_EQUAL;
            }

            @Override
            public String rendered() {
                return "=";
            }
        }
        ,
        NOT_EQUAL{

            @Override
            public ComparisonOperator negated() {
                return EQUAL;
            }

            @Override
            public String rendered() {
                return "<>";
            }
        }
        ,
        LESS_THAN{

            @Override
            public ComparisonOperator negated() {
                return GREATER_THAN_OR_EQUAL;
            }

            @Override
            public String rendered() {
                return "<";
            }
        }
        ,
        LESS_THAN_OR_EQUAL{

            @Override
            public ComparisonOperator negated() {
                return GREATER_THAN;
            }

            @Override
            public String rendered() {
                return "<=";
            }
        }
        ,
        GREATER_THAN{

            @Override
            public ComparisonOperator negated() {
                return LESS_THAN_OR_EQUAL;
            }

            @Override
            public String rendered() {
                return ">";
            }
        }
        ,
        GREATER_THAN_OR_EQUAL{

            @Override
            public ComparisonOperator negated() {
                return LESS_THAN;
            }

            @Override
            public String rendered() {
                return ">=";
            }
        };


        public abstract ComparisonOperator negated();

        public abstract String rendered();
    }
}

