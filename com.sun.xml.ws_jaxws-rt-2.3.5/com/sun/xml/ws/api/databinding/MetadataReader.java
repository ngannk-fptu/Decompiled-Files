/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.databinding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public interface MetadataReader {
    public Annotation[] getAnnotations(Method var1);

    public Annotation[][] getParameterAnnotations(Method var1);

    public <A extends Annotation> A getAnnotation(Class<A> var1, Method var2);

    public <A extends Annotation> A getAnnotation(Class<A> var1, Class<?> var2);

    public Annotation[] getAnnotations(Class<?> var1);

    public void getProperties(Map<String, Object> var1, Class<?> var2);

    public void getProperties(Map<String, Object> var1, Method var2);

    public void getProperties(Map<String, Object> var1, Method var2, int var3);
}

