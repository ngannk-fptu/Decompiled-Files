/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.spi.ConstructorBinding;
import com.google.inject.spi.ConvertedConstantBinding;
import com.google.inject.spi.ExposedBinding;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.ProviderBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderKeyBinding;
import com.google.inject.spi.UntargettedBinding;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface BindingTargetVisitor<T, V> {
    public V visit(InstanceBinding<? extends T> var1);

    public V visit(ProviderInstanceBinding<? extends T> var1);

    public V visit(ProviderKeyBinding<? extends T> var1);

    public V visit(LinkedKeyBinding<? extends T> var1);

    public V visit(ExposedBinding<? extends T> var1);

    public V visit(UntargettedBinding<? extends T> var1);

    public V visit(ConstructorBinding<? extends T> var1);

    public V visit(ConvertedConstantBinding<? extends T> var1);

    public V visit(ProviderBinding<? extends T> var1);
}

