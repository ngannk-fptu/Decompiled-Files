/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.annotations.ValueGenerationType;
import org.hibernate.tuple.UpdateTimestampGeneration;

@ValueGenerationType(generatedBy=UpdateTimestampGeneration.class)
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD, ElementType.METHOD})
public @interface UpdateTimestamp {
}

