/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.metrics.renderers;

public interface ProbeRenderer {
    public void renderLong(String var1, long var2);

    public void renderDouble(String var1, double var2);

    public void renderException(String var1, Exception var2);

    public void renderNoValue(String var1);
}

