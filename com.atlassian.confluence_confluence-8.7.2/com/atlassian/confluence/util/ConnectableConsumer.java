/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 */
package com.atlassian.confluence.util;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class ConnectableConsumer<T>
implements Consumer<T> {
    private final Queue<T> queue = new LinkedList<T>();
    private Consumer<T> delegate = this::enqueue;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void enqueue(T t) {
        ConnectableConsumer connectableConsumer = this;
        synchronized (connectableConsumer) {
            this.queue.add(t);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void connect(Consumer<T> consumer) {
        Objects.requireNonNull(consumer);
        ConnectableConsumer connectableConsumer = this;
        synchronized (connectableConsumer) {
            while (!this.queue.isEmpty()) {
                consumer.accept(this.queue.poll());
            }
            this.delegate = consumer;
        }
    }

    @Override
    public void accept(T t) {
        this.delegate.accept(Objects.requireNonNull(t));
    }
}

