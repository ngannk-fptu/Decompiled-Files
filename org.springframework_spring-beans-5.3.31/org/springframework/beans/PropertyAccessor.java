/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans;

import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;

public interface PropertyAccessor {
    public static final String NESTED_PROPERTY_SEPARATOR = ".";
    public static final char NESTED_PROPERTY_SEPARATOR_CHAR = '.';
    public static final String PROPERTY_KEY_PREFIX = "[";
    public static final char PROPERTY_KEY_PREFIX_CHAR = '[';
    public static final String PROPERTY_KEY_SUFFIX = "]";
    public static final char PROPERTY_KEY_SUFFIX_CHAR = ']';

    public boolean isReadableProperty(String var1);

    public boolean isWritableProperty(String var1);

    @Nullable
    public Class<?> getPropertyType(String var1) throws BeansException;

    @Nullable
    public TypeDescriptor getPropertyTypeDescriptor(String var1) throws BeansException;

    @Nullable
    public Object getPropertyValue(String var1) throws BeansException;

    public void setPropertyValue(String var1, @Nullable Object var2) throws BeansException;

    public void setPropertyValue(PropertyValue var1) throws BeansException;

    public void setPropertyValues(Map<?, ?> var1) throws BeansException;

    public void setPropertyValues(PropertyValues var1) throws BeansException;

    public void setPropertyValues(PropertyValues var1, boolean var2) throws BeansException;

    public void setPropertyValues(PropertyValues var1, boolean var2, boolean var3) throws BeansException;
}

