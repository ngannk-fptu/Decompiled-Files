/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.TypePattern;

public class TypePatternImpl
implements TypePattern {
    private String typePattern;

    public TypePatternImpl(String pattern) {
        this.typePattern = pattern;
    }

    @Override
    public String asString() {
        return this.typePattern;
    }

    public String toString() {
        return this.asString();
    }
}

