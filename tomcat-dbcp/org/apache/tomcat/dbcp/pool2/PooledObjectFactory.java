/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2;

import org.apache.tomcat.dbcp.pool2.DestroyMode;
import org.apache.tomcat.dbcp.pool2.PooledObject;

public interface PooledObjectFactory<T> {
    public void activateObject(PooledObject<T> var1) throws Exception;

    public void destroyObject(PooledObject<T> var1) throws Exception;

    default public void destroyObject(PooledObject<T> p, DestroyMode destroyMode) throws Exception {
        this.destroyObject(p);
    }

    public PooledObject<T> makeObject() throws Exception;

    public void passivateObject(PooledObject<T> var1) throws Exception;

    public boolean validateObject(PooledObject<T> var1);
}

