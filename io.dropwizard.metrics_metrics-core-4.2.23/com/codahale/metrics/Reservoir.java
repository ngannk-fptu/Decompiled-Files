/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Snapshot;

public interface Reservoir {
    public int size();

    public void update(long var1);

    public Snapshot getSnapshot();
}

