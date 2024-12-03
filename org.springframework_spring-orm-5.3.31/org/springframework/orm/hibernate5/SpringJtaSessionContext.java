/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.FlushMode
 *  org.hibernate.Session
 *  org.hibernate.context.internal.JTASessionContext
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package org.springframework.orm.hibernate5;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.context.internal.JTASessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class SpringJtaSessionContext
extends JTASessionContext {
    public SpringJtaSessionContext(SessionFactoryImplementor factory) {
        super(factory);
    }

    protected Session buildOrObtainSession() {
        Session session = super.buildOrObtainSession();
        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            session.setHibernateFlushMode(FlushMode.MANUAL);
        }
        return session;
    }
}

