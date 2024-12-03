/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.event.internal.AbstractFlushingEventListener;
import org.hibernate.event.spi.DirtyCheckEvent;
import org.hibernate.event.spi.DirtyCheckEventListener;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public class DefaultDirtyCheckEventListener
extends AbstractFlushingEventListener
implements DirtyCheckEventListener {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(DefaultDirtyCheckEventListener.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onDirtyCheck(DirtyCheckEvent event) throws HibernateException {
        ActionQueue actionQueue = event.getSession().getActionQueue();
        int oldSize = actionQueue.numberOfCollectionRemovals();
        try {
            this.flushEverythingToExecutions(event);
            boolean wasNeeded = actionQueue.hasAnyQueuedActions();
            if (wasNeeded) {
                LOG.debug("Session dirty");
            } else {
                LOG.debug("Session not dirty");
            }
            event.setDirty(wasNeeded);
        }
        finally {
            actionQueue.clearFromFlushNeededCheck(oldSize);
        }
    }
}

