/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.JoinType
 *  javax.persistence.metamodel.Attribute$PersistentAttributeType
 *  javax.persistence.metamodel.Bindable
 *  javax.persistence.metamodel.Bindable$BindableType
 *  javax.persistence.metamodel.ManagedType
 *  javax.persistence.metamodel.PluralAttribute
 *  javax.persistence.metamodel.SingularAttribute
 *  javax.persistence.metamodel.Type
 */
package org.hibernate.query.criteria.internal.path;

import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.FromImplementor;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.path.AbstractJoinImpl;

public class SingularAttributeJoin<O, X>
extends AbstractJoinImpl<O, X> {
    private final Bindable<X> model;

    public SingularAttributeJoin(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType, PathSource<O> pathSource, SingularAttribute<? super O, ?> joinAttribute, JoinType joinType) {
        super(criteriaBuilder, javaType, pathSource, joinAttribute, joinType);
        this.model = Attribute.PersistentAttributeType.EMBEDDED == joinAttribute.getPersistentAttributeType() ? joinAttribute : (javaType != null ? (Bindable)criteriaBuilder.getEntityManagerFactory().getMetamodel().managedType((Class)javaType) : (Bindable)joinAttribute.getType());
    }

    @Override
    public SingularAttribute<? super O, ?> getAttribute() {
        return (SingularAttribute)super.getAttribute();
    }

    @Override
    public SingularAttributeJoin<O, X> correlateTo(CriteriaSubqueryImpl subquery) {
        return (SingularAttributeJoin)super.correlateTo(subquery);
    }

    @Override
    protected FromImplementor<O, X> createCorrelationDelegate() {
        return new SingularAttributeJoin(this.criteriaBuilder(), this.getJavaType(), this.getPathSource(), this.getAttribute(), this.getJoinType());
    }

    @Override
    protected boolean canBeJoinSource() {
        return true;
    }

    @Override
    protected ManagedType<? super X> locateManagedType() {
        if (this.getModel().getBindableType() == Bindable.BindableType.ENTITY_TYPE) {
            return (ManagedType)this.getModel();
        }
        if (this.getModel().getBindableType() == Bindable.BindableType.SINGULAR_ATTRIBUTE) {
            Type joinedAttributeType = this.getAttribute().getType();
            if (!ManagedType.class.isInstance(joinedAttributeType)) {
                throw new UnsupportedOperationException("Cannot further dereference attribute join [" + this.getPathIdentifier() + "] as its type is not a ManagedType");
            }
            return (ManagedType)joinedAttributeType;
        }
        if (this.getModel().getBindableType() == Bindable.BindableType.PLURAL_ATTRIBUTE) {
            Type elementType = ((PluralAttribute)this.getAttribute()).getElementType();
            if (!ManagedType.class.isInstance(elementType)) {
                throw new UnsupportedOperationException("Cannot further dereference attribute join [" + this.getPathIdentifier() + "] (plural) as its element type is not a ManagedType");
            }
            return (ManagedType)elementType;
        }
        return super.locateManagedType();
    }

    public Bindable<X> getModel() {
        return this.model;
    }

    @Override
    public <T extends X> SingularAttributeJoin<O, T> treatAs(Class<T> treatAsType) {
        return new TreatedSingularAttributeJoin(this, treatAsType);
    }

    public static class TreatedSingularAttributeJoin<O, T>
    extends SingularAttributeJoin<O, T> {
        private final SingularAttributeJoin<O, ? super T> original;
        private final Class<T> treatAsType;

        public TreatedSingularAttributeJoin(SingularAttributeJoin<O, ? super T> original, Class<T> treatAsType) {
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
            ((SingularAttributeJoin)this.original).setAlias(alias);
        }

        @Override
        protected ManagedType<T> locateManagedType() {
            return this.criteriaBuilder().getEntityManagerFactory().getMetamodel().managedType(this.treatAsType);
        }

        @Override
        public String render(RenderingContext renderingContext) {
            return "treat(" + this.original.render(renderingContext) + " as " + this.treatAsType.getName() + ")";
        }
    }
}

