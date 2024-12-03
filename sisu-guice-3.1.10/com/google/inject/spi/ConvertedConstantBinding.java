/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.TypeConverterBinding;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ConvertedConstantBinding<T>
extends Binding<T>,
HasDependencies {
    public T getValue();

    public TypeConverterBinding getTypeConverterBinding();

    public Key<String> getSourceKey();

    @Override
    public Set<Dependency<?>> getDependencies();
}

