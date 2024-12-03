/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.stats;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.jackrabbit.api.stats.RepositoryStatistics;
import org.apache.jackrabbit.api.stats.TimeSeries;
import org.apache.jackrabbit.stats.TimeSeriesAverage;
import org.apache.jackrabbit.stats.TimeSeriesRecorder;

public class RepositoryStatisticsImpl
implements Iterable<Map.Entry<String, TimeSeries>>,
RepositoryStatistics {
    private final Map<String, TimeSeriesRecorder> recorders = new HashMap<String, TimeSeriesRecorder>();
    private final Map<String, TimeSeriesAverage> avg = new HashMap<String, TimeSeriesAverage>();

    public RepositoryStatisticsImpl() {
        this.getOrCreateRecorder(RepositoryStatistics.Type.SESSION_COUNT);
        this.getOrCreateRecorder(RepositoryStatistics.Type.SESSION_LOGIN_COUNTER);
        this.createAvg(RepositoryStatistics.Type.SESSION_READ_COUNTER, RepositoryStatistics.Type.SESSION_READ_DURATION, RepositoryStatistics.Type.SESSION_READ_AVERAGE);
        this.createAvg(RepositoryStatistics.Type.SESSION_WRITE_COUNTER, RepositoryStatistics.Type.SESSION_WRITE_DURATION, RepositoryStatistics.Type.SESSION_WRITE_AVERAGE);
        this.createAvg(RepositoryStatistics.Type.BUNDLE_CACHE_MISS_COUNTER, RepositoryStatistics.Type.BUNDLE_CACHE_MISS_DURATION, RepositoryStatistics.Type.BUNDLE_CACHE_MISS_AVERAGE);
        this.createAvg(RepositoryStatistics.Type.BUNDLE_WRITE_COUNTER, RepositoryStatistics.Type.BUNDLE_WRITE_DURATION, RepositoryStatistics.Type.BUNDLE_WRITE_AVERAGE);
        this.createAvg(RepositoryStatistics.Type.QUERY_COUNT, RepositoryStatistics.Type.QUERY_DURATION, RepositoryStatistics.Type.QUERY_AVERAGE);
        this.createAvg(RepositoryStatistics.Type.OBSERVATION_EVENT_COUNTER, RepositoryStatistics.Type.OBSERVATION_EVENT_DURATION, RepositoryStatistics.Type.OBSERVATION_EVENT_AVERAGE);
    }

    private void createAvg(RepositoryStatistics.Type count, RepositoryStatistics.Type duration, RepositoryStatistics.Type avgTs) {
        this.avg.put(avgTs.name(), new TimeSeriesAverage(this.getOrCreateRecorder(duration), this.getOrCreateRecorder(count)));
    }

    public RepositoryStatisticsImpl(ScheduledExecutorService executor) {
        this();
        executor.scheduleAtFixedRate(new Runnable(){

            @Override
            public void run() {
                RepositoryStatisticsImpl.this.recordOneSecond();
            }
        }, 1L, 1L, TimeUnit.SECONDS);
    }

    @Override
    public synchronized Iterator<Map.Entry<String, TimeSeries>> iterator() {
        TreeMap<String, TimeSeries> map = new TreeMap<String, TimeSeries>();
        map.putAll(this.recorders);
        map.putAll(this.avg);
        return map.entrySet().iterator();
    }

    public AtomicLong getCounter(RepositoryStatistics.Type type) {
        return this.getOrCreateRecorder(type).getCounter();
    }

    public AtomicLong getCounter(String type, boolean resetValueEachSecond) {
        return this.getOrCreateRecorder(type, resetValueEachSecond).getCounter();
    }

    @Override
    public TimeSeries getTimeSeries(RepositoryStatistics.Type type) {
        return this.getTimeSeries(type.name(), type.isResetValueEachSecond());
    }

    @Override
    public TimeSeries getTimeSeries(String type, boolean resetValueEachSecond) {
        if (this.avg.containsKey(type)) {
            return this.avg.get(type);
        }
        return this.getOrCreateRecorder(type, resetValueEachSecond);
    }

    private synchronized TimeSeriesRecorder getOrCreateRecorder(RepositoryStatistics.Type type) {
        return this.getOrCreateRecorder(type.name(), type.isResetValueEachSecond());
    }

    private synchronized TimeSeriesRecorder getOrCreateRecorder(String type, boolean resetValueEachSecond) {
        TimeSeriesRecorder recorder = this.recorders.get(type);
        if (recorder == null) {
            recorder = new TimeSeriesRecorder(resetValueEachSecond);
            this.recorders.put(type, recorder);
        }
        return recorder;
    }

    private synchronized void recordOneSecond() {
        for (TimeSeriesRecorder recorder : this.recorders.values()) {
            recorder.recordOneSecond();
        }
    }
}

