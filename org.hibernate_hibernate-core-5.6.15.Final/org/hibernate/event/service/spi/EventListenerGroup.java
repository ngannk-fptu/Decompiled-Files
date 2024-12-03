/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.service.spi;

import java.io.Serializable;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.hibernate.Incubating;
import org.hibernate.event.service.spi.DuplicationStrategy;
import org.hibernate.event.service.spi.EventActionWithParameter;
import org.hibernate.event.spi.EventType;

public interface EventListenerGroup<T>
extends Serializable {
    public EventType<T> getEventType();

    public boolean isEmpty();

    public int count();

    @Deprecated
    public Iterable<T> listeners();

    public void addDuplicationStrategy(DuplicationStrategy var1);

    public void appendListener(T var1);

    public void appendListeners(T ... var1);

    public void prependListener(T var1);

    public void prependListeners(T ... var1);

    @Deprecated
    public void clear();

    public void clearListeners();

    @Incubating
    public <U> void fireLazyEventOnEachListener(Supplier<U> var1, BiConsumer<T, U> var2);

    @Incubating
    public <U> void fireEventOnEachListener(U var1, BiConsumer<T, U> var2);

    @Incubating
    public <U, X> void fireEventOnEachListener(U var1, X var2, EventActionWithParameter<T, U, X> var3);

    @Incubating
    public <R, U, RL> CompletionStage<R> fireEventOnEachListener(U var1, Function<RL, Function<U, CompletionStage<R>>> var2);

    @Incubating
    public <R, U, RL, X> CompletionStage<R> fireEventOnEachListener(U var1, X var2, Function<RL, BiFunction<U, X, CompletionStage<R>>> var3);

    @Incubating
    public <R, U, RL> CompletionStage<R> fireLazyEventOnEachListener(Supplier<U> var1, Function<RL, Function<U, CompletionStage<R>>> var2);
}

