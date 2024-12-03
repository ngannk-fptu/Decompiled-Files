/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 */
package com.atlassian.crowd.common.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TimedCalls {
    private static ThreadLocal<TimedCalls> timedCalls = new ThreadLocal();
    private final long startTime = System.currentTimeMillis();
    private final Map<String, Stats> durations = new LinkedHashMap<String, Stats>();
    private String currentTimer;
    private long currentTimerStartMs;

    public void addDuration(String name, long duration) {
        this.durations.computeIfAbsent(name, ignore -> new Stats()).add(duration);
    }

    public void startTimer(String name) {
        this.finishTimerIfPresent();
        this.currentTimer = name;
        this.currentTimerStartMs = System.currentTimeMillis();
    }

    public void finishTimer() {
        this.addDuration(this.currentTimer, System.currentTimeMillis() - this.currentTimerStartMs);
        this.currentTimer = null;
    }

    private void finishTimerIfPresent() {
        if (this.currentTimer != null) {
            this.finishTimer();
        }
    }

    public Map<String, Long> createDurationMap() {
        this.finishTimerIfPresent();
        return ImmutableMap.copyOf((Map)Maps.transformValues(this.durations, s -> s.timeMs));
    }

    public List<String> getTopTimersToString(int count) {
        return this.getTopTimersToString(System.currentTimeMillis() - this.startTime, count);
    }

    public List<String> getTopTimersToString(long totalTimeMs, int count) {
        return this.durations.entrySet().stream().sorted(Comparator.comparing(entry -> ((Stats)entry.getValue()).timeMs).reversed()).limit(count).map(entry -> (String)entry.getKey() + ": " + ((Stats)entry.getValue()).describe(totalTimeMs)).collect(Collectors.toList());
    }

    public static TimedCalls getInstance() {
        return timedCalls.get();
    }

    public static TimedCalls clearInstance() {
        TimedCalls result = timedCalls.get();
        timedCalls.remove();
        return result;
    }

    public static TimedCalls createInstanceForCurrentThread() {
        TimedCalls result = new TimedCalls();
        timedCalls.set(result);
        return result;
    }

    private static class Stats {
        long timeMs = 0L;
        int count = 0;

        private Stats() {
        }

        public void add(long durationMs) {
            this.timeMs += durationMs;
            ++this.count;
        }

        public String describe(long totalTimeMs) {
            return "total: " + this.timeMs + " count: " + this.count + " avg: " + this.timeMs / (long)this.count + " percentage: " + this.timeMs * 100L / totalTimeMs;
        }
    }
}

