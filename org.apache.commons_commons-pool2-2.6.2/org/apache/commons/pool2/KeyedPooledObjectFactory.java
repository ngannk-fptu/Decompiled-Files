/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2;

import org.apache.commons.pool2.PooledObject;

public interface KeyedPooledObjectFactory<K, V> {
    public PooledObject<V> makeObject(K var1) throws Exception;

    public void destroyObject(K var1, PooledObject<V> var2) throws Exception;

    public boolean validateObject(K var1, PooledObject<V> var2);

    public void activateObject(K var1, PooledObject<V> var2) throws Exception;

    public void passivateObject(K var1, PooledObject<V> var2) throws Exception;
}

