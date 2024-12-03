/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.configuration;

import java.io.Serializable;

public interface Configuration<K, V>
extends Serializable {
    public Class<K> getKeyType();

    public Class<V> getValueType();

    public boolean isStoreByValue();
}

