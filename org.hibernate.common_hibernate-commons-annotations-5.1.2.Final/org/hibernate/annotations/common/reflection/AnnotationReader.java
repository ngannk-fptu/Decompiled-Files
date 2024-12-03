/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection;

import java.lang.annotation.Annotation;

public interface AnnotationReader {
    public <T extends Annotation> T getAnnotation(Class<T> var1);

    public <T extends Annotation> boolean isAnnotationPresent(Class<T> var1);

    public Annotation[] getAnnotations();
}

