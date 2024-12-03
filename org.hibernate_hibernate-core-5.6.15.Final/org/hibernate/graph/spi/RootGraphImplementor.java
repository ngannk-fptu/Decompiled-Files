/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.graph.spi;

import org.hibernate.graph.RootGraph;
import org.hibernate.graph.spi.GraphImplementor;
import org.hibernate.graph.spi.SubGraphImplementor;
import org.hibernate.metamodel.model.domain.spi.EntityTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;

public interface RootGraphImplementor<J>
extends RootGraph<J>,
GraphImplementor<J> {
    @Override
    public boolean appliesTo(EntityTypeDescriptor<? super J> var1);

    @Override
    default public boolean appliesTo(ManagedTypeDescriptor<? super J> managedType) {
        if (!1.$assertionsDisabled && !(managedType instanceof EntityTypeDescriptor)) {
            throw new AssertionError();
        }
        return this.appliesTo((EntityTypeDescriptor)managedType);
    }

    @Override
    public RootGraphImplementor<J> makeRootGraph(String var1, boolean var2);

    @Override
    public SubGraphImplementor<J> makeSubGraph(boolean var1);

    default public RootGraphImplementor<J> makeImmutableCopy(String name) {
        return this.makeRootGraph(name, false);
    }

    static {
        if (1.$assertionsDisabled) {
            // empty if block
        }
    }
}

