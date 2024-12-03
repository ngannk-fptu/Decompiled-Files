/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.instrumentation.caches;

import com.atlassian.instrumentation.Instrument;
import com.atlassian.instrumentation.SimpleCounter;
import com.atlassian.instrumentation.SimpleTimer;
import com.atlassian.instrumentation.caches.CacheInstrument;
import com.atlassian.instrumentation.caches.CacheKeys;
import com.atlassian.instrumentation.caches.RequestListener;
import com.atlassian.instrumentation.compare.InstrumentComparator;
import com.atlassian.instrumentation.utils.dbc.Assertions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CacheCollector
implements CacheInstrument,
RequestListener {
    private boolean enabled;
    private ThreadLocal<Counters> counters = new ThreadLocal();
    public static final Sizer NOOP_SIZER = () -> -1L;
    protected final String name;
    protected final Sizer sizer;
    private InstrumentComparator comparator = new InstrumentComparator();

    public CacheCollector(String name) {
        this(name, NOOP_SIZER);
    }

    public CacheCollector(String name, Sizer sizer) {
        this.name = Assertions.notNull("name", name);
        this.sizer = Assertions.notNull("sizer", sizer);
    }

    public Counters getCounter() {
        if (this.counters.get() == null) {
            this.counters.set(new Counters());
        }
        return this.counters.get();
    }

    @Override
    public String getLoggingKey() {
        return "cache";
    }

    public long hit() {
        return !this.enabled ? 0L : this.getCounter().hits();
    }

    public long miss() {
        return !this.enabled ? 0L : this.getCounter().misses();
    }

    public long miss(long nanosecondsTaken) {
        if (!this.enabled) {
            return 0L;
        }
        this.getCounter().getSplits().add(new SimpleTimer("loadTime", nanosecondsTaken));
        this.getCounter().loads();
        return this.getCounter().misses();
    }

    public long put() {
        return !this.enabled ? 0L : this.getCounter().puts();
    }

    public long remove() {
        return !this.enabled ? 0L : this.getCounter().removes();
    }

    public List<SimpleTimer> getSplits() {
        return this.getCounter().getSplits();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void onRequestStart() {
        if (this.enabled) {
            if (this.getCounter() == null) {
                this.counters.set(new Counters());
            } else {
                this.getCounter().reset();
            }
        }
    }

    @Override
    public Map<String, Object> onRequestEnd() {
        if (!this.enabled) {
            return ImmutableMap.of();
        }
        if (this.getCounter().getSplits().size() == 0 && this.getCounter().getHits() == 0L && this.getCounter().getMisses() == 0L && this.getCounter().getPuts() == 0L && this.getCounter().getRemoves() == 0L && this.getCounter().getLoads() == 0L) {
            return ImmutableMap.of();
        }
        Map<String, Double> avgs = this.getCounter().getSplits().stream().collect(Collectors.groupingBy(SimpleTimer::getName, Collectors.collectingAndThen(Collectors.averagingLong(t -> t.getDuration().toNanos()), Double::valueOf)));
        HashMap<String, Object> result = new HashMap<String, Object>(avgs);
        this.addIfNonZero(result, CacheKeys.HITS.getName(), this.getCounter().getHits());
        this.addIfNonZero(result, CacheKeys.MISSES.getName(), this.getCounter().getMisses());
        this.addIfNonZero(result, CacheKeys.REMOVES.getName(), this.getCounter().getRemoves());
        this.addIfNonZero(result, CacheKeys.PUTS.getName(), this.getCounter().getPuts());
        this.addIfNonZero(result, CacheKeys.LOADS.getName(), this.getCounter().getLoads());
        result.put(CacheKeys.COUNT.getName(), this.getCacheSize());
        return result;
    }

    private void addIfNonZero(Map<String, Object> result, String key, long value) {
        if (value != 0L) {
            result.put(key, value);
        }
    }

    @Override
    public List<String> getTags() {
        return ImmutableList.of((Object)"cache");
    }

    @Override
    public long getValue() {
        return this.getMisses();
    }

    @Override
    public int compareTo(Instrument that) {
        return this.comparator.compare(this, that);
    }

    @Override
    public long getHits() {
        return this.getCounter().getHits();
    }

    @Override
    public long getMisses() {
        return this.getCounter().getMisses();
    }

    public long getPuts() {
        return this.getCounter().getPuts();
    }

    public long getRemoves() {
        return this.getCounter().getRemoves();
    }

    @Override
    public long getMissTime() {
        return this.getSplits().stream().filter(timer -> timer.getName().equals(CacheKeys.LOAD_TIME.getName())).mapToInt(timer -> timer.getDuration().getNano()).sum();
    }

    @Override
    public double getHitMissRatio() {
        double misses;
        double hits = this.getHits();
        if (hits + (misses = (double)this.getMisses()) == 0.0) {
            return 0.0;
        }
        return hits / (hits + misses);
    }

    @Override
    public long getCacheSize() {
        return this.sizer.getCacheSize();
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    class Counters {
        List<SimpleTimer> splits = new ArrayList<SimpleTimer>();
        private SimpleCounter hits = new SimpleCounter(CacheKeys.HITS.getName());
        private SimpleCounter misses = new SimpleCounter(CacheKeys.MISSES.getName());
        private SimpleCounter puts = new SimpleCounter(CacheKeys.PUTS.getName());
        private SimpleCounter removes = new SimpleCounter(CacheKeys.REMOVES.getName());
        private SimpleCounter loads = new SimpleCounter(CacheKeys.LOADS.getName());

        Counters() {
        }

        public void reset() {
            this.hits = new SimpleCounter(CacheKeys.HITS.getName());
            this.misses = new SimpleCounter(CacheKeys.MISSES.getName());
            this.puts = new SimpleCounter(CacheKeys.PUTS.getName());
            this.removes = new SimpleCounter(CacheKeys.REMOVES.getName());
            this.loads = new SimpleCounter(CacheKeys.LOADS.getName());
            this.splits.clear();
        }

        public long hits() {
            return this.hits.incrementAndGet();
        }

        public long misses() {
            return this.misses.incrementAndGet();
        }

        public long puts() {
            return this.puts.incrementAndGet();
        }

        public long removes() {
            return this.removes.incrementAndGet();
        }

        public long loads() {
            return this.loads.incrementAndGet();
        }

        public long getHits() {
            return this.hits.getValue();
        }

        public long getMisses() {
            return this.misses.getValue();
        }

        public long getPuts() {
            return this.puts.getValue();
        }

        public long getRemoves() {
            return this.removes.getValue();
        }

        public long getLoads() {
            return this.loads.getValue();
        }

        public List<SimpleTimer> getSplits() {
            return this.splits;
        }
    }

    public static interface Sizer {
        public long getCacheSize();
    }
}

