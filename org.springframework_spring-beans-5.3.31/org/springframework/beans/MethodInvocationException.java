/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.beans.PropertyChangeEvent;
import org.springframework.beans.PropertyAccessException;

public class MethodInvocationException
extends PropertyAccessException {
    public static final String ERROR_CODE = "methodInvocation";

    public MethodInvocationException(PropertyChangeEvent propertyChangeEvent, Throwable cause) {
        super(propertyChangeEvent, "Property '" + propertyChangeEvent.getPropertyName() + "' threw exception", cause);
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}

