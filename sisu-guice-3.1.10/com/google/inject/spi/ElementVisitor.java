/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Binding;
import com.google.inject.spi.DisableCircularProxiesOption;
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
public interface ElementVisitor<V> {
    public <T> V visit(Binding<T> var1);

    public V visit(InterceptorBinding var1);

    public V visit(ScopeBinding var1);

    public V visit(TypeConverterBinding var1);

    public V visit(InjectionRequest<?> var1);

    public V visit(StaticInjectionRequest var1);

    public <T> V visit(ProviderLookup<T> var1);

    public <T> V visit(MembersInjectorLookup<T> var1);

    public V visit(Message var1);

    public V visit(PrivateElements var1);

    public V visit(TypeListenerBinding var1);

    public V visit(ProvisionListenerBinding var1);

    public V visit(RequireExplicitBindingsOption var1);

    public V visit(DisableCircularProxiesOption var1);

    public V visit(RequireAtInjectOnConstructorsOption var1);

    public V visit(RequireExactBindingAnnotationsOption var1);
}

