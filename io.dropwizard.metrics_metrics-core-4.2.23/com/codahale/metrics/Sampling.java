/*
 * Decompiled with CFR 0.152.
 */
package com.codahale.metrics;

import com.codahale.metrics.Snapshot;

public interface Sampling {
    public Snapshot getSnapshot();
}

