/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.ValueGenerationType;
import org.hibernate.tuple.ValueGenerator;
import org.hibernate.tuple.VmValueGeneration;

@ValueGenerationType(generatedBy=VmValueGeneration.class)
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD, ElementType.METHOD})
public @interface GeneratorType {
    public Class<? extends ValueGenerator<?>> type();

    public GenerationTime when() default GenerationTime.ALWAYS;
}

