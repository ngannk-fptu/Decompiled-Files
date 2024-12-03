/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.pool;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface KeyedPoolableObjectFactory<K, V> {
    public V makeObject(K var1) throws Exception;

    public void destroyObject(K var1, V var2) throws Exception;

    public boolean validateObject(K var1, V var2);

    public void activateObject(K var1, V var2) throws Exception;

    public void passivateObject(K var1, V var2) throws Exception;
}

