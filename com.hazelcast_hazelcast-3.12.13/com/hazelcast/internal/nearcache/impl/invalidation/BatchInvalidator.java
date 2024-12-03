/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IFunction;
import com.hazelcast.core.LifecycleEvent;
import com.hazelcast.core.LifecycleListener;
import com.hazelcast.core.LifecycleService;
import com.hazelcast.internal.nearcache.impl.invalidation.BatchNearCacheInvalidation;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.internal.nearcache.impl.invalidation.InvalidationQueue;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidator;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.ConcurrencyUtil;
import com.hazelcast.util.ConstructorFunction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BatchInvalidator
extends Invalidator {
    private final String invalidationExecutorName;
    private final ConstructorFunction<String, InvalidationQueue<Invalidation>> invalidationQueueConstructor = new ConstructorFunction<String, InvalidationQueue<Invalidation>>(){

        @Override
        public InvalidationQueue<Invalidation> createNew(String dataStructureName) {
            return new InvalidationQueue<Invalidation>();
        }
    };
    private final ConcurrentMap<String, InvalidationQueue<Invalidation>> invalidationQueues = new ConcurrentHashMap<String, InvalidationQueue<Invalidation>>();
    private final int batchSize;
    private final int batchFrequencySeconds;
    private final String nodeShutdownListenerId;
    private final AtomicBoolean runningBackgroundTask = new AtomicBoolean(false);

    public BatchInvalidator(String serviceName, int batchSize, int batchFrequencySeconds, IFunction<EventRegistration, Boolean> eventFilter, NodeEngine nodeEngine) {
        super(serviceName, eventFilter, nodeEngine);
        this.batchSize = batchSize;
        this.batchFrequencySeconds = batchFrequencySeconds;
        this.nodeShutdownListenerId = this.registerNodeShutdownListener();
        this.invalidationExecutorName = serviceName + this.getClass();
    }

    @Override
    protected Invalidation newInvalidation(Data key, String dataStructureName, String sourceUuid, int partitionId) {
        this.checkBackgroundTaskIsRunning();
        return super.newInvalidation(key, dataStructureName, sourceUuid, partitionId);
    }

    @Override
    protected void invalidateInternal(Invalidation invalidation, int orderKey) {
        String dataStructureName = invalidation.getName();
        InvalidationQueue<Invalidation> invalidationQueue = this.invalidationQueueOf(dataStructureName);
        invalidationQueue.offer(invalidation);
        if (invalidationQueue.size() >= this.batchSize) {
            this.pollAndSendInvalidations(dataStructureName, invalidationQueue);
        }
    }

    private InvalidationQueue<Invalidation> invalidationQueueOf(String dataStructureName) {
        return ConcurrencyUtil.getOrPutIfAbsent(this.invalidationQueues, dataStructureName, this.invalidationQueueConstructor);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void pollAndSendInvalidations(String dataStructureName, InvalidationQueue<Invalidation> invalidationQueue) {
        List<Invalidation> invalidations;
        assert (invalidationQueue != null);
        if (!invalidationQueue.tryAcquire()) {
            return;
        }
        try {
            invalidations = this.pollInvalidations(invalidationQueue);
        }
        finally {
            invalidationQueue.release();
        }
        this.sendInvalidations(dataStructureName, invalidations);
    }

    private List<Invalidation> pollInvalidations(InvalidationQueue<Invalidation> invalidationQueue) {
        Invalidation invalidation;
        int size = invalidationQueue.size();
        ArrayList<Invalidation> invalidations = new ArrayList<Invalidation>(size);
        for (int i = 0; i < size && (invalidation = invalidationQueue.poll()) != null; ++i) {
            invalidations.add(invalidation);
        }
        return invalidations;
    }

    private void sendInvalidations(String dataStructureName, List<Invalidation> invalidations) {
        BatchNearCacheInvalidation invalidation = new BatchNearCacheInvalidation(dataStructureName, invalidations);
        Collection<EventRegistration> registrations = this.eventService.getRegistrations(this.serviceName, dataStructureName);
        for (EventRegistration registration : registrations) {
            if (!((Boolean)this.eventFilter.apply(registration)).booleanValue()) continue;
            int orderKey = registration.getSubscriber().hashCode();
            this.eventService.publishEvent(this.serviceName, registration, (Object)invalidation, orderKey);
        }
    }

    private String registerNodeShutdownListener() {
        HazelcastInstance node = this.nodeEngine.getHazelcastInstance();
        LifecycleService lifecycleService = node.getLifecycleService();
        return lifecycleService.addLifecycleListener(new LifecycleListener(){

            @Override
            public void stateChanged(LifecycleEvent event) {
                if (event.getState() == LifecycleEvent.LifecycleState.SHUTTING_DOWN) {
                    Set entries = BatchInvalidator.this.invalidationQueues.entrySet();
                    for (Map.Entry entry : entries) {
                        BatchInvalidator.this.pollAndSendInvalidations((String)entry.getKey(), (InvalidationQueue)entry.getValue());
                    }
                }
            }
        });
    }

    private void checkBackgroundTaskIsRunning() {
        if (this.runningBackgroundTask.get()) {
            return;
        }
        if (this.runningBackgroundTask.compareAndSet(false, true)) {
            ExecutionService executionService = this.nodeEngine.getExecutionService();
            executionService.scheduleWithRepetition(this.invalidationExecutorName, new BatchInvalidationEventSender(), this.batchFrequencySeconds, this.batchFrequencySeconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public void destroy(String dataStructureName, String sourceUuid) {
        this.invalidationQueues.remove(dataStructureName);
        super.destroy(dataStructureName, sourceUuid);
    }

    @Override
    public void shutdown() {
        ExecutionService executionService = this.nodeEngine.getExecutionService();
        executionService.shutdownExecutor(this.invalidationExecutorName);
        HazelcastInstance node = this.nodeEngine.getHazelcastInstance();
        LifecycleService lifecycleService = node.getLifecycleService();
        lifecycleService.removeLifecycleListener(this.nodeShutdownListenerId);
        this.invalidationQueues.clear();
        super.shutdown();
    }

    @Override
    public void reset() {
        this.invalidationQueues.clear();
        super.reset();
    }

    private class BatchInvalidationEventSender
    implements Runnable {
        private BatchInvalidationEventSender() {
        }

        @Override
        public void run() {
            for (Map.Entry entry : BatchInvalidator.this.invalidationQueues.entrySet()) {
                if (Thread.currentThread().isInterrupted()) break;
                String name = (String)entry.getKey();
                InvalidationQueue invalidationQueue = (InvalidationQueue)entry.getValue();
                if (invalidationQueue.size() <= 0) continue;
                BatchInvalidator.this.pollAndSendInvalidations(name, invalidationQueue);
            }
        }
    }
}

