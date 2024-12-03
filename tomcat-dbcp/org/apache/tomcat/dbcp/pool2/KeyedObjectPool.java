/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2;

import java.io.Closeable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.tomcat.dbcp.pool2.DestroyMode;

public interface KeyedObjectPool<K, V>
extends Closeable {
    public void addObject(K var1) throws Exception;

    default public void addObjects(Collection<K> keys, int count) throws Exception {
        if (keys == null) {
            throw new IllegalArgumentException("keys must not be null.");
        }
        for (K key : keys) {
            this.addObjects(key, count);
        }
    }

    default public void addObjects(K key, int count) throws Exception {
        if (key == null) {
            throw new IllegalArgumentException("key must not be null.");
        }
        for (int i = 0; i < count; ++i) {
            this.addObject(key);
        }
    }

    public V borrowObject(K var1) throws Exception;

    public void clear() throws Exception;

    public void clear(K var1) throws Exception;

    @Override
    public void close();

    default public List<K> getKeys() {
        return Collections.emptyList();
    }

    public int getNumActive();

    public int getNumActive(K var1);

    public int getNumIdle();

    public int getNumIdle(K var1);

    public void invalidateObject(K var1, V var2) throws Exception;

    default public void invalidateObject(K key, V obj, DestroyMode destroyMode) throws Exception {
        this.invalidateObject(key, obj);
    }

    public void returnObject(K var1, V var2) throws Exception;
}

