/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat;

import java.lang.reflect.InvocationTargetException;
import javax.naming.NamingException;

public interface InstanceManager {
    public Object newInstance(Class<?> var1) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, IllegalArgumentException, NoSuchMethodException, SecurityException;

    public Object newInstance(String var1) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, IllegalArgumentException, NoSuchMethodException, SecurityException;

    public Object newInstance(String var1, ClassLoader var2) throws IllegalAccessException, InvocationTargetException, NamingException, InstantiationException, ClassNotFoundException, IllegalArgumentException, NoSuchMethodException, SecurityException;

    public void newInstance(Object var1) throws IllegalAccessException, InvocationTargetException, NamingException;

    public void destroyInstance(Object var1) throws IllegalAccessException, InvocationTargetException;

    default public void backgroundProcess() {
    }
}

