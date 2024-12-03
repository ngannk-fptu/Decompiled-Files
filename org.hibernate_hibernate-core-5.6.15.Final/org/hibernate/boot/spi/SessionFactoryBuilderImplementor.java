/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.spi.SessionFactoryOptions;

public interface SessionFactoryBuilderImplementor
extends SessionFactoryBuilder {
    public void disableJtaTransactionAccess();

    default public void disableRefreshDetachedEntity() {
    }

    public void enableJdbcStyleParamsZeroBased();

    public SessionFactoryOptions buildSessionFactoryOptions();
}

