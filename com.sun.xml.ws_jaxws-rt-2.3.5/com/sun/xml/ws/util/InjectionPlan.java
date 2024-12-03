/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceException;

public abstract class InjectionPlan<T, R> {
    public abstract void inject(T var1, R var2);

    public void inject(T instance, Callable<R> resource) {
        try {
            this.inject(instance, resource.call());
        }
        catch (Exception e) {
            throw new WebServiceException((Throwable)e);
        }
    }

    private static void invokeMethod(final Method method, final Object instance, final Object ... args) {
        if (method == null) {
            return;
        }
        AccessController.doPrivileged(new PrivilegedAction<Void>(){

            @Override
            public Void run() {
                try {
                    if (!method.isAccessible()) {
                        method.setAccessible(true);
                    }
                    method.invoke(instance, args);
                }
                catch (IllegalAccessException e) {
                    throw new WebServiceException((Throwable)e);
                }
                catch (InvocationTargetException e) {
                    throw new WebServiceException((Throwable)e);
                }
                return null;
            }
        });
    }

    public static <T, R> InjectionPlan<T, R> buildInjectionPlan(Class<? extends T> clazz, Class<R> resourceType, boolean isStatic) {
        Resource resource;
        Class<T> cl;
        ArrayList plan = new ArrayList();
        for (cl = clazz; cl != Object.class; cl = cl.getSuperclass()) {
            for (AccessibleObject accessibleObject : cl.getDeclaredFields()) {
                resource = ((Field)accessibleObject).getAnnotation(Resource.class);
                if (resource == null || !InjectionPlan.isInjectionPoint(resource, ((Field)accessibleObject).getType(), "Incorrect type for field" + ((Field)accessibleObject).getName(), resourceType)) continue;
                if (isStatic && !Modifier.isStatic(((Field)accessibleObject).getModifiers())) {
                    throw new WebServiceException("Static resource " + resourceType + " cannot be injected to non-static " + accessibleObject);
                }
                plan.add(new FieldInjectionPlan((Field)accessibleObject));
            }
        }
        for (cl = clazz; cl != Object.class; cl = cl.getSuperclass()) {
            for (AccessibleObject accessibleObject : cl.getDeclaredMethods()) {
                resource = ((Method)accessibleObject).getAnnotation(Resource.class);
                if (resource == null) continue;
                Class<?>[] paramTypes = ((Method)accessibleObject).getParameterTypes();
                if (paramTypes.length != 1) {
                    throw new WebServiceException("Incorrect no of arguments for method " + accessibleObject);
                }
                if (!InjectionPlan.isInjectionPoint(resource, paramTypes[0], "Incorrect argument types for method" + ((Method)accessibleObject).getName(), resourceType)) continue;
                if (isStatic && !Modifier.isStatic(((Method)accessibleObject).getModifiers())) {
                    throw new WebServiceException("Static resource " + resourceType + " cannot be injected to non-static " + accessibleObject);
                }
                plan.add(new MethodInjectionPlan((Method)accessibleObject));
            }
        }
        return new Compositor(plan);
    }

    private static boolean isInjectionPoint(Resource resource, Class fieldType, String errorMessage, Class resourceType) {
        Class t = resource.type();
        if (t.equals(Object.class)) {
            return fieldType.equals(resourceType);
        }
        if (t.equals(resourceType)) {
            if (fieldType.isAssignableFrom(resourceType)) {
                return true;
            }
            throw new WebServiceException(errorMessage);
        }
        return false;
    }

    private static class Compositor<T, R>
    extends InjectionPlan<T, R> {
        private final Collection<InjectionPlan<T, R>> children;

        public Compositor(Collection<InjectionPlan<T, R>> children) {
            this.children = children;
        }

        @Override
        public void inject(T instance, R res) {
            for (InjectionPlan<T, R> plan : this.children) {
                plan.inject(instance, res);
            }
        }

        @Override
        public void inject(T instance, Callable<R> resource) {
            if (!this.children.isEmpty()) {
                super.inject(instance, resource);
            }
        }
    }

    public static class MethodInjectionPlan<T, R>
    extends InjectionPlan<T, R> {
        private final Method method;

        public MethodInjectionPlan(Method method) {
            this.method = method;
        }

        @Override
        public void inject(T instance, R resource) {
            InjectionPlan.invokeMethod(this.method, instance, new Object[]{resource});
        }
    }

    public static class FieldInjectionPlan<T, R>
    extends InjectionPlan<T, R> {
        private final Field field;

        public FieldInjectionPlan(Field field) {
            this.field = field;
        }

        @Override
        public void inject(final T instance, final R resource) {
            AccessController.doPrivileged(new PrivilegedAction<Object>(){

                @Override
                public Object run() {
                    try {
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        field.set(instance, resource);
                        return null;
                    }
                    catch (IllegalAccessException e) {
                        throw new WebServiceException((Throwable)e);
                    }
                }
            });
        }
    }
}

