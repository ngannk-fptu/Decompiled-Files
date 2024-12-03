/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2;

import org.apache.tomcat.dbcp.pool2.BaseObject;
import org.apache.tomcat.dbcp.pool2.KeyedPooledObjectFactory;
import org.apache.tomcat.dbcp.pool2.PooledObject;

public abstract class BaseKeyedPooledObjectFactory<K, V>
extends BaseObject
implements KeyedPooledObjectFactory<K, V> {
    @Override
    public void activateObject(K key, PooledObject<V> p) throws Exception {
    }

    public abstract V create(K var1) throws Exception;

    @Override
    public void destroyObject(K key, PooledObject<V> p) throws Exception {
    }

    @Override
    public PooledObject<V> makeObject(K key) throws Exception {
        return this.wrap(this.create(key));
    }

    @Override
    public void passivateObject(K key, PooledObject<V> p) throws Exception {
    }

    @Override
    public boolean validateObject(K key, PooledObject<V> p) {
        return true;
    }

    public abstract PooledObject<V> wrap(V var1);
}

