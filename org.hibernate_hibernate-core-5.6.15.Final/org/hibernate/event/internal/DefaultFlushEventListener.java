/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.event.internal.AbstractFlushingEventListener;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.FlushEvent;
import org.hibernate.event.spi.FlushEventListener;
import org.hibernate.stat.spi.StatisticsImplementor;

public class DefaultFlushEventListener
extends AbstractFlushingEventListener
implements FlushEventListener {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onFlush(FlushEvent event) throws HibernateException {
        EventSource source = event.getSession();
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        if (persistenceContext.getNumberOfManagedEntities() > 0 || persistenceContext.getCollectionEntriesSize() > 0) {
            try {
                source.getEventListenerManager().flushStart();
                this.flushEverythingToExecutions(event);
                this.performExecutions(source);
                this.postFlush(source);
            }
            finally {
                source.getEventListenerManager().flushEnd(event.getNumberOfEntitiesProcessed(), event.getNumberOfCollectionsProcessed());
            }
            this.postPostFlush(source);
            StatisticsImplementor statistics = source.getFactory().getStatistics();
            if (statistics.isStatisticsEnabled()) {
                statistics.flush();
            }
        }
    }
}

