/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.spi;

import java.io.Serializable;
import org.hibernate.AssertionFailure;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.PostLoadEvent;

public class LoadEvent
extends AbstractEvent {
    public static final LockMode DEFAULT_LOCK_MODE = LockMode.NONE;
    public static final LockOptions DEFAULT_LOCK_OPTIONS = new LockOptions(){

        @Override
        public LockOptions setLockMode(LockMode lockMode) {
            throw new AssertionFailure("Should not be invoked: DEFAULT_LOCK_OPTIONS needs to be treated as immutable.");
        }

        @Override
        public LockOptions setAliasSpecificLockMode(String alias, LockMode lockMode) {
            throw new AssertionFailure("Should not be invoked: DEFAULT_LOCK_OPTIONS needs to be treated as immutable.");
        }

        @Override
        public LockOptions setTimeOut(int timeout) {
            throw new AssertionFailure("Should not be invoked: DEFAULT_LOCK_OPTIONS needs to be treated as immutable.");
        }

        @Override
        public LockOptions setScope(boolean scope) {
            throw new AssertionFailure("Should not be invoked: DEFAULT_LOCK_OPTIONS needs to be treated as immutable.");
        }
    };
    private Serializable entityId;
    private String entityClassName;
    private Object instanceToLoad;
    private LockOptions lockOptions;
    private boolean isAssociationFetch;
    private Object result;
    private PostLoadEvent postLoadEvent;
    private Boolean readOnly;

    public LoadEvent(Serializable entityId, Object instanceToLoad, EventSource source, Boolean readOnly) {
        this(entityId, null, instanceToLoad, DEFAULT_LOCK_OPTIONS, false, source, readOnly);
    }

    public LoadEvent(Serializable entityId, String entityClassName, LockMode lockMode, EventSource source, Boolean readOnly) {
        this(entityId, entityClassName, null, lockMode, false, source, readOnly);
    }

    public LoadEvent(Serializable entityId, String entityClassName, LockOptions lockOptions, EventSource source, Boolean readOnly) {
        this(entityId, entityClassName, null, lockOptions, false, source, readOnly);
    }

    public LoadEvent(Serializable entityId, String entityClassName, boolean isAssociationFetch, EventSource source, Boolean readOnly) {
        this(entityId, entityClassName, null, DEFAULT_LOCK_OPTIONS, isAssociationFetch, source, readOnly);
    }

    public boolean isAssociationFetch() {
        return this.isAssociationFetch;
    }

    private LoadEvent(Serializable entityId, String entityClassName, Object instanceToLoad, LockMode lockMode, boolean isAssociationFetch, EventSource source, Boolean readOnly) {
        this(entityId, entityClassName, instanceToLoad, lockMode == DEFAULT_LOCK_MODE ? DEFAULT_LOCK_OPTIONS : new LockOptions().setLockMode(lockMode), isAssociationFetch, source, readOnly);
    }

    private LoadEvent(Serializable entityId, String entityClassName, Object instanceToLoad, LockOptions lockOptions, boolean isAssociationFetch, EventSource source, Boolean readOnly) {
        super(source);
        if (entityId == null) {
            throw new IllegalArgumentException("id to load is required for loading");
        }
        if (lockOptions.getLockMode() == LockMode.WRITE) {
            throw new IllegalArgumentException("Invalid lock mode for loading");
        }
        if (lockOptions.getLockMode() == null) {
            lockOptions.setLockMode(DEFAULT_LOCK_MODE);
        }
        this.entityId = entityId;
        this.entityClassName = entityClassName;
        this.instanceToLoad = instanceToLoad;
        this.lockOptions = lockOptions;
        this.isAssociationFetch = isAssociationFetch;
        this.postLoadEvent = new PostLoadEvent(source);
        this.readOnly = readOnly;
    }

    public Serializable getEntityId() {
        return this.entityId;
    }

    public void setEntityId(Serializable entityId) {
        this.entityId = entityId;
    }

    public String getEntityClassName() {
        return this.entityClassName;
    }

    public void setEntityClassName(String entityClassName) {
        this.entityClassName = entityClassName;
    }

    public Object getInstanceToLoad() {
        return this.instanceToLoad;
    }

    public void setInstanceToLoad(Object instanceToLoad) {
        this.instanceToLoad = instanceToLoad;
    }

    public LockOptions getLockOptions() {
        return this.lockOptions;
    }

    public LockMode getLockMode() {
        return this.lockOptions.getLockMode();
    }

    public void setLockMode(LockMode lockMode) {
        if (lockMode != this.lockOptions.getLockMode()) {
            this.writingOnLockOptions();
            this.lockOptions.setLockMode(lockMode);
        }
    }

    private void writingOnLockOptions() {
        if (this.lockOptions == DEFAULT_LOCK_OPTIONS) {
            this.lockOptions = new LockOptions();
        }
    }

    public void setLockTimeout(int timeout) {
        if (timeout != this.lockOptions.getTimeOut()) {
            this.writingOnLockOptions();
            this.lockOptions.setTimeOut(timeout);
        }
    }

    public int getLockTimeout() {
        return this.lockOptions.getTimeOut();
    }

    public void setLockScope(boolean cascade) {
        if (this.lockOptions.getScope() != cascade) {
            this.writingOnLockOptions();
            this.lockOptions.setScope(cascade);
        }
    }

    public boolean getLockScope() {
        return this.lockOptions.getScope();
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public PostLoadEvent getPostLoadEvent() {
        return this.postLoadEvent;
    }

    public void setPostLoadEvent(PostLoadEvent postLoadEvent) {
        this.postLoadEvent = postLoadEvent;
    }

    public Boolean getReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }
}

