/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.spi.BindingTargetVisitor
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
    public V visit(MultibinderBinding<? extends T> var1);

    public V visit(MapBinderBinding<? extends T> var1);
}

