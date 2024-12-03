/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import java.lang.reflect.Constructor;
import org.hibernate.HibernateException;
import org.hibernate.annotations.GeneratorType;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.tuple.AnnotationValueGeneration;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.tuple.ValueGenerator;

public class VmValueGeneration
implements AnnotationValueGeneration<GeneratorType> {
    private GenerationTiming generationTiming;
    private Constructor<? extends ValueGenerator<?>> constructor;

    @Override
    public void initialize(GeneratorType annotation, Class<?> propertyType) {
        Class<? extends ValueGenerator<?>> generatorType = annotation.type();
        this.constructor = ReflectHelper.getDefaultConstructor(generatorType);
        this.generationTiming = annotation.when().getEquivalent();
    }

    @Override
    public GenerationTiming getGenerationTiming() {
        return this.generationTiming;
    }

    @Override
    public ValueGenerator<?> getValueGenerator() {
        try {
            return this.constructor.newInstance(new Object[0]);
        }
        catch (Exception e) {
            throw new HibernateException("Couldn't instantiate value generator", e);
        }
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

