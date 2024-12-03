/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.AnnotationValue;

public class ClassAnnotationValue
extends AnnotationValue {
    private String signature;

    public ClassAnnotationValue(String sig) {
        super(99);
        this.signature = sig;
    }

    @Override
    public String stringify() {
        return this.signature;
    }

    public String toString() {
        return this.signature;
    }
}

