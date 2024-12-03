/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityGraph
 *  javax.persistence.metamodel.ManagedType
 */
package org.hibernate.graph.internal;

import javax.persistence.EntityGraph;
import javax.persistence.metamodel.ManagedType;
import org.hibernate.cfg.NotYetImplementedException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.SubGraph;
import org.hibernate.graph.internal.AbstractGraph;
import org.hibernate.graph.internal.SubGraphImpl;
import org.hibernate.graph.spi.GraphImplementor;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.graph.spi.SubGraphImplementor;
import org.hibernate.metamodel.model.domain.spi.EntityTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.IdentifiableTypeDescriptor;

public class RootGraphImpl<J>
extends AbstractGraph<J>
implements EntityGraph<J>,
RootGraphImplementor<J> {
    private final String name;

    public RootGraphImpl(String name, EntityTypeDescriptor<J> entityType, boolean mutable, SessionFactoryImplementor sessionFactory) {
        super(entityType, mutable, sessionFactory);
        this.name = name;
    }

    public RootGraphImpl(String name, EntityTypeDescriptor<J> entityType, SessionFactoryImplementor sessionFactory) {
        this(name, entityType, true, sessionFactory);
    }

    public RootGraphImpl(String name, boolean mutable, GraphImplementor<J> original) {
        super(mutable, original);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public RootGraphImplementor<J> makeCopy(boolean mutable) {
        return new RootGraphImpl<J>(null, mutable, this);
    }

    @Override
    public SubGraphImplementor<J> makeSubGraph(boolean mutable) {
        return new SubGraphImpl(mutable, this);
    }

    @Override
    public RootGraphImplementor<J> makeRootGraph(String name, boolean mutable) {
        if (!mutable && !this.isMutable()) {
            return this;
        }
        return super.makeRootGraph(name, mutable);
    }

    @Override
    public <T1> SubGraph<? extends T1> addSubclassSubgraph(Class<? extends T1> type) {
        throw new NotYetImplementedException();
    }

    @Override
    public boolean appliesTo(EntityTypeDescriptor<? super J> entityType) {
        ManagedType managedTypeDescriptor = this.getGraphedType();
        if (managedTypeDescriptor.equals(entityType)) {
            return true;
        }
        for (IdentifiableTypeDescriptor superType = entityType.getSupertype(); superType != null; superType = superType.getSupertype()) {
            if (!managedTypeDescriptor.equals(superType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean appliesTo(String entityName) {
        return this.appliesTo((EntityTypeDescriptor<? super J>)this.sessionFactory().getMetamodel().entity(entityName));
    }

    @Override
    public boolean appliesTo(Class entityType) {
        return this.appliesTo((EntityTypeDescriptor<? super J>)this.sessionFactory().getMetamodel().entity(entityType));
    }
}

