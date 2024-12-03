/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.GuardedBy
 *  javax.annotation.concurrent.ThreadSafe
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.audit.core;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
public class InMemoryBufferingConsumer<E> {
    private static final Logger log = LoggerFactory.getLogger(InMemoryBufferingConsumer.class);
    private final Supplier<Optional<Consumer<E>>> delegateConsumerSupplier;
    private final int bufferLimit;
    private final Consumer<E> fullBufferHandler;
    @GuardedBy(value="bufferLock")
    private final Queue<E> buffer;
    private final Object bufferLock = new Object();

    public InMemoryBufferingConsumer(Supplier<Optional<Consumer<E>>> delegateConsumerSupplier, int bufferLimit, Consumer<E> fullBufferHandler) {
        this.delegateConsumerSupplier = delegateConsumerSupplier;
        this.bufferLimit = bufferLimit;
        this.fullBufferHandler = fullBufferHandler;
        this.buffer = new LinkedList();
    }

    public void accept(E entity) {
        Optional<Consumer<E>> optionalDelegate = this.delegateConsumerSupplier.get();
        if (optionalDelegate.isPresent()) {
            this.tryFlushBuffer();
            log.trace("#accept dispatching entity {} to delegate consumer", entity);
            this.dispatch(entity, optionalDelegate.get());
        } else {
            this.bufferEntity(entity);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void tryFlushBuffer() {
        if (log.isTraceEnabled()) {
            log.trace("#tryFlushBuffer, buffer.size={}", (Object)this.buffer.size(), (Object)new StackCollector());
        }
        Object object = this.bufferLock;
        synchronized (object) {
            while (!this.buffer.isEmpty()) {
                Optional<Consumer<E>> optionalDelegate = this.delegateConsumerSupplier.get();
                if (!optionalDelegate.isPresent()) {
                    log.trace("#tryFlushBuffer delegate consumer is unavailable");
                    return;
                }
                E entity = this.buffer.remove();
                log.trace("#tryFlushBuffer dispatching buffered entity {} to delegate consumer", entity);
                this.dispatch(entity, optionalDelegate.get());
            }
        }
    }

    private void dispatch(E entity, Consumer<E> delegate) {
        delegate.accept(entity);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void bufferEntity(E entity) {
        Object object = this.bufferLock;
        synchronized (object) {
            while (this.buffer.size() >= this.bufferLimit) {
                E oldestElement = this.buffer.remove();
                log.trace("#bufferEntity dropped oldest entity={}, buffer.size={}, bufferLimit={}", new Object[]{oldestElement, this.buffer.size(), this.bufferLimit});
                this.fullBufferHandler.accept(oldestElement);
            }
            this.buffer.add(entity);
            log.trace("#bufferEntity added to buffer entity={}, buffer.size={}", entity, (Object)this.buffer.size());
        }
    }

    private static class StackCollector
    extends Throwable {
        private StackCollector() {
        }
    }
}

