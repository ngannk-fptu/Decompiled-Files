/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import zipkin2.Endpoint;
import zipkin2.internal.HexCodec;
import zipkin2.internal.Nullable;
import zipkin2.v1.V1Annotation;
import zipkin2.v1.V1BinaryAnnotation;

@Deprecated
public final class V1Span {
    static final Endpoint EMPTY_ENDPOINT = Endpoint.newBuilder().build();
    final long traceIdHigh;
    final long traceId;
    final long id;
    final String name;
    final long parentId;
    final long timestamp;
    final long duration;
    final List<V1Annotation> annotations;
    final List<V1BinaryAnnotation> binaryAnnotations;
    final Boolean debug;

    public long traceIdHigh() {
        return this.traceIdHigh;
    }

    public long traceId() {
        return this.traceId;
    }

    public long id() {
        return this.id;
    }

    public String name() {
        return this.name;
    }

    public long parentId() {
        return this.parentId;
    }

    public long timestamp() {
        return this.timestamp;
    }

    public long duration() {
        return this.duration;
    }

    public List<V1Annotation> annotations() {
        return this.annotations;
    }

    public List<V1BinaryAnnotation> binaryAnnotations() {
        return this.binaryAnnotations;
    }

    public Boolean debug() {
        return this.debug;
    }

    public Set<String> serviceNames() {
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        for (V1Annotation v1Annotation : this.annotations) {
            if (v1Annotation.endpoint == null || v1Annotation.endpoint.serviceName() == null) continue;
            result.add(v1Annotation.endpoint.serviceName());
        }
        for (V1BinaryAnnotation v1BinaryAnnotation : this.binaryAnnotations) {
            if (v1BinaryAnnotation.endpoint == null || v1BinaryAnnotation.endpoint.serviceName() == null) continue;
            result.add(v1BinaryAnnotation.endpoint.serviceName());
        }
        return result;
    }

    V1Span(Builder builder) {
        if (builder.traceId == 0L) {
            throw new IllegalArgumentException("traceId == 0");
        }
        if (builder.id == 0L) {
            throw new IllegalArgumentException("id == 0");
        }
        this.traceId = builder.traceId;
        this.traceIdHigh = builder.traceIdHigh;
        this.name = builder.name;
        this.id = builder.id;
        this.parentId = builder.parentId;
        this.timestamp = builder.timestamp;
        this.duration = builder.duration;
        this.annotations = V1Span.sortedList(builder.annotations);
        this.binaryAnnotations = V1Span.sortedList(builder.binaryAnnotations);
        this.debug = builder.debug;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof V1Span)) {
            return false;
        }
        V1Span that = (V1Span)o;
        return this.traceIdHigh == that.traceIdHigh && this.traceId == that.traceId && (this.name == null ? that.name == null : this.name.equals(that.name)) && this.id == that.id && this.parentId == that.parentId && this.timestamp == that.timestamp && this.duration == that.duration && this.annotations.equals(that.annotations) && this.binaryAnnotations.equals(that.binaryAnnotations) && (this.debug == null ? that.debug == null : this.debug.equals(that.debug));
    }

    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= (int)((long)h ^ (this.traceIdHigh >>> 32 ^ this.traceIdHigh));
        h *= 1000003;
        h ^= (int)((long)h ^ (this.traceId >>> 32 ^ this.traceId));
        h *= 1000003;
        h ^= this.name == null ? 0 : this.name.hashCode();
        h *= 1000003;
        h ^= (int)((long)h ^ (this.id >>> 32 ^ this.id));
        h *= 1000003;
        h ^= (int)((long)h ^ (this.parentId >>> 32 ^ this.parentId));
        h *= 1000003;
        h ^= (int)((long)h ^ (this.timestamp >>> 32 ^ this.timestamp));
        h *= 1000003;
        h ^= (int)((long)h ^ (this.duration >>> 32 ^ this.duration));
        h *= 1000003;
        h ^= this.annotations.hashCode();
        h *= 1000003;
        h ^= this.binaryAnnotations.hashCode();
        h *= 1000003;
        return h ^= this.debug == null ? 0 : this.debug.hashCode();
    }

    static <T extends Comparable<T>> List<T> sortedList(List<T> input) {
        if (input == null) {
            return Collections.emptyList();
        }
        Collections.sort(input);
        return Collections.unmodifiableList(new ArrayList<T>(input));
    }

    public static final class Builder {
        long traceIdHigh;
        long traceId;
        long parentId;
        long id;
        String name;
        long timestamp;
        long duration;
        ArrayList<V1Annotation> annotations;
        ArrayList<V1BinaryAnnotation> binaryAnnotations;
        Boolean debug;

        public long traceIdHigh() {
            return this.traceIdHigh;
        }

        public long traceId() {
            return this.traceId;
        }

        public long id() {
            return this.id;
        }

        Builder() {
        }

        public Builder clear() {
            this.id = 0L;
            this.traceIdHigh = 0L;
            this.traceId = 0L;
            this.name = null;
            this.duration = 0L;
            this.timestamp = 0L;
            this.parentId = 0L;
            if (this.annotations != null) {
                this.annotations.clear();
            }
            if (this.binaryAnnotations != null) {
                this.binaryAnnotations.clear();
            }
            this.debug = null;
            return this;
        }

        public Builder traceId(String traceId) {
            if (traceId == null) {
                throw new NullPointerException("traceId == null");
            }
            if (traceId.length() == 32) {
                this.traceIdHigh = HexCodec.lowerHexToUnsignedLong(traceId, 0);
            }
            this.traceId = HexCodec.lowerHexToUnsignedLong(traceId);
            return this;
        }

        public Builder traceId(long traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder traceIdHigh(long traceIdHigh) {
            this.traceIdHigh = traceIdHigh;
            return this;
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder id(String id) {
            if (id == null) {
                throw new NullPointerException("id == null");
            }
            this.id = HexCodec.lowerHexToUnsignedLong(id);
            return this;
        }

        public Builder parentId(String parentId) {
            this.parentId = parentId != null ? HexCodec.lowerHexToUnsignedLong(parentId) : 0L;
            return this;
        }

        public Builder parentId(long parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder name(String name) {
            this.name = name == null || name.isEmpty() ? null : name.toLowerCase(Locale.ROOT);
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder addAnnotation(long timestamp, String value, @Nullable Endpoint endpoint) {
            if (this.annotations == null) {
                this.annotations = new ArrayList(4);
            }
            if (EMPTY_ENDPOINT.equals(endpoint)) {
                endpoint = null;
            }
            this.annotations.add(new V1Annotation(timestamp, value, endpoint));
            return this;
        }

        public Builder addBinaryAnnotation(String address, Endpoint endpoint) {
            if (endpoint == null || EMPTY_ENDPOINT.equals(endpoint)) {
                return this;
            }
            if (this.binaryAnnotations == null) {
                this.binaryAnnotations = new ArrayList(4);
            }
            this.binaryAnnotations.add(new V1BinaryAnnotation(address, null, endpoint));
            return this;
        }

        public Builder addBinaryAnnotation(String key, String value, Endpoint endpoint) {
            if (value == null) {
                throw new NullPointerException("value == null");
            }
            if (EMPTY_ENDPOINT.equals(endpoint)) {
                endpoint = null;
            }
            if (this.binaryAnnotations == null) {
                this.binaryAnnotations = new ArrayList(4);
            }
            this.binaryAnnotations.add(new V1BinaryAnnotation(key, value, endpoint));
            return this;
        }

        public Builder debug(@Nullable Boolean debug) {
            this.debug = debug;
            return this;
        }

        public V1Span build() {
            return new V1Span(this);
        }
    }
}

