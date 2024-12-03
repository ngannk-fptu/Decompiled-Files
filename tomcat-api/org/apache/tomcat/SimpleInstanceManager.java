/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat;

import java.lang.reflect.InvocationTargetException;
import javax.naming.NamingException;
import org.apache.tomcat.InstanceManager;

public class SimpleInstanceManager
implements InstanceManager {
    @Override
    public Object newInstance(Class<?> clazz) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, NoSuchMethodException {
        return this.prepareInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
    }

    @Override
    public Object newInstance(String className) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, NoSuchMethodException {
        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        return this.prepareInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
    }

    @Override
    public Object newInstance(String fqcn, ClassLoader classLoader) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, NoSuchMethodException {
        Class<?> clazz = classLoader.loadClass(fqcn);
        return this.prepareInstance(clazz.getConstructor(new Class[0]).newInstance(new Object[0]));
    }

    @Override
    public void newInstance(Object o) throws IllegalAccessException, InvocationTargetException, NamingException {
    }

    @Override
    public void destroyInstance(Object o) throws IllegalAccessException, InvocationTargetException {
    }

    private Object prepareInstance(Object o) {
        return o;
    }
}

