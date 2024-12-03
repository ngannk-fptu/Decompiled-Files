/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.JoinType
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.metamodel.CollectionAttribute
 *  javax.persistence.metamodel.ManagedType
 */
package org.hibernate.query.criteria.internal.path;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ManagedType;
import org.hibernate.query.criteria.internal.CollectionJoinImplementor;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.FromImplementor;
import org.hibernate.query.criteria.internal.PathImplementor;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.path.PluralAttributeJoinSupport;

public class CollectionAttributeJoin<O, E>
extends PluralAttributeJoinSupport<O, Collection<E>, E>
implements CollectionJoinImplementor<O, E>,
Serializable {
    public CollectionAttributeJoin(CriteriaBuilderImpl criteriaBuilder, Class<E> javaType, PathSource<O> pathSource, CollectionAttribute<? super O, E> joinAttribute, JoinType joinType) {
        super(criteriaBuilder, javaType, pathSource, joinAttribute, joinType);
    }

    @Override
    public final CollectionAttributeJoin<O, E> correlateTo(CriteriaSubqueryImpl subquery) {
        return (CollectionAttributeJoin)super.correlateTo(subquery);
    }

    @Override
    public CollectionAttribute<? super O, E> getAttribute() {
        return (CollectionAttribute)super.getAttribute();
    }

    public CollectionAttribute<? super O, E> getModel() {
        return this.getAttribute();
    }

    @Override
    protected FromImplementor<O, E> createCorrelationDelegate() {
        return new CollectionAttributeJoin(this.criteriaBuilder(), this.getJavaType(), (PathImplementor)this.getParentPath(), this.getAttribute(), this.getJoinType());
    }

    @Override
    public CollectionAttributeJoin<O, E> on(Predicate ... restrictions) {
        return (CollectionAttributeJoin)super.on(restrictions);
    }

    @Override
    public CollectionAttributeJoin<O, E> on(Expression<Boolean> restriction) {
        return (CollectionAttributeJoin)super.on((Expression)restriction);
    }

    @Override
    public <T extends E> CollectionAttributeJoin<O, T> treatAs(Class<T> treatAsType) {
        return new TreatedCollectionAttributeJoin(this, treatAsType);
    }

    public static class TreatedCollectionAttributeJoin<O, T>
    extends CollectionAttributeJoin<O, T> {
        private final CollectionAttributeJoin<O, ? super T> original;
        private final Class<T> treatAsType;

        public TreatedCollectionAttributeJoin(CollectionAttributeJoin<O, ? super T> original, Class<T> treatAsType) {
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
            ((CollectionAttributeJoin)this.original).setAlias(alias);
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

