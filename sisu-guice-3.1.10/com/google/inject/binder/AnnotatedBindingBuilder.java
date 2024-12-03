/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.binder;

import com.google.inject.binder.LinkedBindingBuilder;
import java.lang.annotation.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface AnnotatedBindingBuilder<T>
extends LinkedBindingBuilder<T> {
    public LinkedBindingBuilder<T> annotatedWith(Class<? extends Annotation> var1);

    public LinkedBindingBuilder<T> annotatedWith(Annotation var1);
}

