/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute
 */
package org.hibernate.graph.internal;

import javax.persistence.metamodel.Attribute;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.internal.AbstractGraph;
import org.hibernate.graph.spi.AttributeNodeImplementor;
import org.hibernate.graph.spi.SubGraphImplementor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;

public class SubGraphImpl<J>
extends AbstractGraph<J>
implements SubGraphImplementor<J> {
    public SubGraphImpl(ManagedTypeDescriptor<J> managedType, boolean mutable, SessionFactoryImplementor sessionFactory) {
        super(managedType, mutable, sessionFactory);
    }

    public SubGraphImpl(boolean mutable, AbstractGraph<J> original) {
        super(mutable, original);
    }

    @Override
    public SubGraphImplementor<J> makeCopy(boolean mutable) {
        return new SubGraphImpl<J>(mutable, this);
    }

    @Override
    public SubGraphImplementor<J> makeSubGraph(boolean mutable) {
        if (!mutable && !this.isMutable()) {
            return this;
        }
        return this.makeCopy(true);
    }

    @Override
    public <AJ> SubGraphImplementor<AJ> addKeySubGraph(String attributeName) {
        return super.addKeySubGraph(attributeName);
    }

    @Override
    public <AJ> AttributeNodeImplementor<AJ> addAttributeNode(Attribute<? extends J, AJ> attribute) {
        return ((AbstractGraph)this).addAttributeNode((PersistentAttributeDescriptor)attribute);
    }

    @Override
    public boolean appliesTo(ManagedTypeDescriptor<? super J> managedType) {
        if (this.getGraphedType().equals(managedType)) {
            return true;
        }
        for (ManagedTypeDescriptor<J> superType = managedType.getSuperType(); superType != null; superType = superType.getSuperType()) {
            if (!superType.equals(managedType)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean appliesTo(Class<? super J> javaType) {
        return this.appliesTo((ManagedTypeDescriptor<? super J>)this.sessionFactory().getMetamodel().managedType(javaType));
    }
}

