/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

public class TypedStringValue
implements BeanMetadataElement {
    @Nullable
    private String value;
    @Nullable
    private volatile Object targetType;
    @Nullable
    private Object source;
    @Nullable
    private String specifiedTypeName;
    private volatile boolean dynamic;

    public TypedStringValue(@Nullable String value) {
        this.setValue(value);
    }

    public TypedStringValue(@Nullable String value, Class<?> targetType) {
        this.setValue(value);
        this.setTargetType(targetType);
    }

    public TypedStringValue(@Nullable String value, String targetTypeName) {
        this.setValue(value);
        this.setTargetTypeName(targetTypeName);
    }

    public void setValue(@Nullable String value) {
        this.value = value;
    }

    @Nullable
    public String getValue() {
        return this.value;
    }

    public void setTargetType(Class<?> targetType) {
        Assert.notNull(targetType, (String)"'targetType' must not be null");
        this.targetType = targetType;
    }

    public Class<?> getTargetType() {
        Object targetTypeValue = this.targetType;
        if (!(targetTypeValue instanceof Class)) {
            throw new IllegalStateException("Typed String value does not carry a resolved target type");
        }
        return (Class)targetTypeValue;
    }

    public void setTargetTypeName(@Nullable String targetTypeName) {
        this.targetType = targetTypeName;
    }

    @Nullable
    public String getTargetTypeName() {
        Object targetTypeValue = this.targetType;
        if (targetTypeValue instanceof Class) {
            return ((Class)targetTypeValue).getName();
        }
        return (String)targetTypeValue;
    }

    public boolean hasTargetType() {
        return this.targetType instanceof Class;
    }

    @Nullable
    public Class<?> resolveTargetType(@Nullable ClassLoader classLoader) throws ClassNotFoundException {
        String typeName = this.getTargetTypeName();
        if (typeName == null) {
            return null;
        }
        Class resolvedClass = ClassUtils.forName((String)typeName, (ClassLoader)classLoader);
        this.targetType = resolvedClass;
        return resolvedClass;
    }

    public void setSource(@Nullable Object source) {
        this.source = source;
    }

    @Override
    @Nullable
    public Object getSource() {
        return this.source;
    }

    public void setSpecifiedTypeName(@Nullable String specifiedTypeName) {
        this.specifiedTypeName = specifiedTypeName;
    }

    @Nullable
    public String getSpecifiedTypeName() {
        return this.specifiedTypeName;
    }

    public void setDynamic() {
        this.dynamic = true;
    }

    public boolean isDynamic() {
        return this.dynamic;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TypedStringValue)) {
            return false;
        }
        TypedStringValue otherValue = (TypedStringValue)other;
        return ObjectUtils.nullSafeEquals((Object)this.value, (Object)otherValue.value) && ObjectUtils.nullSafeEquals((Object)this.targetType, (Object)otherValue.targetType);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode((Object)this.value) * 29 + ObjectUtils.nullSafeHashCode((Object)this.targetType);
    }

    public String toString() {
        return "TypedStringValue: value [" + this.value + "], target type [" + this.targetType + "]";
    }
}

