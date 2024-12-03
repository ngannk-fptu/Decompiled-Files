/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.reflection;

import com.sun.jersey.core.reflection.AnnotatedMethod;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MethodList
implements Iterable<AnnotatedMethod> {
    private AnnotatedMethod[] methods;

    public MethodList(Class c) {
        this(c, false);
    }

    public MethodList(Class c, boolean declaredMethods) {
        this(declaredMethods ? MethodList.getAllDeclaredMethods(c) : MethodList.getMethods(c));
    }

    private static List<Method> getAllDeclaredMethods(Class c) {
        ArrayList<Method> l = new ArrayList<Method>();
        while (c != null && c != Object.class) {
            l.addAll(Arrays.asList(c.getDeclaredMethods()));
            c = c.getSuperclass();
        }
        return l;
    }

    private static List<Method> getMethods(Class c) {
        return Arrays.asList(c.getMethods());
    }

    public MethodList(List<Method> methods) {
        ArrayList<AnnotatedMethod> l = new ArrayList<AnnotatedMethod>();
        for (Method m : methods) {
            if (m.isBridge() || m.getDeclaringClass() == Object.class) continue;
            l.add(new AnnotatedMethod(m));
        }
        this.methods = new AnnotatedMethod[l.size()];
        this.methods = l.toArray(this.methods);
    }

    public MethodList(Method ... methods) {
        ArrayList<AnnotatedMethod> l = new ArrayList<AnnotatedMethod>();
        for (Method m : methods) {
            if (m.isBridge() || m.getDeclaringClass() == Object.class) continue;
            l.add(new AnnotatedMethod(m));
        }
        this.methods = new AnnotatedMethod[l.size()];
        this.methods = l.toArray(this.methods);
    }

    public MethodList(AnnotatedMethod ... methods) {
        this.methods = methods;
    }

    @Override
    public Iterator<AnnotatedMethod> iterator() {
        return Arrays.asList(this.methods).iterator();
    }

    public <T extends Annotation> MethodList isNotPublic() {
        return this.filter(new Filter(){

            @Override
            public boolean keep(AnnotatedMethod m) {
                return !Modifier.isPublic(m.getMethod().getModifiers());
            }
        });
    }

    public <T extends Annotation> MethodList hasNumParams(final int i) {
        return this.filter(new Filter(){

            @Override
            public boolean keep(AnnotatedMethod m) {
                return m.getParameterTypes().length == i;
            }
        });
    }

    public <T extends Annotation> MethodList hasReturnType(final Class<?> r) {
        return this.filter(new Filter(){

            @Override
            public boolean keep(AnnotatedMethod m) {
                return m.getMethod().getReturnType() == r;
            }
        });
    }

    public <T extends Annotation> MethodList nameStartsWith(final String s) {
        return this.filter(new Filter(){

            @Override
            public boolean keep(AnnotatedMethod m) {
                return m.getMethod().getName().startsWith(s);
            }
        });
    }

    public <T extends Annotation> MethodList hasAnnotation(final Class<T> annotation) {
        return this.filter(new Filter(){

            @Override
            public boolean keep(AnnotatedMethod m) {
                return m.getAnnotation(annotation) != null;
            }
        });
    }

    public <T extends Annotation> MethodList hasMetaAnnotation(final Class<T> annotation) {
        return this.filter(new Filter(){

            @Override
            public boolean keep(AnnotatedMethod m) {
                for (Annotation a : m.getAnnotations()) {
                    if (a.annotationType().getAnnotation(annotation) == null) continue;
                    return true;
                }
                return false;
            }
        });
    }

    public <T extends Annotation> MethodList hasNotAnnotation(final Class<T> annotation) {
        return this.filter(new Filter(){

            @Override
            public boolean keep(AnnotatedMethod m) {
                return m.getAnnotation(annotation) == null;
            }
        });
    }

    public <T extends Annotation> MethodList hasNotMetaAnnotation(final Class<T> annotation) {
        return this.filter(new Filter(){

            @Override
            public boolean keep(AnnotatedMethod m) {
                for (Annotation a : m.getAnnotations()) {
                    if (a.annotationType().getAnnotation(annotation) == null) continue;
                    return false;
                }
                return true;
            }
        });
    }

    public MethodList filter(Filter f) {
        ArrayList<AnnotatedMethod> r = new ArrayList<AnnotatedMethod>();
        for (AnnotatedMethod m : this.methods) {
            if (!f.keep(m)) continue;
            r.add(m);
        }
        return new MethodList(r.toArray(new AnnotatedMethod[0]));
    }

    public static interface Filter {
        public boolean keep(AnnotatedMethod var1);
    }
}

