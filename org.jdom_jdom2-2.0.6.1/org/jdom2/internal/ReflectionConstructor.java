/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ReflectionConstructor {
    public static final <E> E construct(String classname, Class<E> targetclass) {
        try {
            Class<?> sclass = Class.forName(classname);
            if (!targetclass.isAssignableFrom(sclass)) {
                throw new ClassCastException("Class '" + classname + "' is not assignable to '" + targetclass.getName() + "'.");
            }
            Constructor<?> constructor = sclass.getConstructor(new Class[0]);
            Object o = constructor.newInstance(new Object[0]);
            return targetclass.cast(o);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Unable to locate class '" + classname + "'.", e);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Unable to locate class no-arg constructor '" + classname + "'.", e);
        }
        catch (SecurityException e) {
            throw new IllegalStateException("Unable to access class constructor '" + classname + "'.", e);
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to access class constructor '" + classname + "'.", e);
        }
        catch (InstantiationException e) {
            throw new IllegalStateException("Unable to instantiate class '" + classname + "'.", e);
        }
        catch (InvocationTargetException e) {
            throw new IllegalStateException("Unable to call class constructor '" + classname + "'.", e);
        }
    }
}

