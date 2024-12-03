/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.storage;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import zipkin2.Annotation;
import zipkin2.Span;
import zipkin2.internal.Nullable;

public final class QueryRequest {
    final String serviceName;
    final String remoteServiceName;
    final String spanName;
    final Map<String, String> annotationQuery;
    final Long minDuration;
    final Long maxDuration;
    final long endTs;
    final long lookback;
    final int limit;

    @Nullable
    public String serviceName() {
        return this.serviceName;
    }

    @Nullable
    public String remoteServiceName() {
        return this.remoteServiceName;
    }

    @Nullable
    public String spanName() {
        return this.spanName;
    }

    public Map<String, String> annotationQuery() {
        return this.annotationQuery;
    }

    @Nullable
    public Long minDuration() {
        return this.minDuration;
    }

    @Nullable
    public Long maxDuration() {
        return this.maxDuration;
    }

    public long endTs() {
        return this.endTs;
    }

    public long lookback() {
        return this.lookback;
    }

    public int limit() {
        return this.limit;
    }

    @Nullable
    public String annotationQueryString() {
        StringBuilder result = new StringBuilder();
        Iterator<Map.Entry<String, String>> i = this.annotationQuery().entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, String> next = i.next();
            result.append(next.getKey());
            if (!next.getValue().isEmpty()) {
                result.append('=').append(next.getValue());
            }
            if (!i.hasNext()) continue;
            result.append(" and ");
        }
        return result.length() > 0 ? result.toString() : null;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean test(List<Span> spans) {
        long timestamp = 0L;
        for (Span span : spans) {
            if (span.timestampAsLong() == 0L) continue;
            if (span.parentId() == null) {
                timestamp = span.timestampAsLong();
                break;
            }
            if (timestamp != 0L && timestamp <= span.timestampAsLong()) continue;
            timestamp = span.timestampAsLong();
        }
        if (timestamp == 0L || timestamp < (this.endTs() - this.lookback()) * 1000L || timestamp > this.endTs() * 1000L) {
            return false;
        }
        boolean testedDuration = this.minDuration() == null && this.maxDuration() == null;
        String serviceNameToMatch = this.serviceName();
        String remoteServiceNameToMatch = this.remoteServiceName();
        String spanNameToMatch = this.spanName();
        LinkedHashMap<String, String> annotationQueryRemaining = new LinkedHashMap<String, String>(this.annotationQuery());
        for (Span span : spans) {
            String localServiceName = span.localServiceName();
            if (this.serviceName() != null && !this.serviceName().equals(localServiceName)) continue;
            serviceNameToMatch = null;
            for (Annotation annotation : span.annotations()) {
                if (!"".equals(annotationQueryRemaining.get(annotation.value()))) continue;
                annotationQueryRemaining.remove(annotation.value());
            }
            for (Map.Entry entry : span.tags().entrySet()) {
                String value = (String)annotationQueryRemaining.get(entry.getKey());
                if (value == null || !value.isEmpty() && !value.equals(entry.getValue())) continue;
                annotationQueryRemaining.remove(entry.getKey());
            }
            if (remoteServiceNameToMatch != null && remoteServiceNameToMatch.equals(span.remoteServiceName())) {
                remoteServiceNameToMatch = null;
            }
            if (spanNameToMatch != null && spanNameToMatch.equals(span.name())) {
                spanNameToMatch = null;
            }
            if (testedDuration) continue;
            if (this.minDuration() != null && this.maxDuration() != null) {
                testedDuration = span.durationAsLong() >= this.minDuration() && span.durationAsLong() <= this.maxDuration();
                continue;
            }
            if (this.minDuration() == null) continue;
            testedDuration = span.durationAsLong() >= this.minDuration();
        }
        return (this.serviceName() == null || serviceNameToMatch == null) && remoteServiceNameToMatch == null && spanNameToMatch == null && annotationQueryRemaining.isEmpty() && testedDuration;
    }

    QueryRequest(@Nullable String serviceName, @Nullable String remoteServiceName, @Nullable String spanName, Map<String, String> annotationQuery, @Nullable Long minDuration, @Nullable Long maxDuration, long endTs, long lookback, int limit) {
        this.serviceName = serviceName;
        this.remoteServiceName = remoteServiceName;
        this.spanName = spanName;
        this.annotationQuery = annotationQuery;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.endTs = endTs;
        this.lookback = lookback;
        this.limit = limit;
    }

    public String toString() {
        String result = "QueryRequest{";
        result = result + "endTs=" + this.endTs + ", ";
        result = result + "lookback=" + this.lookback + ", ";
        if (this.serviceName != null) {
            result = result + "serviceName=" + this.serviceName + ", ";
        }
        if (this.remoteServiceName != null) {
            result = result + "remoteServiceName=" + this.remoteServiceName + ", ";
        }
        if (this.spanName != null) {
            result = result + "spanName=" + this.spanName + ", ";
        }
        if (!this.annotationQuery.isEmpty()) {
            result = result + "annotationQuery=" + this.annotationQuery + ", ";
        }
        if (this.minDuration != null) {
            result = result + "minDuration=" + this.minDuration + ", ";
        }
        if (this.maxDuration != null) {
            result = result + "maxDuration=" + this.maxDuration + ", ";
        }
        return result + "limit=" + this.limit + "}";
    }

    public static final class Builder {
        String serviceName;
        String remoteServiceName;
        String spanName;
        Map<String, String> annotationQuery = Collections.emptyMap();
        Long minDuration;
        Long maxDuration;
        long endTs;
        long lookback;
        int limit;

        Builder(QueryRequest source) {
            this.serviceName = source.serviceName;
            this.remoteServiceName = source.remoteServiceName;
            this.spanName = source.spanName;
            this.annotationQuery = source.annotationQuery;
            this.minDuration = source.minDuration;
            this.maxDuration = source.maxDuration;
            this.endTs = source.endTs;
            this.lookback = source.lookback;
            this.limit = source.limit;
        }

        public Builder serviceName(@Nullable String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder remoteServiceName(@Nullable String remoteServiceName) {
            this.remoteServiceName = remoteServiceName;
            return this;
        }

        public Builder spanName(@Nullable String spanName) {
            this.spanName = spanName;
            return this;
        }

        public Builder parseAnnotationQuery(@Nullable String annotationQuery) {
            if (annotationQuery == null || annotationQuery.isEmpty()) {
                return this;
            }
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
            for (String ann : annotationQuery.split(" and ", 100)) {
                int idx = ann.indexOf(61);
                if (idx == -1) {
                    if (map.containsKey(ann = ann.trim())) continue;
                    map.put(ann, "");
                    continue;
                }
                String[] keyValue = ann.split("=", 2);
                map.put(ann.substring(0, idx).trim(), keyValue.length < 2 ? "" : ann.substring(idx + 1).trim());
            }
            return this.annotationQuery(map);
        }

        public Builder annotationQuery(Map<String, String> annotationQuery) {
            if (annotationQuery == null) {
                throw new NullPointerException("annotationQuery == null");
            }
            this.annotationQuery = annotationQuery;
            return this;
        }

        public Builder minDuration(@Nullable Long minDuration) {
            this.minDuration = minDuration;
            return this;
        }

        public Builder maxDuration(@Nullable Long maxDuration) {
            this.maxDuration = maxDuration;
            return this;
        }

        public Builder endTs(long endTs) {
            this.endTs = endTs;
            return this;
        }

        public Builder lookback(long lookback) {
            this.lookback = lookback;
            return this;
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public final QueryRequest build() {
            if (this.serviceName != null) {
                this.serviceName = this.serviceName.toLowerCase(Locale.ROOT);
            }
            if (this.remoteServiceName != null) {
                this.remoteServiceName = this.remoteServiceName.toLowerCase(Locale.ROOT);
            }
            if (this.spanName != null) {
                this.spanName = this.spanName.toLowerCase(Locale.ROOT);
            }
            this.annotationQuery.remove("");
            if ("".equals(this.serviceName)) {
                this.serviceName = null;
            }
            if ("".equals(this.remoteServiceName)) {
                this.remoteServiceName = null;
            }
            if ("".equals(this.spanName) || "all".equals(this.spanName)) {
                this.spanName = null;
            }
            if (this.endTs <= 0L) {
                throw new IllegalArgumentException("endTs <= 0");
            }
            if (this.limit <= 0) {
                throw new IllegalArgumentException("limit <= 0");
            }
            if (this.lookback <= 0L) {
                throw new IllegalArgumentException("lookback <= 0");
            }
            if (this.minDuration != null) {
                if (this.minDuration <= 0L) {
                    throw new IllegalArgumentException("minDuration <= 0");
                }
                if (this.maxDuration != null && this.maxDuration < this.minDuration) {
                    throw new IllegalArgumentException("maxDuration < minDuration");
                }
            } else if (this.maxDuration != null) {
                throw new IllegalArgumentException("maxDuration is only valid with minDuration");
            }
            return new QueryRequest(this.serviceName, this.remoteServiceName, this.spanName, this.annotationQuery, this.minDuration, this.maxDuration, this.endTs, this.lookback, this.limit);
        }

        Builder() {
        }
    }
}

