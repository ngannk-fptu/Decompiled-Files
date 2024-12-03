/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.reporter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import zipkin2.reporter.ReporterMetrics;

public final class InMemoryReporterMetrics
implements ReporterMetrics {
    private final ConcurrentHashMap<MetricKey, AtomicLong> metrics = new ConcurrentHashMap();
    private final ConcurrentHashMap<Class<? extends Throwable>, AtomicLong> messagesDropped = new ConcurrentHashMap();

    @Override
    public void incrementMessages() {
        this.increment(MetricKey.messages, 1);
    }

    public long messages() {
        return this.get(MetricKey.messages);
    }

    @Override
    public void incrementMessagesDropped(Throwable cause) {
        InMemoryReporterMetrics.increment(this.messagesDropped, cause.getClass(), 1);
    }

    public Map<Class<? extends Throwable>, Long> messagesDroppedByCause() {
        LinkedHashMap<Class<? extends Throwable>, Long> result = new LinkedHashMap<Class<? extends Throwable>, Long>(this.messagesDropped.size());
        for (Map.Entry<Class<? extends Throwable>, AtomicLong> kv : this.messagesDropped.entrySet()) {
            result.put(kv.getKey(), kv.getValue().longValue());
        }
        return result;
    }

    public long messagesDropped() {
        long result = 0L;
        for (AtomicLong count : this.messagesDropped.values()) {
            result += count.longValue();
        }
        return result;
    }

    @Override
    public void incrementMessageBytes(int quantity) {
        this.increment(MetricKey.messageBytes, quantity);
    }

    public long messageBytes() {
        return this.get(MetricKey.messageBytes);
    }

    @Override
    public void incrementSpans(int quantity) {
        this.increment(MetricKey.spans, quantity);
    }

    public long spans() {
        return this.get(MetricKey.spans);
    }

    @Override
    public void incrementSpanBytes(int quantity) {
        this.increment(MetricKey.spanBytes, quantity);
    }

    public long spanBytes() {
        return this.get(MetricKey.spanBytes);
    }

    @Override
    public void incrementSpansDropped(int quantity) {
        this.increment(MetricKey.spansDropped, quantity);
    }

    public long spansDropped() {
        return this.get(MetricKey.spansDropped);
    }

    @Override
    public void updateQueuedSpans(int update) {
        this.update(MetricKey.spansPending, update);
    }

    public long queuedSpans() {
        return this.get(MetricKey.spansPending);
    }

    @Override
    public void updateQueuedBytes(int update) {
        this.update(MetricKey.spanBytesPending, update);
    }

    public long queuedBytes() {
        return this.get(MetricKey.spanBytesPending);
    }

    public void clear() {
        this.metrics.clear();
    }

    private long get(MetricKey key) {
        AtomicLong atomic = this.metrics.get((Object)key);
        return atomic == null ? 0L : atomic.get();
    }

    private void increment(MetricKey key, int quantity) {
        InMemoryReporterMetrics.increment(this.metrics, key, quantity);
    }

    static <K> void increment(ConcurrentHashMap<K, AtomicLong> metrics, K key, int quantity) {
        long update;
        long oldValue;
        if (quantity == 0) {
            return;
        }
        AtomicLong metric = metrics.get(key);
        if (metric == null && (metric = metrics.putIfAbsent(key, new AtomicLong(quantity))) == null) {
            return;
        }
        while (!metric.compareAndSet(oldValue = metric.get(), update = oldValue + (long)quantity)) {
        }
    }

    private void update(MetricKey key, int update) {
        AtomicLong metric = this.metrics.get((Object)key);
        if (metric == null && (metric = this.metrics.putIfAbsent(key, new AtomicLong(update))) == null) {
            return;
        }
        metric.set(update);
    }

    static enum MetricKey {
        messages,
        messageBytes,
        spans,
        spanBytes,
        spansDropped,
        spansPending,
        spanBytesPending;

    }
}

