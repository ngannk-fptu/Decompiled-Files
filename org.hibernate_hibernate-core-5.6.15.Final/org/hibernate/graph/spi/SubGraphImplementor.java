/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute
 */
package org.hibernate.graph.spi;

import javax.persistence.metamodel.Attribute;
import org.hibernate.graph.CannotBecomeEntityGraphException;
import org.hibernate.graph.CannotContainSubGraphException;
import org.hibernate.graph.SubGraph;
import org.hibernate.graph.spi.AttributeNodeImplementor;
import org.hibernate.graph.spi.GraphImplementor;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;

public interface SubGraphImplementor<J>
extends SubGraph<J>,
GraphImplementor<J> {
    @Override
    public SubGraphImplementor<J> makeCopy(boolean var1);

    @Override
    default public SubGraphImplementor<J> makeSubGraph(boolean mutable) {
        if (!mutable && !this.isMutable()) {
            return this;
        }
        return this.makeCopy(mutable);
    }

    @Override
    public RootGraphImplementor<J> makeRootGraph(String var1, boolean var2) throws CannotBecomeEntityGraphException;

    @Override
    public <AJ> SubGraphImplementor<AJ> addKeySubGraph(String var1);

    @Override
    public <AJ> AttributeNodeImplementor<AJ> addAttributeNode(Attribute<? extends J, AJ> var1);

    @Override
    default public <AJ> SubGraphImplementor<? extends AJ> addKeySubGraph(PersistentAttributeDescriptor<? extends J, AJ> attribute, Class<? extends AJ> subType) throws CannotContainSubGraphException {
        return null;
    }
}

