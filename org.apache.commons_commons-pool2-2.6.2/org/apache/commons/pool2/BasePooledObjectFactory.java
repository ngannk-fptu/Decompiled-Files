/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool2;

import org.apache.commons.pool2.BaseObject;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;

public abstract class BasePooledObjectFactory<T>
extends BaseObject
implements PooledObjectFactory<T> {
    public abstract T create() throws Exception;

    public abstract PooledObject<T> wrap(T var1);

    @Override
    public PooledObject<T> makeObject() throws Exception {
        return this.wrap(this.create());
    }

    @Override
    public void destroyObject(PooledObject<T> p) throws Exception {
    }

    @Override
    public boolean validateObject(PooledObject<T> p) {
        return true;
    }

    @Override
    public void activateObject(PooledObject<T> p) throws Exception {
    }

    @Override
    public void passivateObject(PooledObject<T> p) throws Exception {
    }
}

