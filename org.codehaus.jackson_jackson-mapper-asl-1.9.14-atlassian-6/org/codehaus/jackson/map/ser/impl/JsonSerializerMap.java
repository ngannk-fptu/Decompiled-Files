/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.impl;

import java.util.Map;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ser.impl.SerializerCache;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class JsonSerializerMap {
    private final Bucket[] _buckets;
    private final int _size;

    public JsonSerializerMap(Map<SerializerCache.TypeKey, JsonSerializer<Object>> serializers) {
        int size;
        this._size = size = JsonSerializerMap.findSize(serializers.size());
        int hashMask = size - 1;
        Bucket[] buckets = new Bucket[size];
        for (Map.Entry<SerializerCache.TypeKey, JsonSerializer<Object>> entry : serializers.entrySet()) {
            SerializerCache.TypeKey key = entry.getKey();
            int index = key.hashCode() & hashMask;
            buckets[index] = new Bucket(buckets[index], key, entry.getValue());
        }
        this._buckets = buckets;
    }

    private static final int findSize(int size) {
        int result;
        int needed = size <= 64 ? size + size : size + (size >> 2);
        for (result = 8; result < needed; result += result) {
        }
        return result;
    }

    public int size() {
        return this._size;
    }

    public JsonSerializer<Object> find(SerializerCache.TypeKey key) {
        int index = key.hashCode() & this._buckets.length - 1;
        Bucket bucket = this._buckets[index];
        if (bucket == null) {
            return null;
        }
        if (key.equals(bucket.key)) {
            return bucket.value;
        }
        while ((bucket = bucket.next) != null) {
            if (!key.equals(bucket.key)) continue;
            return bucket.value;
        }
        return null;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class Bucket {
        public final SerializerCache.TypeKey key;
        public final JsonSerializer<Object> value;
        public final Bucket next;

        public Bucket(Bucket next, SerializerCache.TypeKey key, JsonSerializer<Object> value) {
            this.next = next;
            this.key = key;
            this.value = value;
        }
    }
}

