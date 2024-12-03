/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import org.aspectj.weaver.AnnotationValue;

public class EnumAnnotationValue
extends AnnotationValue {
    private String typeSignature;
    private String value;

    public EnumAnnotationValue(String typeSignature, String value) {
        super(101);
        this.typeSignature = typeSignature;
        this.value = value;
    }

    public String getType() {
        return this.typeSignature;
    }

    @Override
    public String stringify() {
        return this.typeSignature + this.value;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "E(" + this.typeSignature + " " + this.value + ")";
    }
}

