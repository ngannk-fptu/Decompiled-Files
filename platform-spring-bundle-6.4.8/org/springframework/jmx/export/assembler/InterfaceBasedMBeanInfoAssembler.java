/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.assembler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.assembler.AbstractConfigurableMBeanInfoAssembler;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class InterfaceBasedMBeanInfoAssembler
extends AbstractConfigurableMBeanInfoAssembler
implements BeanClassLoaderAware,
InitializingBean {
    @Nullable
    private Class<?>[] managedInterfaces;
    @Nullable
    private Properties interfaceMappings;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Nullable
    private Map<String, Class<?>[]> resolvedInterfaceMappings;

    public void setManagedInterfaces(Class<?> ... managedInterfaces) {
        if (managedInterfaces != null) {
            for (Class<?> ifc : managedInterfaces) {
                if (ifc.isInterface()) continue;
                throw new IllegalArgumentException("Management interface [" + ifc.getName() + "] is not an interface");
            }
        }
        this.managedInterfaces = managedInterfaces;
    }

    public void setInterfaceMappings(@Nullable Properties mappings) {
        this.interfaceMappings = mappings;
    }

    @Override
    public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.interfaceMappings != null) {
            this.resolvedInterfaceMappings = this.resolveInterfaceMappings(this.interfaceMappings);
        }
    }

    private Map<String, Class<?>[]> resolveInterfaceMappings(Properties mappings) {
        HashMap<String, Class<?>[]> resolvedMappings = CollectionUtils.newHashMap(mappings.size());
        Enumeration<?> en = mappings.propertyNames();
        while (en.hasMoreElements()) {
            String beanKey = (String)en.nextElement();
            String[] classNames = StringUtils.commaDelimitedListToStringArray(mappings.getProperty(beanKey));
            Class<?>[] classes = this.resolveClassNames(classNames, beanKey);
            resolvedMappings.put(beanKey, classes);
        }
        return resolvedMappings;
    }

    private Class<?>[] resolveClassNames(String[] classNames, String beanKey) {
        Class[] classes = new Class[classNames.length];
        for (int x = 0; x < classes.length; ++x) {
            Class<?> cls = ClassUtils.resolveClassName(classNames[x].trim(), this.beanClassLoader);
            if (!cls.isInterface()) {
                throw new IllegalArgumentException("Class [" + classNames[x] + "] mapped to bean key [" + beanKey + "] is no interface");
            }
            classes[x] = cls;
        }
        return classes;
    }

    @Override
    protected boolean includeReadAttribute(Method method, String beanKey) {
        return this.isPublicInInterface(method, beanKey);
    }

    @Override
    protected boolean includeWriteAttribute(Method method, String beanKey) {
        return this.isPublicInInterface(method, beanKey);
    }

    @Override
    protected boolean includeOperation(Method method, String beanKey) {
        return this.isPublicInInterface(method, beanKey);
    }

    private boolean isPublicInInterface(Method method, String beanKey) {
        return Modifier.isPublic(method.getModifiers()) && this.isDeclaredInInterface(method, beanKey);
    }

    private boolean isDeclaredInInterface(Method method, String beanKey) {
        Class<?>[] ifaces = null;
        if (this.resolvedInterfaceMappings != null) {
            ifaces = this.resolvedInterfaceMappings.get(beanKey);
        }
        if (ifaces == null && (ifaces = this.managedInterfaces) == null) {
            ifaces = ClassUtils.getAllInterfacesForClass(method.getDeclaringClass());
        }
        for (Class<?> ifc : ifaces) {
            for (Method ifcMethod : ifc.getMethods()) {
                if (!ifcMethod.getName().equals(method.getName()) || ifcMethod.getParameterCount() != method.getParameterCount() || !Arrays.equals(ifcMethod.getParameterTypes(), method.getParameterTypes())) continue;
                return true;
            }
        }
        return false;
    }
}

