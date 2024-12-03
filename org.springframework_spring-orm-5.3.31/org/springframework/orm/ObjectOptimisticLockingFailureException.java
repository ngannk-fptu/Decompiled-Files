/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.OptimisticLockingFailureException
 *  org.springframework.lang.Nullable
 */
package org.springframework.orm;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.lang.Nullable;

public class ObjectOptimisticLockingFailureException
extends OptimisticLockingFailureException {
    @Nullable
    private final Object persistentClass;
    @Nullable
    private final Object identifier;

    public ObjectOptimisticLockingFailureException(String msg, Throwable cause) {
        super(msg, cause);
        this.persistentClass = null;
        this.identifier = null;
    }

    public ObjectOptimisticLockingFailureException(Class<?> persistentClass, Object identifier) {
        this(persistentClass, identifier, null);
    }

    public ObjectOptimisticLockingFailureException(Class<?> persistentClass, Object identifier, @Nullable Throwable cause) {
        this(persistentClass, identifier, "Object of class [" + persistentClass.getName() + "] with identifier [" + identifier + "]: optimistic locking failed", cause);
    }

    public ObjectOptimisticLockingFailureException(Class<?> persistentClass, Object identifier, String msg, @Nullable Throwable cause) {
        super(msg, cause);
        this.persistentClass = persistentClass;
        this.identifier = identifier;
    }

    public ObjectOptimisticLockingFailureException(String persistentClassName, Object identifier) {
        this(persistentClassName, identifier, null);
    }

    public ObjectOptimisticLockingFailureException(String persistentClassName, Object identifier, @Nullable Throwable cause) {
        this(persistentClassName, identifier, "Object of class [" + persistentClassName + "] with identifier [" + identifier + "]: optimistic locking failed", cause);
    }

    public ObjectOptimisticLockingFailureException(String persistentClassName, Object identifier, String msg, @Nullable Throwable cause) {
        super(msg, cause);
        this.persistentClass = persistentClassName;
        this.identifier = identifier;
    }

    @Nullable
    public Class<?> getPersistentClass() {
        return this.persistentClass instanceof Class ? (Class)this.persistentClass : null;
    }

    @Nullable
    public String getPersistentClassName() {
        if (this.persistentClass instanceof Class) {
            return ((Class)this.persistentClass).getName();
        }
        return this.persistentClass != null ? this.persistentClass.toString() : null;
    }

    @Nullable
    public Object getIdentifier() {
        return this.identifier;
    }
}

