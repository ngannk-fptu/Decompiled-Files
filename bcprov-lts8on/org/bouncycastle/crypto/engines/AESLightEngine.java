/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class AESLightEngine
implements BlockCipher {
    private static final byte[] S = new byte[]{99, 124, 119, 123, -14, 107, 111, -59, 48, 1, 103, 43, -2, -41, -85, 118, -54, -126, -55, 125, -6, 89, 71, -16, -83, -44, -94, -81, -100, -92, 114, -64, -73, -3, -109, 38, 54, 63, -9, -52, 52, -91, -27, -15, 113, -40, 49, 21, 4, -57, 35, -61, 24, -106, 5, -102, 7, 18, -128, -30, -21, 39, -78, 117, 9, -125, 44, 26, 27, 110, 90, -96, 82, 59, -42, -77, 41, -29, 47, -124, 83, -47, 0, -19, 32, -4, -79, 91, 106, -53, -66, 57, 74, 76, 88, -49, -48, -17, -86, -5, 67, 77, 51, -123, 69, -7, 2, 127, 80, 60, -97, -88, 81, -93, 64, -113, -110, -99, 56, -11, -68, -74, -38, 33, 16, -1, -13, -46, -51, 12, 19, -20, 95, -105, 68, 23, -60, -89, 126, 61, 100, 93, 25, 115, 96, -127, 79, -36, 34, 42, -112, -120, 70, -18, -72, 20, -34, 94, 11, -37, -32, 50, 58, 10, 73, 6, 36, 92, -62, -45, -84, 98, -111, -107, -28, 121, -25, -56, 55, 109, -115, -43, 78, -87, 108, 86, -12, -22, 101, 122, -82, 8, -70, 120, 37, 46, 28, -90, -76, -58, -24, -35, 116, 31, 75, -67, -117, -118, 112, 62, -75, 102, 72, 3, -10, 14, 97, 53, 87, -71, -122, -63, 29, -98, -31, -8, -104, 17, 105, -39, -114, -108, -101, 30, -121, -23, -50, 85, 40, -33, -116, -95, -119, 13, -65, -26, 66, 104, 65, -103, 45, 15, -80, 84, -69, 22};
    private static final byte[] Si = new byte[]{82, 9, 106, -43, 48, 54, -91, 56, -65, 64, -93, -98, -127, -13, -41, -5, 124, -29, 57, -126, -101, 47, -1, -121, 52, -114, 67, 68, -60, -34, -23, -53, 84, 123, -108, 50, -90, -62, 35, 61, -18, 76, -107, 11, 66, -6, -61, 78, 8, 46, -95, 102, 40, -39, 36, -78, 118, 91, -94, 73, 109, -117, -47, 37, 114, -8, -10, 100, -122, 104, -104, 22, -44, -92, 92, -52, 93, 101, -74, -110, 108, 112, 72, 80, -3, -19, -71, -38, 94, 21, 70, 87, -89, -115, -99, -124, -112, -40, -85, 0, -116, -68, -45, 10, -9, -28, 88, 5, -72, -77, 69, 6, -48, 44, 30, -113, -54, 63, 15, 2, -63, -81, -67, 3, 1, 19, -118, 107, 58, -111, 17, 65, 79, 103, -36, -22, -105, -14, -49, -50, -16, -76, -26, 115, -106, -84, 116, 34, -25, -83, 53, -123, -30, -7, 55, -24, 28, 117, -33, 110, 71, -15, 26, 113, 29, 41, -59, -119, 111, -73, 98, 14, -86, 24, -66, 27, -4, 86, 62, 75, -58, -46, 121, 32, -102, -37, -64, -2, 120, -51, 90, -12, 31, -35, -88, 51, -120, 7, -57, 49, -79, 18, 16, 89, 39, -128, -20, 95, 96, 81, 127, -87, 25, -75, 74, 13, 45, -27, 122, -97, -109, -55, -100, -17, -96, -32, 59, 77, -82, 42, -11, -80, -56, -21, -69, 60, -125, 83, -103, 97, 23, 43, 4, 126, -70, 119, -42, 38, -31, 105, 20, 99, 85, 33, 12, 125};
    private static final int[] rcon = new int[]{1, 2, 4, 8, 16, 32, 64, 128, 27, 54, 108, 216, 171, 77, 154, 47, 94, 188, 99, 198, 151, 53, 106, 212, 179, 125, 250, 239, 197, 145};
    private static final int m1 = -2139062144;
    private static final int m2 = 0x7F7F7F7F;
    private static final int m3 = 27;
    private static final int m4 = -1061109568;
    private static final int m5 = 0x3F3F3F3F;
    private int ROUNDS;
    private int[][] WorkingKey = null;
    private boolean forEncryption;
    private static final int BLOCK_SIZE = 16;

    private static int shift(int r, int shift) {
        return r >>> shift | r << -shift;
    }

    private static int FFmulX(int x) {
        return (x & 0x7F7F7F7F) << 1 ^ ((x & 0x80808080) >>> 7) * 27;
    }

    private static int FFmulX2(int x) {
        int t0 = (x & 0x3F3F3F3F) << 2;
        int t1 = x & 0xC0C0C0C0;
        t1 ^= t1 >>> 1;
        return t0 ^ t1 >>> 2 ^ t1 >>> 5;
    }

    private static int mcol(int x) {
        int t0 = AESLightEngine.shift(x, 8);
        int t1 = x ^ t0;
        return AESLightEngine.shift(t1, 16) ^ t0 ^ AESLightEngine.FFmulX(t1);
    }

    private static int inv_mcol(int x) {
        int t0 = x;
        int t1 = t0 ^ AESLightEngine.shift(t0, 8);
        t0 ^= AESLightEngine.FFmulX(t1);
        t1 ^= AESLightEngine.FFmulX2(t0);
        return t0 ^= t1 ^ AESLightEngine.shift(t1, 16);
    }

    private static int subWord(int x) {
        return S[x & 0xFF] & 0xFF | (S[x >> 8 & 0xFF] & 0xFF) << 8 | (S[x >> 16 & 0xFF] & 0xFF) << 16 | S[x >> 24 & 0xFF] << 24;
    }

    private int[][] generateWorkingKey(byte[] key, boolean forEncryption) {
        int keyLen = key.length;
        if (keyLen < 16 || keyLen > 32 || (keyLen & 7) != 0) {
            throw new IllegalArgumentException("Key length not 128/192/256 bits.");
        }
        int KC = keyLen >>> 2;
        this.ROUNDS = KC + 6;
        int[][] W = new int[this.ROUNDS + 1][4];
        block0 : switch (KC) {
            case 4: {
                int col3;
                int col2;
                int col1;
                int col0;
                W[0][0] = col0 = Pack.littleEndianToInt(key, 0);
                W[0][1] = col1 = Pack.littleEndianToInt(key, 4);
                W[0][2] = col2 = Pack.littleEndianToInt(key, 8);
                W[0][3] = col3 = Pack.littleEndianToInt(key, 12);
                for (int i = 1; i <= 10; ++i) {
                    int colx = AESLightEngine.subWord(AESLightEngine.shift(col3, 8)) ^ rcon[i - 1];
                    W[i][0] = col0 ^= colx;
                    W[i][1] = col1 ^= col0;
                    W[i][2] = col2 ^= col1;
                    W[i][3] = col3 ^= col2;
                }
                break;
            }
            case 6: {
                int col3;
                int col2;
                int col1;
                int col0;
                W[0][0] = col0 = Pack.littleEndianToInt(key, 0);
                W[0][1] = col1 = Pack.littleEndianToInt(key, 4);
                W[0][2] = col2 = Pack.littleEndianToInt(key, 8);
                W[0][3] = col3 = Pack.littleEndianToInt(key, 12);
                int col4 = Pack.littleEndianToInt(key, 16);
                int col5 = Pack.littleEndianToInt(key, 20);
                int i = 1;
                int rcon = 1;
                while (true) {
                    W[i][0] = col4;
                    W[i][1] = col5;
                    int colx = AESLightEngine.subWord(AESLightEngine.shift(col5, 8)) ^ rcon;
                    rcon <<= 1;
                    W[i][2] = col0 ^= colx;
                    W[i][3] = col1 ^= col0;
                    W[i + 1][0] = col2 ^= col1;
                    W[i + 1][1] = col3 ^= col2;
                    W[i + 1][2] = col4 ^= col3;
                    W[i + 1][3] = col5 ^= col4;
                    colx = AESLightEngine.subWord(AESLightEngine.shift(col5, 8)) ^ rcon;
                    rcon <<= 1;
                    W[i + 2][0] = col0 ^= colx;
                    W[i + 2][1] = col1 ^= col0;
                    W[i + 2][2] = col2 ^= col1;
                    W[i + 2][3] = col3 ^= col2;
                    if ((i += 3) >= 13) break block0;
                    col5 ^= (col4 ^= col3);
                }
            }
            case 8: {
                int col7;
                int col6;
                int col5;
                int col4;
                int col3;
                int col2;
                int col1;
                int col0;
                W[0][0] = col0 = Pack.littleEndianToInt(key, 0);
                W[0][1] = col1 = Pack.littleEndianToInt(key, 4);
                W[0][2] = col2 = Pack.littleEndianToInt(key, 8);
                W[0][3] = col3 = Pack.littleEndianToInt(key, 12);
                W[1][0] = col4 = Pack.littleEndianToInt(key, 16);
                W[1][1] = col5 = Pack.littleEndianToInt(key, 20);
                W[1][2] = col6 = Pack.littleEndianToInt(key, 24);
                W[1][3] = col7 = Pack.littleEndianToInt(key, 28);
                int i = 2;
                int rcon = 1;
                while (true) {
                    int colx = AESLightEngine.subWord(AESLightEngine.shift(col7, 8)) ^ rcon;
                    rcon <<= 1;
                    W[i][0] = col0 ^= colx;
                    W[i][1] = col1 ^= col0;
                    W[i][2] = col2 ^= col1;
                    W[i][3] = col3 ^= col2;
                    if (++i >= 15) break block0;
                    colx = AESLightEngine.subWord(col3);
                    W[i][0] = col4 ^= colx;
                    W[i][1] = col5 ^= col4;
                    W[i][2] = col6 ^= col5;
                    W[i][3] = col7 ^= col6;
                    ++i;
                }
            }
            default: {
                throw new IllegalStateException("Should never get here");
            }
        }
        if (!forEncryption) {
            for (int j = 1; j < this.ROUNDS; ++j) {
                for (int i = 0; i < 4; ++i) {
                    W[j][i] = AESLightEngine.inv_mcol(W[j][i]);
                }
            }
        }
        return W;
    }

    public AESLightEngine() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity()));
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) {
        if (params instanceof KeyParameter) {
            this.WorkingKey = this.generateWorkingKey(((KeyParameter)params).getKey(), forEncryption);
            this.forEncryption = forEncryption;
            CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity(), params, Utils.getPurpose(forEncryption)));
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to AES init - " + params.getClass().getName());
    }

    @Override
    public String getAlgorithmName() {
        return "AES";
    }

    @Override
    public int getBlockSize() {
        return 16;
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.WorkingKey == null) {
            throw new IllegalStateException("AES engine not initialised");
        }
        if (inOff > in.length - 16) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff > out.length - 16) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.forEncryption) {
            this.encryptBlock(in, inOff, out, outOff, this.WorkingKey);
        } else {
            this.decryptBlock(in, inOff, out, outOff, this.WorkingKey);
        }
        return 16;
    }

    @Override
    public void reset() {
    }

    private void encryptBlock(byte[] in, int inOff, byte[] out, int outOff, int[][] KW) {
        int r2;
        int r1;
        int r0;
        int C0 = Pack.littleEndianToInt(in, inOff + 0);
        int C1 = Pack.littleEndianToInt(in, inOff + 4);
        int C2 = Pack.littleEndianToInt(in, inOff + 8);
        int C3 = Pack.littleEndianToInt(in, inOff + 12);
        int t0 = C0 ^ KW[0][0];
        int t1 = C1 ^ KW[0][1];
        int t2 = C2 ^ KW[0][2];
        int r = 1;
        int r3 = C3 ^ KW[0][3];
        while (r < this.ROUNDS - 1) {
            r0 = AESLightEngine.mcol(S[t0 & 0xFF] & 0xFF ^ (S[t1 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[t2 >> 16 & 0xFF] & 0xFF) << 16 ^ S[r3 >> 24 & 0xFF] << 24) ^ KW[r][0];
            r1 = AESLightEngine.mcol(S[t1 & 0xFF] & 0xFF ^ (S[t2 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[r3 >> 16 & 0xFF] & 0xFF) << 16 ^ S[t0 >> 24 & 0xFF] << 24) ^ KW[r][1];
            r2 = AESLightEngine.mcol(S[t2 & 0xFF] & 0xFF ^ (S[r3 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[t0 >> 16 & 0xFF] & 0xFF) << 16 ^ S[t1 >> 24 & 0xFF] << 24) ^ KW[r][2];
            r3 = AESLightEngine.mcol(S[r3 & 0xFF] & 0xFF ^ (S[t0 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[t1 >> 16 & 0xFF] & 0xFF) << 16 ^ S[t2 >> 24 & 0xFF] << 24) ^ KW[r++][3];
            t0 = AESLightEngine.mcol(S[r0 & 0xFF] & 0xFF ^ (S[r1 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[r2 >> 16 & 0xFF] & 0xFF) << 16 ^ S[r3 >> 24 & 0xFF] << 24) ^ KW[r][0];
            t1 = AESLightEngine.mcol(S[r1 & 0xFF] & 0xFF ^ (S[r2 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[r3 >> 16 & 0xFF] & 0xFF) << 16 ^ S[r0 >> 24 & 0xFF] << 24) ^ KW[r][1];
            t2 = AESLightEngine.mcol(S[r2 & 0xFF] & 0xFF ^ (S[r3 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[r0 >> 16 & 0xFF] & 0xFF) << 16 ^ S[r1 >> 24 & 0xFF] << 24) ^ KW[r][2];
            r3 = AESLightEngine.mcol(S[r3 & 0xFF] & 0xFF ^ (S[r0 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[r1 >> 16 & 0xFF] & 0xFF) << 16 ^ S[r2 >> 24 & 0xFF] << 24) ^ KW[r++][3];
        }
        r0 = AESLightEngine.mcol(S[t0 & 0xFF] & 0xFF ^ (S[t1 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[t2 >> 16 & 0xFF] & 0xFF) << 16 ^ S[r3 >> 24 & 0xFF] << 24) ^ KW[r][0];
        r1 = AESLightEngine.mcol(S[t1 & 0xFF] & 0xFF ^ (S[t2 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[r3 >> 16 & 0xFF] & 0xFF) << 16 ^ S[t0 >> 24 & 0xFF] << 24) ^ KW[r][1];
        r2 = AESLightEngine.mcol(S[t2 & 0xFF] & 0xFF ^ (S[r3 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[t0 >> 16 & 0xFF] & 0xFF) << 16 ^ S[t1 >> 24 & 0xFF] << 24) ^ KW[r][2];
        r3 = AESLightEngine.mcol(S[r3 & 0xFF] & 0xFF ^ (S[t0 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[t1 >> 16 & 0xFF] & 0xFF) << 16 ^ S[t2 >> 24 & 0xFF] << 24) ^ KW[r++][3];
        C0 = S[r0 & 0xFF] & 0xFF ^ (S[r1 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[r2 >> 16 & 0xFF] & 0xFF) << 16 ^ S[r3 >> 24 & 0xFF] << 24 ^ KW[r][0];
        C1 = S[r1 & 0xFF] & 0xFF ^ (S[r2 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[r3 >> 16 & 0xFF] & 0xFF) << 16 ^ S[r0 >> 24 & 0xFF] << 24 ^ KW[r][1];
        C2 = S[r2 & 0xFF] & 0xFF ^ (S[r3 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[r0 >> 16 & 0xFF] & 0xFF) << 16 ^ S[r1 >> 24 & 0xFF] << 24 ^ KW[r][2];
        C3 = S[r3 & 0xFF] & 0xFF ^ (S[r0 >> 8 & 0xFF] & 0xFF) << 8 ^ (S[r1 >> 16 & 0xFF] & 0xFF) << 16 ^ S[r2 >> 24 & 0xFF] << 24 ^ KW[r][3];
        Pack.intToLittleEndian(C0, out, outOff + 0);
        Pack.intToLittleEndian(C1, out, outOff + 4);
        Pack.intToLittleEndian(C2, out, outOff + 8);
        Pack.intToLittleEndian(C3, out, outOff + 12);
    }

    private void decryptBlock(byte[] in, int inOff, byte[] out, int outOff, int[][] KW) {
        int r2;
        int r1;
        int r0;
        int C0 = Pack.littleEndianToInt(in, inOff + 0);
        int C1 = Pack.littleEndianToInt(in, inOff + 4);
        int C2 = Pack.littleEndianToInt(in, inOff + 8);
        int C3 = Pack.littleEndianToInt(in, inOff + 12);
        int t0 = C0 ^ KW[this.ROUNDS][0];
        int t1 = C1 ^ KW[this.ROUNDS][1];
        int t2 = C2 ^ KW[this.ROUNDS][2];
        int r = this.ROUNDS - 1;
        int r3 = C3 ^ KW[this.ROUNDS][3];
        while (r > 1) {
            r0 = AESLightEngine.inv_mcol(Si[t0 & 0xFF] & 0xFF ^ (Si[r3 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[t2 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[t1 >> 24 & 0xFF] << 24) ^ KW[r][0];
            r1 = AESLightEngine.inv_mcol(Si[t1 & 0xFF] & 0xFF ^ (Si[t0 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[r3 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[t2 >> 24 & 0xFF] << 24) ^ KW[r][1];
            r2 = AESLightEngine.inv_mcol(Si[t2 & 0xFF] & 0xFF ^ (Si[t1 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[t0 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[r3 >> 24 & 0xFF] << 24) ^ KW[r][2];
            r3 = AESLightEngine.inv_mcol(Si[r3 & 0xFF] & 0xFF ^ (Si[t2 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[t1 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[t0 >> 24 & 0xFF] << 24) ^ KW[r--][3];
            t0 = AESLightEngine.inv_mcol(Si[r0 & 0xFF] & 0xFF ^ (Si[r3 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[r2 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[r1 >> 24 & 0xFF] << 24) ^ KW[r][0];
            t1 = AESLightEngine.inv_mcol(Si[r1 & 0xFF] & 0xFF ^ (Si[r0 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[r3 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[r2 >> 24 & 0xFF] << 24) ^ KW[r][1];
            t2 = AESLightEngine.inv_mcol(Si[r2 & 0xFF] & 0xFF ^ (Si[r1 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[r0 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[r3 >> 24 & 0xFF] << 24) ^ KW[r][2];
            r3 = AESLightEngine.inv_mcol(Si[r3 & 0xFF] & 0xFF ^ (Si[r2 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[r1 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[r0 >> 24 & 0xFF] << 24) ^ KW[r--][3];
        }
        r0 = AESLightEngine.inv_mcol(Si[t0 & 0xFF] & 0xFF ^ (Si[r3 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[t2 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[t1 >> 24 & 0xFF] << 24) ^ KW[r][0];
        r1 = AESLightEngine.inv_mcol(Si[t1 & 0xFF] & 0xFF ^ (Si[t0 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[r3 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[t2 >> 24 & 0xFF] << 24) ^ KW[r][1];
        r2 = AESLightEngine.inv_mcol(Si[t2 & 0xFF] & 0xFF ^ (Si[t1 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[t0 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[r3 >> 24 & 0xFF] << 24) ^ KW[r][2];
        r3 = AESLightEngine.inv_mcol(Si[r3 & 0xFF] & 0xFF ^ (Si[t2 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[t1 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[t0 >> 24 & 0xFF] << 24) ^ KW[r][3];
        C0 = Si[r0 & 0xFF] & 0xFF ^ (Si[r3 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[r2 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[r1 >> 24 & 0xFF] << 24 ^ KW[0][0];
        C1 = Si[r1 & 0xFF] & 0xFF ^ (Si[r0 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[r3 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[r2 >> 24 & 0xFF] << 24 ^ KW[0][1];
        C2 = Si[r2 & 0xFF] & 0xFF ^ (Si[r1 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[r0 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[r3 >> 24 & 0xFF] << 24 ^ KW[0][2];
        C3 = Si[r3 & 0xFF] & 0xFF ^ (Si[r2 >> 8 & 0xFF] & 0xFF) << 8 ^ (Si[r1 >> 16 & 0xFF] & 0xFF) << 16 ^ Si[r0 >> 24 & 0xFF] << 24 ^ KW[0][3];
        Pack.intToLittleEndian(C0, out, outOff + 0);
        Pack.intToLittleEndian(C1, out, outOff + 4);
        Pack.intToLittleEndian(C2, out, outOff + 8);
        Pack.intToLittleEndian(C3, out, outOff + 12);
    }

    private int bitsOfSecurity() {
        if (this.WorkingKey == null) {
            return 256;
        }
        return this.WorkingKey.length - 7 << 5;
    }
}

