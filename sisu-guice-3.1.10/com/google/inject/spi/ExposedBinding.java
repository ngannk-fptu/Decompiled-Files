/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.PrivateElements;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ExposedBinding<T>
extends Binding<T>,
HasDependencies {
    public PrivateElements getPrivateElements();

    @Override
    public void applyTo(Binder var1);
}

