/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.cache.impl.CacheEventData;
import com.hazelcast.cache.impl.CacheEventSet;
import com.hazelcast.collection.impl.collection.CollectionEvent;
import com.hazelcast.collection.impl.queue.QueueEvent;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.internal.diagnostics.DiagnosticsPlugin;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.event.EntryEventData;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.eventservice.impl.LocalEventDispatcher;
import com.hazelcast.spi.properties.HazelcastProperties;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.util.ItemCounter;
import com.hazelcast.util.executor.StripedExecutor;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class EventQueuePlugin
extends DiagnosticsPlugin {
    public static final HazelcastProperty PERIOD_SECONDS = new HazelcastProperty("hazelcast.diagnostics.event.queue.period.seconds", 0, TimeUnit.SECONDS);
    public static final HazelcastProperty THRESHOLD = new HazelcastProperty("hazelcast.diagnostics.event.queue.threshold", 1000);
    public static final HazelcastProperty SAMPLES = new HazelcastProperty("hazelcast.diagnostics.event.queue.samples", 100);
    private final ItemCounter<String> occurrenceMap = new ItemCounter();
    private final Random random = new Random();
    private final NumberFormat defaultFormat = NumberFormat.getPercentInstance();
    private final StripedExecutor eventExecutor;
    private final long periodMillis;
    private final int threshold;
    private final int samples;
    private int eventCount;

    public EventQueuePlugin(NodeEngineImpl nodeEngine, StripedExecutor eventExecutor) {
        this(nodeEngine.getLogger(EventQueuePlugin.class), eventExecutor, nodeEngine.getProperties());
    }

    public EventQueuePlugin(ILogger logger, StripedExecutor eventExecutor, HazelcastProperties props) {
        super(logger);
        this.defaultFormat.setMinimumFractionDigits(3);
        this.eventExecutor = eventExecutor;
        this.periodMillis = props.getMillis(PERIOD_SECONDS);
        this.threshold = props.getInteger(THRESHOLD);
        this.samples = props.getInteger(SAMPLES);
    }

    @Override
    public long getPeriodMillis() {
        return this.periodMillis;
    }

    @Override
    public void onStart() {
        this.logger.info("Plugin:active, period-millis:" + this.periodMillis + " threshold:" + this.threshold + " samples:" + this.samples);
    }

    @Override
    public void run(DiagnosticsLogWriter writer) {
        writer.startSection("EventQueues");
        int index = 1;
        List<BlockingQueue<Runnable>> eventQueues = this.getEventQueues();
        for (BlockingQueue<Runnable> eventQueue : eventQueues) {
            this.scan(writer, eventQueue, index++);
        }
        writer.endSection();
    }

    ItemCounter<String> getOccurrenceMap() {
        return this.occurrenceMap;
    }

    private List<BlockingQueue<Runnable>> getEventQueues() {
        return this.eventExecutor.getTaskQueues();
    }

    private void scan(DiagnosticsLogWriter writer, BlockingQueue<Runnable> eventQueue, int index) {
        int sampleCount = this.sample(eventQueue);
        if (sampleCount < 0) {
            return;
        }
        this.render(writer, sampleCount, index);
    }

    private void render(DiagnosticsLogWriter writer, int sampleCount, int index) {
        writer.startSection("worker=" + index);
        writer.writeKeyValueEntry("eventCount", this.eventCount);
        writer.writeKeyValueEntry("sampleCount", sampleCount);
        this.renderSamples(writer, sampleCount);
        writer.endSection();
    }

    private void renderSamples(DiagnosticsLogWriter writer, int sampleCount) {
        writer.startSection("samples");
        for (String key : this.occurrenceMap.keySet()) {
            long value = this.occurrenceMap.get(key);
            if (value == 0L) continue;
            double percentage = 1.0 * (double)value / (double)sampleCount;
            writer.writeEntry(key + " sampleCount=" + value + " " + this.defaultFormat.format(percentage));
        }
        this.occurrenceMap.reset();
        writer.endSection();
    }

    private int sample(BlockingQueue<Runnable> queue) {
        int actualSampleCount;
        Runnable runnable;
        ArrayList<Runnable> events = new ArrayList<Runnable>(queue);
        this.eventCount = events.size();
        if (this.eventCount < this.threshold) {
            return -1;
        }
        int sampleCount = Math.min(this.samples, this.eventCount);
        for (actualSampleCount = 0; actualSampleCount < sampleCount; actualSampleCount += this.sampleRunnable(runnable)) {
            runnable = events.get(this.random.nextInt(this.eventCount));
        }
        return actualSampleCount;
    }

    int sampleRunnable(Runnable runnable) {
        if (runnable instanceof LocalEventDispatcher) {
            LocalEventDispatcher eventDispatcher = (LocalEventDispatcher)runnable;
            return this.sampleLocalDispatcherEvent(eventDispatcher);
        }
        this.occurrenceMap.add(runnable.getClass().getName(), 1L);
        return 1;
    }

    private int sampleLocalDispatcherEvent(LocalEventDispatcher eventDispatcher) {
        Object dispatcherEvent = eventDispatcher.getEvent();
        if (dispatcherEvent instanceof EntryEventData) {
            EntryEventData event = (EntryEventData)dispatcherEvent;
            EntryEventType type = EntryEventType.getByType(event.getEventType());
            String mapName = event.getMapName();
            this.occurrenceMap.add(String.format("IMap '%s' %s", new Object[]{mapName, type}), 1L);
            return 1;
        }
        if (dispatcherEvent instanceof CacheEventSet) {
            CacheEventSet eventSet = (CacheEventSet)dispatcherEvent;
            Set<CacheEventData> events = eventSet.getEvents();
            for (CacheEventData event : events) {
                this.occurrenceMap.add(String.format("ICache '%s' %s", new Object[]{event.getName(), event.getCacheEventType()}), 1L);
            }
            return events.size();
        }
        if (dispatcherEvent instanceof QueueEvent) {
            QueueEvent event = (QueueEvent)dispatcherEvent;
            this.occurrenceMap.add(String.format("IQueue '%s' %s", new Object[]{event.getName(), event.getEventType()}), 1L);
            return 1;
        }
        if (dispatcherEvent instanceof CollectionEvent) {
            CollectionEvent event = (CollectionEvent)dispatcherEvent;
            String serviceName = eventDispatcher.getServiceName();
            if ("hz:impl:setService".equals(serviceName)) {
                serviceName = "ISet";
            } else if ("hz:impl:listService".equals(serviceName)) {
                serviceName = "IList";
            }
            this.occurrenceMap.add(String.format("%s '%s' %s", new Object[]{serviceName, event.getName(), event.getEventType()}), 1L);
            return 1;
        }
        this.occurrenceMap.add(dispatcherEvent.getClass().getSimpleName(), 1L);
        return 1;
    }
}

