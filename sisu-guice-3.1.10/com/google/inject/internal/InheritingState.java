/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.Errors;
import com.google.inject.internal.MethodAspect;
import com.google.inject.internal.State;
import com.google.inject.internal.WeakKeySet;
import com.google.inject.spi.ProvisionListenerBinding;
import com.google.inject.spi.ScopeBinding;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.spi.TypeListenerBinding;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class InheritingState
implements State {
    private final State parent;
    private final Map<Key<?>, Binding<?>> explicitBindingsMutable = Maps.newLinkedHashMap();
    private final Map<Key<?>, Binding<?>> explicitBindings = Collections.unmodifiableMap(this.explicitBindingsMutable);
    private final Map<Class<? extends Annotation>, ScopeBinding> scopes = Maps.newHashMap();
    private final List<TypeConverterBinding> converters = Lists.newArrayList();
    private final List<MethodAspect> methodAspects = Lists.newArrayList();
    private final List<TypeListenerBinding> typeListenerBindings = Lists.newArrayList();
    private final List<ProvisionListenerBinding> provisionListenerBindings = Lists.newArrayList();
    private final WeakKeySet blacklistedKeys = new WeakKeySet();
    private final Object lock;

    InheritingState(State parent) {
        this.parent = (State)Preconditions.checkNotNull((Object)parent, (Object)"parent");
        this.lock = parent == State.NONE ? this : parent.lock();
    }

    @Override
    public State parent() {
        return this.parent;
    }

    @Override
    public <T> BindingImpl<T> getExplicitBinding(Key<T> key) {
        Binding<?> binding = this.explicitBindings.get(key);
        return binding != null ? (BindingImpl<T>)binding : this.parent.getExplicitBinding(key);
    }

    @Override
    public Map<Key<?>, Binding<?>> getExplicitBindingsThisLevel() {
        return this.explicitBindings;
    }

    @Override
    public void putBinding(Key<?> key, BindingImpl<?> binding) {
        this.explicitBindingsMutable.put(key, binding);
    }

    @Override
    public ScopeBinding getScopeBinding(Class<? extends Annotation> annotationType) {
        ScopeBinding scopeBinding = this.scopes.get(annotationType);
        return scopeBinding != null ? scopeBinding : this.parent.getScopeBinding(annotationType);
    }

    @Override
    public void putScopeBinding(Class<? extends Annotation> annotationType, ScopeBinding scope) {
        this.scopes.put(annotationType, scope);
    }

    @Override
    public Iterable<TypeConverterBinding> getConvertersThisLevel() {
        return this.converters;
    }

    @Override
    public void addConverter(TypeConverterBinding typeConverterBinding) {
        this.converters.add(typeConverterBinding);
    }

    @Override
    public TypeConverterBinding getConverter(String stringValue, TypeLiteral<?> type, Errors errors, Object source) {
        TypeConverterBinding matchingConverter = null;
        for (State s = this; s != State.NONE; s = s.parent()) {
            for (TypeConverterBinding converter : s.getConvertersThisLevel()) {
                if (!converter.getTypeMatcher().matches(type)) continue;
                if (matchingConverter != null) {
                    errors.ambiguousTypeConversion(stringValue, source, type, matchingConverter, converter);
                }
                matchingConverter = converter;
            }
        }
        return matchingConverter;
    }

    @Override
    public void addMethodAspect(MethodAspect methodAspect) {
        this.methodAspects.add(methodAspect);
    }

    @Override
    public ImmutableList<MethodAspect> getMethodAspects() {
        return new ImmutableList.Builder().addAll(this.parent.getMethodAspects()).addAll(this.methodAspects).build();
    }

    @Override
    public void addTypeListener(TypeListenerBinding listenerBinding) {
        this.typeListenerBindings.add(listenerBinding);
    }

    @Override
    public List<TypeListenerBinding> getTypeListenerBindings() {
        List<TypeListenerBinding> parentBindings = this.parent.getTypeListenerBindings();
        ArrayList<TypeListenerBinding> result = new ArrayList<TypeListenerBinding>(parentBindings.size() + 1);
        result.addAll(parentBindings);
        result.addAll(this.typeListenerBindings);
        return result;
    }

    @Override
    public void addProvisionListener(ProvisionListenerBinding listenerBinding) {
        this.provisionListenerBindings.add(listenerBinding);
    }

    @Override
    public List<ProvisionListenerBinding> getProvisionListenerBindings() {
        List<ProvisionListenerBinding> parentBindings = this.parent.getProvisionListenerBindings();
        ArrayList<ProvisionListenerBinding> result = new ArrayList<ProvisionListenerBinding>(parentBindings.size() + 1);
        result.addAll(parentBindings);
        result.addAll(this.provisionListenerBindings);
        return result;
    }

    @Override
    public void blacklist(Key<?> key, Object source) {
        this.parent.blacklist(key, source);
        this.blacklistedKeys.add(key, source);
    }

    @Override
    public boolean isBlacklisted(Key<?> key) {
        return this.blacklistedKeys.contains(key);
    }

    @Override
    public Set<Object> getSourcesForBlacklistedKey(Key<?> key) {
        return this.blacklistedKeys.getSources(key);
    }

    @Override
    public Object lock() {
        return this.lock;
    }

    @Override
    public Map<Class<? extends Annotation>, Scope> getScopes() {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (Map.Entry<Class<? extends Annotation>, ScopeBinding> entry : this.scopes.entrySet()) {
            builder.put(entry.getKey(), (Object)entry.getValue().getScope());
        }
        return builder.build();
    }
}

