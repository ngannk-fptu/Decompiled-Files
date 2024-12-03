/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Type$PersistenceType
 */
package org.hibernate.metamodel.model.domain.internal;

import javax.persistence.metamodel.Type;
import org.hibernate.cfg.NotYetImplementedException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.spi.SubGraphImplementor;
import org.hibernate.mapping.MappedSuperclass;
import org.hibernate.metamodel.model.domain.internal.AbstractIdentifiableType;
import org.hibernate.metamodel.model.domain.spi.IdentifiableTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.MappedSuperclassTypeDescriptor;

public class MappedSuperclassTypeImpl<X>
extends AbstractIdentifiableType<X>
implements MappedSuperclassTypeDescriptor<X> {
    public MappedSuperclassTypeImpl(Class<X> javaType, MappedSuperclass mappedSuperclass, IdentifiableTypeDescriptor<? super X> superType, SessionFactoryImplementor sessionFactory) {
        super(javaType, javaType.getName(), superType, mappedSuperclass.getDeclaredIdentifierMapper() != null || superType != null && superType.hasIdClass(), mappedSuperclass.hasIdentifierProperty(), mappedSuperclass.isVersioned(), sessionFactory);
    }

    public Type.PersistenceType getPersistenceType() {
        return Type.PersistenceType.MAPPED_SUPERCLASS;
    }

    @Override
    public <S extends X> SubGraphImplementor<S> makeSubGraph(Class<S> subType) {
        throw new NotYetImplementedException();
    }
}

