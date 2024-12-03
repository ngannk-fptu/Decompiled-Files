/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.CAST5Engine;

public final class CAST6Engine
extends CAST5Engine {
    protected static final int ROUNDS = 12;
    protected static final int BLOCK_SIZE = 16;
    protected int[] _Kr = new int[48];
    protected int[] _Km = new int[48];
    protected int[] _Tr = new int[192];
    protected int[] _Tm = new int[192];
    private int[] _workingKey = new int[8];

    @Override
    public String getAlgorithmName() {
        return "CAST6";
    }

    @Override
    public void reset() {
    }

    @Override
    public int getBlockSize() {
        return 16;
    }

    @Override
    protected void setKey(byte[] key) {
        int i;
        int Cm = 1518500249;
        int Mm = 1859775393;
        int Cr = 19;
        int Mr = 17;
        for (int i2 = 0; i2 < 24; ++i2) {
            for (int j = 0; j < 8; ++j) {
                this._Tm[i2 * 8 + j] = Cm;
                Cm += Mm;
                this._Tr[i2 * 8 + j] = Cr;
                Cr = Cr + Mr & 0x1F;
            }
        }
        byte[] tmpKey = new byte[64];
        int length = key.length;
        System.arraycopy(key, 0, tmpKey, 0, length);
        for (i = 0; i < 8; ++i) {
            this._workingKey[i] = this.BytesTo32bits(tmpKey, i * 4);
        }
        for (i = 0; i < 12; ++i) {
            int i2 = i * 2 * 8;
            this._workingKey[6] = this._workingKey[6] ^ this.F1(this._workingKey[7], this._Tm[i2], this._Tr[i2]);
            this._workingKey[5] = this._workingKey[5] ^ this.F2(this._workingKey[6], this._Tm[i2 + 1], this._Tr[i2 + 1]);
            this._workingKey[4] = this._workingKey[4] ^ this.F3(this._workingKey[5], this._Tm[i2 + 2], this._Tr[i2 + 2]);
            this._workingKey[3] = this._workingKey[3] ^ this.F1(this._workingKey[4], this._Tm[i2 + 3], this._Tr[i2 + 3]);
            this._workingKey[2] = this._workingKey[2] ^ this.F2(this._workingKey[3], this._Tm[i2 + 4], this._Tr[i2 + 4]);
            this._workingKey[1] = this._workingKey[1] ^ this.F3(this._workingKey[2], this._Tm[i2 + 5], this._Tr[i2 + 5]);
            this._workingKey[0] = this._workingKey[0] ^ this.F1(this._workingKey[1], this._Tm[i2 + 6], this._Tr[i2 + 6]);
            this._workingKey[7] = this._workingKey[7] ^ this.F2(this._workingKey[0], this._Tm[i2 + 7], this._Tr[i2 + 7]);
            i2 = (i * 2 + 1) * 8;
            this._workingKey[6] = this._workingKey[6] ^ this.F1(this._workingKey[7], this._Tm[i2], this._Tr[i2]);
            this._workingKey[5] = this._workingKey[5] ^ this.F2(this._workingKey[6], this._Tm[i2 + 1], this._Tr[i2 + 1]);
            this._workingKey[4] = this._workingKey[4] ^ this.F3(this._workingKey[5], this._Tm[i2 + 2], this._Tr[i2 + 2]);
            this._workingKey[3] = this._workingKey[3] ^ this.F1(this._workingKey[4], this._Tm[i2 + 3], this._Tr[i2 + 3]);
            this._workingKey[2] = this._workingKey[2] ^ this.F2(this._workingKey[3], this._Tm[i2 + 4], this._Tr[i2 + 4]);
            this._workingKey[1] = this._workingKey[1] ^ this.F3(this._workingKey[2], this._Tm[i2 + 5], this._Tr[i2 + 5]);
            this._workingKey[0] = this._workingKey[0] ^ this.F1(this._workingKey[1], this._Tm[i2 + 6], this._Tr[i2 + 6]);
            this._workingKey[7] = this._workingKey[7] ^ this.F2(this._workingKey[0], this._Tm[i2 + 7], this._Tr[i2 + 7]);
            this._Kr[i * 4] = this._workingKey[0] & 0x1F;
            this._Kr[i * 4 + 1] = this._workingKey[2] & 0x1F;
            this._Kr[i * 4 + 2] = this._workingKey[4] & 0x1F;
            this._Kr[i * 4 + 3] = this._workingKey[6] & 0x1F;
            this._Km[i * 4] = this._workingKey[7];
            this._Km[i * 4 + 1] = this._workingKey[5];
            this._Km[i * 4 + 2] = this._workingKey[3];
            this._Km[i * 4 + 3] = this._workingKey[1];
        }
    }

    @Override
    protected int encryptBlock(byte[] src, int srcIndex, byte[] dst, int dstIndex) {
        int[] result = new int[4];
        int A = this.BytesTo32bits(src, srcIndex);
        int B = this.BytesTo32bits(src, srcIndex + 4);
        int C = this.BytesTo32bits(src, srcIndex + 8);
        int D = this.BytesTo32bits(src, srcIndex + 12);
        this.CAST_Encipher(A, B, C, D, result);
        this.Bits32ToBytes(result[0], dst, dstIndex);
        this.Bits32ToBytes(result[1], dst, dstIndex + 4);
        this.Bits32ToBytes(result[2], dst, dstIndex + 8);
        this.Bits32ToBytes(result[3], dst, dstIndex + 12);
        return 16;
    }

    @Override
    protected int decryptBlock(byte[] src, int srcIndex, byte[] dst, int dstIndex) {
        int[] result = new int[4];
        int A = this.BytesTo32bits(src, srcIndex);
        int B = this.BytesTo32bits(src, srcIndex + 4);
        int C = this.BytesTo32bits(src, srcIndex + 8);
        int D = this.BytesTo32bits(src, srcIndex + 12);
        this.CAST_Decipher(A, B, C, D, result);
        this.Bits32ToBytes(result[0], dst, dstIndex);
        this.Bits32ToBytes(result[1], dst, dstIndex + 4);
        this.Bits32ToBytes(result[2], dst, dstIndex + 8);
        this.Bits32ToBytes(result[3], dst, dstIndex + 12);
        return 16;
    }

    protected final void CAST_Encipher(int A, int B, int C, int D, int[] result) {
        int x;
        int i;
        for (i = 0; i < 6; ++i) {
            x = i * 4;
            D ^= this.F1(A ^= this.F3(B ^= this.F2(C ^= this.F1(D, this._Km[x], this._Kr[x]), this._Km[x + 1], this._Kr[x + 1]), this._Km[x + 2], this._Kr[x + 2]), this._Km[x + 3], this._Kr[x + 3]);
        }
        for (i = 6; i < 12; ++i) {
            x = i * 4;
            B ^= this.F2(C, this._Km[x + 1], this._Kr[x + 1]);
            C ^= this.F1(D ^= this.F1(A ^= this.F3(B, this._Km[x + 2], this._Kr[x + 2]), this._Km[x + 3], this._Kr[x + 3]), this._Km[x], this._Kr[x]);
        }
        result[0] = A;
        result[1] = B;
        result[2] = C;
        result[3] = D;
    }

    protected final void CAST_Decipher(int A, int B, int C, int D, int[] result) {
        int x;
        int i;
        for (i = 0; i < 6; ++i) {
            x = (11 - i) * 4;
            D ^= this.F1(A ^= this.F3(B ^= this.F2(C ^= this.F1(D, this._Km[x], this._Kr[x]), this._Km[x + 1], this._Kr[x + 1]), this._Km[x + 2], this._Kr[x + 2]), this._Km[x + 3], this._Kr[x + 3]);
        }
        for (i = 6; i < 12; ++i) {
            x = (11 - i) * 4;
            B ^= this.F2(C, this._Km[x + 1], this._Kr[x + 1]);
            C ^= this.F1(D ^= this.F1(A ^= this.F3(B, this._Km[x + 2], this._Kr[x + 2]), this._Km[x + 3], this._Kr[x + 3]), this._Km[x], this._Kr[x]);
        }
        result[0] = A;
        result[1] = B;
        result[2] = C;
        result[3] = D;
    }
}

