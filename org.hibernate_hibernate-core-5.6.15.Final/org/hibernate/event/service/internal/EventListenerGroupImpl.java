/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.event.service.internal;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.hibernate.event.service.spi.DuplicationStrategy;
import org.hibernate.event.service.spi.EventActionWithParameter;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistrationException;
import org.hibernate.event.service.spi.JpaBootstrapSensitive;
import org.hibernate.event.spi.EventType;
import org.hibernate.jpa.event.spi.CallbackRegistry;
import org.hibernate.jpa.event.spi.CallbackRegistryConsumer;
import org.jboss.logging.Logger;

class EventListenerGroupImpl<T>
implements EventListenerGroup<T> {
    private static final Logger log = Logger.getLogger(EventListenerGroupImpl.class);
    private static final Set<DuplicationStrategy> DEFAULT_DUPLICATION_STRATEGIES = Collections.unmodifiableSet(EventListenerGroupImpl.makeDefaultDuplicationStrategy());
    private static final CompletableFuture COMPLETED = CompletableFuture.completedFuture(null);
    private final EventType<T> eventType;
    private final CallbackRegistry callbackRegistry;
    private final boolean isJpaBootstrap;
    private Set<DuplicationStrategy> duplicationStrategies = DEFAULT_DUPLICATION_STRATEGIES;
    private T[] listeners = null;

    public EventListenerGroupImpl(EventType<T> eventType, CallbackRegistry callbackRegistry, boolean isJpaBootstrap) {
        this.eventType = eventType;
        this.callbackRegistry = callbackRegistry;
        this.isJpaBootstrap = isJpaBootstrap;
    }

    @Override
    public EventType<T> getEventType() {
        return this.eventType;
    }

    @Override
    public boolean isEmpty() {
        return this.count() <= 0;
    }

    @Override
    public int count() {
        T[] ls = this.listeners;
        return ls == null ? 0 : ls.length;
    }

    @Override
    public void clear() {
        this.duplicationStrategies = new LinkedHashSet<DuplicationStrategy>();
        this.listeners = null;
    }

    @Override
    public void clearListeners() {
        this.listeners = null;
    }

    @Override
    public final <U> void fireLazyEventOnEachListener(Supplier<U> eventSupplier, BiConsumer<T, U> actionOnEvent) {
        T[] ls = this.listeners;
        if (ls != null && ls.length != 0) {
            U event = eventSupplier.get();
            for (int i = 0; i < ls.length; ++i) {
                actionOnEvent.accept(ls[i], event);
            }
        }
    }

    @Override
    public final <U> void fireEventOnEachListener(U event, BiConsumer<T, U> actionOnEvent) {
        T[] ls = this.listeners;
        if (ls != null) {
            for (int i = 0; i < ls.length; ++i) {
                actionOnEvent.accept(ls[i], event);
            }
        }
    }

    @Override
    public <U, X> void fireEventOnEachListener(U event, X parameter, EventActionWithParameter<T, U, X> actionOnEvent) {
        T[] ls = this.listeners;
        if (ls != null) {
            for (int i = 0; i < ls.length; ++i) {
                actionOnEvent.applyEventToListener(ls[i], event, parameter);
            }
        }
    }

    @Override
    public <R, U, RL> CompletionStage<R> fireEventOnEachListener(U event, Function<RL, Function<U, CompletionStage<R>>> fun) {
        CompletionStage<Object> ret = COMPLETED;
        T[] ls = this.listeners;
        if (ls != null && ls.length != 0) {
            for (Object listener : ls) {
                ret = ret.thenCompose(v -> (CompletionStage)((Function)fun.apply(listener)).apply(event));
            }
        }
        return ret;
    }

    @Override
    public <R, U, RL, X> CompletionStage<R> fireEventOnEachListener(U event, X param, Function<RL, BiFunction<U, X, CompletionStage<R>>> fun) {
        CompletionStage<Object> ret = COMPLETED;
        T[] ls = this.listeners;
        if (ls != null && ls.length != 0) {
            for (Object listener : ls) {
                ret = ret.thenCompose(v -> (CompletionStage)((BiFunction)fun.apply(listener)).apply(event, param));
            }
        }
        return ret;
    }

    @Override
    public <R, U, RL> CompletionStage<R> fireLazyEventOnEachListener(Supplier<U> eventSupplier, Function<RL, Function<U, CompletionStage<R>>> fun) {
        CompletionStage<Object> ret = COMPLETED;
        T[] ls = this.listeners;
        if (ls != null && ls.length != 0) {
            Object event = eventSupplier.get();
            for (Object listener : ls) {
                ret = ret.thenCompose(v -> (CompletionStage)((Function)fun.apply(listener)).apply(event));
            }
        }
        return ret;
    }

    @Override
    public void addDuplicationStrategy(DuplicationStrategy strategy) {
        if (this.duplicationStrategies == DEFAULT_DUPLICATION_STRATEGIES) {
            this.duplicationStrategies = EventListenerGroupImpl.makeDefaultDuplicationStrategy();
        }
        this.duplicationStrategies.add(strategy);
    }

    @Override
    public void appendListener(T listener) {
        this.handleListenerAddition(listener, this::internalAppend);
    }

    @Override
    @SafeVarargs
    public final void appendListeners(T ... listeners) {
        for (int i = 0; i < listeners.length; ++i) {
            this.handleListenerAddition(listeners[i], this::internalAppend);
        }
    }

    private void internalAppend(T listener) {
        this.prepareListener(listener);
        if (this.listeners == null) {
            this.listeners = (Object[])Array.newInstance(this.eventType.baseListenerInterface(), 1);
            this.listeners[0] = listener;
        } else {
            int size = this.listeners.length;
            Object[] newCopy = (Object[])Array.newInstance(this.eventType.baseListenerInterface(), size + 1);
            System.arraycopy(this.listeners, 0, newCopy, 0, size);
            newCopy[size] = listener;
            this.listeners = newCopy;
        }
    }

    @Override
    public void prependListener(T listener) {
        this.handleListenerAddition(listener, this::internalPrepend);
    }

    @Override
    @SafeVarargs
    public final void prependListeners(T ... listeners) {
        for (int i = 0; i < listeners.length; ++i) {
            this.handleListenerAddition(listeners[i], this::internalPrepend);
        }
    }

    private void internalPrepend(T listener) {
        this.prepareListener(listener);
        if (this.listeners == null) {
            this.listeners = (Object[])Array.newInstance(this.eventType.baseListenerInterface(), 1);
            this.listeners[0] = listener;
        } else {
            int size = this.listeners.length;
            Object[] newCopy = (Object[])Array.newInstance(this.eventType.baseListenerInterface(), size + 1);
            newCopy[0] = listener;
            System.arraycopy(this.listeners, 0, newCopy, 1, size);
            this.listeners = newCopy;
        }
    }

    private void handleListenerAddition(T listener, Consumer<T> additionHandler) {
        if (this.listeners == null) {
            additionHandler.accept(listener);
            return;
        }
        T[] localListenersRef = this.listeners;
        boolean debugEnabled = log.isDebugEnabled();
        for (DuplicationStrategy strategy : this.duplicationStrategies) {
            for (int i = 0; i < localListenersRef.length; ++i) {
                T existingListener = localListenersRef[i];
                if (debugEnabled) {
                    log.debugf("Checking incoming listener [`%s`] for match against existing listener [`%s`]", listener, existingListener);
                }
                if (!strategy.areMatch(listener, existingListener)) continue;
                if (debugEnabled) {
                    log.debugf("Found listener match between `%s` and `%s`", listener, existingListener);
                }
                switch (strategy.getAction()) {
                    case ERROR: {
                        throw new EventListenerRegistrationException("Duplicate event listener found");
                    }
                    case KEEP_ORIGINAL: {
                        if (debugEnabled) {
                            log.debugf("Skipping listener registration (%s) : `%s`", (Object)strategy.getAction(), listener);
                        }
                        return;
                    }
                    case REPLACE_ORIGINAL: {
                        if (debugEnabled) {
                            log.debugf("Replacing listener registration (%s) : `%s` -> %s", (Object)strategy.getAction(), existingListener, listener);
                        }
                        this.prepareListener(listener);
                        this.listeners[i] = listener;
                    }
                }
                return;
            }
        }
        this.checkAgainstBaseInterface(listener);
        this.performInjections(listener);
        additionHandler.accept(listener);
    }

    private void prepareListener(T listener) {
        this.checkAgainstBaseInterface(listener);
        this.performInjections(listener);
    }

    private void performInjections(T listener) {
        if (listener instanceof CallbackRegistryConsumer) {
            ((CallbackRegistryConsumer)listener).injectCallbackRegistry(this.callbackRegistry);
        }
        if (listener instanceof JpaBootstrapSensitive) {
            ((JpaBootstrapSensitive)listener).wasJpaBootstrap(this.isJpaBootstrap);
        }
    }

    private void checkAgainstBaseInterface(T listener) {
        if (!this.eventType.baseListenerInterface().isInstance(listener)) {
            throw new EventListenerRegistrationException("Listener did not implement expected interface [" + this.eventType.baseListenerInterface().getName() + "]");
        }
    }

    @Override
    @Deprecated
    public final Iterable<T> listeners() {
        if (this.listeners == null) {
            return Collections.EMPTY_LIST;
        }
        return Arrays.asList(this.listeners);
    }

    private static Set<DuplicationStrategy> makeDefaultDuplicationStrategy() {
        LinkedHashSet<DuplicationStrategy> duplicationStrategies = new LinkedHashSet<DuplicationStrategy>();
        duplicationStrategies.add(new DuplicationStrategy(){

            @Override
            public boolean areMatch(Object listener, Object original) {
                return listener.getClass().equals(original.getClass());
            }

            @Override
            public DuplicationStrategy.Action getAction() {
                return DuplicationStrategy.Action.ERROR;
            }
        });
        return duplicationStrategies;
    }
}

