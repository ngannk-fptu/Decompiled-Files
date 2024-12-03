/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.Element;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Binding<T>
extends Element {
    public Key<T> getKey();

    public Provider<T> getProvider();

    public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> var1);

    public <V> V acceptScopingVisitor(BindingScopingVisitor<V> var1);
}

