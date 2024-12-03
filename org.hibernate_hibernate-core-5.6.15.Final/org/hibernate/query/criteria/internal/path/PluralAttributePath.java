/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute
 *  javax.persistence.metamodel.Bindable
 *  javax.persistence.metamodel.Bindable$BindableType
 *  javax.persistence.metamodel.EntityType
 *  javax.persistence.metamodel.IdentifiableType
 *  javax.persistence.metamodel.MappedSuperclassType
 *  javax.persistence.metamodel.PluralAttribute
 *  javax.persistence.metamodel.SingularAttribute
 *  javax.persistence.metamodel.Type$PersistenceType
 */
package org.hibernate.query.criteria.internal.path;

import java.io.Serializable;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.MappedSuperclassType;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import org.hibernate.AssertionFailure;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.PathSource;
import org.hibernate.query.criteria.internal.path.AbstractPathImpl;
import org.hibernate.query.criteria.internal.path.SingularAttributePath;

public class PluralAttributePath<X>
extends AbstractPathImpl<X>
implements Serializable {
    private final PluralAttribute<?, X, ?> attribute;
    private final CollectionPersister persister;

    public PluralAttributePath(CriteriaBuilderImpl criteriaBuilder, PathSource source, PluralAttribute<?, X, ?> attribute) {
        super(criteriaBuilder, attribute.getJavaType(), source);
        this.attribute = attribute;
        this.persister = this.resolvePersister(criteriaBuilder, attribute);
    }

    private CollectionPersister resolvePersister(CriteriaBuilderImpl criteriaBuilder, PluralAttribute attribute) {
        SessionFactoryImplementor sfi = criteriaBuilder.getEntityManagerFactory().getSessionFactory();
        return sfi.getCollectionPersister(this.resolveRole(attribute));
    }

    private String resolveRole(PluralAttribute attribute) {
        switch (attribute.getDeclaringType().getPersistenceType()) {
            case ENTITY: {
                return attribute.getDeclaringType().getJavaType().getName() + '.' + attribute.getName();
            }
            case MAPPED_SUPERCLASS: {
                if (this.getPathSource().getModel().getBindableType() == Bindable.BindableType.ENTITY_TYPE) {
                    EntityType entityTypeNearestDeclaringType = this.locateNearestSubclassEntity((MappedSuperclassType)attribute.getDeclaringType(), (EntityType)this.getPathSource().getModel());
                    return entityTypeNearestDeclaringType.getJavaType().getName() + '.' + attribute.getName();
                }
                throw new AssertionFailure(String.format("Unexpected BindableType; expected [%s]; instead got [%s]", Bindable.BindableType.ENTITY_TYPE, this.getPathSource().getModel().getBindableType()));
            }
            case EMBEDDABLE: {
                EntityType entityType;
                SingularAttribute singularAttribute;
                SingularAttributePath singularAttributePath;
                StringBuilder role = new StringBuilder().append('.').append(attribute.getName());
                PathSource parentPath = this.getPathSource();
                do {
                    singularAttributePath = (SingularAttributePath)parentPath;
                    singularAttribute = singularAttributePath.getAttribute();
                    role.insert(0, '.');
                    role.insert(1, singularAttributePath.getAttribute().getName());
                } while (SingularAttributePath.class.isInstance(parentPath = singularAttributePath.getPathSource()));
                if (singularAttribute.getDeclaringType().getPersistenceType() == Type.PersistenceType.ENTITY) {
                    entityType = (EntityType)singularAttribute.getDeclaringType();
                } else if (singularAttribute.getDeclaringType().getPersistenceType() == Type.PersistenceType.MAPPED_SUPERCLASS) {
                    entityType = this.locateNearestSubclassEntity((MappedSuperclassType)singularAttribute.getDeclaringType(), (EntityType)parentPath.getModel());
                } else {
                    throw new AssertionFailure(String.format("Unexpected PersistenceType: [%s]", singularAttribute.getDeclaringType().getPersistenceType()));
                }
                return role.insert(0, entityType.getJavaType().getName()).toString();
            }
        }
        throw new AssertionFailure(String.format("Unexpected PersistenceType: [%s]", attribute.getDeclaringType().getPersistenceType()));
    }

    public PluralAttribute<?, X, ?> getAttribute() {
        return this.attribute;
    }

    public CollectionPersister getPersister() {
        return this.persister;
    }

    @Override
    protected boolean canBeDereferenced() {
        return false;
    }

    @Override
    protected Attribute locateAttributeInternal(String attributeName) {
        throw new IllegalArgumentException("Plural attribute paths cannot be further dereferenced");
    }

    public Bindable<X> getModel() {
        return null;
    }

    @Override
    public <T extends X> PluralAttributePath<T> treatAs(Class<T> treatAsType) {
        throw new UnsupportedOperationException("Plural attribute path [" + this.getPathSource().getPathIdentifier() + '.' + this.attribute.getName() + "] cannot be dereferenced");
    }

    private EntityType locateNearestSubclassEntity(MappedSuperclassType mappedSuperclassType, EntityType entityTypeTop) {
        EntityType entityTypeNearestDeclaringType = entityTypeTop;
        for (IdentifiableType superType = entityTypeNearestDeclaringType.getSupertype(); superType != mappedSuperclassType; superType = superType.getSupertype()) {
            if (superType == null) {
                throw new IllegalStateException(String.format("Cannot determine nearest EntityType extending mapped superclass [%s] starting from [%s]; a supertype of [%s] is null", mappedSuperclassType.getJavaType().getName(), entityTypeTop.getJavaType().getName(), entityTypeTop.getJavaType().getName()));
            }
            if (superType.getPersistenceType() != Type.PersistenceType.ENTITY) continue;
            entityTypeNearestDeclaringType = (EntityType)superType;
        }
        return entityTypeNearestDeclaringType;
    }
}

