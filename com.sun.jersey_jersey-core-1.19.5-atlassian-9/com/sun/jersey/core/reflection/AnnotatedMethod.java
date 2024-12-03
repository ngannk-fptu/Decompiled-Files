/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.CookieParam
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.Encoded
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.HeaderParam
 *  javax.ws.rs.HttpMethod
 *  javax.ws.rs.MatrixParam
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 */
package com.sun.jersey.core.reflection;

import com.sun.jersey.core.reflection.ReflectionHelper;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

public final class AnnotatedMethod
implements AnnotatedElement {
    private static final Set<Class<? extends Annotation>> METHOD_META_ANNOTATIONS = AnnotatedMethod.getSet(HttpMethod.class);
    private static final Set<Class<? extends Annotation>> METHOD_ANNOTATIONS = AnnotatedMethod.getSet(Path.class, Produces.class, Consumes.class);
    private static final Set<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = AnnotatedMethod.getSet(Context.class, Encoded.class, DefaultValue.class, MatrixParam.class, QueryParam.class, CookieParam.class, HeaderParam.class, PathParam.class, FormParam.class);
    private final Method m;
    private final Method am;
    private final Annotation[] methodAnnotations;
    private final Annotation[][] parameterAnnotations;

    private static Set<Class<? extends Annotation>> getSet(Class<? extends Annotation> ... cs) {
        HashSet<Class<? extends Annotation>> s = new HashSet<Class<? extends Annotation>>();
        for (Class<? extends Annotation> c : cs) {
            s.add(c);
        }
        return s;
    }

    public AnnotatedMethod(Method m) {
        this.m = m;
        this.am = AnnotatedMethod.findAnnotatedMethod(m);
        if (m.equals(this.am)) {
            this.methodAnnotations = m.getAnnotations();
            this.parameterAnnotations = m.getParameterAnnotations();
        } else {
            this.methodAnnotations = AnnotatedMethod.mergeMethodAnnotations(m, this.am);
            this.parameterAnnotations = AnnotatedMethod.mergeParameterAnnotations(m, this.am);
        }
    }

    public Method getMethod() {
        return this.am;
    }

    public Annotation[][] getParameterAnnotations() {
        return (Annotation[][])this.parameterAnnotations.clone();
    }

    public Class<?>[] getParameterTypes() {
        return this.am.getParameterTypes();
    }

    public TypeVariable<Method>[] getTypeParameters() {
        return this.am.getTypeParameters();
    }

    public Type[] getGenericParameterTypes() {
        return this.am.getGenericParameterTypes();
    }

    public <T extends Annotation> List<T> getMetaMethodAnnotations(Class<T> annotation) {
        ArrayList<T> ma = new ArrayList<T>();
        for (Annotation a : this.methodAnnotations) {
            if (a.annotationType().getAnnotation(annotation) == null) continue;
            ma.add(a.annotationType().getAnnotation(annotation));
        }
        return ma;
    }

    public String toString() {
        return this.m.toString();
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        for (Annotation ma : this.methodAnnotations) {
            if (ma.annotationType() != annotationType) continue;
            return true;
        }
        return false;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        for (Annotation ma : this.methodAnnotations) {
            if (ma.annotationType() != annotationType) continue;
            return (T)((Annotation)annotationType.cast(ma));
        }
        return this.am.getAnnotation(annotationType);
    }

    @Override
    public Annotation[] getAnnotations() {
        return (Annotation[])this.methodAnnotations.clone();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return this.getAnnotations();
    }

    private static Annotation[] mergeMethodAnnotations(Method m, Method am) {
        List<Annotation> al = AnnotatedMethod.asList(m.getAnnotations());
        for (Annotation a : am.getAnnotations()) {
            if (m.isAnnotationPresent(a.getClass())) continue;
            al.add(a);
        }
        return al.toArray(new Annotation[0]);
    }

    private static Annotation[][] mergeParameterAnnotations(Method m, Method am) {
        Annotation[][] mp = m.getParameterAnnotations();
        Annotation[][] amp = am.getParameterAnnotations();
        ArrayList<List<Annotation>> ala = new ArrayList<List<Annotation>>();
        for (int i = 0; i < mp.length; ++i) {
            List<Annotation> al = AnnotatedMethod.asList(mp[i]);
            for (Annotation a : amp[i]) {
                if (AnnotatedMethod.isAnnotatonPresent(a.getClass(), al)) continue;
                al.add(a);
            }
            ala.add(al);
        }
        Annotation[][] paa = new Annotation[mp.length][];
        for (int i = 0; i < mp.length; ++i) {
            paa[i] = ((List)ala.get(i)).toArray(new Annotation[0]);
        }
        return paa;
    }

    private static boolean isAnnotatonPresent(Class<? extends Annotation> ca, List<Annotation> la) {
        for (Annotation a : la) {
            if (ca != a.getClass()) continue;
            return true;
        }
        return false;
    }

    private static Method findAnnotatedMethod(Method m) {
        Method am = AnnotatedMethod.findAnnotatedMethod(m.getDeclaringClass(), m);
        return am != null ? am : m;
    }

    private static Method findAnnotatedMethod(Class<?> c, Method m) {
        Method sm;
        if (c == Object.class) {
            return null;
        }
        if ((m = AccessController.doPrivileged(ReflectionHelper.findMethodOnClassPA(c, m))) == null) {
            return null;
        }
        if (AnnotatedMethod.hasAnnotations(m)) {
            return m;
        }
        Class<?> sc = c.getSuperclass();
        if (sc != null && sc != Object.class && (sm = AnnotatedMethod.findAnnotatedMethod(sc, m)) != null) {
            return sm;
        }
        for (Class<?> ic : c.getInterfaces()) {
            Method im = AnnotatedMethod.findAnnotatedMethod(ic, m);
            if (im == null) continue;
            return im;
        }
        return null;
    }

    private static boolean hasAnnotations(Method m) {
        return AnnotatedMethod.hasMetaMethodAnnotations(m) || AnnotatedMethod.hasMethodAnnotations(m) || AnnotatedMethod.hasParameterAnnotations(m);
    }

    private static boolean hasMetaMethodAnnotations(Method m) {
        for (Class<? extends Annotation> ac : METHOD_META_ANNOTATIONS) {
            for (Annotation a : m.getAnnotations()) {
                if (a.annotationType().getAnnotation(ac) == null) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean hasMethodAnnotations(Method m) {
        for (Class<? extends Annotation> ac : METHOD_ANNOTATIONS) {
            if (!m.isAnnotationPresent(ac)) continue;
            return true;
        }
        return false;
    }

    private static boolean hasParameterAnnotations(Method m) {
        Annotation[][] annotationArray = m.getParameterAnnotations();
        int n = annotationArray.length;
        for (int i = 0; i < n; ++i) {
            Annotation[] as;
            for (Annotation a : as = annotationArray[i]) {
                if (!PARAMETER_ANNOTATIONS.contains(a.annotationType())) continue;
                return true;
            }
        }
        return false;
    }

    private static <T> List<T> asList(T ... ts) {
        ArrayList<T> l = new ArrayList<T>();
        for (T t : ts) {
            l.add(t);
        }
        return l;
    }
}

