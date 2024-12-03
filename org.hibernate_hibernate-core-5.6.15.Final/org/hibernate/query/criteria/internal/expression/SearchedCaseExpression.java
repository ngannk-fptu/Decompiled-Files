/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CriteriaBuilder$Case
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

public class SearchedCaseExpression<R>
extends ExpressionImpl<R>
implements CriteriaBuilder.Case<R>,
Serializable {
    private List<WhenClause> whenClauses = new ArrayList<WhenClause>();
    private Expression<? extends R> otherwiseResult;

    public SearchedCaseExpression(CriteriaBuilderImpl criteriaBuilder, Class<R> javaType) {
        super(criteriaBuilder, javaType);
    }

    public CriteriaBuilder.Case<R> when(Expression<Boolean> condition, R result) {
        return this.when(condition, (Expression<? extends R>)this.buildLiteral(result));
    }

    private LiteralExpression<R> buildLiteral(R result) {
        Class<Object> type = result != null ? result.getClass() : this.getJavaType();
        return new LiteralExpression(this.criteriaBuilder(), type, result);
    }

    public CriteriaBuilder.Case<R> when(Expression<Boolean> condition, Expression<? extends R> result) {
        WhenClause whenClause = new WhenClause(condition, result);
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
        ParameterContainer.Helper.possibleParameter(this.getOtherwiseResult(), registry);
        for (WhenClause whenClause : this.getWhenClauses()) {
            ParameterContainer.Helper.possibleParameter(whenClause.getCondition(), registry);
            ParameterContainer.Helper.possibleParameter(whenClause.getResult(), registry);
        }
    }

    @Override
    public String render(RenderingContext renderingContext) {
        StringBuilder caseStatement = new StringBuilder("case");
        for (WhenClause whenClause : this.getWhenClauses()) {
            caseStatement.append(" when ").append(((Renderable)whenClause.getCondition()).render(renderingContext)).append(" then ").append(((Renderable)whenClause.getResult()).render(renderingContext));
        }
        Expression<R> otherwiseResult = this.getOtherwiseResult();
        if (otherwiseResult != null) {
            caseStatement.append(" else ").append(((Renderable)otherwiseResult).render(renderingContext));
        }
        caseStatement.append(" end");
        return caseStatement.toString();
    }

    public class WhenClause {
        private final Expression<Boolean> condition;
        private final Expression<? extends R> result;

        public WhenClause(Expression<Boolean> condition, Expression<? extends R> result) {
            this.condition = condition;
            this.result = result;
        }

        public Expression<Boolean> getCondition() {
            return this.condition;
        }

        public Expression<? extends R> getResult() {
            return this.result;
        }
    }
}

