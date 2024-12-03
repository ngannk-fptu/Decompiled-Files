/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jdk.utilities.runtimeinformation;

public interface MemoryInformation {
    public String getName();

    public long getTotal();

    public long getUsed();

    public long getFree();
}

