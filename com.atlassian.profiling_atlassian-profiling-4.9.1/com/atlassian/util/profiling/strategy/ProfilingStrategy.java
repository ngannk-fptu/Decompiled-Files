/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.util.profiling.strategy;

@Deprecated
public interface ProfilingStrategy {
    public boolean isEnabled();

    public void start(String var1);

    public void stop(String var1);
}

