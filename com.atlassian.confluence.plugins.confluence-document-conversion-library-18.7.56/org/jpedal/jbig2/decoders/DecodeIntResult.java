/*
 * Decompiled with CFR 0.152.
 */
package org.jpedal.jbig2.decoders;

public class DecodeIntResult {
    private int intResult;
    private boolean booleanResult;

    public DecodeIntResult(int n, boolean bl) {
        this.intResult = n;
        this.booleanResult = bl;
    }

    public int intResult() {
        return this.intResult;
    }

    public boolean booleanResult() {
        return this.booleanResult;
    }
}

