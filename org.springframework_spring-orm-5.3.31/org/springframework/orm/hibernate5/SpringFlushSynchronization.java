/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.Session
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.support.TransactionSynchronization
 */
package org.springframework.orm.hibernate5;

import org.hibernate.Session;
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.transaction.support.TransactionSynchronization;

public class SpringFlushSynchronization
implements TransactionSynchronization {
    private final Session session;

    public SpringFlushSynchronization(Session session) {
        this.session = session;
    }

    public void flush() {
        SessionFactoryUtils.flush(this.session, false);
    }

    public boolean equals(@Nullable Object other) {
        return this == other || other instanceof SpringFlushSynchronization && this.session == ((SpringFlushSynchronization)other).session;
    }

    public int hashCode() {
        return this.session.hashCode();
    }
}

