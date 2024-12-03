/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyException
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.soy.impl.data;

import com.atlassian.soy.impl.data.JavaBeanAccessorResolver;
import com.atlassian.soy.renderer.SoyException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class IntrospectorJavaBeanAccessorResolver
implements JavaBeanAccessorResolver {
    private static final Set<String> BANNED_PROPERTY_NAMES = ImmutableSet.of((Object)"class", (Object)"classLoader");

    @Override
    public void clearCaches() {
        Introspector.flushCaches();
    }

    @Override
    public Map<String, Method> resolveAccessors(Class<?> targetClass) {
        Preconditions.checkNotNull(targetClass, (Object)"targetClass");
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(targetClass);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
            if (descriptors == null) {
                return Collections.emptyMap();
            }
            ImmutableMap.Builder builder = ImmutableMap.builder();
            for (PropertyDescriptor descriptor : descriptors) {
                String propertyName = descriptor.getName();
                Method readMethod = this.getPublicMethod(descriptor.getReadMethod());
                if (readMethod == null || BANNED_PROPERTY_NAMES.contains(propertyName)) continue;
                builder.put((Object)propertyName, (Object)readMethod);
            }
            return builder.build();
        }
        catch (IntrospectionException e) {
            throw new SoyException("Failed to introspect class " + targetClass.getName(), (Throwable)e);
        }
    }

    private Method getPublicMethod(Method method) {
        if (method == null) {
            return null;
        }
        if (!Modifier.isPublic(method.getModifiers())) {
            return null;
        }
        Class<?> declaringClass = method.getDeclaringClass();
        if (Modifier.isPublic(declaringClass.getModifiers())) {
            return method;
        }
        return this.findPublicMethodOnInterfaces(declaringClass, method.getName(), method.getParameterTypes());
    }

    private Method findPublicMethodOnInterfaces(Class<?> targetClass, String name, Class<?> ... parameterTypes) {
        while (targetClass != null) {
            for (Class<?> interfaceClass : targetClass.getInterfaces()) {
                if (!Modifier.isPublic(interfaceClass.getModifiers())) continue;
                try {
                    return interfaceClass.getDeclaredMethod(name, parameterTypes);
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    Method method = this.findPublicMethodOnInterfaces(interfaceClass, name, parameterTypes);
                    if (method == null) continue;
                    return null;
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        return null;
    }
}

