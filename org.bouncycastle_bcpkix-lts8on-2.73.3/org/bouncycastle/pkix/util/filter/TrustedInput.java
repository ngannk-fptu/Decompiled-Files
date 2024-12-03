/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix.util.filter;

public class TrustedInput {
    protected Object input;

    public TrustedInput(Object input) {
        this.input = input;
    }

    public Object getInput() {
        return this.input;
    }

    public String toString() {
        return this.input.toString();
    }
}

