/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2;

import org.apache.tomcat.dbcp.pool2.DestroyMode;
import org.apache.tomcat.dbcp.pool2.PooledObject;

public interface KeyedPooledObjectFactory<K, V> {
    public void activateObject(K var1, PooledObject<V> var2) throws Exception;

    public void destroyObject(K var1, PooledObject<V> var2) throws Exception;

    default public void destroyObject(K key, PooledObject<V> p, DestroyMode destroyMode) throws Exception {
        this.destroyObject(key, p);
    }

    public PooledObject<V> makeObject(K var1) throws Exception;

    public void passivateObject(K var1, PooledObject<V> var2) throws Exception;

    public boolean validateObject(K var1, PooledObject<V> var2);
}

