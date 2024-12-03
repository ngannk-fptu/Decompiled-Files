/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Binding;
import com.google.inject.spi.DisableCircularProxiesOption;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.InterceptorBinding;
import com.google.inject.spi.MembersInjectorLookup;
import com.google.inject.spi.Message;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ProviderLookup;
import com.google.inject.spi.ProvisionListenerBinding;
import com.google.inject.spi.RequireAtInjectOnConstructorsOption;
import com.google.inject.spi.RequireExactBindingAnnotationsOption;
import com.google.inject.spi.RequireExplicitBindingsOption;
import com.google.inject.spi.ScopeBinding;
import com.google.inject.spi.StaticInjectionRequest;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.spi.TypeListenerBinding;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class DefaultElementVisitor<V>
implements ElementVisitor<V> {
    protected V visitOther(Element element) {
        return null;
    }

    @Override
    public V visit(Message message) {
        return this.visitOther(message);
    }

    @Override
    public <T> V visit(Binding<T> binding) {
        return this.visitOther(binding);
    }

    @Override
    public V visit(InterceptorBinding interceptorBinding) {
        return this.visitOther(interceptorBinding);
    }

    @Override
    public V visit(ScopeBinding scopeBinding) {
        return this.visitOther(scopeBinding);
    }

    @Override
    public V visit(TypeConverterBinding typeConverterBinding) {
        return this.visitOther(typeConverterBinding);
    }

    @Override
    public <T> V visit(ProviderLookup<T> providerLookup) {
        return this.visitOther(providerLookup);
    }

    @Override
    public V visit(InjectionRequest<?> injectionRequest) {
        return this.visitOther(injectionRequest);
    }

    @Override
    public V visit(StaticInjectionRequest staticInjectionRequest) {
        return this.visitOther(staticInjectionRequest);
    }

    @Override
    public V visit(PrivateElements privateElements) {
        return this.visitOther(privateElements);
    }

    @Override
    public <T> V visit(MembersInjectorLookup<T> lookup) {
        return this.visitOther(lookup);
    }

    @Override
    public V visit(TypeListenerBinding binding) {
        return this.visitOther(binding);
    }

    @Override
    public V visit(ProvisionListenerBinding binding) {
        return this.visitOther(binding);
    }

    @Override
    public V visit(DisableCircularProxiesOption option) {
        return this.visitOther(option);
    }

    @Override
    public V visit(RequireExplicitBindingsOption option) {
        return this.visitOther(option);
    }

    @Override
    public V visit(RequireAtInjectOnConstructorsOption option) {
        return this.visitOther(option);
    }

    @Override
    public V visit(RequireExactBindingAnnotationsOption option) {
        return this.visitOther(option);
    }
}

