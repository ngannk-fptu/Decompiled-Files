/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Scope;
import java.lang.annotation.Annotation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface BindingScopingVisitor<V> {
    public V visitEagerSingleton();

    public V visitScope(Scope var1);

    public V visitScopeAnnotation(Class<? extends Annotation> var1);

    public V visitNoScoping();
}

