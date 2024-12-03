/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.Errors;
import com.google.inject.internal.ErrorsException;
import com.google.inject.internal.Initializable;
import com.google.inject.internal.Initializables;
import com.google.inject.internal.InjectorImpl;
import com.google.inject.internal.MembersInjectorImpl;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Maps;
import com.google.inject.internal.util.$Preconditions;
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
    private final Map<Object, InjectableReference<?>> pendingInjection = $Maps.newIdentityHashMap();

    Initializer() {
    }

    <T> Initializable<T> requestInjection(InjectorImpl injector, T instance, Object source, Set<InjectionPoint> injectionPoints) {
        $Preconditions.checkNotNull(source);
        if (instance == null || injectionPoints.isEmpty() && !injector.membersInjectorStore.hasTypeListeners()) {
            return Initializables.of(instance);
        }
        InjectableReference<T> initializable = new InjectableReference<T>(injector, instance, source);
        this.pendingInjection.put(instance, initializable);
        return initializable;
    }

    void validateOustandingInjections(Errors errors) {
        for (InjectableReference<?> reference : this.pendingInjection.values()) {
            try {
                reference.validate(errors);
            }
            catch (ErrorsException e) {
                errors.merge(e.getErrors());
            }
        }
    }

    void injectAll(Errors errors) {
        for (InjectableReference<?> reference : $Lists.newArrayList(this.pendingInjection.values())) {
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
        private MembersInjectorImpl<T> membersInjector;

        public InjectableReference(InjectorImpl injector, T instance, Object source) {
            this.injector = injector;
            this.instance = $Preconditions.checkNotNull(instance, "instance");
            this.source = $Preconditions.checkNotNull(source, "source");
        }

        public void validate(Errors errors) throws ErrorsException {
            TypeLiteral<?> type = TypeLiteral.get(this.instance.getClass());
            this.membersInjector = this.injector.membersInjectorStore.get(type, errors.withSource(this.source));
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
                this.membersInjector.injectAndNotify(this.instance, errors.withSource(this.source), this.injector.options.stage == Stage.TOOL);
            }
            return this.instance;
        }

        public String toString() {
            return this.instance.toString();
        }
    }
}

