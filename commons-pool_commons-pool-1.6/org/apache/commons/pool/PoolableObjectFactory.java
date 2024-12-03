/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface PoolableObjectFactory<T> {
    public T makeObject() throws Exception;

    public void destroyObject(T var1) throws Exception;

    public boolean validateObject(T var1);

    public void activateObject(T var1) throws Exception;

    public void passivateObject(T var1) throws Exception;
}

