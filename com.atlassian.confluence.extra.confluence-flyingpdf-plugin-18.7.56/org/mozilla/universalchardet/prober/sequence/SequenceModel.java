/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet.prober.sequence;

public abstract class SequenceModel {
    protected short[] charToOrderMap;
    protected byte[] precedenceMatrix;
    protected float typicalPositiveRatio;
    protected boolean keepEnglishLetter;
    protected String charsetName;

    public SequenceModel(short[] charToOrderMap, byte[] precedenceMatrix, float typicalPositiveRatio, boolean keepEnglishLetter, String charsetName) {
        this.charToOrderMap = (short[])charToOrderMap.clone();
        this.precedenceMatrix = (byte[])precedenceMatrix.clone();
        this.typicalPositiveRatio = typicalPositiveRatio;
        this.keepEnglishLetter = keepEnglishLetter;
        this.charsetName = charsetName;
    }

    public short getOrder(byte b) {
        int c = b & 0xFF;
        return this.charToOrderMap[c];
    }

    public byte getPrecedence(int pos) {
        return this.precedenceMatrix[pos];
    }

    public float getTypicalPositiveRatio() {
        return this.typicalPositiveRatio;
    }

    public boolean getKeepEnglishLetter() {
        return this.keepEnglishLetter;
    }

    public String getCharsetName() {
        return this.charsetName;
    }
}

