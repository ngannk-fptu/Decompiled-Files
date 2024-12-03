/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.graph.internal;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.spi.GraphNodeImplementor;

public abstract class AbstractGraphNode<J>
implements GraphNodeImplementor<J> {
    private final SessionFactoryImplementor sessionFactory;
    private final boolean mutable;

    public AbstractGraphNode(boolean mutable, SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.mutable = mutable;
    }

    protected SessionFactoryImplementor sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public boolean isMutable() {
        return this.mutable;
    }

    protected void verifyMutability() {
        if (!this.isMutable()) {
            throw new IllegalStateException("Cannot mutate immutable graph node");
        }
    }
}

