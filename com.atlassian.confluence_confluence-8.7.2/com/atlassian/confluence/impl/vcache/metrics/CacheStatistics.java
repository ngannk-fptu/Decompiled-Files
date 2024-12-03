/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.impl.vcache.metrics;

import com.atlassian.confluence.impl.vcache.metrics.CacheMetricsKeys;
import com.atlassian.confluence.impl.vcache.metrics.Statistics;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class CacheStatistics
implements Statistics {
    public static final String CACHE_LAAS_ID = "cache";
    @JsonProperty
    private final String name;
    @JsonProperty
    private final List<String> tags;
    @JsonProperty
    private final String type;
    @JsonIgnore
    private final long hits;
    @JsonIgnore
    private final long misses;
    @JsonIgnore
    private final double loadTime;
    @JsonIgnore
    private final long getTime;
    @JsonIgnore
    private final long putTime;
    @JsonIgnore
    private Map<String, Object> otherStats;

    public CacheStatistics(String name, List<String> tags, String type, long hits, long misses, double loadTime, Map<String, ?> otherStats, long getTime, long putTime) {
        this.type = type;
        this.name = Objects.requireNonNull(name);
        this.tags = ImmutableList.copyOf((Collection)Objects.requireNonNull(tags));
        this.hits = hits;
        this.misses = misses;
        this.loadTime = loadTime;
        this.getTime = getTime;
        this.putTime = putTime;
        this.otherStats = this.copyNonConflictingEntries(Objects.requireNonNull(otherStats));
    }

    private Map<String, Object> copyNonConflictingEntries(Map<String, ?> otherStats) {
        HashMap<String, Object> copy = new HashMap<String, Object>(otherStats);
        Arrays.stream(CacheMetricsKeys.values()).map(CacheMetricsKeys::key).forEach(copy::remove);
        return copy;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getLoggingKey() {
        return CACHE_LAAS_ID;
    }

    @Override
    public List<String> getTags() {
        return this.tags;
    }

    @Override
    @JsonProperty
    public Object getStats() {
        return this.getStatsMap();
    }

    @JsonIgnore
    public Map<String, Object> getStatsMap() {
        if (this.hits == 0L && this.misses == 0L && this.loadTime == 0.0 && this.getTime == 0L && this.putTime == 0L && this.otherStats.size() == 0) {
            return ImmutableMap.of();
        }
        return ImmutableMap.builder().put((Object)CacheMetricsKeys.HITS.key(), (Object)this.hits).put((Object)CacheMetricsKeys.MISSES.key(), (Object)this.misses).put((Object)CacheMetricsKeys.LOAD_TIME.key(), (Object)this.loadTime).put((Object)CacheMetricsKeys.GET_TIME.key(), (Object)this.getTime).put((Object)CacheMetricsKeys.PUT_TIME.key(), (Object)this.putTime).putAll(this.otherStats).build();
    }

    public String type() {
        return this.type;
    }

    public long hits() {
        return this.hits;
    }

    public long misses() {
        return this.misses;
    }

    public double loadTime() {
        return this.loadTime;
    }

    public long getTime() {
        return this.getTime;
    }

    public long putTime() {
        return this.putTime;
    }

    public static class CacheStatisticsBuilder {
        private String name;
        private List<String> tags = Collections.emptyList();
        private String type = "?";
        private long hits;
        private long misses;
        private double loadTime;
        private long getTime;
        private long putTime;
        private Map<String, ?> otherStats = Collections.emptyMap();

        public CacheStatisticsBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CacheStatisticsBuilder withTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public CacheStatisticsBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public CacheStatisticsBuilder withHits(long hits) {
            this.hits = hits;
            return this;
        }

        public CacheStatisticsBuilder withMisses(long misses) {
            this.misses = misses;
            return this;
        }

        public CacheStatisticsBuilder withGetTime(long getTime) {
            this.getTime = getTime;
            return this;
        }

        public CacheStatisticsBuilder withPutTime(long putTime) {
            this.putTime = putTime;
            return this;
        }

        public CacheStatisticsBuilder withLoadTime(double loadTime) {
            this.loadTime = loadTime;
            return this;
        }

        public CacheStatisticsBuilder withOtherStats(Map<String, ?> otherStats) {
            this.otherStats = otherStats;
            return this;
        }

        public CacheStatistics build() {
            return new CacheStatistics(this.name, this.tags, this.type, this.hits, this.misses, this.loadTime, this.otherStats, this.getTime, this.putTime);
        }
    }
}

