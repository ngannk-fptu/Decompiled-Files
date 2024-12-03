/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

public interface Offloadable {
    public static final String NO_OFFLOADING = "no-offloading";
    public static final String OFFLOADABLE_EXECUTOR = "hz:offloadable";

    public String getExecutorName();
}

