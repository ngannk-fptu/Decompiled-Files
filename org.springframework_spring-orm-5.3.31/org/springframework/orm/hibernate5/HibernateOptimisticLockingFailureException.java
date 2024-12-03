/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.StaleObjectStateException
 *  org.hibernate.StaleStateException
 *  org.hibernate.dialect.lock.OptimisticEntityLockException
 */
package org.springframework.orm.hibernate5;

import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

public class HibernateOptimisticLockingFailureException
extends ObjectOptimisticLockingFailureException {
    public HibernateOptimisticLockingFailureException(StaleObjectStateException ex) {
        super(ex.getEntityName(), (Object)ex.getIdentifier(), (Throwable)ex);
    }

    public HibernateOptimisticLockingFailureException(StaleStateException ex) {
        super(ex.getMessage(), (Throwable)ex);
    }

    public HibernateOptimisticLockingFailureException(OptimisticEntityLockException ex) {
        super(ex.getMessage(), (Throwable)ex);
    }
}

