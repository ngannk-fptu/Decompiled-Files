/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Objects;

public final class AnnotationDelegate {
    private final Method method1;
    private final Method method2;

    public AnnotationDelegate(Method method1, Method method2) {
        this.method1 = Objects.requireNonNull(method1, "method1 can't be null");
        this.method2 = method2;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        T a = this.method1.getAnnotation(annotationClass);
        if (a != null) {
            return a;
        }
        return this.method2 == null ? null : (T)this.method2.getAnnotation(annotationClass);
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return this.method1.isAnnotationPresent(annotationClass) || this.method2 != null && this.method2.isAnnotationPresent(annotationClass);
    }
}

