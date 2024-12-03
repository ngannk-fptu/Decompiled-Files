/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.SpringProperties
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.io.support.SpringFactoriesLoader
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanInfoFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.GenericTypeAwarePropertyDescriptor;
import org.springframework.beans.PropertyDescriptorUtils;
import org.springframework.core.SpringProperties;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

public final class CachedIntrospectionResults {
    public static final String IGNORE_BEANINFO_PROPERTY_NAME = "spring.beaninfo.ignore";
    private static final boolean shouldIntrospectorIgnoreBeaninfoClasses = SpringProperties.getFlag((String)"spring.beaninfo.ignore");
    private static final List<BeanInfoFactory> beanInfoFactories = SpringFactoriesLoader.loadFactories(BeanInfoFactory.class, (ClassLoader)CachedIntrospectionResults.class.getClassLoader());
    private static final Log logger = LogFactory.getLog(CachedIntrospectionResults.class);
    static final Set<ClassLoader> acceptedClassLoaders = Collections.newSetFromMap(new ConcurrentHashMap(16));
    static final ConcurrentMap<Class<?>, CachedIntrospectionResults> strongClassCache = new ConcurrentHashMap(64);
    static final ConcurrentMap<Class<?>, CachedIntrospectionResults> softClassCache = new ConcurrentReferenceHashMap(64);
    private final BeanInfo beanInfo;
    private final Map<String, PropertyDescriptor> propertyDescriptors;
    private final ConcurrentMap<PropertyDescriptor, TypeDescriptor> typeDescriptorCache;

    public static void acceptClassLoader(@Nullable ClassLoader classLoader) {
        if (classLoader != null) {
            acceptedClassLoaders.add(classLoader);
        }
    }

    public static void clearClassLoader(@Nullable ClassLoader classLoader) {
        acceptedClassLoaders.removeIf(registeredLoader -> CachedIntrospectionResults.isUnderneathClassLoader(registeredLoader, classLoader));
        strongClassCache.keySet().removeIf(beanClass -> CachedIntrospectionResults.isUnderneathClassLoader(beanClass.getClassLoader(), classLoader));
        softClassCache.keySet().removeIf(beanClass -> CachedIntrospectionResults.isUnderneathClassLoader(beanClass.getClassLoader(), classLoader));
    }

    static CachedIntrospectionResults forClass(Class<?> beanClass) throws BeansException {
        ConcurrentMap<Class<?>, CachedIntrospectionResults> classCacheToUse;
        CachedIntrospectionResults results = (CachedIntrospectionResults)strongClassCache.get(beanClass);
        if (results != null) {
            return results;
        }
        results = (CachedIntrospectionResults)softClassCache.get(beanClass);
        if (results != null) {
            return results;
        }
        results = new CachedIntrospectionResults(beanClass);
        if (ClassUtils.isCacheSafe(beanClass, (ClassLoader)CachedIntrospectionResults.class.getClassLoader()) || CachedIntrospectionResults.isClassLoaderAccepted(beanClass.getClassLoader())) {
            classCacheToUse = strongClassCache;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Not strongly caching class [" + beanClass.getName() + "] because it is not cache-safe"));
            }
            classCacheToUse = softClassCache;
        }
        CachedIntrospectionResults existing = classCacheToUse.putIfAbsent(beanClass, results);
        return existing != null ? existing : results;
    }

    private static boolean isClassLoaderAccepted(ClassLoader classLoader) {
        for (ClassLoader acceptedLoader : acceptedClassLoaders) {
            if (!CachedIntrospectionResults.isUnderneathClassLoader(classLoader, acceptedLoader)) continue;
            return true;
        }
        return false;
    }

    private static boolean isUnderneathClassLoader(@Nullable ClassLoader candidate, @Nullable ClassLoader parent) {
        if (candidate == parent) {
            return true;
        }
        if (candidate == null) {
            return false;
        }
        ClassLoader classLoaderToCheck = candidate;
        while (classLoaderToCheck != null) {
            if ((classLoaderToCheck = classLoaderToCheck.getParent()) != parent) continue;
            return true;
        }
        return false;
    }

    private static BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
        for (BeanInfoFactory beanInfoFactory : beanInfoFactories) {
            BeanInfo beanInfo = beanInfoFactory.getBeanInfo(beanClass);
            if (beanInfo == null) continue;
            return beanInfo;
        }
        BeanInfo beanInfo = shouldIntrospectorIgnoreBeaninfoClasses ? Introspector.getBeanInfo(beanClass, 3) : Introspector.getBeanInfo(beanClass);
        Class<?> classToFlush = beanClass;
        do {
            Introspector.flushFromCaches(classToFlush);
        } while ((classToFlush = classToFlush.getSuperclass()) != null && classToFlush != Object.class);
        return beanInfo;
    }

    private CachedIntrospectionResults(Class<?> beanClass) throws BeansException {
        try {
            PropertyDescriptor[] pds;
            if (logger.isTraceEnabled()) {
                logger.trace((Object)("Getting BeanInfo for class [" + beanClass.getName() + "]"));
            }
            this.beanInfo = CachedIntrospectionResults.getBeanInfo(beanClass);
            if (logger.isTraceEnabled()) {
                logger.trace((Object)("Caching PropertyDescriptors for class [" + beanClass.getName() + "]"));
            }
            this.propertyDescriptors = new LinkedHashMap<String, PropertyDescriptor>();
            HashSet<String> readMethodNames = new HashSet<String>();
            for (PropertyDescriptor pd : pds = this.beanInfo.getPropertyDescriptors()) {
                if (Class.class == beanClass && !"name".equals(pd.getName()) && (!pd.getName().endsWith("Name") || String.class != pd.getPropertyType()) || URL.class == beanClass && "content".equals(pd.getName()) || pd.getWriteMethod() == null && this.isInvalidReadOnlyPropertyType(pd.getPropertyType(), beanClass)) continue;
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Found bean property '" + pd.getName() + "'" + (pd.getPropertyType() != null ? " of type [" + pd.getPropertyType().getName() + "]" : "") + (pd.getPropertyEditorClass() != null ? "; editor [" + pd.getPropertyEditorClass().getName() + "]" : "")));
                }
                pd = this.buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
                this.propertyDescriptors.put(pd.getName(), pd);
                Method readMethod = pd.getReadMethod();
                if (readMethod == null) continue;
                readMethodNames.add(readMethod.getName());
            }
            for (Class<?> currClass = beanClass; currClass != null && currClass != Object.class; currClass = currClass.getSuperclass()) {
                this.introspectInterfaces(beanClass, currClass, readMethodNames);
            }
            this.introspectPlainAccessors(beanClass, readMethodNames);
            this.typeDescriptorCache = new ConcurrentReferenceHashMap();
        }
        catch (IntrospectionException ex) {
            throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", ex);
        }
    }

    private void introspectInterfaces(Class<?> beanClass, Class<?> currClass, Set<String> readMethodNames) throws IntrospectionException {
        for (Class<?> ifc : currClass.getInterfaces()) {
            if (ClassUtils.isJavaLanguageInterface(ifc)) continue;
            for (PropertyDescriptor pd : CachedIntrospectionResults.getBeanInfo(ifc).getPropertyDescriptors()) {
                PropertyDescriptor existingPd = this.propertyDescriptors.get(pd.getName());
                if (existingPd != null && (existingPd.getReadMethod() != null || pd.getReadMethod() == null) || (pd = this.buildGenericTypeAwarePropertyDescriptor(beanClass, pd)).getWriteMethod() == null && this.isInvalidReadOnlyPropertyType(pd.getPropertyType(), beanClass)) continue;
                this.propertyDescriptors.put(pd.getName(), pd);
                Method readMethod = pd.getReadMethod();
                if (readMethod == null) continue;
                readMethodNames.add(readMethod.getName());
            }
            this.introspectInterfaces(ifc, ifc, readMethodNames);
        }
    }

    private void introspectPlainAccessors(Class<?> beanClass, Set<String> readMethodNames) throws IntrospectionException {
        for (Method method : beanClass.getMethods()) {
            if (this.propertyDescriptors.containsKey(method.getName()) || readMethodNames.contains(method.getName()) || !this.isPlainAccessor(method)) continue;
            this.propertyDescriptors.put(method.getName(), new GenericTypeAwarePropertyDescriptor(beanClass, method.getName(), method, null, null));
            readMethodNames.add(method.getName());
        }
    }

    private boolean isPlainAccessor(Method method) {
        if (Modifier.isStatic(method.getModifiers()) || method.getDeclaringClass() == Object.class || method.getDeclaringClass() == Class.class || method.getParameterCount() > 0 || method.getReturnType() == Void.TYPE || this.isInvalidReadOnlyPropertyType(method.getReturnType(), method.getDeclaringClass())) {
            return false;
        }
        try {
            method.getDeclaringClass().getDeclaredField(method.getName());
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    private boolean isInvalidReadOnlyPropertyType(@Nullable Class<?> returnType, Class<?> beanClass) {
        return returnType != null && (ClassLoader.class.isAssignableFrom(returnType) || ProtectionDomain.class.isAssignableFrom(returnType) || AutoCloseable.class.isAssignableFrom(returnType) && !AutoCloseable.class.isAssignableFrom(beanClass));
    }

    BeanInfo getBeanInfo() {
        return this.beanInfo;
    }

    Class<?> getBeanClass() {
        return this.beanInfo.getBeanDescriptor().getBeanClass();
    }

    @Nullable
    PropertyDescriptor getPropertyDescriptor(String name) {
        PropertyDescriptor pd = this.propertyDescriptors.get(name);
        if (pd == null && StringUtils.hasLength((String)name) && (pd = this.propertyDescriptors.get(StringUtils.uncapitalize((String)name))) == null) {
            pd = this.propertyDescriptors.get(StringUtils.capitalize((String)name));
        }
        return pd;
    }

    PropertyDescriptor[] getPropertyDescriptors() {
        return this.propertyDescriptors.values().toArray(PropertyDescriptorUtils.EMPTY_PROPERTY_DESCRIPTOR_ARRAY);
    }

    private PropertyDescriptor buildGenericTypeAwarePropertyDescriptor(Class<?> beanClass, PropertyDescriptor pd) {
        try {
            return new GenericTypeAwarePropertyDescriptor(beanClass, pd.getName(), pd.getReadMethod(), pd.getWriteMethod(), pd.getPropertyEditorClass());
        }
        catch (IntrospectionException ex) {
            throw new FatalBeanException("Failed to re-introspect class [" + beanClass.getName() + "]", ex);
        }
    }

    TypeDescriptor addTypeDescriptor(PropertyDescriptor pd, TypeDescriptor td) {
        TypeDescriptor existing = this.typeDescriptorCache.putIfAbsent(pd, td);
        return existing != null ? existing : td;
    }

    @Nullable
    TypeDescriptor getTypeDescriptor(PropertyDescriptor pd) {
        return (TypeDescriptor)this.typeDescriptorCache.get(pd);
    }
}

