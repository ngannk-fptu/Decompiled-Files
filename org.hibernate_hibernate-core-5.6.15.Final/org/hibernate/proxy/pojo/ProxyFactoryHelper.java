/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy.pojo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Subclass;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.proxy.HibernateProxy;

public final class ProxyFactoryHelper {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(ProxyFactoryHelper.class);

    private ProxyFactoryHelper() {
    }

    public static Set<Class> extractProxyInterfaces(PersistentClass persistentClass, String entityName) {
        LinkedHashSet<Class> proxyInterfaces = new LinkedHashSet<Class>();
        Class mappedClass = persistentClass.getMappedClass();
        Class proxyInterface = persistentClass.getProxyInterface();
        if (proxyInterface != null && !mappedClass.equals(proxyInterface)) {
            if (!proxyInterface.isInterface()) {
                throw new MappingException("proxy must be either an interface, or the class itself: " + entityName);
            }
            proxyInterfaces.add(proxyInterface);
        }
        if (mappedClass.isInterface()) {
            proxyInterfaces.add(mappedClass);
        }
        Iterator subclasses = persistentClass.getSubclassIterator();
        while (subclasses.hasNext()) {
            Subclass subclass = (Subclass)subclasses.next();
            Class subclassProxy = subclass.getProxyInterface();
            Class subclassClass = subclass.getMappedClass();
            if (subclassProxy == null || subclassClass.equals(subclassProxy)) continue;
            if (!subclassProxy.isInterface()) {
                throw new MappingException("proxy must be either an interface, or the class itself: " + subclass.getEntityName());
            }
            proxyInterfaces.add(subclassProxy);
        }
        proxyInterfaces.add(HibernateProxy.class);
        return proxyInterfaces;
    }

    public static void validateProxyability(PersistentClass persistentClass) {
        Iterator properties = persistentClass.getPropertyIterator();
        Class clazz = persistentClass.getMappedClass();
        while (properties.hasNext()) {
            Property property = (Property)properties.next();
            ProxyFactoryHelper.validateGetterSetterMethodProxyability("Getter", property.getGetter(clazz).getMethod());
            ProxyFactoryHelper.validateGetterSetterMethodProxyability("Setter", property.getSetter(clazz).getMethod());
        }
    }

    public static void validateGetterSetterMethodProxyability(String getterOrSetter, Method method) {
        if (method != null && Modifier.isFinal(method.getModifiers())) {
            throw new HibernateException(String.format("%s methods of lazy classes cannot be final: %s#%s", getterOrSetter, method.getDeclaringClass().getName(), method.getName()));
        }
    }

    public static Method extractProxySetIdentifierMethod(Setter idSetter, Class proxyInterface) {
        Method idSetterMethod = idSetter == null ? null : idSetter.getMethod();
        Method proxySetIdentifierMethod = idSetterMethod == null || proxyInterface == null ? null : ReflectHelper.getMethod(proxyInterface, idSetterMethod);
        return proxySetIdentifierMethod;
    }

    public static Method extractProxyGetIdentifierMethod(Getter idGetter, Class proxyInterface) {
        Method idGetterMethod = idGetter == null ? null : idGetter.getMethod();
        Method proxyGetIdentifierMethod = idGetterMethod == null || proxyInterface == null ? null : ReflectHelper.getMethod(proxyInterface, idGetterMethod);
        return proxyGetIdentifierMethod;
    }
}

