/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.spi;

import org.hibernate.engine.spi.AbstractDelegatingSessionBuilder;
import org.hibernate.engine.spi.SessionBuilderImplementor;
import org.hibernate.engine.spi.SessionOwner;

public abstract class AbstractDelegatingSessionBuilderImplementor<T extends SessionBuilderImplementor>
extends AbstractDelegatingSessionBuilder<T>
implements SessionBuilderImplementor<T> {
    public AbstractDelegatingSessionBuilderImplementor(SessionBuilderImplementor delegate) {
        super(delegate);
    }

    @Override
    protected SessionBuilderImplementor delegate() {
        return (SessionBuilderImplementor)super.delegate();
    }

    @Override
    public T owner(SessionOwner sessionOwner) {
        this.delegate().owner(sessionOwner);
        return (T)this;
    }
}

