/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.ascii;

public interface TextCommandProcessor<T> {
    public void handle(T var1);

    public void handleRejection(T var1);
}

