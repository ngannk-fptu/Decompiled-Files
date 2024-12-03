/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.converters.reflection.ObjectAccessException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Cloneables {
    public static Object clone(Object o) {
        if (o instanceof Cloneable) {
            if (o.getClass().isArray()) {
                Class<?> componentType = o.getClass().getComponentType();
                if (!componentType.isPrimitive()) {
                    return ((Object[])o).clone();
                }
                int length = Array.getLength(o);
                Object clone = Array.newInstance(componentType, length);
                while (length-- > 0) {
                    Array.set(clone, length, Array.get(o, length));
                }
                return clone;
            }
            try {
                Method clone = o.getClass().getMethod("clone", null);
                return clone.invoke(o, (Object[])null);
            }
            catch (NoSuchMethodException e) {
                throw new ObjectAccessException("Cloneable type has no clone method", e);
            }
            catch (IllegalAccessException e) {
                throw new ObjectAccessException("Cannot clone Cloneable type", e);
            }
            catch (InvocationTargetException e) {
                throw new ObjectAccessException("Exception cloning Cloneable type", e.getCause());
            }
        }
        return null;
    }

    public static Object cloneIfPossible(Object o) {
        Object clone = Cloneables.clone(o);
        return clone == null ? o : clone;
    }
}

