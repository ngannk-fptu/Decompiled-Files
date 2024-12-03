/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.jpa.internal;

import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.AfterCompletionAction;
import org.jboss.logging.Logger;

public class AfterCompletionActionLegacyJpaImpl
implements AfterCompletionAction {
    private static final Logger log = Logger.getLogger(AfterCompletionActionLegacyJpaImpl.class);
    public static final AfterCompletionActionLegacyJpaImpl INSTANCE = new AfterCompletionActionLegacyJpaImpl();

    @Override
    public void doAction(boolean successful, SessionImplementor session) {
        if (session.isClosed()) {
            log.trace((Object)"Session was closed; nothing to do");
            return;
        }
        if (!successful && session.getTransactionCoordinator().getTransactionCoordinatorBuilder().isJta()) {
            session.clear();
        }
    }
}

