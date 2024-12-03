/*
 * Decompiled with CFR 0.152.
 */
package org.reactivestreams;

public interface Subscription {
    public void request(long var1);

    public void cancel();
}

