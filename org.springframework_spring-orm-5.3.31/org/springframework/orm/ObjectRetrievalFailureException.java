/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.DataRetrievalFailureException
 *  org.springframework.lang.Nullable
 */
package org.springframework.orm;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.lang.Nullable;

public class ObjectRetrievalFailureException
extends DataRetrievalFailureException {
    @Nullable
    private final Object persistentClass;
    @Nullable
    private final Object identifier;

    public ObjectRetrievalFailureException(String msg, Throwable cause) {
        super(msg, cause);
        this.persistentClass = null;
        this.identifier = null;
    }

    public ObjectRetrievalFailureException(Class<?> persistentClass, Object identifier) {
        this(persistentClass, identifier, "Object of class [" + persistentClass.getName() + "] with identifier [" + identifier + "]: not found", null);
    }

    public ObjectRetrievalFailureException(Class<?> persistentClass, Object identifier, String msg, @Nullable Throwable cause) {
        super(msg, cause);
        this.persistentClass = persistentClass;
        this.identifier = identifier;
    }

    public ObjectRetrievalFailureException(String persistentClassName, Object identifier) {
        this(persistentClassName, identifier, "Object of class [" + persistentClassName + "] with identifier [" + identifier + "]: not found", null);
    }

    public ObjectRetrievalFailureException(String persistentClassName, Object identifier, String msg, @Nullable Throwable cause) {
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

