/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.beans.PropertyChangeEvent;
import org.springframework.beans.TypeMismatchException;
import org.springframework.lang.Nullable;

public class ConversionNotSupportedException
extends TypeMismatchException {
    public ConversionNotSupportedException(PropertyChangeEvent propertyChangeEvent, @Nullable Class<?> requiredType, @Nullable Throwable cause) {
        super(propertyChangeEvent, requiredType, cause);
    }

    public ConversionNotSupportedException(@Nullable Object value, @Nullable Class<?> requiredType, @Nullable Throwable cause) {
        super(value, requiredType, cause);
    }
}

