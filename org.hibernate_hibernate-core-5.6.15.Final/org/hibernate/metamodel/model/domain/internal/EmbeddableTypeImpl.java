/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Type$PersistenceType
 */
package org.hibernate.metamodel.model.domain.internal;

import java.io.Serializable;
import javax.persistence.metamodel.Type;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.internal.SubGraphImpl;
import org.hibernate.graph.spi.SubGraphImplementor;
import org.hibernate.metamodel.model.domain.internal.AbstractManagedType;
import org.hibernate.metamodel.model.domain.spi.EmbeddedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.type.CompositeType;

public class EmbeddableTypeImpl<J>
extends AbstractManagedType<J>
implements EmbeddedTypeDescriptor<J>,
Serializable {
    private final ManagedTypeDescriptor<?> parent;
    private final CompositeType hibernateType;

    public EmbeddableTypeImpl(Class<J> javaType, ManagedTypeDescriptor<?> parent, CompositeType hibernateType, SessionFactoryImplementor sessionFactory) {
        super(javaType, null, null, sessionFactory);
        this.parent = parent;
        this.hibernateType = hibernateType;
    }

    public Type.PersistenceType getPersistenceType() {
        return Type.PersistenceType.EMBEDDABLE;
    }

    @Override
    public ManagedTypeDescriptor<?> getParent() {
        return this.parent;
    }

    @Override
    public CompositeType getHibernateType() {
        return this.hibernateType;
    }

    @Override
    public <S extends J> SubGraphImplementor<S> makeSubGraph(Class<S> subType) {
        return new SubGraphImpl(this, true, this.sessionFactory());
    }
}

