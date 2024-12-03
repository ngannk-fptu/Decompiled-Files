/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.util;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.util.AbstractPartition;
import java.util.List;

public class MeterPartition
extends AbstractPartition<Meter> {
    public MeterPartition(List<Meter> meters, int partitionSize) {
        super(meters, partitionSize);
    }

    public MeterPartition(MeterRegistry registry, int partitionSize) {
        this(registry.getMeters(), partitionSize);
    }

    public static List<List<Meter>> partition(MeterRegistry registry, int partitionSize) {
        return new MeterPartition(registry, partitionSize);
    }
}

