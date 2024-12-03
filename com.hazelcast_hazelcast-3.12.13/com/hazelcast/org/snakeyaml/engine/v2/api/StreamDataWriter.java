/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api;

public interface StreamDataWriter {
    default public void flush() {
    }

    public void write(String var1);

    public void write(String var1, int var2, int var3);
}

