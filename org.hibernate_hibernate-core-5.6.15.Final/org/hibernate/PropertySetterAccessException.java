/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.PropertyAccessException;

public class PropertySetterAccessException
extends PropertyAccessException {
    public PropertySetterAccessException(Throwable cause, Class persistentClass, String propertyName, Class expectedType, Object target, Object value) {
        super(cause, String.format("IllegalArgumentException occurred while calling setter for property [%s.%s (expected type = %s)]; target = [%s], property value = [%s]", persistentClass.getName(), propertyName, expectedType.getName(), target, value), true, persistentClass, propertyName);
    }

    public String toString() {
        return super.originalMessage();
    }
}

