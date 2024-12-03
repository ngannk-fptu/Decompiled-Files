/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.internal;

import org.hibernate.FlushMode;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.ManagedFlushChecker;

public class ManagedFlushCheckerLegacyJpaImpl
implements ManagedFlushChecker {
    public static final ManagedFlushCheckerLegacyJpaImpl INSTANCE = new ManagedFlushCheckerLegacyJpaImpl();

    @Override
    public boolean shouldDoManagedFlush(SessionImplementor session) {
        return !session.isClosed() && session.getHibernateFlushMode() == FlushMode.MANUAL;
    }
}

