/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.AbstractQuery
 *  javax.persistence.criteria.CommonAbstractCriteria
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Fetch
 *  javax.persistence.criteria.From
 *  javax.persistence.criteria.Join
 *  javax.persistence.criteria.JoinType
 *  javax.persistence.criteria.ParameterExpression
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.criteria.Root
 *  javax.persistence.criteria.Selection
 *  javax.persistence.criteria.Subquery
 *  javax.persistence.metamodel.EntityType
 */
package org.hibernate.query.criteria.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.FromImplementor;
import org.hibernate.query.criteria.internal.JoinImplementor;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.path.RootImpl;
import org.hibernate.sql.ast.Clause;

public class QueryStructure<T>
implements Serializable {
    private final AbstractQuery<T> owner;
    private final CriteriaBuilderImpl criteriaBuilder;
    private final boolean isSubQuery;
    private boolean distinct;
    private Selection<? extends T> selection;
    private Set<Root<?>> roots = new LinkedHashSet();
    private Set<FromImplementor> correlationRoots;
    private Predicate restriction;
    private List<Expression<?>> groupings = Collections.emptyList();
    private Predicate having;
    private List<Subquery<?>> subqueries;

    public QueryStructure(AbstractQuery<T> owner, CriteriaBuilderImpl criteriaBuilder) {
        this.owner = owner;
        this.criteriaBuilder = criteriaBuilder;
        this.isSubQuery = Subquery.class.isInstance(owner);
    }

    public Set<ParameterExpression<?>> getParameters() {
        final LinkedHashSet parameters = new LinkedHashSet();
        ParameterRegistry registry = new ParameterRegistry(){

            @Override
            public void registerParameter(ParameterExpression<?> parameter) {
                parameters.add(parameter);
            }
        };
        ParameterContainer.Helper.possibleParameter(this.selection, registry);
        ParameterContainer.Helper.possibleParameter((Selection)this.restriction, registry);
        ParameterContainer.Helper.possibleParameter((Selection)this.having, registry);
        if (this.subqueries != null) {
            for (Subquery<?> subquery : this.subqueries) {
                ParameterContainer.Helper.possibleParameter(subquery, registry);
            }
        }
        ParameterContainer.Helper.possibleParameter((Selection)this.having, registry);
        if (this.groupings != null) {
            for (Expression expression : this.groupings) {
                ParameterContainer.Helper.possibleParameter((Selection)expression, registry);
            }
        }
        return parameters;
    }

    public boolean isDistinct() {
        return this.distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public Selection<? extends T> getSelection() {
        return this.selection;
    }

    public void setSelection(Selection<? extends T> selection) {
        this.selection = selection;
    }

    public Set<Root<?>> getRoots() {
        return this.roots;
    }

    public <X> Root<X> from(Class<X> entityClass) {
        EntityType entityType = this.criteriaBuilder.getEntityManagerFactory().getMetamodel().entity((Class)entityClass);
        if (entityType == null) {
            throw new IllegalArgumentException(entityClass + " is not an entity");
        }
        return this.from(entityType);
    }

    public <X> Root<X> from(EntityType<X> entityType) {
        RootImpl<X> root = new RootImpl<X>(this.criteriaBuilder, entityType);
        this.roots.add(root);
        return root;
    }

    public void addCorrelationRoot(FromImplementor fromImplementor) {
        if (!this.isSubQuery) {
            throw new IllegalStateException("Query is not identified as sub-query");
        }
        if (this.correlationRoots == null) {
            this.correlationRoots = new HashSet<FromImplementor>();
        }
        this.correlationRoots.add(fromImplementor);
    }

    public Set<Join<?, ?>> collectCorrelatedJoins() {
        Set<Join<?, ?>> correlatedJoins;
        if (!this.isSubQuery) {
            throw new IllegalStateException("Query is not identified as sub-query");
        }
        if (this.correlationRoots != null) {
            correlatedJoins = new HashSet();
            for (FromImplementor correlationRoot : this.correlationRoots) {
                if (correlationRoot instanceof Join && correlationRoot.isCorrelated()) {
                    correlatedJoins.add((Join)correlationRoot);
                }
                correlatedJoins.addAll(correlationRoot.getJoins());
            }
        } else {
            correlatedJoins = Collections.emptySet();
        }
        return correlatedJoins;
    }

    public Predicate getRestriction() {
        return this.restriction;
    }

    public void setRestriction(Predicate restriction) {
        this.restriction = restriction;
    }

    public List<Expression<?>> getGroupings() {
        return this.groupings;
    }

    public void setGroupings(List<Expression<?>> groupings) {
        this.groupings = groupings;
    }

    public void setGroupings(Expression<?> ... groupings) {
        this.groupings = groupings != null && groupings.length > 0 ? Arrays.asList(groupings) : Collections.emptyList();
    }

    public Predicate getHaving() {
        return this.having;
    }

    public void setHaving(Predicate having) {
        this.having = having;
    }

    public List<Subquery<?>> getSubqueries() {
        return this.subqueries;
    }

    public List<Subquery<?>> internalGetSubqueries() {
        if (this.subqueries == null) {
            this.subqueries = new ArrayList();
        }
        return this.subqueries;
    }

    public <U> Subquery<U> subquery(Class<U> subqueryType) {
        CriteriaSubqueryImpl<U> subquery = new CriteriaSubqueryImpl<U>(this.criteriaBuilder, subqueryType, (CommonAbstractCriteria)this.owner);
        this.internalGetSubqueries().add(subquery);
        return subquery;
    }

    public void render(StringBuilder jpaqlQuery, RenderingContext renderingContext) {
        this.renderSelectClause(jpaqlQuery, renderingContext);
        this.renderFromClause(jpaqlQuery, renderingContext);
        this.renderWhereClause(jpaqlQuery, renderingContext);
        this.renderGroupByClause(jpaqlQuery, renderingContext);
    }

    protected void renderSelectClause(StringBuilder jpaqlQuery, RenderingContext renderingContext) {
        renderingContext.getClauseStack().push(Clause.SELECT);
        try {
            jpaqlQuery.append("select ");
            if (this.isDistinct()) {
                jpaqlQuery.append("distinct ");
            }
            if (this.getSelection() == null) {
                jpaqlQuery.append(this.locateImplicitSelection().render(renderingContext));
            } else {
                jpaqlQuery.append(((Renderable)this.getSelection()).render(renderingContext));
            }
        }
        finally {
            renderingContext.getClauseStack().pop();
        }
    }

    private FromImplementor locateImplicitSelection() {
        FromImplementor implicitSelection = null;
        if (!this.isSubQuery) {
            implicitSelection = (FromImplementor)this.getRoots().iterator().next();
        } else {
            Set<Join<?, ?>> correlatedJoins = this.collectCorrelatedJoins();
            if (correlatedJoins != null && correlatedJoins.size() == 1) {
                implicitSelection = (FromImplementor)correlatedJoins.iterator().next();
            }
        }
        if (implicitSelection == null) {
            throw new IllegalStateException("No explicit selection and an implicit one could not be determined");
        }
        return implicitSelection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderFromClause(StringBuilder jpaqlQuery, RenderingContext renderingContext) {
        block11: {
            renderingContext.getClauseStack().push(Clause.FROM);
            try {
                jpaqlQuery.append(" from ");
                String sep = "";
                for (Root<?> root : this.getRoots()) {
                    ((FromImplementor)root).prepareAlias(renderingContext);
                    jpaqlQuery.append(sep);
                    sep = ", ";
                    jpaqlQuery.append(((FromImplementor)root).renderTableExpression(renderingContext));
                }
                for (Root<?> root : this.getRoots()) {
                    this.renderJoins(jpaqlQuery, renderingContext, root.getJoins());
                    if (root instanceof RootImpl) {
                        Set treats = ((RootImpl)root).getTreats();
                        for (RootImpl.TreatedRoot treatedRoot : treats) {
                            this.renderJoins(jpaqlQuery, renderingContext, treatedRoot.getJoins());
                            this.renderFetches(jpaqlQuery, renderingContext, treatedRoot.getFetches());
                        }
                    }
                    this.renderFetches(jpaqlQuery, renderingContext, root.getFetches());
                }
                if (!this.isSubQuery || this.correlationRoots == null) break block11;
                for (FromImplementor correlationRoot : this.correlationRoots) {
                    From correlationParent = correlationRoot.getCorrelationParent();
                    correlationParent.prepareAlias(renderingContext);
                    String correlationRootAlias = correlationParent.getAlias();
                    if (correlationRoot.canBeReplacedByCorrelatedParentInSubQuery()) {
                        for (Join correlationJoin : correlationRoot.getJoins()) {
                            JoinImplementor correlationJoinImpl = (JoinImplementor)correlationJoin;
                            jpaqlQuery.append(sep);
                            correlationJoinImpl.prepareAlias(renderingContext);
                            jpaqlQuery.append(correlationRootAlias).append('.').append(correlationJoinImpl.getAttribute().getName()).append(" as ").append(correlationJoinImpl.getAlias());
                            sep = ", ";
                            this.renderJoins(jpaqlQuery, renderingContext, correlationJoinImpl.getJoins());
                        }
                        continue;
                    }
                    correlationRoot.prepareAlias(renderingContext);
                    jpaqlQuery.append(sep);
                    sep = ", ";
                    jpaqlQuery.append(correlationRoot.renderTableExpression(renderingContext));
                    this.renderJoins(jpaqlQuery, renderingContext, correlationRoot.getJoins());
                    if (!(correlationRoot instanceof Root)) continue;
                    Set set = ((RootImpl)correlationRoot).getTreats();
                    for (RootImpl.TreatedRoot treat : set) {
                        this.renderJoins(jpaqlQuery, renderingContext, treat.getJoins());
                    }
                }
            }
            finally {
                renderingContext.getClauseStack().pop();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void renderWhereClause(StringBuilder jpaqlQuery, RenderingContext renderingContext) {
        String correlationRestrictionWhereFragment = this.getCorrelationRestrictionsWhereFragment();
        if (this.getRestriction() == null && correlationRestrictionWhereFragment.isEmpty()) {
            return;
        }
        renderingContext.getClauseStack().push(Clause.WHERE);
        try {
            jpaqlQuery.append(" where ");
            jpaqlQuery.append(correlationRestrictionWhereFragment);
            if (this.getRestriction() != null) {
                if (!correlationRestrictionWhereFragment.isEmpty()) {
                    jpaqlQuery.append(" and ( ");
                }
                jpaqlQuery.append(((Renderable)this.getRestriction()).render(renderingContext));
                if (!correlationRestrictionWhereFragment.isEmpty()) {
                    jpaqlQuery.append(" )");
                }
            }
        }
        finally {
            renderingContext.getClauseStack().pop();
        }
    }

    private String getCorrelationRestrictionsWhereFragment() {
        if (!this.isSubQuery || this.correlationRoots == null) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        String sep = "";
        for (FromImplementor correlationRoot : this.correlationRoots) {
            if (correlationRoot.canBeReplacedByCorrelatedParentInSubQuery()) continue;
            buffer.append(sep);
            sep = " and ";
            buffer.append(correlationRoot.getAlias()).append("=").append(correlationRoot.getCorrelationParent().getAlias());
        }
        return buffer.toString();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void renderGroupByClause(StringBuilder jpaqlQuery, RenderingContext renderingContext) {
        if (this.getGroupings().isEmpty()) {
            return;
        }
        renderingContext.getClauseStack().push(Clause.GROUP);
        try {
            jpaqlQuery.append(" group by ");
            String sep = "";
            for (Expression<?> grouping : this.getGroupings()) {
                jpaqlQuery.append(sep).append(((Renderable)grouping).render(renderingContext));
                sep = ", ";
            }
            this.renderHavingClause(jpaqlQuery, renderingContext);
        }
        finally {
            renderingContext.getClauseStack().pop();
        }
    }

    private void renderHavingClause(StringBuilder jpaqlQuery, RenderingContext renderingContext) {
        if (this.getHaving() == null) {
            return;
        }
        renderingContext.getClauseStack().push(Clause.HAVING);
        try {
            jpaqlQuery.append(" having ").append(((Renderable)this.getHaving()).render(renderingContext));
        }
        finally {
            renderingContext.getClauseStack().pop();
        }
    }

    private void renderJoins(StringBuilder jpaqlQuery, RenderingContext renderingContext, Collection<? extends Join<?, ?>> joins) {
        if (joins == null) {
            return;
        }
        for (Join<?, ?> join : joins) {
            ((FromImplementor)join).prepareAlias(renderingContext);
            jpaqlQuery.append(this.renderJoinType(join.getJoinType())).append(((FromImplementor)join).renderTableExpression(renderingContext));
            this.renderJoins(jpaqlQuery, renderingContext, join.getJoins());
            this.renderFetches(jpaqlQuery, renderingContext, join.getFetches());
        }
    }

    private String renderJoinType(JoinType joinType) {
        switch (joinType) {
            case INNER: {
                return " inner join ";
            }
            case LEFT: {
                return " left join ";
            }
            case RIGHT: {
                return " right join ";
            }
        }
        throw new IllegalStateException("Unknown join type " + joinType);
    }

    private void renderFetches(StringBuilder jpaqlQuery, RenderingContext renderingContext, Collection<? extends Fetch> fetches) {
        if (fetches == null) {
            return;
        }
        for (Fetch fetch : fetches) {
            ((FromImplementor)fetch).prepareAlias(renderingContext);
            jpaqlQuery.append(this.renderJoinType(fetch.getJoinType())).append("fetch ").append(((FromImplementor)fetch).renderTableExpression(renderingContext));
            this.renderFetches(jpaqlQuery, renderingContext, fetch.getFetches());
            if (!(fetch instanceof From)) continue;
            From from = (From)fetch;
            this.renderJoins(jpaqlQuery, renderingContext, from.getJoins());
        }
    }
}

