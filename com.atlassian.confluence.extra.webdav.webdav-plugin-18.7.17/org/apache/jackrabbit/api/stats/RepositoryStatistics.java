/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.stats;

import org.apache.jackrabbit.api.stats.TimeSeries;

public interface RepositoryStatistics {
    public TimeSeries getTimeSeries(Type var1);

    public TimeSeries getTimeSeries(String var1, boolean var2);

    public static enum Type {
        BUNDLE_READ_COUNTER(true),
        BUNDLE_WRITE_COUNTER(true),
        BUNDLE_WRITE_DURATION(true),
        BUNDLE_WRITE_AVERAGE(false),
        BUNDLE_CACHE_ACCESS_COUNTER(true),
        BUNDLE_CACHE_SIZE_COUNTER(false),
        BUNDLE_CACHE_MISS_COUNTER(true),
        BUNDLE_CACHE_MISS_DURATION(true),
        BUNDLE_CACHE_MISS_AVERAGE(false),
        BUNDLE_COUNTER(true),
        BUNDLE_WS_SIZE_COUNTER(true),
        SESSION_READ_COUNTER(true),
        SESSION_READ_DURATION(true),
        SESSION_READ_AVERAGE(false),
        SESSION_WRITE_COUNTER(true),
        SESSION_WRITE_DURATION(true),
        SESSION_WRITE_AVERAGE(false),
        SESSION_LOGIN_COUNTER(true),
        SESSION_COUNT(false),
        QUERY_COUNT(true),
        QUERY_DURATION(true),
        QUERY_AVERAGE(true),
        OBSERVATION_EVENT_COUNTER(true),
        OBSERVATION_EVENT_DURATION(true),
        OBSERVATION_EVENT_AVERAGE(true);

        private final boolean resetValueEachSecond;

        private Type(boolean resetValueEachSecond) {
            this.resetValueEachSecond = resetValueEachSecond;
        }

        public static Type getType(String type) {
            Type realType = null;
            try {
                realType = Type.valueOf(type);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
            return realType;
        }

        public boolean isResetValueEachSecond() {
            return this.resetValueEachSecond;
        }
    }
}

