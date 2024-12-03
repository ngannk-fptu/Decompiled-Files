/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.stat.internal;

import java.util.Collections;
import java.util.Set;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.stat.SessionStatistics;

public class SessionStatisticsImpl
implements SessionStatistics {
    private final SessionImplementor session;

    public SessionStatisticsImpl(SessionImplementor session) {
        this.session = session;
    }

    @Override
    public int getEntityCount() {
        return this.session.getPersistenceContextInternal().getNumberOfManagedEntities();
    }

    @Override
    public int getCollectionCount() {
        return this.session.getPersistenceContextInternal().getCollectionEntriesSize();
    }

    @Override
    public Set getEntityKeys() {
        return Collections.unmodifiableSet(this.session.getPersistenceContextInternal().getEntitiesByKey().keySet());
    }

    @Override
    public Set getCollectionKeys() {
        return Collections.unmodifiableSet(this.session.getPersistenceContextInternal().getCollectionsByKey().keySet());
    }

    public String toString() {
        return "SessionStatistics[" + "entity count=" + this.getEntityCount() + ",collection count=" + this.getCollectionCount() + ']';
    }
}

