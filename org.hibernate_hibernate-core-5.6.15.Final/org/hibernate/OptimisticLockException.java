/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.dialect.lock.OptimisticEntityLockException;

@Deprecated
public class OptimisticLockException
extends OptimisticEntityLockException {
    public OptimisticLockException(Object entity, String message) {
        super(entity, message);
    }
}

