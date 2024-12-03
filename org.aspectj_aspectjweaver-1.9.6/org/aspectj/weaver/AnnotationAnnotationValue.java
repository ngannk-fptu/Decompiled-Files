/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.AnnotationValue;

public class AnnotationAnnotationValue
extends AnnotationValue {
    private AnnotationAJ value;

    public AnnotationAnnotationValue(AnnotationAJ value) {
        super(64);
        this.value = value;
    }

    public AnnotationAJ getAnnotation() {
        return this.value;
    }

    @Override
    public String stringify() {
        return this.value.stringify();
    }

    public String toString() {
        return this.value.toString();
    }
}

