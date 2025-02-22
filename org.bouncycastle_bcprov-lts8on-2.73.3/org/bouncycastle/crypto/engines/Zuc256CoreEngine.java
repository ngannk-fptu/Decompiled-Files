/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.Zuc128CoreEngine;
import org.bouncycastle.util.Memoable;

public class Zuc256CoreEngine
extends Zuc128CoreEngine {
    private static final byte[] EK_d = new byte[]{34, 47, 36, 42, 109, 64, 64, 64, 64, 64, 64, 64, 64, 82, 16, 48};
    private static final byte[] EK_d32 = new byte[]{34, 47, 37, 42, 109, 64, 64, 64, 64, 64, 64, 64, 64, 82, 16, 48};
    private static final byte[] EK_d64 = new byte[]{35, 47, 36, 42, 109, 64, 64, 64, 64, 64, 64, 64, 64, 82, 16, 48};
    private static final byte[] EK_d128 = new byte[]{35, 47, 37, 42, 109, 64, 64, 64, 64, 64, 64, 64, 64, 82, 16, 48};
    private byte[] theD;

    protected Zuc256CoreEngine() {
        this.theD = EK_d;
    }

    protected Zuc256CoreEngine(int pLength) {
        switch (pLength) {
            case 32: {
                this.theD = EK_d32;
                break;
            }
            case 64: {
                this.theD = EK_d64;
                break;
            }
            case 128: {
                this.theD = EK_d128;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported length: " + pLength);
            }
        }
    }

    protected Zuc256CoreEngine(Zuc256CoreEngine pSource) {
        super(pSource);
    }

    @Override
    protected int getMaxIterations() {
        return 625;
    }

    @Override
    public String getAlgorithmName() {
        return "Zuc-256";
    }

    private static int MAKEU31(byte a, byte b, byte c, byte d) {
        return (a & 0xFF) << 23 | (b & 0xFF) << 16 | (c & 0xFF) << 8 | d & 0xFF;
    }

    @Override
    protected void setKeyAndIV(int[] pLFSR, byte[] k, byte[] iv) {
        if (k == null || k.length != 32) {
            throw new IllegalArgumentException("A key of 32 bytes is needed");
        }
        if (iv == null || iv.length != 25) {
            throw new IllegalArgumentException("An IV of 25 bytes is needed");
        }
        pLFSR[0] = Zuc256CoreEngine.MAKEU31(k[0], this.theD[0], k[21], k[16]);
        pLFSR[1] = Zuc256CoreEngine.MAKEU31(k[1], this.theD[1], k[22], k[17]);
        pLFSR[2] = Zuc256CoreEngine.MAKEU31(k[2], this.theD[2], k[23], k[18]);
        pLFSR[3] = Zuc256CoreEngine.MAKEU31(k[3], this.theD[3], k[24], k[19]);
        pLFSR[4] = Zuc256CoreEngine.MAKEU31(k[4], this.theD[4], k[25], k[20]);
        pLFSR[5] = Zuc256CoreEngine.MAKEU31(iv[0], (byte)(this.theD[5] | iv[17] & 0x3F), k[5], k[26]);
        pLFSR[6] = Zuc256CoreEngine.MAKEU31(iv[1], (byte)(this.theD[6] | iv[18] & 0x3F), k[6], k[27]);
        pLFSR[7] = Zuc256CoreEngine.MAKEU31(iv[10], (byte)(this.theD[7] | iv[19] & 0x3F), k[7], iv[2]);
        pLFSR[8] = Zuc256CoreEngine.MAKEU31(k[8], (byte)(this.theD[8] | iv[20] & 0x3F), iv[3], iv[11]);
        pLFSR[9] = Zuc256CoreEngine.MAKEU31(k[9], (byte)(this.theD[9] | iv[21] & 0x3F), iv[12], iv[4]);
        pLFSR[10] = Zuc256CoreEngine.MAKEU31(iv[5], (byte)(this.theD[10] | iv[22] & 0x3F), k[10], k[28]);
        pLFSR[11] = Zuc256CoreEngine.MAKEU31(k[11], (byte)(this.theD[11] | iv[23] & 0x3F), iv[6], iv[13]);
        pLFSR[12] = Zuc256CoreEngine.MAKEU31(k[12], (byte)(this.theD[12] | iv[24] & 0x3F), iv[7], iv[14]);
        pLFSR[13] = Zuc256CoreEngine.MAKEU31(k[13], this.theD[13], iv[15], iv[8]);
        pLFSR[14] = Zuc256CoreEngine.MAKEU31(k[14], (byte)(this.theD[14] | k[31] >>> 4 & 0xF), iv[16], iv[9]);
        pLFSR[15] = Zuc256CoreEngine.MAKEU31(k[15], (byte)(this.theD[15] | k[31] & 0xF), k[30], k[29]);
    }

    @Override
    public Memoable copy() {
        return new Zuc256CoreEngine(this);
    }

    @Override
    public void reset(Memoable pState) {
        Zuc256CoreEngine e = (Zuc256CoreEngine)pState;
        super.reset(pState);
        this.theD = e.theD;
    }
}

