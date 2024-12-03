/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool;

import org.apache.commons.pool.KeyedPoolableObjectFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class BaseKeyedPoolableObjectFactory<K, V>
implements KeyedPoolableObjectFactory<K, V> {
    @Override
    public abstract V makeObject(K var1) throws Exception;

    @Override
    public void destroyObject(K key, V obj) throws Exception {
    }

    @Override
    public boolean validateObject(K key, V obj) {
        return true;
    }

    @Override
    public void activateObject(K key, V obj) throws Exception {
    }

    @Override
    public void passivateObject(K key, V obj) throws Exception {
    }
}

