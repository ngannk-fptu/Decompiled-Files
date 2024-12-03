/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans;

import java.beans.PropertyChangeEvent;
import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

public abstract class PropertyAccessException
extends BeansException {
    @Nullable
    private final PropertyChangeEvent propertyChangeEvent;

    public PropertyAccessException(PropertyChangeEvent propertyChangeEvent, String msg, @Nullable Throwable cause) {
        super(msg, cause);
        this.propertyChangeEvent = propertyChangeEvent;
    }

    public PropertyAccessException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
        this.propertyChangeEvent = null;
    }

    @Nullable
    public PropertyChangeEvent getPropertyChangeEvent() {
        return this.propertyChangeEvent;
    }

    @Nullable
    public String getPropertyName() {
        return this.propertyChangeEvent != null ? this.propertyChangeEvent.getPropertyName() : null;
    }

    @Nullable
    public Object getValue() {
        return this.propertyChangeEvent != null ? this.propertyChangeEvent.getNewValue() : null;
    }

    public abstract String getErrorCode();
}

