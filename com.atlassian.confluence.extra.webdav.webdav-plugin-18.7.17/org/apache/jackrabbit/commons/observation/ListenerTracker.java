/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.observation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.management.openmbean.CompositeData;
import org.apache.jackrabbit.api.jmx.EventListenerMBean;
import org.apache.jackrabbit.api.observation.JackrabbitEvent;
import org.apache.jackrabbit.commons.iterator.EventIteratorAdapter;
import org.apache.jackrabbit.commons.observation.EventTracker;
import org.apache.jackrabbit.commons.observation.JackrabbitEventTracker;
import org.apache.jackrabbit.stats.TimeSeriesMax;
import org.apache.jackrabbit.stats.TimeSeriesRecorder;
import org.apache.jackrabbit.stats.TimeSeriesStatsUtil;

public class ListenerTracker {
    private final EventListener listener;
    private final int eventTypes;
    private final String absPath;
    private final boolean isDeep;
    private final String[] uuid;
    private final String[] nodeTypeName;
    private final boolean noLocal;
    protected final Exception initStackTrace = new Exception("The event listener was registered here:");
    private final long startTime = System.currentTimeMillis();
    private final AtomicLong eventDeliveries = new AtomicLong();
    private final AtomicLong eventsDelivered = new AtomicLong();
    private final AtomicLong eventDeliveryTime = new AtomicLong();
    private final AtomicLong headTimestamp = new AtomicLong();
    private final TimeSeriesMax queueLength = new TimeSeriesMax();
    private final TimeSeriesRecorder eventCount = new TimeSeriesRecorder(true);
    private final TimeSeriesRecorder eventConsumerTime = new TimeSeriesRecorder(true);
    private final TimeSeriesRecorder eventProducerTime = new TimeSeriesRecorder(true);
    final AtomicBoolean userInfoAccessedWithoutExternalsCheck = new AtomicBoolean();
    final AtomicBoolean userInfoAccessedFromExternalEvent = new AtomicBoolean();
    final AtomicBoolean dateAccessedWithoutExternalsCheck = new AtomicBoolean();
    final AtomicBoolean dateAccessedFromExternalEvent = new AtomicBoolean();

    public ListenerTracker(EventListener listener, int eventTypes, String absPath, boolean isDeep, String[] uuid, String[] nodeTypeName, boolean noLocal) {
        this.listener = listener;
        this.eventTypes = eventTypes;
        this.absPath = absPath;
        this.isDeep = isDeep;
        this.uuid = ListenerTracker.copy(uuid);
        this.nodeTypeName = ListenerTracker.copy(nodeTypeName);
        this.noLocal = noLocal;
    }

    protected void warn(String message) {
    }

    protected void beforeEventDelivery() {
    }

    protected void afterEventDelivery() {
    }

    public void recordQueueLength(long length) {
        this.queueLength.recordValue(length);
    }

    public void recordQueueLength(long length, long headTimestamp) {
        this.queueLength.recordValue(length);
        this.headTimestamp.set(length == 0L ? 0L : headTimestamp);
    }

    public void recordOneSecond() {
        this.queueLength.recordOneSecond();
        this.eventCount.recordOneSecond();
        this.eventConsumerTime.recordOneSecond();
        this.eventProducerTime.recordOneSecond();
    }

    public void recordProducerTime(long time, TimeUnit unit) {
        this.eventProducerTime.getCounter().addAndGet(unit.toNanos(time));
    }

    public EventListener getTrackedListener() {
        return new EventListener(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void onEvent(EventIterator events) {
                ListenerTracker.this.eventDeliveries.incrementAndGet();
                final long start = System.nanoTime();
                try {
                    ListenerTracker.this.beforeEventDelivery();
                    ListenerTracker.this.listener.onEvent(new EventIteratorAdapter(events){
                        long t0;
                        {
                            super(iterator);
                            this.t0 = start;
                        }

                        private void recordTime(TimeSeriesRecorder recorder) {
                            this.t0 = System.nanoTime();
                            recorder.getCounter().addAndGet(-(this.t0 - this.t0));
                        }

                        @Override
                        public Object next() {
                            this.recordTime(ListenerTracker.this.eventConsumerTime);
                            ListenerTracker.this.eventsDelivered.incrementAndGet();
                            ListenerTracker.this.eventCount.getCounter().incrementAndGet();
                            Object object = super.next();
                            if (object instanceof JackrabbitEvent) {
                                object = new JackrabbitEventTracker(ListenerTracker.this, (JackrabbitEvent)object);
                            } else if (object instanceof Event) {
                                object = new EventTracker(ListenerTracker.this, (Event)object);
                            }
                            this.recordTime(ListenerTracker.this.eventProducerTime);
                            return object;
                        }

                        @Override
                        public boolean hasNext() {
                            this.recordTime(ListenerTracker.this.eventConsumerTime);
                            boolean result = super.hasNext();
                            this.recordTime(ListenerTracker.this.eventProducerTime);
                            return result;
                        }
                    });
                }
                finally {
                    ListenerTracker.this.afterEventDelivery();
                    ListenerTracker.this.eventDeliveryTime.addAndGet(System.nanoTime() - start);
                }
            }

            public String toString() {
                return ListenerTracker.this.toString();
            }
        };
    }

    public EventListenerMBean getListenerMBean() {
        return new EventListenerMBean(){

            @Override
            public String getClassName() {
                return ListenerTracker.this.listener.getClass().getName();
            }

            @Override
            public String getToString() {
                return ListenerTracker.this.listener.toString();
            }

            @Override
            public String getInitStackTrace() {
                StringWriter writer = new StringWriter();
                ListenerTracker.this.initStackTrace.printStackTrace(new PrintWriter(writer));
                return writer.toString();
            }

            @Override
            public int getEventTypes() {
                return ListenerTracker.this.eventTypes;
            }

            @Override
            public String getAbsPath() {
                return ListenerTracker.this.absPath;
            }

            @Override
            public boolean isDeep() {
                return ListenerTracker.this.isDeep;
            }

            @Override
            public String[] getUuid() {
                return ListenerTracker.copy(ListenerTracker.this.uuid);
            }

            @Override
            public String[] getNodeTypeName() {
                return ListenerTracker.copy(ListenerTracker.this.nodeTypeName);
            }

            @Override
            public boolean isNoLocal() {
                return ListenerTracker.this.noLocal;
            }

            @Override
            public long getEventDeliveries() {
                return ListenerTracker.this.eventDeliveries.get();
            }

            @Override
            public long getEventDeliveriesPerHour() {
                return TimeUnit.HOURS.toMillis(this.getEventDeliveries()) / Math.max(System.currentTimeMillis() - ListenerTracker.this.startTime, 1L);
            }

            @Override
            public long getMicrosecondsPerEventDelivery() {
                return TimeUnit.NANOSECONDS.toMicros(ListenerTracker.this.eventDeliveryTime.get()) / Math.max(this.getEventDeliveries(), 1L);
            }

            @Override
            public long getEventsDelivered() {
                return ListenerTracker.this.eventsDelivered.get();
            }

            @Override
            public long getEventsDeliveredPerHour() {
                return TimeUnit.HOURS.toMillis(this.getEventsDelivered()) / Math.max(System.currentTimeMillis() - ListenerTracker.this.startTime, 1L);
            }

            @Override
            public long getMicrosecondsPerEventDelivered() {
                return TimeUnit.NANOSECONDS.toMicros(ListenerTracker.this.eventDeliveryTime.get()) / Math.max(this.getEventsDelivered(), 1L);
            }

            @Override
            public double getRatioOfTimeSpentProcessingEvents() {
                double timeSpentProcessingEvents = TimeUnit.NANOSECONDS.toMillis(ListenerTracker.this.eventDeliveryTime.get());
                return timeSpentProcessingEvents / (double)Math.max(System.currentTimeMillis() - ListenerTracker.this.startTime, 1L);
            }

            @Override
            public double getEventConsumerTimeRatio() {
                double consumerTime = ListenerTracker.sum(ListenerTracker.this.eventConsumerTime);
                double producerTime = ListenerTracker.sum(ListenerTracker.this.eventProducerTime);
                return consumerTime / Math.max(consumerTime + producerTime, 1.0);
            }

            @Override
            public boolean isUserInfoAccessedWithoutExternalsCheck() {
                return ListenerTracker.this.userInfoAccessedWithoutExternalsCheck.get();
            }

            @Override
            public synchronized boolean isUserInfoAccessedFromExternalEvent() {
                return ListenerTracker.this.userInfoAccessedFromExternalEvent.get();
            }

            @Override
            public synchronized boolean isDateAccessedWithoutExternalsCheck() {
                return ListenerTracker.this.dateAccessedWithoutExternalsCheck.get();
            }

            @Override
            public synchronized boolean isDateAccessedFromExternalEvent() {
                return ListenerTracker.this.dateAccessedFromExternalEvent.get();
            }

            @Override
            public long getQueueBacklogMillis() {
                long t = ListenerTracker.this.headTimestamp.get();
                if (t > 0L) {
                    t = System.currentTimeMillis() - t;
                }
                return t;
            }

            @Override
            public CompositeData getQueueLength() {
                return TimeSeriesStatsUtil.asCompositeData(ListenerTracker.this.queueLength, "queueLength");
            }

            @Override
            public CompositeData getEventCount() {
                return TimeSeriesStatsUtil.asCompositeData(ListenerTracker.this.eventCount, "eventCount");
            }

            @Override
            public CompositeData getEventConsumerTime() {
                return TimeSeriesStatsUtil.asCompositeData(ListenerTracker.this.eventConsumerTime, "eventConsumerTime");
            }

            @Override
            public CompositeData getEventProducerTime() {
                return TimeSeriesStatsUtil.asCompositeData(ListenerTracker.this.eventProducerTime, "eventProducerTime");
            }
        };
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.absPath != null) {
            builder.append(this.absPath);
        }
        if (this.isDeep) {
            builder.append("//*");
        } else {
            builder.append("/*");
        }
        builder.append('[');
        builder.append(Integer.toBinaryString(this.eventTypes));
        builder.append('b');
        if (this.uuid != null) {
            for (String id : this.uuid) {
                builder.append(", ");
                builder.append(id);
            }
        }
        if (this.nodeTypeName != null) {
            for (String name : this.nodeTypeName) {
                builder.append(", ");
                builder.append(name);
            }
        }
        if (this.noLocal) {
            builder.append(", no local");
        }
        builder.append("]@");
        builder.append(this.listener.getClass().getName());
        return builder.toString();
    }

    private static String[] copy(String[] array) {
        if (array != null && array.length > 0) {
            String[] copy = new String[array.length];
            System.arraycopy(array, 0, copy, 0, array.length);
            return copy;
        }
        return array;
    }

    private static long sum(TimeSeriesRecorder timeSeries) {
        long missingValue = timeSeries.getMissingValue();
        long sum = 0L;
        sum += ListenerTracker.sum(timeSeries.getValuePerSecond(), missingValue);
        sum += ListenerTracker.sum(timeSeries.getValuePerMinute(), missingValue);
        sum += ListenerTracker.sum(timeSeries.getValuePerHour(), missingValue);
        return sum += ListenerTracker.sum(timeSeries.getValuePerWeek(), missingValue);
    }

    private static long sum(long[] values, long missing) {
        long sum = 0L;
        for (long v : values) {
            if (v == missing) continue;
            sum += v;
        }
        return sum;
    }
}

