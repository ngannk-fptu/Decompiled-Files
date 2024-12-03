/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

public interface MovingAverages {
    public void tickIfNecessary();

    public void update(long var1);

    public double getM1Rate();

    public double getM5Rate();

    public double getM15Rate();
}

