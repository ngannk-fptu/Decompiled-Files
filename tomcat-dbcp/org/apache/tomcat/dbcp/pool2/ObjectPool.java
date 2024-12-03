/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.pool2;

import java.io.Closeable;
import org.apache.tomcat.dbcp.pool2.DestroyMode;

public interface ObjectPool<T>
extends Closeable {
    public void addObject() throws Exception;

    default public void addObjects(int count) throws Exception {
        for (int i = 0; i < count; ++i) {
            this.addObject();
        }
    }

    public T borrowObject() throws Exception;

    public void clear() throws Exception;

    @Override
    public void close();

    public int getNumActive();

    public int getNumIdle();

    public void invalidateObject(T var1) throws Exception;

    default public void invalidateObject(T obj, DestroyMode destroyMode) throws Exception {
        this.invalidateObject(obj);
    }

    public void returnObject(T var1) throws Exception;
}

