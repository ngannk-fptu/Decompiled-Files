/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import net.java.ao.Implementation;
import net.java.ao.MethodImplWrapper;
import net.java.ao.RawEntity;

class ImplementationWrapper<T extends RawEntity<?>> {
    private List<Object> implementations = new ArrayList<Object>();
    private boolean initialized = false;

    public void init(T instance) {
        this.init(instance, instance.getEntityType());
        this.initialized = true;
    }

    private void init(T instance, Class<? extends RawEntity<?>> clazz) {
        Implementation implAnnotation = clazz.getAnnotation(Implementation.class);
        if (implAnnotation != null) {
            try {
                Constructor<?> con = implAnnotation.value().getConstructor(clazz);
                this.implementations.add(con.newInstance(instance));
            }
            catch (SecurityException securityException) {
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
            catch (InstantiationException instantiationException) {
            }
            catch (IllegalAccessException illegalAccessException) {
            }
            catch (InvocationTargetException invocationTargetException) {
                // empty catch block
            }
        }
        for (Class<?> sup : clazz.getInterfaces()) {
            if (!RawEntity.class.isAssignableFrom(sup)) continue;
            this.init(instance, sup);
        }
    }

    public MethodImplWrapper getMethod(String name, Class<?> ... parameterTypes) {
        if (!this.initialized) {
            return null;
        }
        for (Object obj : this.implementations) {
            try {
                return new MethodImplWrapper(obj, obj.getClass().getMethod(name, parameterTypes));
            }
            catch (SecurityException securityException) {
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
        }
        return null;
    }
}

