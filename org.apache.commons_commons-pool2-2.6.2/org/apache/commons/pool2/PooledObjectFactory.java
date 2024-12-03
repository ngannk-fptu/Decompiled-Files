/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2;

import org.apache.commons.pool2.PooledObject;

public interface PooledObjectFactory<T> {
    public PooledObject<T> makeObject() throws Exception;

    public void destroyObject(PooledObject<T> var1) throws Exception;

    public boolean validateObject(PooledObject<T> var1);

    public void activateObject(PooledObject<T> var1) throws Exception;

    public void passivateObject(PooledObject<T> var1) throws Exception;
}

