/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.driver;

import com.atlassian.instrumentation.driver.Instrumentation;
import com.atlassian.instrumentation.instruments.EventType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

public class JdbcThreadLocalCollector {
    private static final ThreadLocal<List<Counter>> counts;

    public static Map<String, LongSummaryStatistics> getStatistics() {
        if (counts.get() != null) {
            List<Counter> results = counts.get();
            return results.stream().collect(Collectors.groupingBy(Counter::getSql, Collectors.summarizingLong(Counter::getTime)));
        }
        return Collections.emptyMap();
    }

    public static Map<String, Long> getMedianTime() {
        if (counts.get() != null) {
            List<Counter> results = counts.get();
            HashMap<String, Long> medians = new HashMap<String, Long>();
            results.stream().collect(Collectors.groupingBy(Counter::getSql)).forEach((key, values) -> {
                List sorted = values.stream().map(Counter::getTime).sorted().collect(Collectors.toList());
                if (sorted.size() > 1 && JdbcThreadLocalCollector.isEven(sorted.size())) {
                    Long median = ((Long)sorted.get(sorted.size() / 2 - 1) + (Long)sorted.get(sorted.size() / 2)) / 2L;
                    medians.put((String)key, median);
                } else if (sorted.size() > 1) {
                    Long median = (Long)sorted.get(sorted.size() / 2);
                    medians.put((String)key, median);
                } else {
                    medians.put((String)key, (Long)sorted.get(0));
                }
            });
            return medians;
        }
        return Collections.emptyMap();
    }

    public static void start() {
        counts.set(new ArrayList());
    }

    public static void clear() {
        counts.remove();
    }

    public static void add(String sql, long time) {
        if (counts.get() == null) {
            return;
        }
        counts.get().add(new Counter(sql, time));
    }

    private static boolean isEven(long number) {
        return number % 2L == 0L;
    }

    static {
        Instrumentation.registerFactory(context -> context.getEventType().filter(et -> et == EventType.EXECUTION).map(eventType -> {
            long start = System.nanoTime();
            return () -> JdbcThreadLocalCollector.add(context.getSql().get(), System.nanoTime() - start);
        }).orElse(() -> {}));
        counts = new ThreadLocal();
    }

    static class Counter {
        private String sql;
        private long time;

        public Counter(String sql, long totalTime) {
            this.sql = sql;
            this.time = totalTime;
        }

        public String getSql() {
            return this.sql;
        }

        public long getTime() {
            return this.time;
        }
    }
}

