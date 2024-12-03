/*
 * Decompiled with CFR 0.152.
 */
package zipkin2.v1;

import zipkin2.Endpoint;
import zipkin2.internal.Nullable;

@Deprecated
public final class V1BinaryAnnotation
implements Comparable<V1BinaryAnnotation> {
    public static final int TYPE_BOOLEAN = 0;
    public static final int TYPE_STRING = 6;
    final String key;
    final String stringValue;
    final int type;
    final Endpoint endpoint;

    public static V1BinaryAnnotation createAddress(String address, Endpoint endpoint) {
        if (endpoint == null) {
            throw new NullPointerException("endpoint == null");
        }
        return new V1BinaryAnnotation(address, null, endpoint);
    }

    public static V1BinaryAnnotation createString(String key, String value, Endpoint endpoint) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }
        return new V1BinaryAnnotation(key, value, endpoint);
    }

    public String key() {
        return this.key;
    }

    public int type() {
        return this.type;
    }

    @Nullable
    public String stringValue() {
        return this.stringValue;
    }

    public Endpoint endpoint() {
        return this.endpoint;
    }

    V1BinaryAnnotation(String key, String stringValue, Endpoint endpoint) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        this.key = key;
        this.stringValue = stringValue;
        this.type = stringValue != null ? 6 : 0;
        this.endpoint = endpoint;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof V1BinaryAnnotation)) {
            return false;
        }
        V1BinaryAnnotation that = (V1BinaryAnnotation)o;
        return this.key.equals(that.key) && (this.stringValue == null ? that.stringValue == null : this.stringValue.equals(that.stringValue)) && (this.endpoint == null ? that.endpoint == null : this.endpoint.equals(that.endpoint));
    }

    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.key.hashCode();
        h *= 1000003;
        h ^= this.stringValue == null ? 0 : this.stringValue.hashCode();
        h *= 1000003;
        return h ^= this.endpoint == null ? 0 : this.endpoint.hashCode();
    }

    @Override
    public int compareTo(V1BinaryAnnotation that) {
        if (this == that) {
            return 0;
        }
        return this.key.compareTo(that.key);
    }
}

