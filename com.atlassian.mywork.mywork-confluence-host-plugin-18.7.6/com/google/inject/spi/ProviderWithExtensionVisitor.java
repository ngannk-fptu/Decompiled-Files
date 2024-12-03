/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Provider;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ProviderInstanceBinding;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ProviderWithExtensionVisitor<T>
extends Provider<T> {
    public <B, V> V acceptExtensionVisitor(BindingTargetVisitor<B, V> var1, ProviderInstanceBinding<? extends B> var2);
}

