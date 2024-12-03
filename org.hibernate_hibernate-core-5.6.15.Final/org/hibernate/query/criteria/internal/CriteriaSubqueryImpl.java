/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.AbstractQuery
 *  javax.persistence.criteria.CollectionJoin
 *  javax.persistence.criteria.CommonAbstractCriteria
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Join
 *  javax.persistence.criteria.ListJoin
 *  javax.persistence.criteria.MapJoin
 *  javax.persistence.criteria.ParameterExpression
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.criteria.Root
 *  javax.persistence.criteria.Selection
 *  javax.persistence.criteria.SetJoin
 *  javax.persistence.criteria.Subquery
 *  javax.persistence.metamodel.EntityType
 */
package org.hibernate.query.criteria.internal;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;
import org.hibernate.query.criteria.internal.CollectionJoinImplementor;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.FromImplementor;
import org.hibernate.query.criteria.internal.JoinImplementor;
import org.hibernate.query.criteria.internal.ListJoinImplementor;
import org.hibernate.query.criteria.internal.MapJoinImplementor;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.QueryStructure;
import org.hibernate.query.criteria.internal.SetJoinImplementor;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.DelegatedExpressionImpl;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.path.RootImpl;
import org.hibernate.sql.ast.Clause;

public class CriteriaSubqueryImpl<T>
extends ExpressionImpl<T>
implements Subquery<T>,
Serializable {
    private final CommonAbstractCriteria parent;
    private final QueryStructure<T> queryStructure;
    private Expression<T> wrappedSelection;

    public CriteriaSubqueryImpl(CriteriaBuilderImpl criteriaBuilder, Class<T> javaType, CommonAbstractCriteria parent) {
        super(criteriaBuilder, javaType);
        this.parent = parent;
        this.queryStructure = new QueryStructure(this, criteriaBuilder);
    }

    public AbstractQuery<?> getParent() {
        if (!AbstractQuery.class.isInstance(this.parent)) {
            throw new IllegalStateException("Cannot call getParent on update/delete criterias");
        }
        return (AbstractQuery)this.parent;
    }

    public CommonAbstractCriteria getContainingQuery() {
        return this.parent;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        for (ParameterExpression<?> param : this.queryStructure.getParameters()) {
            registry.registerParameter(param);
        }
    }

    public Class<T> getResultType() {
        return this.getJavaType();
    }

    public Set<Root<?>> getRoots() {
        return this.queryStructure.getRoots();
    }

    public <X> Root<X> from(EntityType<X> entityType) {
        return this.queryStructure.from(entityType);
    }

    public <X> Root<X> from(Class<X> entityClass) {
        return this.queryStructure.from(entityClass);
    }

    public Subquery<T> distinct(boolean applyDistinction) {
        this.queryStructure.setDistinct(applyDistinction);
        return this;
    }

    public boolean isDistinct() {
        return this.queryStructure.isDistinct();
    }

    public Expression<T> getSelection() {
        if (this.wrappedSelection == null) {
            if (this.queryStructure.getSelection() == null) {
                return null;
            }
            this.wrappedSelection = new SubquerySelection<T>((ExpressionImpl)this.queryStructure.getSelection(), this);
        }
        return this.wrappedSelection;
    }

    public Subquery<T> select(Expression<T> expression) {
        this.queryStructure.setSelection((Selection<T>)expression);
        return this;
    }

    public Predicate getRestriction() {
        return this.queryStructure.getRestriction();
    }

    public Subquery<T> where(Expression<Boolean> expression) {
        this.queryStructure.setRestriction(this.criteriaBuilder().wrap(expression));
        return this;
    }

    public Subquery<T> where(Predicate ... predicates) {
        this.queryStructure.setRestriction(this.criteriaBuilder().and(predicates));
        return this;
    }

    public List<Expression<?>> getGroupList() {
        return this.queryStructure.getGroupings();
    }

    public Subquery<T> groupBy(Expression<?> ... groupings) {
        this.queryStructure.setGroupings(groupings);
        return this;
    }

    public Subquery<T> groupBy(List<Expression<?>> groupings) {
        this.queryStructure.setGroupings(groupings);
        return this;
    }

    public Predicate getGroupRestriction() {
        return this.queryStructure.getHaving();
    }

    public Subquery<T> having(Expression<Boolean> expression) {
        this.queryStructure.setHaving(this.criteriaBuilder().wrap(expression));
        return this;
    }

    public Subquery<T> having(Predicate ... predicates) {
        this.queryStructure.setHaving(this.criteriaBuilder().and(predicates));
        return this;
    }

    public Set<Join<?, ?>> getCorrelatedJoins() {
        return this.queryStructure.collectCorrelatedJoins();
    }

    public <Y> Root<Y> correlate(Root<Y> source) {
        FromImplementor correlation = ((RootImpl)source).correlateTo(this);
        this.queryStructure.addCorrelationRoot(correlation);
        return correlation;
    }

    public <X, Y> Join<X, Y> correlate(Join<X, Y> source) {
        FromImplementor correlation = ((JoinImplementor)source).correlateTo(this);
        this.queryStructure.addCorrelationRoot(correlation);
        return correlation;
    }

    public <X, Y> CollectionJoin<X, Y> correlate(CollectionJoin<X, Y> source) {
        JoinImplementor correlation = ((CollectionJoinImplementor)source).correlateTo(this);
        this.queryStructure.addCorrelationRoot(correlation);
        return correlation;
    }

    public <X, Y> SetJoin<X, Y> correlate(SetJoin<X, Y> source) {
        JoinImplementor correlation = ((SetJoinImplementor)source).correlateTo(this);
        this.queryStructure.addCorrelationRoot(correlation);
        return correlation;
    }

    public <X, Y> ListJoin<X, Y> correlate(ListJoin<X, Y> source) {
        JoinImplementor correlation = ((ListJoinImplementor)source).correlateTo(this);
        this.queryStructure.addCorrelationRoot(correlation);
        return correlation;
    }

    public <X, K, V> MapJoin<X, K, V> correlate(MapJoin<X, K, V> source) {
        JoinImplementor correlation = ((MapJoinImplementor)source).correlateTo(this);
        this.queryStructure.addCorrelationRoot(correlation);
        return correlation;
    }

    public <U> Subquery<U> subquery(Class<U> subqueryType) {
        return this.queryStructure.subquery(subqueryType);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        if (this.criteriaBuilder().getEntityManagerFactory().getSessionFactoryOptions().getJpaCompliance().isJpaQueryComplianceEnabled() && renderingContext.getClauseStack().getCurrent() == Clause.SELECT) {
            throw new IllegalStateException("The JPA specification does not support subqueries in select clauses. Please disable the JPA query compliance if you want to use this feature.");
        }
        StringBuilder subqueryBuffer = new StringBuilder("(");
        this.queryStructure.render(subqueryBuffer, renderingContext);
        subqueryBuffer.append(')');
        return subqueryBuffer.toString();
    }

    public static class SubquerySelection<S>
    extends DelegatedExpressionImpl<S> {
        private final CriteriaSubqueryImpl subQuery;

        public SubquerySelection(ExpressionImpl<S> wrapped, CriteriaSubqueryImpl subQuery) {
            super(wrapped);
            this.subQuery = subQuery;
        }

        @Override
        public String render(RenderingContext renderingContext) {
            return this.subQuery.render(renderingContext);
        }
    }
}

