/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.beans.PropertyDescriptor;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.InvalidPropertyException;

public interface BeanWrapper
extends ConfigurablePropertyAccessor {
    public void setAutoGrowCollectionLimit(int var1);

    public int getAutoGrowCollectionLimit();

    public Object getWrappedInstance();

    public Class<?> getWrappedClass();

    public PropertyDescriptor[] getPropertyDescriptors();

    public PropertyDescriptor getPropertyDescriptor(String var1) throws InvalidPropertyException;
}

