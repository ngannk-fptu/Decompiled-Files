/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$Nullable;
import com.google.inject.internal.util.$Objects;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class $AbstractMapEntry<K, V>
implements Map.Entry<K, V> {
    @Override
    public abstract K getKey();

    @Override
    public abstract V getValue();

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(@$Nullable Object object) {
        if (object instanceof Map.Entry) {
            Map.Entry that = (Map.Entry)object;
            return $Objects.equal(this.getKey(), that.getKey()) && $Objects.equal(this.getValue(), that.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        K k = this.getKey();
        V v = this.getValue();
        return (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
    }

    public String toString() {
        return this.getKey() + "=" + this.getValue();
    }
}

