/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  org.hibernate.FlushMode
 *  org.hibernate.Session
 *  org.hibernate.Transaction
 *  org.springframework.lang.Nullable
 */
package org.springframework.orm.hibernate5;

import javax.persistence.EntityManager;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerHolder;

public class SessionHolder
extends EntityManagerHolder {
    @Nullable
    private Transaction transaction;
    @Nullable
    private FlushMode previousFlushMode;

    public SessionHolder(Session session) {
        super((EntityManager)session);
    }

    public Session getSession() {
        return (Session)this.getEntityManager();
    }

    public void setTransaction(@Nullable Transaction transaction) {
        this.transaction = transaction;
        this.setTransactionActive(transaction != null);
    }

    @Nullable
    public Transaction getTransaction() {
        return this.transaction;
    }

    public void setPreviousFlushMode(@Nullable FlushMode previousFlushMode) {
        this.previousFlushMode = previousFlushMode;
    }

    @Nullable
    public FlushMode getPreviousFlushMode() {
        return this.previousFlushMode;
    }

    @Override
    public void clear() {
        super.clear();
        this.transaction = null;
        this.previousFlushMode = null;
    }
}

