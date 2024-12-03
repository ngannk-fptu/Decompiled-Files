/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import org.hibernate.boot.spi.AbstractDelegatingSessionFactoryBuilder;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;
import org.hibernate.boot.spi.SessionFactoryOptions;

public abstract class AbstractDelegatingSessionFactoryBuilderImplementor<T extends SessionFactoryBuilderImplementor>
extends AbstractDelegatingSessionFactoryBuilder<T>
implements SessionFactoryBuilderImplementor {
    public AbstractDelegatingSessionFactoryBuilderImplementor(SessionFactoryBuilderImplementor delegate) {
        super(delegate);
    }

    @Override
    protected SessionFactoryBuilderImplementor delegate() {
        return (SessionFactoryBuilderImplementor)super.delegate();
    }

    @Override
    public void disableJtaTransactionAccess() {
        this.delegate().disableJtaTransactionAccess();
    }

    @Override
    public void enableJdbcStyleParamsZeroBased() {
        this.delegate().enableJdbcStyleParamsZeroBased();
    }

    @Override
    public SessionFactoryOptions buildSessionFactoryOptions() {
        return this.delegate().buildSessionFactoryOptions();
    }
}

