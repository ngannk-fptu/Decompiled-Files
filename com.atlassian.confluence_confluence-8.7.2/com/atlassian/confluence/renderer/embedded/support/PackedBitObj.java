/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.renderer.embedded.support;

class PackedBitObj {
    public int bitIndex = 0;
    public int byteIndex = 0;
    public int value = 0;
    public int nextBitIndex = 0;
    public int nextByteIndex = 0;
    public int nextByteBoundary = 0;

    public PackedBitObj(int bitMarker, int byteMarker, int decimalValue) {
        this.bitIndex = bitMarker;
        this.byteIndex = byteMarker;
        this.value = decimalValue;
        this.nextBitIndex = bitMarker;
        if (bitMarker <= 7) {
            ++this.nextBitIndex;
            this.nextByteIndex = byteMarker;
            this.nextByteBoundary = byteMarker++;
        } else {
            this.nextBitIndex = 0;
            ++this.nextByteIndex;
            this.nextByteBoundary = this.nextByteIndex;
        }
    }
}

