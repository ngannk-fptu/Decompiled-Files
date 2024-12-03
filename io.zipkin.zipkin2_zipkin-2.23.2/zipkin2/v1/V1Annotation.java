/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.v1;

import zipkin2.Endpoint;
import zipkin2.internal.Nullable;

@Deprecated
public final class V1Annotation
implements Comparable<V1Annotation> {
    final long timestamp;
    final String value;
    final Endpoint endpoint;

    public static V1Annotation create(long timestamp, String value, @Nullable Endpoint endpoint) {
        return new V1Annotation(timestamp, value, endpoint);
    }

    public long timestamp() {
        return this.timestamp;
    }

    public String value() {
        return this.value;
    }

    @Nullable
    public Endpoint endpoint() {
        return this.endpoint;
    }

    V1Annotation(long timestamp, String value, @Nullable Endpoint endpoint) {
        this.timestamp = timestamp;
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        this.value = value;
        this.endpoint = endpoint;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof V1Annotation)) {
            return false;
        }
        V1Annotation that = (V1Annotation)o;
        return this.timestamp == that.timestamp && this.value.equals(that.value) && (this.endpoint == null ? that.endpoint == null : this.endpoint.equals(that.endpoint));
    }

    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= (int)((long)h ^ (this.timestamp >>> 32 ^ this.timestamp));
        h *= 1000003;
        h ^= this.value.hashCode();
        h *= 1000003;
        return h ^= this.endpoint == null ? 0 : this.endpoint.hashCode();
    }

    @Override
    public int compareTo(V1Annotation that) {
        int byTimestamp;
        if (this == that) {
            return 0;
        }
        int n = this.timestamp < that.timestamp ? -1 : (byTimestamp = this.timestamp == that.timestamp ? 0 : 1);
        if (byTimestamp != 0) {
            return byTimestamp;
        }
        return this.value.compareTo(that.value);
    }
}

