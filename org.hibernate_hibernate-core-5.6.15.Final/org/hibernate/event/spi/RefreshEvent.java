/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;

public class RefreshEvent
extends AbstractEvent {
    private Object object;
    private String entityName;
    private LockOptions lockOptions = new LockOptions().setLockMode(LockMode.READ);

    public RefreshEvent(Object object, EventSource source) {
        super(source);
        if (object == null) {
            throw new IllegalArgumentException("Attempt to generate refresh event with null object");
        }
        this.object = object;
    }

    public RefreshEvent(String entityName, Object object, EventSource source) {
        this(object, source);
        this.entityName = entityName;
    }

    public RefreshEvent(Object object, LockMode lockMode, EventSource source) {
        this(object, source);
        if (lockMode == null) {
            throw new IllegalArgumentException("Attempt to generate refresh event with null lock mode");
        }
        this.lockOptions.setLockMode(lockMode);
    }

    public RefreshEvent(Object object, LockOptions lockOptions, EventSource source) {
        this(object, source);
        if (lockOptions == null) {
            throw new IllegalArgumentException("Attempt to generate refresh event with null lock request");
        }
        this.lockOptions = lockOptions;
    }

    public RefreshEvent(String entityName, Object object, LockOptions lockOptions, EventSource source) {
        this(object, lockOptions, source);
        this.entityName = entityName;
    }

    public Object getObject() {
        return this.object;
    }

    public LockOptions getLockOptions() {
        return this.lockOptions;
    }

    public LockMode getLockMode() {
        return this.lockOptions.getLockMode();
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public int getLockTimeout() {
        return this.lockOptions.getTimeOut();
    }

    public boolean getLockScope() {
        return this.lockOptions.getScope();
    }
}

