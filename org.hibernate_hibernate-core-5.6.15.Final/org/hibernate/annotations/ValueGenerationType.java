/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.tuple.AnnotationValueGeneration;

@Target(value={ElementType.ANNOTATION_TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface ValueGenerationType {
    public Class<? extends AnnotationValueGeneration<?>> generatedBy();
}

