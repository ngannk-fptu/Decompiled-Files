/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.JoinType
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.metamodel.ListAttribute
 *  javax.persistence.metamodel.ManagedType
 */
package org.hibernate.query.criteria.internal.path;

import java.io.Serializable;
import java.util.List;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.FromImplementor;
import org.hibernate.query.criteria.internal.ListJoinImplementor;
import org.hibernate.query.criteria.internal.PathImplementor;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ListIndexExpression;
import org.hibernate.query.criteria.internal.path.PluralAttributeJoinSupport;

public class ListAttributeJoin<O, E>
extends PluralAttributeJoinSupport<O, List<E>, E>
implements ListJoinImplementor<O, E>,
Serializable {
    public ListAttributeJoin(CriteriaBuilderImpl criteriaBuilder, Class<E> javaType, PathSource<O> pathSource, ListAttribute<? super O, E> joinAttribute, JoinType joinType) {
        super(criteriaBuilder, javaType, pathSource, joinAttribute, joinType);
    }

    public Expression<Integer> index() {
        return new ListIndexExpression(this.criteriaBuilder(), this, this.getAttribute());
    }

    @Override
    public ListAttribute<? super O, E> getAttribute() {
        return (ListAttribute)super.getAttribute();
    }

    public ListAttribute<? super O, E> getModel() {
        return this.getAttribute();
    }

    @Override
    public final ListAttributeJoin<O, E> correlateTo(CriteriaSubqueryImpl subquery) {
        return (ListAttributeJoin)super.correlateTo(subquery);
    }

    @Override
    protected FromImplementor<O, E> createCorrelationDelegate() {
        return new ListAttributeJoin(this.criteriaBuilder(), this.getJavaType(), (PathImplementor)this.getParentPath(), this.getAttribute(), this.getJoinType());
    }

    @Override
    public ListAttributeJoin<O, E> on(Predicate ... restrictions) {
        return (ListAttributeJoin)super.on(restrictions);
    }

    @Override
    public ListAttributeJoin<O, E> on(Expression<Boolean> restriction) {
        return (ListAttributeJoin)super.on((Expression)restriction);
    }

    @Override
    public <T extends E> ListAttributeJoin<O, T> treatAs(Class<T> treatAsType) {
        return new TreatedListAttributeJoin(this, treatAsType);
    }

    public static class TreatedListAttributeJoin<O, T>
    extends ListAttributeJoin<O, T> {
        private final ListAttributeJoin<O, ? super T> original;
        private final Class<T> treatAsType;

        public TreatedListAttributeJoin(ListAttributeJoin<O, ? super T> original, Class<T> treatAsType) {
            super(original.criteriaBuilder(), treatAsType, original.getPathSource(), original.getAttribute(), original.getJoinType());
            this.original = original;
            this.treatAsType = treatAsType;
        }

        @Override
        public String getAlias() {
            return this.isCorrelated() ? this.getCorrelationParent().getAlias() : super.getAlias();
        }

        @Override
        public void prepareAlias(RenderingContext renderingContext) {
            if (this.getAlias() == null) {
                if (this.isCorrelated()) {
                    this.setAlias(this.getCorrelationParent().getAlias());
                } else {
                    this.setAlias(renderingContext.generateAlias());
                }
            }
        }

        @Override
        protected void setAlias(String alias) {
            super.setAlias(alias);
            ((ListAttributeJoin)this.original).setAlias(alias);
        }

        @Override
        public String render(RenderingContext renderingContext) {
            return "treat(" + this.original.render(renderingContext) + " as " + this.treatAsType.getName() + ")";
        }

        @Override
        protected ManagedType<T> locateManagedType() {
            return this.criteriaBuilder().getEntityManagerFactory().getMetamodel().managedType(this.treatAsType);
        }

        @Override
        public String getPathIdentifier() {
            return "treat(" + this.getAlias() + " as " + this.treatAsType.getName() + ")";
        }

        @Override
        protected PathSource getPathSourceForSubPaths() {
            return this;
        }
    }
}

