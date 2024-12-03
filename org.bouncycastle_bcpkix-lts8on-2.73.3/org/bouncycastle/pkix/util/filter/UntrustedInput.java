/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix.util.filter;

public class UntrustedInput {
    protected Object input;

    public UntrustedInput(Object input) {
        this.input = input;
    }

    public Object getInput() {
        return this.input;
    }

    public String getString() {
        return this.input.toString();
    }

    public String toString() {
        return this.input.toString();
    }
}

