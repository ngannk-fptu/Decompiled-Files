/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CriteriaBuilder$In
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Subquery
 */
package org.hibernate.query.criteria.internal.predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Subquery;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.ValueHandlerFactory;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.hibernate.query.criteria.internal.expression.ParameterExpressionImpl;
import org.hibernate.query.criteria.internal.predicate.AbstractSimplePredicate;
import org.hibernate.type.Type;

public class InPredicate<T>
extends AbstractSimplePredicate
implements CriteriaBuilder.In<T>,
Serializable {
    private final Expression<? extends T> expression;
    private final List<Expression<? extends T>> values;

    public InPredicate(CriteriaBuilderImpl criteriaBuilder, Expression<? extends T> expression) {
        this(criteriaBuilder, expression, (List<Expression<? extends T>>)new ArrayList<Expression<? extends T>>());
    }

    public InPredicate(CriteriaBuilderImpl criteriaBuilder, Expression<? extends T> expression, Expression<? extends T> ... values) {
        this(criteriaBuilder, expression, Arrays.asList(values));
    }

    public InPredicate(CriteriaBuilderImpl criteriaBuilder, Expression<? extends T> expression, List<Expression<? extends T>> values) {
        super(criteriaBuilder);
        this.expression = expression;
        this.values = values;
    }

    public InPredicate(CriteriaBuilderImpl criteriaBuilder, Expression<? extends T> expression, T ... values) {
        this(criteriaBuilder, expression, (Collection<? extends T>)Arrays.asList(values));
    }

    public InPredicate(CriteriaBuilderImpl criteriaBuilder, Expression<? extends T> expression, Collection<T> values) {
        super(criteriaBuilder);
        this.expression = expression;
        this.values = new ArrayList<Expression<? extends T>>(values.size());
        Class javaType = expression.getJavaType();
        ValueHandlerFactory.NoOpValueHandler valueHandler = javaType != null && ValueHandlerFactory.isNumeric(javaType) ? ValueHandlerFactory.determineAppropriateHandler(javaType) : new ValueHandlerFactory.NoOpValueHandler();
        for (T value : values) {
            if (value instanceof Expression) {
                this.values.add((Expression)value);
                continue;
            }
            this.values.add(new LiteralExpression(criteriaBuilder, valueHandler.convert(value)));
        }
    }

    public Expression<T> getExpression() {
        return this.expression;
    }

    public Expression<? extends T> getExpressionInternal() {
        return this.expression;
    }

    public List<Expression<? extends T>> getValues() {
        return this.values;
    }

    public InPredicate<T> value(T value) {
        return this.value(new LiteralExpression<T>(this.criteriaBuilder(), value));
    }

    public InPredicate<T> value(Expression<? extends T> value) {
        this.values.add(value);
        return this;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getExpressionInternal(), registry);
        for (Expression<T> value : this.getValues()) {
            ParameterContainer.Helper.possibleParameter(value, registry);
        }
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        boolean isInSubqueryPredicate;
        StringBuilder buffer = new StringBuilder();
        Expression<T> exp = this.getExpression();
        if (ParameterExpressionImpl.class.isInstance(exp)) {
            ParameterExpressionImpl parameterExpression = (ParameterExpressionImpl)exp;
            SessionFactoryImplementor sfi = this.criteriaBuilder().getEntityManagerFactory().unwrap(SessionFactoryImplementor.class);
            Type mappingType = sfi.getTypeResolver().heuristicType(parameterExpression.getParameterType().getName());
            buffer.append("cast(").append(parameterExpression.render(renderingContext)).append(" as ").append(mappingType.getName()).append(")");
        } else {
            buffer.append(((Renderable)this.getExpression()).render(renderingContext));
        }
        if (isNegated) {
            buffer.append(" not");
        }
        buffer.append(" in ");
        List<Expression<T>> values = this.getValues();
        boolean bl = isInSubqueryPredicate = values.size() == 1 && Subquery.class.isInstance(values.get(0));
        if (isInSubqueryPredicate) {
            buffer.append(((Renderable)values.get(0)).render(renderingContext));
        } else if (values.isEmpty()) {
            if (renderingContext.getDialect().supportsEmptyInList()) {
                buffer.append("()");
            } else {
                buffer.append("(null)");
            }
        } else {
            buffer.append('(');
            String sep = "";
            for (Expression<T> value : values) {
                buffer.append(sep).append(((Renderable)value).render(renderingContext));
                sep = ", ";
            }
            buffer.append(')');
        }
        return buffer.toString();
    }
}

