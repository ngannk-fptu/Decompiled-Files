/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.event.internal;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.event.internal.AbstractFlushingEventListener;
import org.hibernate.event.spi.AutoFlushEvent;
import org.hibernate.event.spi.AutoFlushEventListener;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.jboss.logging.Logger;

public class DefaultAutoFlushEventListener
extends AbstractFlushingEventListener
implements AutoFlushEventListener {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)DefaultAutoFlushEventListener.class.getName());

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onAutoFlush(AutoFlushEvent event) throws HibernateException {
        EventSource source = event.getSession();
        SessionEventListenerManager eventListenerManager = source.getEventListenerManager();
        try {
            eventListenerManager.partialFlushStart();
            if (this.flushMightBeNeeded(source)) {
                ActionQueue actionQueue = source.getActionQueue();
                int oldSize = actionQueue.numberOfCollectionRemovals();
                this.flushEverythingToExecutions(event);
                if (this.flushIsReallyNeeded(event, source)) {
                    LOG.trace("Need to execute flush");
                    event.setFlushRequired(true);
                    this.performExecutions(source);
                    this.postFlush(source);
                    this.postPostFlush(source);
                    StatisticsImplementor statistics = source.getFactory().getStatistics();
                    if (statistics.isStatisticsEnabled()) {
                        statistics.flush();
                    }
                } else {
                    LOG.trace("Don't need to execute flush");
                    event.setFlushRequired(false);
                    actionQueue.clearFromFlushNeededCheck(oldSize);
                }
            }
        }
        finally {
            eventListenerManager.partialFlushEnd(event.getNumberOfEntitiesProcessed(), event.getNumberOfEntitiesProcessed());
        }
    }

    private boolean flushIsReallyNeeded(AutoFlushEvent event, EventSource source) {
        return source.getHibernateFlushMode() == FlushMode.ALWAYS || source.getActionQueue().areTablesToBeUpdated(event.getQuerySpaces());
    }

    private boolean flushMightBeNeeded(EventSource source) {
        PersistenceContext persistenceContext = source.getPersistenceContextInternal();
        return !source.getHibernateFlushMode().lessThan(FlushMode.AUTO) && source.getDontFlushFromFind() == 0 && (persistenceContext.getNumberOfManagedEntities() > 0 || persistenceContext.getCollectionEntriesSize() > 0);
    }
}

