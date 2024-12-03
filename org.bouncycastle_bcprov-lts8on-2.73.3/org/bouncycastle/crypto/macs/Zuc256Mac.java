/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.engines.Zuc256CoreEngine;

public final class Zuc256Mac
implements Mac {
    private static final int TOPBIT = 128;
    private final InternalZuc256Engine theEngine;
    private final int theMacLength;
    private final int[] theMac;
    private final int[] theKeyStream;
    private Zuc256CoreEngine theState;
    private int theWordIndex;
    private int theByteIndex;

    public Zuc256Mac(int pLength) {
        this.theEngine = new InternalZuc256Engine(pLength);
        this.theMacLength = pLength;
        int numWords = pLength / 32;
        this.theMac = new int[numWords];
        this.theKeyStream = new int[numWords + 1];
    }

    @Override
    public String getAlgorithmName() {
        return "Zuc256Mac-" + this.theMacLength;
    }

    @Override
    public int getMacSize() {
        return this.theMacLength / 8;
    }

    @Override
    public void init(CipherParameters pParams) {
        this.theEngine.init(true, pParams);
        this.theState = (Zuc256CoreEngine)this.theEngine.copy();
        this.initKeyStream();
    }

    private void initKeyStream() {
        int i;
        for (i = 0; i < this.theMac.length; ++i) {
            this.theMac[i] = this.theEngine.createKeyStreamWord();
        }
        for (i = 0; i < this.theKeyStream.length - 1; ++i) {
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

    private void shift4Final() {
        this.theByteIndex = (this.theByteIndex + 1) % 4;
        if (this.theByteIndex == 0) {
            this.theWordIndex = (this.theWordIndex + 1) % this.theKeyStream.length;
        }
    }

    private void updateMac(int bitNo) {
        for (int wordNo = 0; wordNo < this.theMac.length; ++wordNo) {
            int n = wordNo;
            this.theMac[n] = this.theMac[n] ^ this.getKeyStreamWord(wordNo, bitNo);
        }
    }

    private int getKeyStreamWord(int wordNo, int bitNo) {
        int myFirst = this.theKeyStream[(this.theWordIndex + wordNo) % this.theKeyStream.length];
        if (bitNo == 0) {
            return myFirst;
        }
        int mySecond = this.theKeyStream[(this.theWordIndex + wordNo + 1) % this.theKeyStream.length];
        return myFirst << bitNo | mySecond >>> 32 - bitNo;
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        for (int byteNo = 0; byteNo < len; ++byteNo) {
            this.update(in[inOff + byteNo]);
        }
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        this.shift4Final();
        this.updateMac(this.theByteIndex * 8);
        for (int i = 0; i < this.theMac.length; ++i) {
            Zuc256CoreEngine.encode32be(this.theMac[i], out, outOff + i * 4);
        }
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

    private static class InternalZuc256Engine
    extends Zuc256CoreEngine {
        public InternalZuc256Engine(int pLength) {
            super(pLength);
        }

        int createKeyStreamWord() {
            return super.makeKeyStreamWord();
        }
    }
}

