/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.com.fasterxml.jackson.core;

public interface FormatFeature {
    public boolean enabledByDefault();

    public int getMask();

    public boolean enabledIn(int var1);
}

