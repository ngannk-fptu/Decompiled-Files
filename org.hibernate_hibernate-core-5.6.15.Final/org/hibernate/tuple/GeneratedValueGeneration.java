/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import org.hibernate.annotations.Generated;
import org.hibernate.tuple.AnnotationValueGeneration;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.ValueGenerator;

public class GeneratedValueGeneration
implements AnnotationValueGeneration<Generated> {
    private GenerationTiming timing;

    public GeneratedValueGeneration() {
    }

    public GeneratedValueGeneration(GenerationTiming timing) {
        this.timing = timing;
    }

    @Override
    public void initialize(Generated annotation, Class<?> propertyType) {
        this.timing = annotation.value().getEquivalent();
    }

    @Override
    public GenerationTiming getGenerationTiming() {
        return this.timing;
    }

    @Override
    public ValueGenerator<?> getValueGenerator() {
        return null;
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

