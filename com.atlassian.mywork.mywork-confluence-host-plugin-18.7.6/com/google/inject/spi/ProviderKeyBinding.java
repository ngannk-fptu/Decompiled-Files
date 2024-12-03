/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Binding;
import com.google.inject.Key;
import javax.inject.Provider;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ProviderKeyBinding<T>
extends Binding<T> {
    public Key<? extends Provider<? extends T>> getProviderKey();
}

