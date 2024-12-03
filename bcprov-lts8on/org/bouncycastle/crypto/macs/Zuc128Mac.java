/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.Zuc128CoreEngine;

public final class Zuc128Mac
implements Mac {
    private static final int TOPBIT = 128;
    private final InternalZuc128Engine theEngine = new InternalZuc128Engine();
    private int theMac;
    private final int[] theKeyStream = new int[2];
    private Zuc128CoreEngine theState;
    private int theWordIndex;
    private int theByteIndex;

    @Override
    public String getAlgorithmName() {
        return "Zuc128Mac";
    }

    @Override
    public int getMacSize() {
        return 4;
    }

    @Override
    public void init(CipherParameters pParams) {
        this.theEngine.init(true, pParams);
        this.theState = (Zuc128CoreEngine)this.theEngine.copy();
        this.initKeyStream();
    }

    private void initKeyStream() {
        this.theMac = 0;
        for (int i = 0; i < this.theKeyStream.length - 1; ++i) {
            this.theKeyStream[i] = this.theEngine.createKeyStreamWord();
        }
        this.theWordIndex = this.theKeyStream.length - 1;
        this.theByteIndex = 3;
    }

    @Override
    public void update(byte in) {
        this.shift4NextByte();
        int bitBase = this.theByteIndex * 8;
        int bitMask = 128;
        int bitNo = 0;
        while (bitMask > 0) {
            if ((in & bitMask) != 0) {
                this.updateMac(bitBase + bitNo);
            }
            bitMask >>= 1;
            ++bitNo;
        }
    }

    private void shift4NextByte() {
        this.theByteIndex = (this.theByteIndex + 1) % 4;
        if (this.theByteIndex == 0) {
            this.theKeyStream[this.theWordIndex] = this.theEngine.createKeyStreamWord();
            this.theWordIndex = (this.theWordIndex + 1) % this.theKeyStream.length;
        }
    }

    private void updateMac(int bitNo) {
        this.theMac ^= this.getKeyStreamWord(bitNo);
    }

    private int getKeyStreamWord(int bitNo) {
        int myFirst = this.theKeyStream[this.theWordIndex];
        if (bitNo == 0) {
            return myFirst;
        }
        int mySecond = this.theKeyStream[(this.theWordIndex + 1) % this.theKeyStream.length];
        return myFirst << bitNo | mySecond >>> 32 - bitNo;
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        for (int byteNo = 0; byteNo < len; ++byteNo) {
            this.update(in[inOff + byteNo]);
        }
    }

    private int getFinalWord() {
        if (this.theByteIndex != 0) {
            return this.theEngine.createKeyStreamWord();
        }
        this.theWordIndex = (this.theWordIndex + 1) % this.theKeyStream.length;
        return this.theKeyStream[this.theWordIndex];
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        this.shift4NextByte();
        this.theMac ^= this.getKeyStreamWord(this.theByteIndex * 8);
        this.theMac ^= this.getFinalWord();
        Zuc128CoreEngine.encode32be(this.theMac, out, outOff);
        this.reset();
        return this.getMacSize();
    }

    @Override
    public void reset() {
        if (this.theState != null) {
            this.theEngine.reset(this.theState);
        }
        this.initKeyStream();
    }

    private static class InternalZuc128Engine
    extends Zuc128CoreEngine {
        private InternalZuc128Engine() {
        }

        int createKeyStreamWord() {
            return super.makeKeyStreamWord();
        }
    }
}

