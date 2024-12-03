/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component;

import com.sun.jersey.core.reflection.AnnotatedMethod;
import com.sun.jersey.core.reflection.MethodList;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.component.AnnotatedContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Errors;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class ComponentConstructor<T> {
    private final InjectableProviderContext ipc;
    private final Class<T> c;
    private final List<Method> postConstructs;
    private final ComponentInjector<T> ci;

    public ComponentConstructor(InjectableProviderContext ipc, Class<T> c, ComponentInjector<T> ci) {
        this.ipc = ipc;
        this.c = c;
        this.ci = ci;
        this.postConstructs = ComponentConstructor.getPostConstructMethods(c);
    }

    private static List<Method> getPostConstructMethods(Class c) {
        Class<?> postConstructClass = AccessController.doPrivileged(ReflectionHelper.classForNamePA("javax.annotation.PostConstruct"));
        LinkedList<Method> list = new LinkedList<Method>();
        HashSet<String> names = new HashSet<String>();
        if (postConstructClass != null) {
            MethodList methodList = new MethodList(c, true);
            for (AnnotatedMethod m : methodList.hasAnnotation(postConstructClass).hasNumParams(0).hasReturnType(Void.TYPE)) {
                Method method = m.getMethod();
                if (!names.add(method.getName())) continue;
                AccessController.doPrivileged(ReflectionHelper.setAccessibleMethodPA(method));
                list.addFirst(method);
            }
        }
        return list;
    }

    public T getInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        int modifiers = this.c.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            Errors.nonPublicClass(this.c);
        }
        if (Modifier.isAbstract(modifiers)) {
            if (Modifier.isInterface(modifiers)) {
                Errors.interfaceClass(this.c);
            } else {
                Errors.abstractClass(this.c);
            }
        }
        if (this.c.getEnclosingClass() != null && !Modifier.isStatic(modifiers)) {
            Errors.innerClass(this.c);
        }
        if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers) && this.c.getConstructors().length == 0) {
            Errors.nonPublicConstructor(this.c);
        }
        T t = this._getInstance();
        this.ci.inject(t);
        for (Method postConstruct : this.postConstructs) {
            postConstruct.invoke(t, new Object[0]);
        }
        return t;
    }

    private T _getInstance() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ConstructorInjectablePair<T> cip = this.getConstructor();
        if (cip == null || ((ConstructorInjectablePair)cip).is.isEmpty()) {
            return this.c.newInstance();
        }
        if (((ConstructorInjectablePair)cip).is.contains(null)) {
            for (int i = 0; i < ((ConstructorInjectablePair)cip).is.size(); ++i) {
                if (((ConstructorInjectablePair)cip).is.get(i) != null) continue;
                Errors.missingDependency(((ConstructorInjectablePair)cip).con, i);
            }
        }
        Object[] params = new Object[((ConstructorInjectablePair)cip).is.size()];
        int i = 0;
        for (Injectable injectable : ((ConstructorInjectablePair)cip).is) {
            if (injectable == null) continue;
            params[i++] = injectable.getValue();
        }
        return ((ConstructorInjectablePair)cip).con.newInstance(params);
    }

    private ConstructorInjectablePair<T> getConstructor() {
        if (this.c.getConstructors().length == 0) {
            return null;
        }
        TreeSet cs = new TreeSet(new ConstructorComparator());
        AnnotatedContext aoc = new AnnotatedContext();
        for (Constructor<?> con : this.c.getConstructors()) {
            ArrayList<Injectable> is = new ArrayList<Injectable>();
            int ps = con.getParameterTypes().length;
            aoc.setAccessibleObject(con);
            for (int p = 0; p < ps; ++p) {
                Type pgtype = con.getGenericParameterTypes()[p];
                Annotation[] as = con.getParameterAnnotations()[p];
                aoc.setAnnotations(as);
                Injectable i = null;
                for (Annotation a : as) {
                    i = this.ipc.getInjectable(a.annotationType(), (ComponentContext)aoc, a, pgtype, ComponentScope.UNDEFINED_SINGLETON);
                }
                is.add(i);
            }
            cs.add(new ConstructorInjectablePair(con, is));
        }
        return (ConstructorInjectablePair)cs.first();
    }

    private static class ConstructorComparator<T>
    implements Comparator<ConstructorInjectablePair<T>> {
        private ConstructorComparator() {
        }

        @Override
        public int compare(ConstructorInjectablePair<T> o1, ConstructorInjectablePair<T> o2) {
            int p = Collections.frequency(((ConstructorInjectablePair)o1).is, null) - Collections.frequency(((ConstructorInjectablePair)o2).is, null);
            if (p != 0) {
                return p;
            }
            return ((ConstructorInjectablePair)o2).con.getParameterTypes().length - ((ConstructorInjectablePair)o1).con.getParameterTypes().length;
        }
    }

    private static class ConstructorInjectablePair<T> {
        private final Constructor<T> con;
        private final List<Injectable> is;

        private ConstructorInjectablePair(Constructor<T> con, List<Injectable> is) {
            this.con = con;
            this.is = is;
        }
    }
}

