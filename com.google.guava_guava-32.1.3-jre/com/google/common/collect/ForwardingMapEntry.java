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
import com.google.common.collect.ForwardingObject;
import com.google.common.collect.ParametricNullness;
import java.util.Map;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingMapEntry<K, V>
extends ForwardingObject
implements Map.Entry<K, V> {
    protected ForwardingMapEntry() {
    }

    @Override
    protected abstract Map.Entry<K, V> delegate();

    @Override
    @ParametricNullness
    public K getKey() {
        return this.delegate().getKey();
    }

    @Override
    @ParametricNullness
    public V getValue() {
        return this.delegate().getValue();
    }

    @Override
    @ParametricNullness
    public V setValue(@ParametricNullness V value) {
        return this.delegate().setValue(value);
    }

    @Override
    public boolean equals(@CheckForNull Object object) {
        return this.delegate().equals(object);
    }

    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }

    protected boolean standardEquals(@CheckForNull Object object) {
        if (object instanceof Map.Entry) {
            Map.Entry that = (Map.Entry)object;
            return Objects.equal(this.getKey(), that.getKey()) && Objects.equal(this.getValue(), that.getValue());
        }
        return false;
    }

    protected int standardHashCode() {
        K k = this.getKey();
        V v = this.getValue();
        return (k == null ? 0 : k.hashCode()) ^ (v == null ? 0 : v.hashCode());
    }

    protected String standardToString() {
        return this.getKey() + "=" + this.getValue();
    }
}

