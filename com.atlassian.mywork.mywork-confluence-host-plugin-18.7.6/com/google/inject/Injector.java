/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverterBinding;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Injector {
    public void injectMembers(Object var1);

    public <T> MembersInjector<T> getMembersInjector(TypeLiteral<T> var1);

    public <T> MembersInjector<T> getMembersInjector(Class<T> var1);

    public Map<Key<?>, Binding<?>> getBindings();

    public Map<Key<?>, Binding<?>> getAllBindings();

    public <T> Binding<T> getBinding(Key<T> var1);

    public <T> Binding<T> getBinding(Class<T> var1);

    public <T> Binding<T> getExistingBinding(Key<T> var1);

    public <T> List<Binding<T>> findBindingsByType(TypeLiteral<T> var1);

    public <T> Provider<T> getProvider(Key<T> var1);

    public <T> Provider<T> getProvider(Class<T> var1);

    public <T> T getInstance(Key<T> var1);

    public <T> T getInstance(Class<T> var1);

    public Injector getParent();

    public Injector createChildInjector(Iterable<? extends Module> var1);

    public Injector createChildInjector(Module ... var1);

    public Map<Class<? extends Annotation>, Scope> getScopeBindings();

    public Set<TypeConverterBinding> getTypeConverterBindings();
}

