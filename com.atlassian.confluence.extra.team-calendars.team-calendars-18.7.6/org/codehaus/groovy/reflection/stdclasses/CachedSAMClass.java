/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.reflection.stdclasses;

import groovy.lang.Closure;
import groovy.util.ProxyGenerator;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.reflection.CachedClass;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.reflection.ReflectionCache;
import org.codehaus.groovy.runtime.ConvertedClosure;
import org.codehaus.groovy.transform.trait.Traits;

public class CachedSAMClass
extends CachedClass {
    private static final int ABSTRACT_STATIC_PRIVATE = 1034;
    private static final int VISIBILITY = 5;
    private static final Method[] EMPTY_METHOD_ARRAY = new Method[0];
    private final Method method;

    public CachedSAMClass(Class klazz, ClassInfo classInfo) {
        super(klazz, classInfo);
        this.method = CachedSAMClass.getSAMMethod(klazz);
        if (this.method == null) {
            throw new GroovyBugError("assigned method should not have been null!");
        }
    }

    @Override
    public boolean isAssignableFrom(Class argument) {
        return argument == null || Closure.class.isAssignableFrom(argument) || ReflectionCache.isAssignableFrom(this.getTheClass(), argument);
    }

    public static Object coerceToSAM(Closure argument, Method method, Class clazz, boolean isInterface) {
        if (argument != null && clazz.isAssignableFrom(argument.getClass())) {
            return argument;
        }
        if (isInterface) {
            if (Traits.isTrait(clazz)) {
                Map<String, Closure> impl = Collections.singletonMap(method.getName(), argument);
                return ProxyGenerator.INSTANCE.instantiateAggregate(impl, Collections.singletonList(clazz));
            }
            return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, (InvocationHandler)new ConvertedClosure(argument));
        }
        HashMap<String, Closure> m = new HashMap<String, Closure>();
        m.put(method.getName(), argument);
        return ProxyGenerator.INSTANCE.instantiateAggregateFromBaseClass(m, clazz);
    }

    @Override
    public Object coerceArgument(Object argument) {
        if (argument instanceof Closure) {
            Class clazz = this.getTheClass();
            return CachedSAMClass.coerceToSAM((Closure)argument, this.method, clazz, clazz.isInterface());
        }
        return argument;
    }

    private static Method[] getDeclaredMethods(final Class c) {
        try {
            Method[] methods = AccessController.doPrivileged(new PrivilegedAction<Method[]>(){

                @Override
                public Method[] run() {
                    return c.getDeclaredMethods();
                }
            });
            if (methods != null) {
                return methods;
            }
        }
        catch (AccessControlException accessControlException) {
            // empty catch block
        }
        return EMPTY_METHOD_ARRAY;
    }

    private static void getAbstractMethods(Class c, List<Method> current) {
        if (c == null || !Modifier.isAbstract(c.getModifiers())) {
            return;
        }
        CachedSAMClass.getAbstractMethods(c.getSuperclass(), current);
        for (Class<?> clazz : c.getInterfaces()) {
            CachedSAMClass.getAbstractMethods(clazz, current);
        }
        for (GenericDeclaration genericDeclaration : CachedSAMClass.getDeclaredMethods(c)) {
            if (Modifier.isPrivate(((Method)genericDeclaration).getModifiers()) || !Modifier.isAbstract(((Method)genericDeclaration).getModifiers())) continue;
            current.add((Method)genericDeclaration);
        }
    }

    private static boolean hasUsableImplementation(Class c, Method m) {
        if (c == m.getDeclaringClass()) {
            return false;
        }
        try {
            Method found = c.getMethod(m.getName(), m.getParameterTypes());
            int asp = found.getModifiers() & 0x40A;
            int visible = found.getModifiers() & 5;
            if (visible != 0 && asp == 0) {
                return true;
            }
        }
        catch (NoSuchMethodException noSuchMethodException) {
            // empty catch block
        }
        if (c == Object.class) {
            return false;
        }
        return CachedSAMClass.hasUsableImplementation(c.getSuperclass(), m);
    }

    private static Method getSingleNonDuplicateMethod(List<Method> current) {
        if (current.isEmpty()) {
            return null;
        }
        if (current.size() == 1) {
            return current.get(0);
        }
        Method m = current.remove(0);
        for (Method m2 : current) {
            if (m.getName().equals(m2.getName()) && Arrays.equals(m.getParameterTypes(), m2.getParameterTypes())) continue;
            return null;
        }
        return m;
    }

    public static Method getSAMMethod(Class<?> c) {
        try {
            return CachedSAMClass.getSAMMethodImpl(c);
        }
        catch (NoClassDefFoundError ignore) {
            return null;
        }
    }

    private static Method getSAMMethodImpl(Class<?> c) {
        if (!Modifier.isAbstract(c.getModifiers())) {
            return null;
        }
        if (c.isInterface()) {
            Method[] methods = c.getMethods();
            Method res = null;
            for (Method mi : methods) {
                if (!Modifier.isAbstract(mi.getModifiers()) || mi.getAnnotation(Traits.Implemented.class) != null) continue;
                try {
                    Object.class.getMethod(mi.getName(), mi.getParameterTypes());
                }
                catch (NoSuchMethodException noSuchMethodException) {
                    if (res != null) {
                        return null;
                    }
                    res = mi;
                }
            }
            return res;
        }
        LinkedList<Method> methods = new LinkedList<Method>();
        CachedSAMClass.getAbstractMethods(c, methods);
        if (methods.isEmpty()) {
            return null;
        }
        ListIterator it = methods.listIterator();
        while (it.hasNext()) {
            Method m = (Method)it.next();
            if (!CachedSAMClass.hasUsableImplementation(c, m)) continue;
            it.remove();
        }
        return CachedSAMClass.getSingleNonDuplicateMethod(methods);
    }
}

