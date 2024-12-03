/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.BindingImpl;
import com.google.inject.internal.Errors;
import com.google.inject.internal.MethodAspect;
import com.google.inject.internal.State;
import com.google.inject.internal.WeakKeySet;
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Maps;
import com.google.inject.internal.util.$Preconditions;
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
    private final Map<Key<?>, Binding<?>> explicitBindingsMutable = $Maps.newLinkedHashMap();
    private final Map<Key<?>, Binding<?>> explicitBindings = Collections.unmodifiableMap(this.explicitBindingsMutable);
    private final Map<Class<? extends Annotation>, Scope> scopes = $Maps.newHashMap();
    private final List<TypeConverterBinding> converters = $Lists.newArrayList();
    private final List<MethodAspect> methodAspects = $Lists.newArrayList();
    private final List<TypeListenerBinding> listenerBindings = $Lists.newArrayList();
    private final WeakKeySet blacklistedKeys = new WeakKeySet();
    private final Object lock;

    InheritingState(State parent) {
        this.parent = $Preconditions.checkNotNull(parent, "parent");
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
    public Scope getScope(Class<? extends Annotation> annotationType) {
        Scope scope = this.scopes.get(annotationType);
        return scope != null ? scope : this.parent.getScope(annotationType);
    }

    @Override
    public void putAnnotation(Class<? extends Annotation> annotationType, Scope scope) {
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
    public $ImmutableList<MethodAspect> getMethodAspects() {
        return new $ImmutableList.Builder<MethodAspect>().addAll(this.parent.getMethodAspects()).addAll(this.methodAspects).build();
    }

    @Override
    public void addTypeListener(TypeListenerBinding listenerBinding) {
        this.listenerBindings.add(listenerBinding);
    }

    @Override
    public List<TypeListenerBinding> getTypeListenerBindings() {
        List<TypeListenerBinding> parentBindings = this.parent.getTypeListenerBindings();
        ArrayList<TypeListenerBinding> result = new ArrayList<TypeListenerBinding>(parentBindings.size() + 1);
        result.addAll(parentBindings);
        result.addAll(this.listenerBindings);
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
        return this.scopes;
    }
}

