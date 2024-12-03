/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.beans;

import java.beans.PropertyChangeEvent;
import org.springframework.beans.PropertyAccessException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class TypeMismatchException
extends PropertyAccessException {
    public static final String ERROR_CODE = "typeMismatch";
    @Nullable
    private String propertyName;
    @Nullable
    private final transient Object value;
    @Nullable
    private final Class<?> requiredType;

    public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, Class<?> requiredType) {
        this(propertyChangeEvent, requiredType, null);
    }

    public TypeMismatchException(PropertyChangeEvent propertyChangeEvent, @Nullable Class<?> requiredType, @Nullable Throwable cause) {
        super(propertyChangeEvent, "Failed to convert property value of type '" + ClassUtils.getDescriptiveType((Object)propertyChangeEvent.getNewValue()) + "'" + (requiredType != null ? " to required type '" + ClassUtils.getQualifiedName(requiredType) + "'" : "") + (propertyChangeEvent.getPropertyName() != null ? " for property '" + propertyChangeEvent.getPropertyName() + "'" : ""), cause);
        this.propertyName = propertyChangeEvent.getPropertyName();
        this.value = propertyChangeEvent.getNewValue();
        this.requiredType = requiredType;
    }

    public TypeMismatchException(@Nullable Object value, @Nullable Class<?> requiredType) {
        this(value, requiredType, null);
    }

    public TypeMismatchException(@Nullable Object value, @Nullable Class<?> requiredType, @Nullable Throwable cause) {
        super("Failed to convert value of type '" + ClassUtils.getDescriptiveType((Object)value) + "'" + (requiredType != null ? " to required type '" + ClassUtils.getQualifiedName(requiredType) + "'" : ""), cause);
        this.value = value;
        this.requiredType = requiredType;
    }

    public void initPropertyName(String propertyName) {
        Assert.state((this.propertyName == null ? 1 : 0) != 0, (String)"Property name already initialized");
        this.propertyName = propertyName;
    }

    @Override
    @Nullable
    public String getPropertyName() {
        return this.propertyName;
    }

    @Override
    @Nullable
    public Object getValue() {
        return this.value;
    }

    @Nullable
    public Class<?> getRequiredType() {
        return this.requiredType;
    }

    @Override
    public String getErrorCode() {
        return ERROR_CODE;
    }
}

