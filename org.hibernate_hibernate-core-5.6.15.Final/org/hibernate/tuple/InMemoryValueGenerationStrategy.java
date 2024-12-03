/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.ValueGenerator;

public interface InMemoryValueGenerationStrategy {
    public GenerationTiming getGenerationTiming();

    public ValueGenerator getValueGenerator();
}

