/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.core.instrument.noop;

import io.micrometer.core.instrument.AbstractMeter;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import java.util.Collections;
import java.util.List;

public class NoopMeter
extends AbstractMeter {
    public NoopMeter(Meter.Id id) {
        super(id);
    }

    public List<Measurement> measure() {
        return Collections.emptyList();
    }
}

