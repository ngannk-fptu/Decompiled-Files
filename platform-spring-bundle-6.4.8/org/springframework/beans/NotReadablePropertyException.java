/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import org.springframework.beans.InvalidPropertyException;

public class NotReadablePropertyException
extends InvalidPropertyException {
    public NotReadablePropertyException(Class<?> beanClass, String propertyName) {
        super(beanClass, propertyName, "Bean property '" + propertyName + "' is not readable or has an invalid getter method: Does the return type of the getter match the parameter type of the setter?");
    }

    public NotReadablePropertyException(Class<?> beanClass, String propertyName, String msg) {
        super(beanClass, propertyName, msg);
    }

    public NotReadablePropertyException(Class<?> beanClass, String propertyName, String msg, Throwable cause) {
        super(beanClass, propertyName, msg, cause);
    }
}

