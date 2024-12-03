/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.JoinType
 *  javax.persistence.criteria.PluralJoin
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.metamodel.Attribute
 *  javax.persistence.metamodel.ManagedType
 *  javax.persistence.metamodel.PluralAttribute
 *  javax.persistence.metamodel.Type$PersistenceType
 */
package org.hibernate.query.criteria.internal.path;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.PluralJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.Type;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.JoinImplementor;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.path.AbstractJoinImpl;

public abstract class PluralAttributeJoinSupport<O, C, E>
extends AbstractJoinImpl<O, E>
implements PluralJoin<O, C, E> {
    public PluralAttributeJoinSupport(CriteriaBuilderImpl criteriaBuilder, Class<E> javaType, PathSource<O> pathSource, Attribute<? super O, ?> joinAttribute, JoinType joinType) {
        super(criteriaBuilder, javaType, pathSource, joinAttribute, joinType);
    }

    public PluralAttribute<? super O, C, E> getAttribute() {
        return (PluralAttribute)super.getAttribute();
    }

    public PluralAttribute<? super O, C, E> getModel() {
        return this.getAttribute();
    }

    @Override
    protected ManagedType<E> locateManagedType() {
        return this.isBasicCollection() ? null : (ManagedType)this.getAttribute().getElementType();
    }

    public boolean isBasicCollection() {
        return Type.PersistenceType.BASIC.equals((Object)this.getAttribute().getElementType().getPersistenceType());
    }

    @Override
    protected boolean canBeDereferenced() {
        return !this.isBasicCollection();
    }

    @Override
    protected boolean canBeJoinSource() {
        return !this.isBasicCollection();
    }

    @Override
    public JoinImplementor<O, E> on(Predicate ... restrictions) {
        return super.on(restrictions);
    }

    @Override
    public JoinImplementor<O, E> on(Expression<Boolean> restriction) {
        return super.on((Expression)restriction);
    }
}

