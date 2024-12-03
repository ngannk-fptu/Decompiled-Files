/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CriteriaBuilder$SimpleCase
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;

public class SimpleCaseExpression<C, R>
extends ExpressionImpl<R>
implements CriteriaBuilder.SimpleCase<C, R>,
Serializable {
    private final Expression<? extends C> expression;
    private List<WhenClause> whenClauses = new ArrayList<WhenClause>();
    private Expression<? extends R> otherwiseResult;

    public SimpleCaseExpression(CriteriaBuilderImpl criteriaBuilder, Class<R> javaType, Expression<? extends C> expression) {
        super(criteriaBuilder, javaType);
        this.expression = expression;
    }

    public Expression<C> getExpression() {
        return this.expression;
    }

    public CriteriaBuilder.SimpleCase<C, R> when(C condition, R result) {
        return this.when(condition, (Expression<? extends R>)this.buildLiteral(result));
    }

    private LiteralExpression<R> buildLiteral(R result) {
        Class<Object> type = result != null ? result.getClass() : this.getJavaType();
        return new LiteralExpression(this.criteriaBuilder(), type, result);
    }

    public CriteriaBuilder.SimpleCase<C, R> when(C condition, Expression<? extends R> result) {
        WhenClause whenClause = new WhenClause(new LiteralExpression<C>(this.criteriaBuilder(), condition), result);
        this.whenClauses.add(whenClause);
        this.resetJavaType(result.getJavaType());
        return this;
    }

    public Expression<R> otherwise(R result) {
        return this.otherwise((Expression<? extends R>)this.buildLiteral(result));
    }

    public Expression<R> otherwise(Expression<? extends R> result) {
        this.otherwiseResult = result;
        this.resetJavaType(result.getJavaType());
        return this;
    }

    public Expression<? extends R> getOtherwiseResult() {
        return this.otherwiseResult;
    }

    public List<WhenClause> getWhenClauses() {
        return this.whenClauses;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getExpression(), registry);
        for (WhenClause whenClause : this.getWhenClauses()) {
            ParameterContainer.Helper.possibleParameter(whenClause.getResult(), registry);
        }
        ParameterContainer.Helper.possibleParameter(this.getOtherwiseResult(), registry);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        StringBuilder caseExpr = new StringBuilder();
        caseExpr.append("case ").append(((Renderable)this.getExpression()).render(renderingContext));
        for (WhenClause whenClause : this.getWhenClauses()) {
            caseExpr.append(" when ").append(whenClause.getCondition().render(renderingContext)).append(" then ").append(((Renderable)whenClause.getResult()).render(renderingContext));
        }
        caseExpr.append(" else ").append(((Renderable)this.getOtherwiseResult()).render(renderingContext)).append(" end");
        return caseExpr.toString();
    }

    public class WhenClause {
        private final LiteralExpression<C> condition;
        private final Expression<? extends R> result;

        public WhenClause(LiteralExpression<C> condition, Expression<? extends R> result) {
            this.condition = condition;
            this.result = result;
        }

        public LiteralExpression<C> getCondition() {
            return this.condition;
        }

        public Expression<? extends R> getResult() {
            return this.result;
        }
    }
}

