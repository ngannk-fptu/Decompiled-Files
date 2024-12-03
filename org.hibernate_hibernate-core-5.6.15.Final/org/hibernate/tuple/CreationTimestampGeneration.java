/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.tuple.AnnotationValueGeneration;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.TimestampGenerators;
import org.hibernate.tuple.ValueGenerator;

public class CreationTimestampGeneration
implements AnnotationValueGeneration<CreationTimestamp> {
    private ValueGenerator<?> generator;

    @Override
    public void initialize(CreationTimestamp annotation, Class<?> propertyType) {
        this.generator = TimestampGenerators.get(propertyType);
    }

    @Override
    public GenerationTiming getGenerationTiming() {
        return GenerationTiming.INSERT;
    }

    @Override
    public ValueGenerator<?> getValueGenerator() {
        return this.generator;
    }

    @Override
    public boolean referenceColumnInSql() {
        return false;
    }

    @Override
    public String getDatabaseGeneratedReferencedColumnValue() {
        return null;
    }
}

