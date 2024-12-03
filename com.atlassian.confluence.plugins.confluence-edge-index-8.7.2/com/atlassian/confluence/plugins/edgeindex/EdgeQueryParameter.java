/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.plugins.edgeindex.ScoreConfig;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeTargetInfo;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class EdgeQueryParameter {
    private final long time;
    private final TimeUnit timeUnit;
    private final ScoreConfig scoreConfig;
    private final List<String> edgeTypes;
    private final int maxEdgeInfo;
    private final Predicate<EdgeTargetInfo> acceptFilter;

    @Deprecated
    public EdgeQueryParameter(long time, TimeUnit timeUnit, ScoreConfig scoreConfig, List<String> edgeTypes) {
        this.time = time;
        this.timeUnit = timeUnit;
        this.scoreConfig = scoreConfig;
        this.edgeTypes = edgeTypes;
        this.maxEdgeInfo = Integer.MAX_VALUE;
        this.acceptFilter = x -> true;
    }

    private EdgeQueryParameter(Builder builder) {
        this.time = builder.time;
        this.timeUnit = builder.timeUnit;
        this.scoreConfig = builder.scoreConfig;
        this.edgeTypes = builder.edgeTypes;
        this.maxEdgeInfo = builder.maxEdgeInfo;
        this.acceptFilter = builder.acceptFilter;
    }

    public long getTime() {
        return this.time;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    public ScoreConfig getScoreConfig() {
        return this.scoreConfig;
    }

    public List<String> getEdgeTypes() {
        return this.edgeTypes;
    }

    public int getMaxEdgeInfo() {
        return this.maxEdgeInfo;
    }

    public Predicate<EdgeTargetInfo> getAcceptFilter() {
        return this.acceptFilter;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private long time;
        private TimeUnit timeUnit;
        private ScoreConfig scoreConfig;
        private int maxEdgeInfo = Integer.MAX_VALUE;
        private List<String> edgeTypes;
        private Predicate<EdgeTargetInfo> acceptFilter = x -> true;

        public Builder since(long time, TimeUnit timeUnit) {
            this.time = time;
            this.timeUnit = timeUnit;
            return this;
        }

        public Builder withScoreConfig(ScoreConfig config) {
            this.scoreConfig = config;
            return this;
        }

        public Builder withEdgeTypes(List<String> edgeTypes) {
            this.edgeTypes = edgeTypes;
            return this;
        }

        public Builder withMaxEdgeInfo(int maxEdgeInfo) {
            this.maxEdgeInfo = maxEdgeInfo;
            return this;
        }

        public Builder withAcceptFilter(Predicate<EdgeTargetInfo> acceptFilter) {
            this.acceptFilter = acceptFilter;
            return this;
        }

        public EdgeQueryParameter build() {
            if (this.scoreConfig == null) {
                this.scoreConfig = new ScoreConfig();
            }
            return new EdgeQueryParameter(this);
        }
    }
}

