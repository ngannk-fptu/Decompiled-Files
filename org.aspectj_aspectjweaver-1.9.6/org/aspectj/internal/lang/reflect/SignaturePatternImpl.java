/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.internal.lang.reflect;

import org.aspectj.lang.reflect.SignaturePattern;

public class SignaturePatternImpl
implements SignaturePattern {
    private String sigPattern;

    public SignaturePatternImpl(String pattern) {
        this.sigPattern = pattern;
    }

    @Override
    public String asString() {
        return this.sigPattern;
    }

    public String toString() {
        return this.asString();
    }
}

