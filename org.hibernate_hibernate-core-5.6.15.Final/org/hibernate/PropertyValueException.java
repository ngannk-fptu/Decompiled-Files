/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.internal.util.StringHelper;

public class PropertyValueException
extends HibernateException {
    private final String entityName;
    private final String propertyName;

    public PropertyValueException(String message, String entityName, String propertyName) {
        super(message);
        this.entityName = entityName;
        this.propertyName = propertyName;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public String getMessage() {
        return super.getMessage() + " : " + StringHelper.qualify(this.entityName, this.propertyName);
    }
}

