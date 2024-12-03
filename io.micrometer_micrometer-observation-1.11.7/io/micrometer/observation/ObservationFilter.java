/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.observation;

import io.micrometer.observation.Observation;

public interface ObservationFilter {
    public Observation.Context map(Observation.Context var1);
}

