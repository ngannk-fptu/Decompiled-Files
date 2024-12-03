/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.Initializable;
import com.google.inject.internal.Initializables;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.MembersInjectorImpl;
import com.google.inject.internal.ProvisionListenerStackCallback;
import com.google.inject.spi.InjectionPoint;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class Initializer {
    private final Thread creatingThread = Thread.currentThread();
    private final CountDownLatch ready = new CountDownLatch(1);
    private final Map<Object, MembersInjectorImpl<?>> pendingMembersInjectors = Maps.newIdentityHashMap();
    private final Map<Object, InjectableReference<?>> pendingInjection = Maps.newIdentityHashMap();

    Initializer() {
    }

    <T> Initializable<T> requestInjection(InjectorImpl injector, T instance, Binding<T> binding, Object source, Set<InjectionPoint> injectionPoints) {
        ProvisionListenerStackCallback<T> provisionCallback;
        Preconditions.checkNotNull((Object)source);
        ProvisionListenerStackCallback<T> provisionListenerStackCallback = provisionCallback = binding == null ? null : injector.provisionListenerStore.get(binding);
        if (instance == null || injectionPoints.isEmpty() && !injector.membersInjectorStore.hasTypeListeners() && (provisionCallback == null || !provisionCallback.hasListeners())) {
            return Initializables.of(instance);
        }
        InjectableReference<T> initializable = new InjectableReference<T>(injector, instance, binding == null ? null : binding.getKey(), provisionCallback, source);
        this.pendingInjection.put(instance, initializable);
        return initializable;
    }

    void validateOustandingInjections(Errors errors) {
        for (InjectableReference<?> reference : this.pendingInjection.values()) {
            try {
                this.pendingMembersInjectors.put(((InjectableReference)reference).instance, reference.validate(errors));
            }
            catch (ErrorsException e) {
                errors.merge(e.getErrors());
            }
        }
    }

    void injectAll(Errors errors) {
        for (InjectableReference reference : Lists.newArrayList(this.pendingInjection.values())) {
            try {
                reference.get(errors);
            }
            catch (ErrorsException e) {
                errors.merge(e.getErrors());
            }
        }
        if (!this.pendingInjection.isEmpty()) {
            throw new AssertionError((Object)("Failed to satisfy " + this.pendingInjection));
        }
        this.ready.countDown();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private class InjectableReference<T>
    implements Initializable<T> {
        private final InjectorImpl injector;
        private final T instance;
        private final Object source;
        private final Key<T> key;
        private final ProvisionListenerStackCallback<T> provisionCallback;

        public InjectableReference(InjectorImpl injector, T instance, Key<T> key, ProvisionListenerStackCallback<T> provisionCallback, Object source) {
            this.injector = injector;
            this.key = key;
            this.provisionCallback = provisionCallback;
            this.instance = Preconditions.checkNotNull(instance, (Object)"instance");
            this.source = Preconditions.checkNotNull((Object)source, (Object)"source");
        }

        public MembersInjectorImpl<T> validate(Errors errors) throws ErrorsException {
            TypeLiteral<?> type = TypeLiteral.get(this.instance.getClass());
            return this.injector.membersInjectorStore.get(type, errors.withSource(this.source));
        }

        @Override
        public T get(Errors errors) throws ErrorsException {
            if (Initializer.this.ready.getCount() == 0L) {
                return this.instance;
            }
            if (Thread.currentThread() != Initializer.this.creatingThread) {
                try {
                    Initializer.this.ready.await();
                    return this.instance;
                }
                catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            if (Initializer.this.pendingInjection.remove(this.instance) != null) {
                MembersInjectorImpl membersInjector = (MembersInjectorImpl)Initializer.this.pendingMembersInjectors.remove(this.instance);
                Preconditions.checkState((membersInjector != null ? 1 : 0) != 0, (String)"No membersInjector available for instance: %s, from key: %s", (Object[])new Object[]{this.instance, this.key});
                membersInjector.injectAndNotify(this.instance, errors.withSource(this.source), this.key, this.provisionCallback, this.source, this.injector.options.stage == Stage.TOOL);
            }
            return this.instance;
        }

        public String toString() {
            return this.instance.toString();
        }
    }
}

