/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Bindable$BindableType
 *  javax.persistence.metamodel.Type$PersistenceType
 */
package org.hibernate.metamodel.model.domain.internal;

import java.io.Serializable;
import javax.persistence.metamodel.Bindable;
import javax.persistence.metamodel.Type;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.internal.SubGraphImpl;
import org.hibernate.graph.spi.SubGraphImplementor;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.metamodel.model.domain.internal.AbstractIdentifiableType;
import org.hibernate.metamodel.model.domain.spi.EntityTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.IdentifiableTypeDescriptor;

public class EntityTypeImpl<J>
extends AbstractIdentifiableType<J>
implements EntityTypeDescriptor<J>,
Serializable {
    private final String jpaEntityName;

    public EntityTypeImpl(Class javaType, IdentifiableTypeDescriptor<? super J> superType, PersistentClass persistentClass, SessionFactoryImplementor sessionFactory) {
        super(javaType, persistentClass.getEntityName(), superType, persistentClass.getDeclaredIdentifierMapper() != null || superType != null && superType.hasIdClass(), persistentClass.hasIdentifierProperty(), persistentClass.isVersioned(), sessionFactory);
        this.jpaEntityName = persistentClass.getJpaEntityName();
    }

    @Override
    public String getName() {
        return this.jpaEntityName;
    }

    public Bindable.BindableType getBindableType() {
        return Bindable.BindableType.ENTITY_TYPE;
    }

    public Class<J> getBindableJavaType() {
        return this.getJavaType();
    }

    public Type.PersistenceType getPersistenceType() {
        return Type.PersistenceType.ENTITY;
    }

    @Override
    public IdentifiableTypeDescriptor<? super J> getSuperType() {
        return super.getSuperType();
    }

    @Override
    public <S extends J> SubGraphImplementor<S> makeSubGraph(Class<S> subType) {
        if (!this.getBindableJavaType().isAssignableFrom(subType)) {
            throw new IllegalArgumentException(String.format("Entity type [%s] cannot be treated as requested sub-type [%s]", this.getName(), subType.getName()));
        }
        return new SubGraphImpl(this, true, this.sessionFactory());
    }

    @Override
    public SubGraphImplementor<J> makeSubGraph() {
        return this.makeSubGraph(this.getBindableJavaType());
    }

    public String toString() {
        return this.getName();
    }
}

