/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

public final class BufferRecycler {
    private char[] mSmallCBuffer = null;
    private char[] mMediumCBuffer = null;
    private char[] mFullCBuffer = null;
    private byte[] mFullBBuffer = null;

    public char[] getSmallCBuffer(int minSize) {
        char[] result = null;
        if (this.mSmallCBuffer != null && this.mSmallCBuffer.length >= minSize) {
            result = this.mSmallCBuffer;
            this.mSmallCBuffer = null;
        }
        return result;
    }

    public void returnSmallCBuffer(char[] buffer) {
        this.mSmallCBuffer = buffer;
    }

    public char[] getMediumCBuffer(int minSize) {
        char[] result = null;
        if (this.mMediumCBuffer != null && this.mMediumCBuffer.length >= minSize) {
            result = this.mMediumCBuffer;
            this.mMediumCBuffer = null;
        }
        return result;
    }

    public void returnMediumCBuffer(char[] buffer) {
        this.mMediumCBuffer = buffer;
    }

    public char[] getFullCBuffer(int minSize) {
        char[] result = null;
        if (this.mFullCBuffer != null && this.mFullCBuffer.length >= minSize) {
            result = this.mFullCBuffer;
            this.mFullCBuffer = null;
        }
        return result;
    }

    public void returnFullCBuffer(char[] buffer) {
        this.mFullCBuffer = buffer;
    }

    public byte[] getFullBBuffer(int minSize) {
        byte[] result = null;
        if (this.mFullBBuffer != null && this.mFullBBuffer.length >= minSize) {
            result = this.mFullBBuffer;
            this.mFullBBuffer = null;
        }
        return result;
    }

    public void returnFullBBuffer(byte[] buffer) {
        this.mFullBBuffer = buffer;
    }
}

