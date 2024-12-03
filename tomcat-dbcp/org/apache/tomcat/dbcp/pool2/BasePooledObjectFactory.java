/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2;

import org.apache.tomcat.dbcp.pool2.BaseObject;
import org.apache.tomcat.dbcp.pool2.PooledObject;
import org.apache.tomcat.dbcp.pool2.PooledObjectFactory;

public abstract class BasePooledObjectFactory<T>
extends BaseObject
implements PooledObjectFactory<T> {
    @Override
    public void activateObject(PooledObject<T> p) throws Exception {
    }

    public abstract T create() throws Exception;

    @Override
    public void destroyObject(PooledObject<T> p) throws Exception {
    }

    @Override
    public PooledObject<T> makeObject() throws Exception {
        return this.wrap(this.create());
    }

    @Override
    public void passivateObject(PooledObject<T> p) throws Exception {
    }

    @Override
    public boolean validateObject(PooledObject<T> p) {
        return true;
    }

    public abstract PooledObject<T> wrap(T var1);
}

