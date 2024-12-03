/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 */
package com.google.inject.internal;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.internal.ProvisionListenerStackCallback;
import com.google.inject.spi.ProvisionListenerBinding;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class ProvisionListenerCallbackStore {
    private static final Set<Key<?>> INTERNAL_BINDINGS = ImmutableSet.of(Key.get(Injector.class), Key.get(Stage.class), Key.get(Logger.class));
    private final ImmutableList<ProvisionListenerBinding> listenerBindings;
    private final LoadingCache<KeyBinding, ProvisionListenerStackCallback<?>> cache = CacheBuilder.newBuilder().build(new CacheLoader<KeyBinding, ProvisionListenerStackCallback<?>>(){

        public ProvisionListenerStackCallback<?> load(KeyBinding key) {
            return ProvisionListenerCallbackStore.this.create(key.binding);
        }
    });

    ProvisionListenerCallbackStore(List<ProvisionListenerBinding> listenerBindings) {
        this.listenerBindings = ImmutableList.copyOf(listenerBindings);
    }

    public <T> ProvisionListenerStackCallback<T> get(Binding<T> binding) {
        if (!INTERNAL_BINDINGS.contains(binding.getKey())) {
            return (ProvisionListenerStackCallback)this.cache.getUnchecked((Object)new KeyBinding(binding.getKey(), binding));
        }
        return ProvisionListenerStackCallback.emptyListener();
    }

    boolean remove(Binding<?> type) {
        return this.cache.asMap().remove(type) != null;
    }

    private <T> ProvisionListenerStackCallback<T> create(Binding<T> binding) {
        List listeners = null;
        for (ProvisionListenerBinding provisionBinding : this.listenerBindings) {
            if (!provisionBinding.getBindingMatcher().matches(binding)) continue;
            if (listeners == null) {
                listeners = Lists.newArrayList();
            }
            listeners.addAll(provisionBinding.getListeners());
        }
        if (listeners == null || listeners.isEmpty()) {
            return ProvisionListenerStackCallback.emptyListener();
        }
        return new ProvisionListenerStackCallback<T>(binding, listeners);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class KeyBinding {
        final Key<?> key;
        final Binding<?> binding;

        KeyBinding(Key<?> key, Binding<?> binding) {
            this.key = key;
            this.binding = binding;
        }

        public boolean equals(Object obj) {
            return obj instanceof KeyBinding && this.key.equals(((KeyBinding)obj).key);
        }

        public int hashCode() {
            return this.key.hashCode();
        }
    }
}

