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
import com.google.inject.internal.util.$ImmutableList;
import com.google.inject.internal.util.$ImmutableMap;
import com.google.inject.internal.util.$ImmutableSet;
import com.google.inject.spi.TypeConverterBinding;
import com.google.inject.spi.TypeListenerBinding;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
interface State {
    public static final State NONE = new State(){

        @Override
        public State parent() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> BindingImpl<T> getExplicitBinding(Key<T> key) {
            return null;
        }

        @Override
        public Map<Key<?>, Binding<?>> getExplicitBindingsThisLevel() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putBinding(Key<?> key, BindingImpl<?> binding) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Scope getScope(Class<? extends Annotation> scopingAnnotation) {
            return null;
        }

        @Override
        public void putAnnotation(Class<? extends Annotation> annotationType, Scope scope) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addConverter(TypeConverterBinding typeConverterBinding) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TypeConverterBinding getConverter(String stringValue, TypeLiteral<?> type, Errors errors, Object source) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterable<TypeConverterBinding> getConvertersThisLevel() {
            return $ImmutableSet.of();
        }

        @Override
        public void addMethodAspect(MethodAspect methodAspect) {
            throw new UnsupportedOperationException();
        }

        @Override
        public $ImmutableList<MethodAspect> getMethodAspects() {
            return $ImmutableList.of();
        }

        @Override
        public void addTypeListener(TypeListenerBinding typeListenerBinding) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<TypeListenerBinding> getTypeListenerBindings() {
            return $ImmutableList.of();
        }

        @Override
        public void blacklist(Key<?> key, Object source) {
        }

        @Override
        public boolean isBlacklisted(Key<?> key) {
            return true;
        }

        @Override
        public Set<Object> getSourcesForBlacklistedKey(Key<?> key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object lock() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<Class<? extends Annotation>, Scope> getScopes() {
            return $ImmutableMap.of();
        }
    };

    public State parent();

    public <T> BindingImpl<T> getExplicitBinding(Key<T> var1);

    public Map<Key<?>, Binding<?>> getExplicitBindingsThisLevel();

    public void putBinding(Key<?> var1, BindingImpl<?> var2);

    public Scope getScope(Class<? extends Annotation> var1);

    public void putAnnotation(Class<? extends Annotation> var1, Scope var2);

    public void addConverter(TypeConverterBinding var1);

    public TypeConverterBinding getConverter(String var1, TypeLiteral<?> var2, Errors var3, Object var4);

    public Iterable<TypeConverterBinding> getConvertersThisLevel();

    public void addMethodAspect(MethodAspect var1);

    public $ImmutableList<MethodAspect> getMethodAspects();

    public void addTypeListener(TypeListenerBinding var1);

    public List<TypeListenerBinding> getTypeListenerBindings();

    public void blacklist(Key<?> var1, Object var2);

    public boolean isBlacklisted(Key<?> var1);

    public Set<Object> getSourcesForBlacklistedKey(Key<?> var1);

    public Object lock();

    public Map<Class<? extends Annotation>, Scope> getScopes();
}

