/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.JoinType
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.metamodel.ManagedType
 *  javax.persistence.metamodel.SetAttribute
 */
package org.hibernate.query.criteria.internal.path;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SetAttribute;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.FromImplementor;
import org.hibernate.query.criteria.internal.PathImplementor;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.SetJoinImplementor;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.path.PluralAttributeJoinSupport;

public class SetAttributeJoin<O, E>
extends PluralAttributeJoinSupport<O, Set<E>, E>
implements SetJoinImplementor<O, E>,
Serializable {
    public SetAttributeJoin(CriteriaBuilderImpl criteriaBuilder, Class<E> javaType, PathSource<O> pathSource, SetAttribute<? super O, E> joinAttribute, JoinType joinType) {
        super(criteriaBuilder, javaType, pathSource, joinAttribute, joinType);
    }

    @Override
    public SetAttribute<? super O, E> getAttribute() {
        return (SetAttribute)super.getAttribute();
    }

    public SetAttribute<? super O, E> getModel() {
        return this.getAttribute();
    }

    @Override
    public final SetAttributeJoin<O, E> correlateTo(CriteriaSubqueryImpl subquery) {
        return (SetAttributeJoin)super.correlateTo(subquery);
    }

    @Override
    protected FromImplementor<O, E> createCorrelationDelegate() {
        return new SetAttributeJoin(this.criteriaBuilder(), this.getJavaType(), (PathImplementor)this.getParentPath(), this.getAttribute(), this.getJoinType());
    }

    @Override
    public SetJoinImplementor<O, E> on(Predicate ... restrictions) {
        return (SetJoinImplementor)super.on(restrictions);
    }

    @Override
    public SetJoinImplementor<O, E> on(Expression<Boolean> restriction) {
        return (SetJoinImplementor)super.on((Expression)restriction);
    }

    @Override
    public <T extends E> SetAttributeJoin<O, T> treatAs(Class<T> treatAsType) {
        return new TreatedSetAttributeJoin(this, treatAsType);
    }

    public static class TreatedSetAttributeJoin<O, T>
    extends SetAttributeJoin<O, T> {
        private final SetAttributeJoin<O, ? super T> original;
        private final Class<T> treatAsType;

        public TreatedSetAttributeJoin(SetAttributeJoin<O, ? super T> original, Class<T> treatAsType) {
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
            ((SetAttributeJoin)this.original).setAlias(alias);
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

