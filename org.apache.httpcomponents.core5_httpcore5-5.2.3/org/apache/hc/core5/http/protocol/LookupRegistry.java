/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.protocol;

public interface LookupRegistry<T> {
    public void register(String var1, T var2);

    public T lookup(String var1);

    public void unregister(String var1);
}

