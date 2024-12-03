/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.reflection;

import com.opensymphony.xwork2.util.reflection.ReflectionException;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

public interface ReflectionProvider {
    public Method getGetMethod(Class var1, String var2) throws IntrospectionException, ReflectionException;

    public Method getSetMethod(Class var1, String var2) throws IntrospectionException, ReflectionException;

    public Field getField(Class var1, String var2);

    public void setProperties(Map<String, ?> var1, Object var2, Map<String, Object> var3);

    public void setProperties(Map<String, ?> var1, Object var2, Map<String, Object> var3, boolean var4) throws ReflectionException;

    public void setProperties(Map<String, ?> var1, Object var2);

    public PropertyDescriptor getPropertyDescriptor(Class var1, String var2) throws IntrospectionException, ReflectionException;

    public void copy(Object var1, Object var2, Map<String, Object> var3, Collection<String> var4, Collection<String> var5);

    public void copy(Object var1, Object var2, Map<String, Object> var3, Collection<String> var4, Collection<String> var5, Class<?> var6);

    public Object getRealTarget(String var1, Map<String, Object> var2, Object var3) throws ReflectionException;

    public void setProperty(String var1, Object var2, Object var3, Map<String, Object> var4, boolean var5);

    public void setProperty(String var1, Object var2, Object var3, Map<String, Object> var4);

    public Map<String, Object> getBeanMap(Object var1) throws IntrospectionException, ReflectionException;

    public Object getValue(String var1, Map<String, Object> var2, Object var3) throws ReflectionException;

    public void setValue(String var1, Map<String, Object> var2, Object var3, Object var4) throws ReflectionException;

    public PropertyDescriptor[] getPropertyDescriptors(Object var1) throws IntrospectionException;
}

