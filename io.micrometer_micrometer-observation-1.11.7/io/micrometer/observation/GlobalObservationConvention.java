/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.observation;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;

public interface GlobalObservationConvention<T extends Observation.Context>
extends ObservationConvention<T> {
}

