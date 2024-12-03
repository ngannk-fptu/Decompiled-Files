/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.springframework.lang.Nullable;

interface AnnotationAttributeExtractor<S> {
    public Class<? extends Annotation> getAnnotationType();

    @Nullable
    public Object getAnnotatedElement();

    public S getSource();

    @Nullable
    public Object getAttributeValue(Method var1);
}

