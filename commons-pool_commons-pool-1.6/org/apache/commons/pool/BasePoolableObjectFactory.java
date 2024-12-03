/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool;

import org.apache.commons.pool.PoolableObjectFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class BasePoolableObjectFactory<T>
implements PoolableObjectFactory<T> {
    @Override
    public abstract T makeObject() throws Exception;

    @Override
    public void destroyObject(T obj) throws Exception {
    }

    @Override
    public boolean validateObject(T obj) {
        return true;
    }

    @Override
    public void activateObject(T obj) throws Exception {
    }

    @Override
    public void passivateObject(T obj) throws Exception {
    }
}

