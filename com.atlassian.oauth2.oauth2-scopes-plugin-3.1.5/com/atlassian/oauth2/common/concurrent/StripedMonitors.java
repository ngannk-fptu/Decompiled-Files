/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.oauth2.common.concurrent;

import java.util.stream.IntStream;
import javax.annotation.Nonnull;

public class StripedMonitors<T> {
    private final Object[] stripes;

    public StripedMonitors(int stripeCount) {
        this.stripes = IntStream.range(0, stripeCount).mapToObj(i -> new Object()).toArray();
    }

    @Nonnull
    public Object getMonitor(T key) {
        return this.stripes[Math.floorMod(key.hashCode(), this.stripes.length)];
    }
}

