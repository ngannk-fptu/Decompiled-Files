/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 *  com.google.inject.Key
 *  com.google.inject.OutOfScopeException
 *  com.google.inject.Provider
 *  com.google.inject.Scope
 */
package com.google.template.soy.shared.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;
import java.util.Map;
import java.util.Stack;

public class GuiceSimpleScope
implements Scope {
    private static final Provider<Object> UNSCOPED_PROVIDER = new Provider<Object>(){

        public Object get() {
            throw new IllegalStateException("If you got here then it means that your code asked for scoped object which should have been explicitly seeded in this scope by calling GuiceSimpleScope.seed(), but was not.");
        }
    };
    private final ThreadLocal<Stack<Map<Key<?>, Object>>> scopedValuesTl = new ThreadLocal();

    public static <T> Provider<T> getUnscopedProvider() {
        return UNSCOPED_PROVIDER;
    }

    public void enter() {
        Stack<Map<Object, Object>> stack = this.scopedValuesTl.get();
        if (stack == null) {
            stack = new Stack();
            this.scopedValuesTl.set(stack);
        }
        stack.push(Maps.newHashMap());
    }

    public void exit() {
        Preconditions.checkState((boolean)this.isActive(), (Object)"No scoping block in progress");
        Stack<Map<Key<?>, Object>> stack = this.scopedValuesTl.get();
        stack.pop();
        if (stack.isEmpty()) {
            this.scopedValuesTl.remove();
        }
    }

    public boolean isActive() {
        Stack<Map<Key<?>, Object>> stack = this.scopedValuesTl.get();
        return stack != null && !stack.isEmpty();
    }

    public <T> void seed(Key<T> key, T value) {
        Map<Key<?>, Object> scopedObjects = this.getScopedValues(key);
        Preconditions.checkState((!scopedObjects.containsKey(key) ? 1 : 0) != 0, (String)"A value for the key %s was already seeded in this scope. Old value: %s New value: %s", key, (Object)scopedObjects.get(key), value);
        scopedObjects.put(key, value);
    }

    public <T> void seed(Class<T> class0, T value) {
        this.seed(Key.get(class0), value);
    }

    public <T> T getForTesting(Key<T> key) {
        Map<Key<?>, Object> scopedValues = this.getScopedValues(key);
        Object value = scopedValues.get(key);
        if (value == null && !scopedValues.containsKey(key)) {
            throw new IllegalStateException("The key " + key + " has not been seeded in this scope");
        }
        return (T)value;
    }

    public <T> T getForTesting(Class<T> class0) {
        return this.getForTesting(Key.get(class0));
    }

    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscopedProvider) {
        return new Provider<T>(){

            public T get() {
                Map scopedValues = GuiceSimpleScope.this.getScopedValues(key);
                Object value = scopedValues.get(key);
                if (value == null && !scopedValues.containsKey(key)) {
                    value = unscopedProvider.get();
                    scopedValues.put(key, value);
                }
                return value;
            }
        };
    }

    private <T> Map<Key<?>, Object> getScopedValues(Key<T> key) {
        if (!this.isActive()) {
            throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");
        }
        return this.scopedValuesTl.get().peek();
    }
}

