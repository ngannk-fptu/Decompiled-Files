/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.security.ProtectionDomain;
import java.util.Collections;
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
import org.springframework.core.SpringProperties;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StringUtils;

public final class CachedIntrospectionResults {
    public static final String IGNORE_BEANINFO_PROPERTY_NAME = "spring.beaninfo.ignore";
    private static final PropertyDescriptor[] EMPTY_PROPERTY_DESCRIPTOR_ARRAY = new PropertyDescriptor[0];
    private static final boolean shouldIntrospectorIgnoreBeaninfoClasses = SpringProperties.getFlag("spring.beaninfo.ignore");
    private static final List<BeanInfoFactory> beanInfoFactories = SpringFactoriesLoader.loadFactories(BeanInfoFactory.class, CachedIntrospectionResults.class.getClassLoader());
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
        if (ClassUtils.isCacheSafe(beanClass, CachedIntrospectionResults.class.getClassLoader()) || CachedIntrospectionResults.isClassLoaderAccepted(beanClass.getClassLoader())) {
            classCacheToUse = strongClassCache;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Not strongly caching class [" + beanClass.getName() + "] because it is not cache-safe");
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
        return shouldIntrospectorIgnoreBeaninfoClasses ? Introspector.getBeanInfo(beanClass, 3) : Introspector.getBeanInfo(beanClass);
    }

    private CachedIntrospectionResults(Class<?> beanClass) throws BeansException {
        try {
            PropertyDescriptor[] pds;
            if (logger.isTraceEnabled()) {
                logger.trace("Getting BeanInfo for class [" + beanClass.getName() + "]");
            }
            this.beanInfo = CachedIntrospectionResults.getBeanInfo(beanClass);
            if (logger.isTraceEnabled()) {
                logger.trace("Caching PropertyDescriptors for class [" + beanClass.getName() + "]");
            }
            this.propertyDescriptors = new LinkedHashMap<String, PropertyDescriptor>();
            for (PropertyDescriptor pd : pds = this.beanInfo.getPropertyDescriptors()) {
                if (Class.class == beanClass && !"name".equals(pd.getName()) && !pd.getName().endsWith("Name") || pd.getPropertyType() != null && (ClassLoader.class.isAssignableFrom(pd.getPropertyType()) || ProtectionDomain.class.isAssignableFrom(pd.getPropertyType()))) continue;
                if (logger.isTraceEnabled()) {
                    logger.trace("Found bean property '" + pd.getName() + "'" + (pd.getPropertyType() != null ? " of type [" + pd.getPropertyType().getName() + "]" : "") + (pd.getPropertyEditorClass() != null ? "; editor [" + pd.getPropertyEditorClass().getName() + "]" : ""));
                }
                pd = this.buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
                this.propertyDescriptors.put(pd.getName(), pd);
            }
            for (Class<?> currClass = beanClass; currClass != null && currClass != Object.class; currClass = currClass.getSuperclass()) {
                this.introspectInterfaces(beanClass, currClass);
            }
            this.typeDescriptorCache = new ConcurrentReferenceHashMap<PropertyDescriptor, TypeDescriptor>();
        }
        catch (IntrospectionException ex) {
            throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", ex);
        }
    }

    private void introspectInterfaces(Class<?> beanClass, Class<?> currClass) throws IntrospectionException {
        for (Class<?> ifc : currClass.getInterfaces()) {
            if (ClassUtils.isJavaLanguageInterface(ifc)) continue;
            for (PropertyDescriptor pd : CachedIntrospectionResults.getBeanInfo(ifc).getPropertyDescriptors()) {
                PropertyDescriptor existingPd = this.propertyDescriptors.get(pd.getName());
                if (existingPd != null && (existingPd.getReadMethod() != null || pd.getReadMethod() == null) || (pd = this.buildGenericTypeAwarePropertyDescriptor(beanClass, pd)).getPropertyType() != null && (ClassLoader.class.isAssignableFrom(pd.getPropertyType()) || ProtectionDomain.class.isAssignableFrom(pd.getPropertyType()))) continue;
                this.propertyDescriptors.put(pd.getName(), pd);
            }
            this.introspectInterfaces(ifc, ifc);
        }
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
        if (pd == null && StringUtils.hasLength(name) && (pd = this.propertyDescriptors.get(StringUtils.uncapitalize(name))) == null) {
            pd = this.propertyDescriptors.get(StringUtils.capitalize(name));
        }
        return pd;
    }

    PropertyDescriptor[] getPropertyDescriptors() {
        return this.propertyDescriptors.values().toArray(EMPTY_PROPERTY_DESCRIPTOR_ARRAY);
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

