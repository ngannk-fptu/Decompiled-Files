/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.model;

import com.sun.xml.ws.api.databinding.MetadataReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

public class ReflectAnnotationReader
implements MetadataReader {
    @Override
    public Annotation[] getAnnotations(Method m) {
        return m.getAnnotations();
    }

    @Override
    public Annotation[][] getParameterAnnotations(final Method method) {
        return AccessController.doPrivileged(new PrivilegedAction<Annotation[][]>(){

            @Override
            public Annotation[][] run() {
                return method.getParameterAnnotations();
            }
        });
    }

    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annType, final Method m) {
        return (A)((Annotation)AccessController.doPrivileged(new PrivilegedAction<A>(){

            @Override
            public A run() {
                return m.getAnnotation(annType);
            }
        }));
    }

    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annType, final Class<?> cls) {
        return (A)((Annotation)AccessController.doPrivileged(new PrivilegedAction<A>(){

            @Override
            public A run() {
                return cls.getAnnotation(annType);
            }
        }));
    }

    @Override
    public Annotation[] getAnnotations(final Class<?> cls) {
        return AccessController.doPrivileged(new PrivilegedAction<Annotation[]>(){

            @Override
            public Annotation[] run() {
                return cls.getAnnotations();
            }
        });
    }

    @Override
    public void getProperties(Map<String, Object> prop, Class<?> cls) {
    }

    @Override
    public void getProperties(Map<String, Object> prop, Method method) {
    }

    @Override
    public void getProperties(Map<String, Object> prop, Method method, int pos) {
    }
}

