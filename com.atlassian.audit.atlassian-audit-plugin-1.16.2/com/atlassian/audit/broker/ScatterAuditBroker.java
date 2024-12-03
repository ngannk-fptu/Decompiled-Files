/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditConsumer
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.ThreadSafe
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.audit.broker;

import com.atlassian.audit.api.AuditConsumer;
import com.atlassian.audit.broker.AuditConsumerExceptionHandler;
import com.atlassian.audit.broker.AuditEntityRejectionHandler;
import com.atlassian.audit.broker.AuditPolicy;
import com.atlassian.audit.broker.InternalAuditBroker;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.event.AuditConsumerAddedEvent;
import com.atlassian.audit.event.AuditConsumerRemovedEvent;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@ThreadSafe
public class ScatterAuditBroker
implements InternalAuditBroker,
InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(ScatterAuditBroker.class);
    private final EventPublisher eventPublisher;
    private final AuditPolicy auditPolicy;
    private final AuditEntityRejectionHandler rejectAuditEntityHandler;
    private final AuditConsumerExceptionHandler exceptionHandler;
    private final ConcurrentHashMap<AuditConsumer, ConsumerRegistration> consumerRegistry;
    private final int defaultConsumerBufferSize;
    private final int defaultConsumerBatchSize;

    public ScatterAuditBroker(EventPublisher eventPublisher, AuditPolicy auditPolicy, AuditEntityRejectionHandler rejectionHandler, AuditConsumerExceptionHandler exceptionHandler, int defaultConsumerBufferSize, int defaultConsumerBatchSize) {
        this.eventPublisher = eventPublisher;
        this.defaultConsumerBatchSize = defaultConsumerBatchSize;
        this.defaultConsumerBufferSize = defaultConsumerBufferSize;
        this.auditPolicy = Objects.requireNonNull(auditPolicy);
        this.rejectAuditEntityHandler = Objects.requireNonNull(rejectionHandler);
        this.exceptionHandler = Objects.requireNonNull(exceptionHandler);
        this.consumerRegistry = new ConcurrentHashMap();
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.shutdown();
    }

    public synchronized void shutdown() {
        this.consumerRegistry.values().forEach(x -> x.getThread().shutdown());
        this.waitForTermination();
    }

    public synchronized void shutdownNow() {
        this.consumerRegistry.values().forEach(x -> x.getThread().shutdownNow());
        this.waitForTermination();
    }

    @EventListener
    public void onAuditConsumerAdded(AuditConsumerAddedEvent event) {
        this.addConsumer(event.getConsumerService(), this.defaultConsumerBufferSize, this.defaultConsumerBatchSize);
    }

    @EventListener
    public void onAuditConsumerRemoved(AuditConsumerRemovedEvent event) {
        this.removeConsumer(event.getConsumerService(), false);
    }

    public void addConsumer(AuditConsumer consumer, int bufferSize, int batchSize) {
        ConsumerQueue queue = new ConsumerQueue(consumer, new ArrayBlockingQueue<AuditEntity>(bufferSize), batchSize, entity -> this.rejectAuditEntityHandler.reject(this, consumer, (List<AuditEntity>)entity));
        ConsumerThread thread = new ConsumerThread(queue, consumer, (exception, batch) -> this.exceptionHandler.handle(consumer, (RuntimeException)exception, (List<AuditEntity>)batch));
        log.trace("#addConsumer consumer={}, bufferSize={}, batchSize={}, thread={}", new Object[]{consumer, bufferSize, batchSize, thread.getName()});
        this.consumerRegistry.put(consumer, new ConsumerRegistration(queue, thread));
        thread.start();
    }

    public void removeConsumer(AuditConsumer consumer, boolean force) {
        ConsumerRegistration registration = this.consumerRegistry.remove(consumer);
        log.trace("#removeConsumer consumer={}, force={}, registration={}", new Object[]{consumer, force, registration});
        if (registration != null) {
            if (force) {
                registration.getThread().shutdownNow();
            } else {
                registration.getThread().shutdown();
            }
        }
    }

    @Override
    public void audit(@Nonnull AuditEntity entity) {
        Objects.requireNonNull(entity, "entity");
        if (this.auditPolicy.pass(entity)) {
            if (log.isTraceEnabled()) {
                Map<AuditConsumer, Boolean> enabledConsumers = this.getIsConsumerEnabledMap();
                log.trace("#audit auditPolicy.pass=true, entity={}, enabledConsumers={}", (Object)entity, enabledConsumers);
            }
            this.consumerRegistry.entrySet().stream().filter(e -> ((AuditConsumer)e.getKey()).isEnabled()).forEach(e -> ((ConsumerRegistration)e.getValue()).queue.offer(entity));
        } else {
            log.trace("#audit auditPolicy.pass=false, entity={}", (Object)entity);
        }
    }

    private Map<AuditConsumer, Boolean> getIsConsumerEnabledMap() {
        return this.consumerRegistry.keySet().stream().collect(Collectors.toMap(auditConsumer -> auditConsumer, AuditConsumer::isEnabled));
    }

    private void waitForTermination() {
        this.consumerRegistry.values().forEach(x -> {
            try {
                ((ConsumerRegistration)x).thread.join();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        });
    }

    @ThreadSafe
    private static final class ConsumerQueue {
        private final AuditConsumer auditConsumer;
        private final BlockingQueue<AuditEntity> queue;
        private final int batchSize;
        private final Consumer<List<AuditEntity>> rejectionHandler;

        ConsumerQueue(AuditConsumer auditConsumer, BlockingQueue<AuditEntity> queue, int batchSize, Consumer<List<AuditEntity>> rejectionHandler) {
            this.auditConsumer = auditConsumer;
            this.queue = Objects.requireNonNull(queue);
            this.batchSize = batchSize;
            this.rejectionHandler = Objects.requireNonNull(rejectionHandler);
        }

        void offer(AuditEntity entity) {
            while (!this.queue.offer(entity)) {
                this.discardOldestEntities();
            }
            log.trace("#offer auditConsumer={}, entity={}", (Object)this.auditConsumer, (Object)entity);
        }

        void clear() {
            this.queue.clear();
        }

        List<AuditEntity> take() throws InterruptedException {
            ArrayList<AuditEntity> batch = new ArrayList<AuditEntity>(this.batchSize);
            AuditEntity entity = this.queue.take();
            batch.add(entity);
            while (batch.size() < this.batchSize && (entity = (AuditEntity)this.queue.poll()) != null) {
                batch.add(entity);
            }
            return batch;
        }

        List<AuditEntity> poll() {
            AuditEntity entity;
            ArrayList<AuditEntity> batch = new ArrayList<AuditEntity>(this.batchSize);
            while (batch.size() < this.batchSize && (entity = (AuditEntity)this.queue.poll()) != null) {
                batch.add(entity);
            }
            return batch;
        }

        private void discardOldestEntities() {
            AuditEntity entity;
            ArrayList<AuditEntity> batch = new ArrayList<AuditEntity>(this.batchSize);
            for (int i = 0; i < this.batchSize && (entity = (AuditEntity)this.queue.poll()) != null; ++i) {
                batch.add(entity);
            }
            log.trace("#discardOldestEntities auditConsumer={}, batchSize={}, batch={}", new Object[]{this.auditConsumer, this.batchSize, batch});
            this.rejectionHandler.accept(batch);
        }
    }

    private final class ConsumerThread
    extends Thread {
        private final AtomicBoolean running;
        private final AuditConsumer consumer;
        private final ConsumerQueue queue;
        private final BiConsumer<RuntimeException, List<AuditEntity>> exceptionHandler;

        ConsumerThread(ConsumerQueue queue, AuditConsumer consumer, BiConsumer<RuntimeException, List<AuditEntity>> exceptionHandler) {
            super("audit-broker-consumer-thread-" + Integer.toHexString(ScatterAuditBroker.this.hashCode()) + "-consumer-" + Integer.toHexString(consumer.hashCode()));
            this.running = new AtomicBoolean(false);
            this.queue = Objects.requireNonNull(queue);
            this.consumer = Objects.requireNonNull(consumer);
            this.exceptionHandler = Objects.requireNonNull(exceptionHandler);
        }

        @Override
        public void run() {
            try {
                while (!this.isInterrupted()) {
                    try {
                        List<AuditEntity> batch = this.queue.take();
                        log.trace("#run batch={}", batch);
                        this.processBatch(batch);
                    }
                    catch (InterruptedException e) {
                        // empty catch block
                        break;
                    }
                }
                log.trace("#run ConsumerThread interrupted, consumer={}, queue={}", (Object)this.consumer, (Object)this.queue.queue);
            }
            catch (Throwable uncaughtThrowable) {
                log.error("#run ConsumerThread killed by an uncaught throwable", uncaughtThrowable);
                throw uncaughtThrowable;
            }
        }

        @Override
        public void start() {
            if (this.running.compareAndSet(false, true)) {
                super.start();
            }
        }

        public void shutdown() {
            if (this.running.compareAndSet(true, false)) {
                this.interrupt();
                this.drainQueue();
            }
        }

        public void shutdownNow() {
            if (this.running.compareAndSet(true, false)) {
                this.interrupt();
            }
            this.queue.clear();
            this.running.set(false);
        }

        private void drainQueue() {
            List<AuditEntity> batch;
            while (!(batch = this.queue.poll()).isEmpty()) {
                log.trace("#drainQueue batch={}", batch);
                this.processBatch(batch);
            }
        }

        private void processBatch(List<AuditEntity> batch) {
            try {
                this.consumer.accept(batch);
            }
            catch (RuntimeException e) {
                this.exceptionHandler.accept(e, batch);
            }
        }
    }

    private static class ConsumerRegistration {
        private final ConsumerQueue queue;
        private final ConsumerThread thread;

        private ConsumerRegistration(ConsumerQueue queue, ConsumerThread thread) {
            this.queue = Objects.requireNonNull(queue);
            this.thread = Objects.requireNonNull(thread);
        }

        ConsumerThread getThread() {
            return this.thread;
        }
    }
}

