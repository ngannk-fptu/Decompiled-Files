/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.processor;

import javax.cache.Cache;

public interface MutableEntry<K, V>
extends Cache.Entry<K, V> {
    public boolean exists();

    public void remove();

    public void setValue(V var1);
}

