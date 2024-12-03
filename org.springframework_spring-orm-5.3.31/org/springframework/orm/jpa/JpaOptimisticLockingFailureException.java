/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.OptimisticLockException
 */
package org.springframework.orm.jpa;

import javax.persistence.OptimisticLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

public class JpaOptimisticLockingFailureException
extends ObjectOptimisticLockingFailureException {
    public JpaOptimisticLockingFailureException(OptimisticLockException ex) {
        super(ex.getMessage(), (Throwable)ex);
    }
}

