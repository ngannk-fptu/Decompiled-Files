/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.concurrent;

public interface FutureCallback<T> {
    public void completed(T var1);

    public void failed(Exception var1);

    public void cancelled();
}

