/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanWrapperImpl
 *  org.springframework.beans.NotReadablePropertyException
 *  org.springframework.beans.NotWritablePropertyException
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.util;

import java.lang.reflect.Field;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

public class DirectFieldAccessFallbackBeanWrapper
extends BeanWrapperImpl {
    public DirectFieldAccessFallbackBeanWrapper(Object entity) {
        super(entity);
    }

    public DirectFieldAccessFallbackBeanWrapper(Class<?> type) {
        super(type);
    }

    @Nullable
    public Object getPropertyValue(String propertyName) {
        try {
            return super.getPropertyValue(propertyName);
        }
        catch (NotReadablePropertyException e) {
            Field field = ReflectionUtils.findField((Class)this.getWrappedClass(), (String)propertyName);
            if (field == null) {
                throw new NotReadablePropertyException(this.getWrappedClass(), propertyName, "Could not find field for property during fallback access!");
            }
            ReflectionUtils.makeAccessible((Field)field);
            return ReflectionUtils.getField((Field)field, (Object)this.getWrappedInstance());
        }
    }

    public void setPropertyValue(String propertyName, @Nullable Object value) {
        try {
            super.setPropertyValue(propertyName, value);
        }
        catch (NotWritablePropertyException e) {
            Field field = ReflectionUtils.findField((Class)this.getWrappedClass(), (String)propertyName);
            if (field == null) {
                throw new NotWritablePropertyException(this.getWrappedClass(), propertyName, "Could not find field for property during fallback access!", (Throwable)e);
            }
            ReflectionUtils.makeAccessible((Field)field);
            ReflectionUtils.setField((Field)field, (Object)this.getWrappedInstance(), (Object)value);
        }
    }
}

