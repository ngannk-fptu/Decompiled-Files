/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.spi.BindingTargetVisitor
 */
package com.google.inject.assistedinject;

import com.google.inject.assistedinject.AssistedInjectBinding;
import com.google.inject.spi.BindingTargetVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface AssistedInjectTargetVisitor<T, V>
extends BindingTargetVisitor<T, V> {
    public V visit(AssistedInjectBinding<? extends T> var1);
}

