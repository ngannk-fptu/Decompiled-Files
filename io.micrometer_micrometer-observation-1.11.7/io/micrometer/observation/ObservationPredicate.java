/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.observation;

import io.micrometer.observation.Observation;
import java.util.function.BiPredicate;

public interface ObservationPredicate
extends BiPredicate<String, Observation.Context> {
}

