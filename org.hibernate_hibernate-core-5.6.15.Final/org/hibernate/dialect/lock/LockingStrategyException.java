/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.dialect.lock;

import org.hibernate.HibernateException;

public abstract class LockingStrategyException
extends HibernateException {
    private final Object entity;

    public LockingStrategyException(Object entity, String message) {
        super(message);
        this.entity = entity;
    }

    public LockingStrategyException(Object entity, String message, Throwable cause) {
        super(message, cause);
        this.entity = entity;
    }

    public Object getEntity() {
        return this.entity;
    }
}

