/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Binding;
import com.google.inject.spi.BindingTargetVisitor;
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
public abstract class DefaultBindingTargetVisitor<T, V>
implements BindingTargetVisitor<T, V> {
    protected V visitOther(Binding<? extends T> binding) {
        return null;
    }

    @Override
    public V visit(InstanceBinding<? extends T> instanceBinding) {
        return this.visitOther(instanceBinding);
    }

    @Override
    public V visit(ProviderInstanceBinding<? extends T> providerInstanceBinding) {
        return this.visitOther(providerInstanceBinding);
    }

    @Override
    public V visit(ProviderKeyBinding<? extends T> providerKeyBinding) {
        return this.visitOther(providerKeyBinding);
    }

    @Override
    public V visit(LinkedKeyBinding<? extends T> linkedKeyBinding) {
        return this.visitOther(linkedKeyBinding);
    }

    @Override
    public V visit(ExposedBinding<? extends T> exposedBinding) {
        return this.visitOther(exposedBinding);
    }

    @Override
    public V visit(UntargettedBinding<? extends T> untargettedBinding) {
        return this.visitOther(untargettedBinding);
    }

    @Override
    public V visit(ConstructorBinding<? extends T> constructorBinding) {
        return this.visitOther(constructorBinding);
    }

    @Override
    public V visit(ConvertedConstantBinding<? extends T> convertedConstantBinding) {
        return this.visitOther(convertedConstantBinding);
    }

    @Override
    public V visit(ProviderBinding<? extends T> providerBinding) {
        return this.visitOther(providerBinding);
    }
}

