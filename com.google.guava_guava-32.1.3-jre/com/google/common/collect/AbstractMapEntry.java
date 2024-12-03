/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ParametricNullness;
import java.util.Map;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractMapEntry<K, V>
implements Map.Entry<K, V> {
    AbstractMapEntry() {
    }

    @Override
    @ParametricNullness
    public abstract K getKey();

    @Override
    @ParametricNullness
    public abstract V getValue();

    @Override
    @ParametricNullness
    public V setValue(@ParametricNullness V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(@CheckForNull Object object) {
        if (object instanceof Map.Entry) {
            Map.Entry that = (Map.Entry)object;
            return Objects.equal(this.getKey(), that.getKey()) && Objects.equal(this.getValue(), that.getValue());
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

