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
import org.hibernate.tuple.GeneratedValueGeneration;

@ValueGenerationType(generatedBy=GeneratedValueGeneration.class)
@Target(value={ElementType.FIELD, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Generated {
    public GenerationTime value();
}

