/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.multibindings;

import com.google.inject.multibindings.MapBinderBinding;
import com.google.inject.multibindings.MultibinderBinding;
import com.google.inject.spi.BindingTargetVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface MultibindingsTargetVisitor<T, V>
extends BindingTargetVisitor<T, V> {
    @Override
    public V visit(MultibinderBinding<? extends T> var1);

    @Override
    public V visit(MapBinderBinding<? extends T> var1);
}

