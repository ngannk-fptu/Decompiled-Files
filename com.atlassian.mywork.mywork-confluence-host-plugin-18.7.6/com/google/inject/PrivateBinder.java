/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedElementBuilder;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface PrivateBinder
extends Binder {
    public void expose(Key<?> var1);

    public AnnotatedElementBuilder expose(Class<?> var1);

    public AnnotatedElementBuilder expose(TypeLiteral<?> var1);

    @Override
    public PrivateBinder withSource(Object var1);

    @Override
    public PrivateBinder skipSources(Class ... var1);
}

