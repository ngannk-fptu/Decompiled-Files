/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.internal.util.StringHelper;

public class PropertyAccessException
extends HibernateException {
    private final Class persistentClass;
    private final String propertyName;
    private final boolean wasSetter;

    public PropertyAccessException(Throwable cause, String message, boolean wasSetter, Class persistentClass, String propertyName) {
        super(message, cause);
        this.persistentClass = persistentClass;
        this.wasSetter = wasSetter;
        this.propertyName = propertyName;
    }

    public Class getPersistentClass() {
        return this.persistentClass;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    protected String originalMessage() {
        return super.getMessage();
    }

    public String getMessage() {
        return this.originalMessage() + (this.wasSetter ? " setter of " : " getter of ") + StringHelper.qualify(this.persistentClass.getName(), this.propertyName);
    }
}

