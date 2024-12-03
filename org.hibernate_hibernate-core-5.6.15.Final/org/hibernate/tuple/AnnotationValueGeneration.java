/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tuple;

import java.lang.annotation.Annotation;
import org.hibernate.tuple.ValueGeneration;

public interface AnnotationValueGeneration<A extends Annotation>
extends ValueGeneration {
    public void initialize(A var1, Class<?> var2);
}

