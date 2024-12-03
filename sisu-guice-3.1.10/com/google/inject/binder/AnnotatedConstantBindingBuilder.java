/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.binder;

import com.google.inject.binder.ConstantBindingBuilder;
import java.lang.annotation.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface AnnotatedConstantBindingBuilder {
    public ConstantBindingBuilder annotatedWith(Class<? extends Annotation> var1);

    public ConstantBindingBuilder annotatedWith(Annotation var1);
}

