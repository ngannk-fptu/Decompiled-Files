/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import java.io.Serializable;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.ValueGenerator;

public interface ValueGeneration
extends Serializable {
    public GenerationTiming getGenerationTiming();

    public ValueGenerator<?> getValueGenerator();

    public boolean referenceColumnInSql();

    public String getDatabaseGeneratedReferencedColumnValue();

    default public boolean timingMatches(GenerationTiming timing) {
        GenerationTiming generationTiming = this.getGenerationTiming();
        return timing == GenerationTiming.INSERT && generationTiming.includesInsert() || timing == GenerationTiming.ALWAYS && generationTiming.includesUpdate();
    }
}

