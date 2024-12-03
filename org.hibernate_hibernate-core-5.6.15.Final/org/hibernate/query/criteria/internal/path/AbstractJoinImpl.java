/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.From
 *  javax.persistence.criteria.JoinType
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.metamodel.Attribute
 */
package org.hibernate.query.criteria.internal.path;

import java.io.Serializable;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.FromImplementor;
import org.hibernate.query.criteria.internal.JoinImplementor;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.path.AbstractFromImpl;
import org.hibernate.query.criteria.internal.predicate.PredicateImplementor;

public abstract class AbstractJoinImpl<Z, X>
extends AbstractFromImpl<Z, X>
implements JoinImplementor<Z, X>,
Serializable {
    private final Attribute<? super Z, ?> joinAttribute;
    private final JoinType joinType;
    private Predicate suppliedJoinCondition;

    public AbstractJoinImpl(CriteriaBuilderImpl criteriaBuilder, PathSource<Z> pathSource, Attribute<? super Z, X> joinAttribute, JoinType joinType) {
        this(criteriaBuilder, joinAttribute.getJavaType(), pathSource, joinAttribute, joinType);
    }

    public AbstractJoinImpl(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType, PathSource<Z> pathSource, Attribute<? super Z, ?> joinAttribute, JoinType joinType) {
        super(criteriaBuilder, javaType, pathSource);
        this.joinAttribute = joinAttribute;
        this.joinType = joinType;
    }

    @Override
    public Attribute<? super Z, ?> getAttribute() {
        return this.joinAttribute;
    }

    public JoinType getJoinType() {
        return this.joinType;
    }

    @Override
    public From<?, Z> getParent() {
        return (From)this.getPathSource();
    }

    @Override
    public String renderTableExpression(RenderingContext renderingContext) {
        this.prepareAlias(renderingContext);
        ((FromImplementor)this.getParent()).prepareAlias(renderingContext);
        StringBuilder tableExpression = new StringBuilder();
        tableExpression.append(this.getParent().getAlias()).append('.').append(this.getAttribute().getName()).append(" as ").append(this.getAlias());
        if (this.suppliedJoinCondition != null) {
            tableExpression.append(" with ").append(((PredicateImplementor)this.suppliedJoinCondition).render(renderingContext));
        }
        return tableExpression.toString();
    }

    @Override
    public JoinImplementor<Z, X> correlateTo(CriteriaSubqueryImpl subquery) {
        return (JoinImplementor)super.correlateTo(subquery);
    }

    @Override
    public JoinImplementor<Z, X> on(Predicate ... restrictions) {
        this.suppliedJoinCondition = null;
        if (restrictions != null && restrictions.length > 0) {
            this.suppliedJoinCondition = this.criteriaBuilder().and(restrictions);
        }
        return this;
    }

    @Override
    public JoinImplementor<Z, X> on(Expression<Boolean> restriction) {
        this.suppliedJoinCondition = this.criteriaBuilder().wrap(restriction);
        return this;
    }

    public Predicate getOn() {
        return this.suppliedJoinCondition;
    }
}

