/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.HibernateException;

public class InstantiationException
extends HibernateException {
    private final Class clazz;

    public InstantiationException(String message, Class clazz, Throwable cause) {
        super(message, cause);
        this.clazz = clazz;
    }

    public InstantiationException(String message, Class clazz) {
        this(message, clazz, null);
    }

    public InstantiationException(String message, Class clazz, Exception cause) {
        super(message, cause);
        this.clazz = clazz;
    }

    @Deprecated
    public Class getPersistentClass() {
        return this.clazz;
    }

    public Class getUninstantiatableClass() {
        return this.clazz;
    }

    public String getMessage() {
        return super.getMessage() + " : " + this.clazz.getName();
    }
}

