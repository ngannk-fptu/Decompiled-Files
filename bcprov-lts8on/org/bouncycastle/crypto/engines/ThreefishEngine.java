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
import org.bouncycastle.crypto.params.TweakableBlockCipherParameters;
import org.bouncycastle.util.Pack;

public class ThreefishEngine
implements BlockCipher {
    public static final int BLOCKSIZE_256 = 256;
    public static final int BLOCKSIZE_512 = 512;
    public static final int BLOCKSIZE_1024 = 1024;
    private static final int TWEAK_SIZE_BYTES = 16;
    private static final int TWEAK_SIZE_WORDS = 2;
    private static final int ROUNDS_256 = 72;
    private static final int ROUNDS_512 = 72;
    private static final int ROUNDS_1024 = 80;
    private static final int MAX_ROUNDS = 80;
    private static final long C_240 = 2004413935125273122L;
    private static int[] MOD9 = new int[80];
    private static int[] MOD17 = new int[MOD9.length];
    private static int[] MOD5 = new int[MOD9.length];
    private static int[] MOD3 = new int[MOD9.length];
    private int blocksizeBytes;
    private int blocksizeWords;
    private long[] currentBlock;
    private long[] t = new long[5];
    private long[] kw;
    private ThreefishCipher cipher;
    private boolean forEncryption;

    public ThreefishEngine(int blocksizeBits) {
        this.blocksizeBytes = blocksizeBits / 8;
        this.blocksizeWords = this.blocksizeBytes / 8;
        this.currentBlock = new long[this.blocksizeWords];
        this.kw = new long[2 * this.blocksizeWords + 1];
        switch (blocksizeBits) {
            case 256: {
                this.cipher = new Threefish256Cipher(this.kw, this.t);
                break;
            }
            case 512: {
                this.cipher = new Threefish512Cipher(this.kw, this.t);
                break;
            }
            case 1024: {
                this.cipher = new Threefish1024Cipher(this.kw, this.t);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid blocksize - Threefish is defined with block size of 256, 512, or 1024 bits");
            }
        }
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        byte[] tweakBytes;
        byte[] keyBytes;
        if (params instanceof TweakableBlockCipherParameters) {
            TweakableBlockCipherParameters tParams = (TweakableBlockCipherParameters)params;
            keyBytes = tParams.getKey().getKey();
            tweakBytes = tParams.getTweak();
        } else if (params instanceof KeyParameter) {
            keyBytes = ((KeyParameter)params).getKey();
            tweakBytes = null;
        } else {
            throw new IllegalArgumentException("Invalid parameter passed to Threefish init - " + params.getClass().getName());
        }
        long[] keyWords = null;
        long[] tweakWords = null;
        if (keyBytes != null) {
            if (keyBytes.length != this.blocksizeBytes) {
                throw new IllegalArgumentException("Threefish key must be same size as block (" + this.blocksizeBytes + " bytes)");
            }
            keyWords = new long[this.blocksizeWords];
            Pack.littleEndianToLong(keyBytes, 0, keyWords);
        }
        if (tweakBytes != null) {
            if (tweakBytes.length != 16) {
                throw new IllegalArgumentException("Threefish tweak must be 16 bytes");
            }
            tweakWords = new long[2];
            Pack.littleEndianToLong(tweakBytes, 0, tweakWords);
        }
        this.init(forEncryption, keyWords, tweakWords);
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 256, params, Utils.getPurpose(forEncryption)));
    }

    public void init(boolean forEncryption, long[] key, long[] tweak) {
        this.forEncryption = forEncryption;
        if (key != null) {
            this.setKey(key);
        }
        if (tweak != null) {
            this.setTweak(tweak);
        }
    }

    private void setKey(long[] key) {
        if (key.length != this.blocksizeWords) {
            throw new IllegalArgumentException("Threefish key must be same size as block (" + this.blocksizeWords + " words)");
        }
        long knw = 2004413935125273122L;
        for (int i = 0; i < this.blocksizeWords; ++i) {
            this.kw[i] = key[i];
            knw ^= this.kw[i];
        }
        this.kw[this.blocksizeWords] = knw;
        System.arraycopy(this.kw, 0, this.kw, this.blocksizeWords + 1, this.blocksizeWords);
    }

    private void setTweak(long[] tweak) {
        if (tweak.length != 2) {
            throw new IllegalArgumentException("Tweak must be 2 words.");
        }
        this.t[0] = tweak[0];
        this.t[1] = tweak[1];
        this.t[2] = this.t[0] ^ this.t[1];
        this.t[3] = this.t[0];
        this.t[4] = this.t[1];
    }

    @Override
    public String getAlgorithmName() {
        return "Threefish-" + this.blocksizeBytes * 8;
    }

    @Override
    public int getBlockSize() {
        return this.blocksizeBytes;
    }

    @Override
    public void reset() {
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (inOff + this.blocksizeBytes > in.length) {
            throw new DataLengthException("Input buffer too short");
        }
        if (outOff + this.blocksizeBytes > out.length) {
            throw new OutputLengthException("Output buffer too short");
        }
        Pack.littleEndianToLong(in, inOff, this.currentBlock);
        this.processBlock(this.currentBlock, this.currentBlock);
        Pack.longToLittleEndian(this.currentBlock, out, outOff);
        return this.blocksizeBytes;
    }

    public int processBlock(long[] in, long[] out) throws DataLengthException, IllegalStateException {
        if (this.kw[this.blocksizeWords] == 0L) {
            throw new IllegalStateException("Threefish engine not initialised");
        }
        if (in.length != this.blocksizeWords) {
            throw new DataLengthException("Input buffer too short");
        }
        if (out.length != this.blocksizeWords) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.forEncryption) {
            this.cipher.encryptBlock(in, out);
        } else {
            this.cipher.decryptBlock(in, out);
        }
        return this.blocksizeWords;
    }

    static long rotlXor(long x, int n, long xor) {
        return (x << n | x >>> -n) ^ xor;
    }

    static long xorRotr(long x, int n, long xor) {
        long xored = x ^ xor;
        return xored >>> n | xored << -n;
    }

    static {
        for (int i = 0; i < MOD9.length; ++i) {
            ThreefishEngine.MOD17[i] = i % 17;
            ThreefishEngine.MOD9[i] = i % 9;
            ThreefishEngine.MOD5[i] = i % 5;
            ThreefishEngine.MOD3[i] = i % 3;
        }
    }

    private static final class Threefish1024Cipher
    extends ThreefishCipher {
        private static final int ROTATION_0_0 = 24;
        private static final int ROTATION_0_1 = 13;
        private static final int ROTATION_0_2 = 8;
        private static final int ROTATION_0_3 = 47;
        private static final int ROTATION_0_4 = 8;
        private static final int ROTATION_0_5 = 17;
        private static final int ROTATION_0_6 = 22;
        private static final int ROTATION_0_7 = 37;
        private static final int ROTATION_1_0 = 38;
        private static final int ROTATION_1_1 = 19;
        private static final int ROTATION_1_2 = 10;
        private static final int ROTATION_1_3 = 55;
        private static final int ROTATION_1_4 = 49;
        private static final int ROTATION_1_5 = 18;
        private static final int ROTATION_1_6 = 23;
        private static final int ROTATION_1_7 = 52;
        private static final int ROTATION_2_0 = 33;
        private static final int ROTATION_2_1 = 4;
        private static final int ROTATION_2_2 = 51;
        private static final int ROTATION_2_3 = 13;
        private static final int ROTATION_2_4 = 34;
        private static final int ROTATION_2_5 = 41;
        private static final int ROTATION_2_6 = 59;
        private static final int ROTATION_2_7 = 17;
        private static final int ROTATION_3_0 = 5;
        private static final int ROTATION_3_1 = 20;
        private static final int ROTATION_3_2 = 48;
        private static final int ROTATION_3_3 = 41;
        private static final int ROTATION_3_4 = 47;
        private static final int ROTATION_3_5 = 28;
        private static final int ROTATION_3_6 = 16;
        private static final int ROTATION_3_7 = 25;
        private static final int ROTATION_4_0 = 41;
        private static final int ROTATION_4_1 = 9;
        private static final int ROTATION_4_2 = 37;
        private static final int ROTATION_4_3 = 31;
        private static final int ROTATION_4_4 = 12;
        private static final int ROTATION_4_5 = 47;
        private static final int ROTATION_4_6 = 44;
        private static final int ROTATION_4_7 = 30;
        private static final int ROTATION_5_0 = 16;
        private static final int ROTATION_5_1 = 34;
        private static final int ROTATION_5_2 = 56;
        private static final int ROTATION_5_3 = 51;
        private static final int ROTATION_5_4 = 4;
        private static final int ROTATION_5_5 = 53;
        private static final int ROTATION_5_6 = 42;
        private static final int ROTATION_5_7 = 41;
        private static final int ROTATION_6_0 = 31;
        private static final int ROTATION_6_1 = 44;
        private static final int ROTATION_6_2 = 47;
        private static final int ROTATION_6_3 = 46;
        private static final int ROTATION_6_4 = 19;
        private static final int ROTATION_6_5 = 42;
        private static final int ROTATION_6_6 = 44;
        private static final int ROTATION_6_7 = 25;
        private static final int ROTATION_7_0 = 9;
        private static final int ROTATION_7_1 = 48;
        private static final int ROTATION_7_2 = 35;
        private static final int ROTATION_7_3 = 52;
        private static final int ROTATION_7_4 = 23;
        private static final int ROTATION_7_5 = 31;
        private static final int ROTATION_7_6 = 37;
        private static final int ROTATION_7_7 = 20;

        public Threefish1024Cipher(long[] kw, long[] t) {
            super(kw, t);
        }

        @Override
        void encryptBlock(long[] block, long[] out) {
            long[] kw = this.kw;
            long[] t = this.t;
            int[] mod17 = MOD17;
            int[] mod3 = MOD3;
            if (kw.length != 33) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long b0 = block[0];
            long b1 = block[1];
            long b2 = block[2];
            long b3 = block[3];
            long b4 = block[4];
            long b5 = block[5];
            long b6 = block[6];
            long b7 = block[7];
            long b8 = block[8];
            long b9 = block[9];
            long b10 = block[10];
            long b11 = block[11];
            long b12 = block[12];
            long b13 = block[13];
            long b14 = block[14];
            long b15 = block[15];
            b0 += kw[0];
            b1 += kw[1];
            b2 += kw[2];
            b3 += kw[3];
            b4 += kw[4];
            b5 += kw[5];
            b6 += kw[6];
            b7 += kw[7];
            b8 += kw[8];
            b9 += kw[9];
            b10 += kw[10];
            b11 += kw[11];
            b12 += kw[12];
            b13 += kw[13] + t[0];
            b14 += kw[14] + t[1];
            b15 += kw[15];
            for (int d = 1; d < 20; d += 2) {
                int dm17 = mod17[d];
                int dm3 = mod3[d];
                b0 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 24, b0);
                b2 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 13, b2);
                b4 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 8, b4);
                b6 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 47, b6);
                b8 += b9;
                b9 = ThreefishEngine.rotlXor(b9, 8, b8);
                b10 += b11;
                b11 = ThreefishEngine.rotlXor(b11, 17, b10);
                b12 += b13;
                b13 = ThreefishEngine.rotlXor(b13, 22, b12);
                b14 += b15;
                b15 = ThreefishEngine.rotlXor(b15, 37, b14);
                b0 += b9;
                b9 = ThreefishEngine.rotlXor(b9, 38, b0);
                b2 += b13;
                b13 = ThreefishEngine.rotlXor(b13, 19, b2);
                b6 += b11;
                b11 = ThreefishEngine.rotlXor(b11, 10, b6);
                b4 += b15;
                b15 = ThreefishEngine.rotlXor(b15, 55, b4);
                b10 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 49, b10);
                b12 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 18, b12);
                b14 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 23, b14);
                b8 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 52, b8);
                b0 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 33, b0);
                b2 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 4, b2);
                b4 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 51, b4);
                b6 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 13, b6);
                b12 += b15;
                b15 = ThreefishEngine.rotlXor(b15, 34, b12);
                b14 += b13;
                b13 = ThreefishEngine.rotlXor(b13, 41, b14);
                b8 += b11;
                b11 = ThreefishEngine.rotlXor(b11, 59, b8);
                b10 += b9;
                b9 = ThreefishEngine.rotlXor(b9, 17, b10);
                b0 += b15;
                b15 = ThreefishEngine.rotlXor(b15, 5, b0);
                b2 += b11;
                b11 = ThreefishEngine.rotlXor(b11, 20, b2);
                b6 += b13;
                b13 = ThreefishEngine.rotlXor(b13, 48, b6);
                b4 += b9;
                b9 = ThreefishEngine.rotlXor(b9, 41, b4);
                b14 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 47, b14);
                b8 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 28, b8);
                b10 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 16, b10);
                b12 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 25, b12);
                b0 += kw[dm17];
                b1 += kw[dm17 + 1];
                b2 += kw[dm17 + 2];
                b3 += kw[dm17 + 3];
                b4 += kw[dm17 + 4];
                b5 += kw[dm17 + 5];
                b6 += kw[dm17 + 6];
                b7 += kw[dm17 + 7];
                b8 += kw[dm17 + 8];
                b9 += kw[dm17 + 9];
                b10 += kw[dm17 + 10];
                b11 += kw[dm17 + 11];
                b12 += kw[dm17 + 12];
                b13 += kw[dm17 + 13] + t[dm3];
                b14 += kw[dm17 + 14] + t[dm3 + 1];
                b15 += kw[dm17 + 15] + (long)d;
                b0 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 41, b0);
                b2 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 9, b2);
                b4 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 37, b4);
                b6 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 31, b6);
                b8 += b9;
                b9 = ThreefishEngine.rotlXor(b9, 12, b8);
                b10 += b11;
                b11 = ThreefishEngine.rotlXor(b11, 47, b10);
                b12 += b13;
                b13 = ThreefishEngine.rotlXor(b13, 44, b12);
                b14 += b15;
                b15 = ThreefishEngine.rotlXor(b15, 30, b14);
                b0 += b9;
                b9 = ThreefishEngine.rotlXor(b9, 16, b0);
                b2 += b13;
                b13 = ThreefishEngine.rotlXor(b13, 34, b2);
                b6 += b11;
                b11 = ThreefishEngine.rotlXor(b11, 56, b6);
                b4 += b15;
                b15 = ThreefishEngine.rotlXor(b15, 51, b4);
                b10 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 4, b10);
                b12 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 53, b12);
                b14 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 42, b14);
                b8 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 41, b8);
                b0 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 31, b0);
                b2 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 44, b2);
                b4 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 47, b4);
                b6 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 46, b6);
                b12 += b15;
                b15 = ThreefishEngine.rotlXor(b15, 19, b12);
                b14 += b13;
                b13 = ThreefishEngine.rotlXor(b13, 42, b14);
                b8 += b11;
                b11 = ThreefishEngine.rotlXor(b11, 44, b8);
                b10 += b9;
                b9 = ThreefishEngine.rotlXor(b9, 25, b10);
                b0 += b15;
                b15 = ThreefishEngine.rotlXor(b15, 9, b0);
                b2 += b11;
                b11 = ThreefishEngine.rotlXor(b11, 48, b2);
                b6 += b13;
                b13 = ThreefishEngine.rotlXor(b13, 35, b6);
                b4 += b9;
                b9 = ThreefishEngine.rotlXor(b9, 52, b4);
                b14 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 23, b14);
                b8 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 31, b8);
                b10 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 37, b10);
                b12 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 20, b12);
                b0 += kw[dm17 + 1];
                b1 += kw[dm17 + 2];
                b2 += kw[dm17 + 3];
                b3 += kw[dm17 + 4];
                b4 += kw[dm17 + 5];
                b5 += kw[dm17 + 6];
                b6 += kw[dm17 + 7];
                b7 += kw[dm17 + 8];
                b8 += kw[dm17 + 9];
                b9 += kw[dm17 + 10];
                b10 += kw[dm17 + 11];
                b11 += kw[dm17 + 12];
                b12 += kw[dm17 + 13];
                b13 += kw[dm17 + 14] + t[dm3 + 1];
                b14 += kw[dm17 + 15] + t[dm3 + 2];
                b15 += kw[dm17 + 16] + (long)d + 1L;
            }
            out[0] = b0;
            out[1] = b1;
            out[2] = b2;
            out[3] = b3;
            out[4] = b4;
            out[5] = b5;
            out[6] = b6;
            out[7] = b7;
            out[8] = b8;
            out[9] = b9;
            out[10] = b10;
            out[11] = b11;
            out[12] = b12;
            out[13] = b13;
            out[14] = b14;
            out[15] = b15;
        }

        @Override
        void decryptBlock(long[] block, long[] state) {
            long[] kw = this.kw;
            long[] t = this.t;
            int[] mod17 = MOD17;
            int[] mod3 = MOD3;
            if (kw.length != 33) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long b0 = block[0];
            long b1 = block[1];
            long b2 = block[2];
            long b3 = block[3];
            long b4 = block[4];
            long b5 = block[5];
            long b6 = block[6];
            long b7 = block[7];
            long b8 = block[8];
            long b9 = block[9];
            long b10 = block[10];
            long b11 = block[11];
            long b12 = block[12];
            long b13 = block[13];
            long b14 = block[14];
            long b15 = block[15];
            for (int d = 19; d >= 1; d -= 2) {
                int dm17 = mod17[d];
                int dm3 = mod3[d];
                b0 -= kw[dm17 + 1];
                b1 -= kw[dm17 + 2];
                b2 -= kw[dm17 + 3];
                b3 -= kw[dm17 + 4];
                b4 -= kw[dm17 + 5];
                b5 -= kw[dm17 + 6];
                b6 -= kw[dm17 + 7];
                b7 -= kw[dm17 + 8];
                b8 -= kw[dm17 + 9];
                b9 -= kw[dm17 + 10];
                b10 -= kw[dm17 + 11];
                b11 -= kw[dm17 + 12];
                b12 -= kw[dm17 + 13];
                b13 -= kw[dm17 + 14] + t[dm3 + 1];
                b14 -= kw[dm17 + 15] + t[dm3 + 2];
                b15 -= kw[dm17 + 16] + (long)d + 1L;
                b15 = ThreefishEngine.xorRotr(b15, 9, b0);
                b0 -= b15;
                b11 = ThreefishEngine.xorRotr(b11, 48, b2);
                b2 -= b11;
                b13 = ThreefishEngine.xorRotr(b13, 35, b6);
                b6 -= b13;
                b9 = ThreefishEngine.xorRotr(b9, 52, b4);
                b4 -= b9;
                b1 = ThreefishEngine.xorRotr(b1, 23, b14);
                b14 -= b1;
                b5 = ThreefishEngine.xorRotr(b5, 31, b8);
                b8 -= b5;
                b3 = ThreefishEngine.xorRotr(b3, 37, b10);
                b10 -= b3;
                b7 = ThreefishEngine.xorRotr(b7, 20, b12);
                b12 -= b7;
                b7 = ThreefishEngine.xorRotr(b7, 31, b0);
                b0 -= b7;
                b5 = ThreefishEngine.xorRotr(b5, 44, b2);
                b2 -= b5;
                b3 = ThreefishEngine.xorRotr(b3, 47, b4);
                b4 -= b3;
                b1 = ThreefishEngine.xorRotr(b1, 46, b6);
                b6 -= b1;
                b15 = ThreefishEngine.xorRotr(b15, 19, b12);
                b12 -= b15;
                b13 = ThreefishEngine.xorRotr(b13, 42, b14);
                b14 -= b13;
                b11 = ThreefishEngine.xorRotr(b11, 44, b8);
                b8 -= b11;
                b9 = ThreefishEngine.xorRotr(b9, 25, b10);
                b10 -= b9;
                b9 = ThreefishEngine.xorRotr(b9, 16, b0);
                b0 -= b9;
                b13 = ThreefishEngine.xorRotr(b13, 34, b2);
                b2 -= b13;
                b11 = ThreefishEngine.xorRotr(b11, 56, b6);
                b6 -= b11;
                b15 = ThreefishEngine.xorRotr(b15, 51, b4);
                b4 -= b15;
                b7 = ThreefishEngine.xorRotr(b7, 4, b10);
                b10 -= b7;
                b3 = ThreefishEngine.xorRotr(b3, 53, b12);
                b12 -= b3;
                b5 = ThreefishEngine.xorRotr(b5, 42, b14);
                b14 -= b5;
                b1 = ThreefishEngine.xorRotr(b1, 41, b8);
                b8 -= b1;
                b1 = ThreefishEngine.xorRotr(b1, 41, b0);
                b0 -= b1;
                b3 = ThreefishEngine.xorRotr(b3, 9, b2);
                b2 -= b3;
                b5 = ThreefishEngine.xorRotr(b5, 37, b4);
                b4 -= b5;
                b7 = ThreefishEngine.xorRotr(b7, 31, b6);
                b6 -= b7;
                b9 = ThreefishEngine.xorRotr(b9, 12, b8);
                b8 -= b9;
                b11 = ThreefishEngine.xorRotr(b11, 47, b10);
                b10 -= b11;
                b13 = ThreefishEngine.xorRotr(b13, 44, b12);
                b12 -= b13;
                b15 = ThreefishEngine.xorRotr(b15, 30, b14);
                b14 -= b15;
                b0 -= kw[dm17];
                b1 -= kw[dm17 + 1];
                b2 -= kw[dm17 + 2];
                b3 -= kw[dm17 + 3];
                b4 -= kw[dm17 + 4];
                b5 -= kw[dm17 + 5];
                b6 -= kw[dm17 + 6];
                b7 -= kw[dm17 + 7];
                b8 -= kw[dm17 + 8];
                b9 -= kw[dm17 + 9];
                b10 -= kw[dm17 + 10];
                b11 -= kw[dm17 + 11];
                b12 -= kw[dm17 + 12];
                b13 -= kw[dm17 + 13] + t[dm3];
                b14 -= kw[dm17 + 14] + t[dm3 + 1];
                b15 -= kw[dm17 + 15] + (long)d;
                b15 = ThreefishEngine.xorRotr(b15, 5, b0);
                b0 -= b15;
                b11 = ThreefishEngine.xorRotr(b11, 20, b2);
                b2 -= b11;
                b13 = ThreefishEngine.xorRotr(b13, 48, b6);
                b6 -= b13;
                b9 = ThreefishEngine.xorRotr(b9, 41, b4);
                b4 -= b9;
                b1 = ThreefishEngine.xorRotr(b1, 47, b14);
                b14 -= b1;
                b5 = ThreefishEngine.xorRotr(b5, 28, b8);
                b8 -= b5;
                b3 = ThreefishEngine.xorRotr(b3, 16, b10);
                b10 -= b3;
                b7 = ThreefishEngine.xorRotr(b7, 25, b12);
                b12 -= b7;
                b7 = ThreefishEngine.xorRotr(b7, 33, b0);
                b0 -= b7;
                b5 = ThreefishEngine.xorRotr(b5, 4, b2);
                b2 -= b5;
                b3 = ThreefishEngine.xorRotr(b3, 51, b4);
                b4 -= b3;
                b1 = ThreefishEngine.xorRotr(b1, 13, b6);
                b6 -= b1;
                b15 = ThreefishEngine.xorRotr(b15, 34, b12);
                b12 -= b15;
                b13 = ThreefishEngine.xorRotr(b13, 41, b14);
                b14 -= b13;
                b11 = ThreefishEngine.xorRotr(b11, 59, b8);
                b8 -= b11;
                b9 = ThreefishEngine.xorRotr(b9, 17, b10);
                b10 -= b9;
                b9 = ThreefishEngine.xorRotr(b9, 38, b0);
                b0 -= b9;
                b13 = ThreefishEngine.xorRotr(b13, 19, b2);
                b2 -= b13;
                b11 = ThreefishEngine.xorRotr(b11, 10, b6);
                b6 -= b11;
                b15 = ThreefishEngine.xorRotr(b15, 55, b4);
                b4 -= b15;
                b7 = ThreefishEngine.xorRotr(b7, 49, b10);
                b10 -= b7;
                b3 = ThreefishEngine.xorRotr(b3, 18, b12);
                b12 -= b3;
                b5 = ThreefishEngine.xorRotr(b5, 23, b14);
                b14 -= b5;
                b1 = ThreefishEngine.xorRotr(b1, 52, b8);
                b8 -= b1;
                b1 = ThreefishEngine.xorRotr(b1, 24, b0);
                b0 -= b1;
                b3 = ThreefishEngine.xorRotr(b3, 13, b2);
                b2 -= b3;
                b5 = ThreefishEngine.xorRotr(b5, 8, b4);
                b4 -= b5;
                b7 = ThreefishEngine.xorRotr(b7, 47, b6);
                b6 -= b7;
                b9 = ThreefishEngine.xorRotr(b9, 8, b8);
                b8 -= b9;
                b11 = ThreefishEngine.xorRotr(b11, 17, b10);
                b10 -= b11;
                b13 = ThreefishEngine.xorRotr(b13, 22, b12);
                b12 -= b13;
                b15 = ThreefishEngine.xorRotr(b15, 37, b14);
                b14 -= b15;
            }
            b0 -= kw[0];
            b1 -= kw[1];
            b2 -= kw[2];
            b3 -= kw[3];
            b4 -= kw[4];
            b5 -= kw[5];
            b6 -= kw[6];
            b7 -= kw[7];
            b8 -= kw[8];
            b9 -= kw[9];
            b10 -= kw[10];
            b11 -= kw[11];
            b12 -= kw[12];
            b13 -= kw[13] + t[0];
            b14 -= kw[14] + t[1];
            b15 -= kw[15];
            state[0] = b0;
            state[1] = b1;
            state[2] = b2;
            state[3] = b3;
            state[4] = b4;
            state[5] = b5;
            state[6] = b6;
            state[7] = b7;
            state[8] = b8;
            state[9] = b9;
            state[10] = b10;
            state[11] = b11;
            state[12] = b12;
            state[13] = b13;
            state[14] = b14;
            state[15] = b15;
        }
    }

    private static final class Threefish256Cipher
    extends ThreefishCipher {
        private static final int ROTATION_0_0 = 14;
        private static final int ROTATION_0_1 = 16;
        private static final int ROTATION_1_0 = 52;
        private static final int ROTATION_1_1 = 57;
        private static final int ROTATION_2_0 = 23;
        private static final int ROTATION_2_1 = 40;
        private static final int ROTATION_3_0 = 5;
        private static final int ROTATION_3_1 = 37;
        private static final int ROTATION_4_0 = 25;
        private static final int ROTATION_4_1 = 33;
        private static final int ROTATION_5_0 = 46;
        private static final int ROTATION_5_1 = 12;
        private static final int ROTATION_6_0 = 58;
        private static final int ROTATION_6_1 = 22;
        private static final int ROTATION_7_0 = 32;
        private static final int ROTATION_7_1 = 32;

        public Threefish256Cipher(long[] kw, long[] t) {
            super(kw, t);
        }

        @Override
        void encryptBlock(long[] block, long[] out) {
            long[] kw = this.kw;
            long[] t = this.t;
            int[] mod5 = MOD5;
            int[] mod3 = MOD3;
            if (kw.length != 9) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long b0 = block[0];
            long b1 = block[1];
            long b2 = block[2];
            long b3 = block[3];
            b0 += kw[0];
            b1 += kw[1] + t[0];
            b2 += kw[2] + t[1];
            b3 += kw[3];
            for (int d = 1; d < 18; d += 2) {
                int dm5 = mod5[d];
                int dm3 = mod3[d];
                b0 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 14, b0);
                b2 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 16, b2);
                b0 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 52, b0);
                b2 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 57, b2);
                b0 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 23, b0);
                b2 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 40, b2);
                b0 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 5, b0);
                b2 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 37, b2);
                b0 += kw[dm5];
                b2 += kw[dm5 + 2] + t[dm3 + 1];
                b1 = ThreefishEngine.rotlXor(b1, 25, b0 += (b1 += kw[dm5 + 1] + t[dm3]));
                b3 = ThreefishEngine.rotlXor(b3, 33, b2 += (b3 += kw[dm5 + 3] + (long)d));
                b0 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 46, b0);
                b2 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 12, b2);
                b0 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 58, b0);
                b2 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 22, b2);
                b0 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 32, b0);
                b2 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 32, b2);
                b0 += kw[dm5 + 1];
                b1 += kw[dm5 + 2] + t[dm3 + 1];
                b2 += kw[dm5 + 3] + t[dm3 + 2];
                b3 += kw[dm5 + 4] + (long)d + 1L;
            }
            out[0] = b0;
            out[1] = b1;
            out[2] = b2;
            out[3] = b3;
        }

        @Override
        void decryptBlock(long[] block, long[] state) {
            long[] kw = this.kw;
            long[] t = this.t;
            int[] mod5 = MOD5;
            int[] mod3 = MOD3;
            if (kw.length != 9) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long b0 = block[0];
            long b1 = block[1];
            long b2 = block[2];
            long b3 = block[3];
            for (int d = 17; d >= 1; d -= 2) {
                int dm5 = mod5[d];
                int dm3 = mod3[d];
                b1 -= kw[dm5 + 2] + t[dm3 + 1];
                b3 -= kw[dm5 + 4] + (long)d + 1L;
                b3 = ThreefishEngine.xorRotr(b3, 32, b0 -= kw[dm5 + 1]);
                b1 = ThreefishEngine.xorRotr(b1, 32, b2 -= kw[dm5 + 3] + t[dm3 + 2]);
                b2 -= b1;
                b1 = ThreefishEngine.xorRotr(b1, 58, b0 -= b3);
                b3 = ThreefishEngine.xorRotr(b3, 22, b2);
                b2 -= b3;
                b3 = ThreefishEngine.xorRotr(b3, 46, b0 -= b1);
                b1 = ThreefishEngine.xorRotr(b1, 12, b2);
                b2 -= b1;
                b1 = ThreefishEngine.xorRotr(b1, 25, b0 -= b3);
                b0 -= b1;
                b3 = ThreefishEngine.xorRotr(b3, 33, b2);
                b2 -= b3;
                b1 -= kw[dm5 + 1] + t[dm3];
                b3 -= kw[dm5 + 3] + (long)d;
                b3 = ThreefishEngine.xorRotr(b3, 5, b0 -= kw[dm5]);
                b1 = ThreefishEngine.xorRotr(b1, 37, b2 -= kw[dm5 + 2] + t[dm3 + 1]);
                b2 -= b1;
                b1 = ThreefishEngine.xorRotr(b1, 23, b0 -= b3);
                b3 = ThreefishEngine.xorRotr(b3, 40, b2);
                b2 -= b3;
                b3 = ThreefishEngine.xorRotr(b3, 52, b0 -= b1);
                b1 = ThreefishEngine.xorRotr(b1, 57, b2);
                b2 -= b1;
                b1 = ThreefishEngine.xorRotr(b1, 14, b0 -= b3);
                b0 -= b1;
                b3 = ThreefishEngine.xorRotr(b3, 16, b2);
                b2 -= b3;
            }
            state[0] = b0 -= kw[0];
            state[1] = b1 -= kw[1] + t[0];
            state[2] = b2 -= kw[2] + t[1];
            state[3] = b3 -= kw[3];
        }
    }

    private static final class Threefish512Cipher
    extends ThreefishCipher {
        private static final int ROTATION_0_0 = 46;
        private static final int ROTATION_0_1 = 36;
        private static final int ROTATION_0_2 = 19;
        private static final int ROTATION_0_3 = 37;
        private static final int ROTATION_1_0 = 33;
        private static final int ROTATION_1_1 = 27;
        private static final int ROTATION_1_2 = 14;
        private static final int ROTATION_1_3 = 42;
        private static final int ROTATION_2_0 = 17;
        private static final int ROTATION_2_1 = 49;
        private static final int ROTATION_2_2 = 36;
        private static final int ROTATION_2_3 = 39;
        private static final int ROTATION_3_0 = 44;
        private static final int ROTATION_3_1 = 9;
        private static final int ROTATION_3_2 = 54;
        private static final int ROTATION_3_3 = 56;
        private static final int ROTATION_4_0 = 39;
        private static final int ROTATION_4_1 = 30;
        private static final int ROTATION_4_2 = 34;
        private static final int ROTATION_4_3 = 24;
        private static final int ROTATION_5_0 = 13;
        private static final int ROTATION_5_1 = 50;
        private static final int ROTATION_5_2 = 10;
        private static final int ROTATION_5_3 = 17;
        private static final int ROTATION_6_0 = 25;
        private static final int ROTATION_6_1 = 29;
        private static final int ROTATION_6_2 = 39;
        private static final int ROTATION_6_3 = 43;
        private static final int ROTATION_7_0 = 8;
        private static final int ROTATION_7_1 = 35;
        private static final int ROTATION_7_2 = 56;
        private static final int ROTATION_7_3 = 22;

        protected Threefish512Cipher(long[] kw, long[] t) {
            super(kw, t);
        }

        @Override
        public void encryptBlock(long[] block, long[] out) {
            long[] kw = this.kw;
            long[] t = this.t;
            int[] mod9 = MOD9;
            int[] mod3 = MOD3;
            if (kw.length != 17) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long b0 = block[0];
            long b1 = block[1];
            long b2 = block[2];
            long b3 = block[3];
            long b4 = block[4];
            long b5 = block[5];
            long b6 = block[6];
            long b7 = block[7];
            b0 += kw[0];
            b1 += kw[1];
            b2 += kw[2];
            b3 += kw[3];
            b4 += kw[4];
            b5 += kw[5] + t[0];
            b6 += kw[6] + t[1];
            b7 += kw[7];
            for (int d = 1; d < 18; d += 2) {
                int dm9 = mod9[d];
                int dm3 = mod3[d];
                b0 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 46, b0);
                b2 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 36, b2);
                b4 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 19, b4);
                b6 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 37, b6);
                b2 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 33, b2);
                b4 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 27, b4);
                b6 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 14, b6);
                b0 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 42, b0);
                b4 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 17, b4);
                b6 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 49, b6);
                b0 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 36, b0);
                b2 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 39, b2);
                b6 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 44, b6);
                b0 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 9, b0);
                b2 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 54, b2);
                b4 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 56, b4);
                b0 += kw[dm9];
                b1 += kw[dm9 + 1];
                b2 += kw[dm9 + 2];
                b3 += kw[dm9 + 3];
                b4 += kw[dm9 + 4];
                b5 += kw[dm9 + 5] + t[dm3];
                b6 += kw[dm9 + 6] + t[dm3 + 1];
                b7 += kw[dm9 + 7] + (long)d;
                b0 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 39, b0);
                b2 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 30, b2);
                b4 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 34, b4);
                b6 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 24, b6);
                b2 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 13, b2);
                b4 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 50, b4);
                b6 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 10, b6);
                b0 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 17, b0);
                b4 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 25, b4);
                b6 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 29, b6);
                b0 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 39, b0);
                b2 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 43, b2);
                b6 += b1;
                b1 = ThreefishEngine.rotlXor(b1, 8, b6);
                b0 += b7;
                b7 = ThreefishEngine.rotlXor(b7, 35, b0);
                b2 += b5;
                b5 = ThreefishEngine.rotlXor(b5, 56, b2);
                b4 += b3;
                b3 = ThreefishEngine.rotlXor(b3, 22, b4);
                b0 += kw[dm9 + 1];
                b1 += kw[dm9 + 2];
                b2 += kw[dm9 + 3];
                b3 += kw[dm9 + 4];
                b4 += kw[dm9 + 5];
                b5 += kw[dm9 + 6] + t[dm3 + 1];
                b6 += kw[dm9 + 7] + t[dm3 + 2];
                b7 += kw[dm9 + 8] + (long)d + 1L;
            }
            out[0] = b0;
            out[1] = b1;
            out[2] = b2;
            out[3] = b3;
            out[4] = b4;
            out[5] = b5;
            out[6] = b6;
            out[7] = b7;
        }

        @Override
        public void decryptBlock(long[] block, long[] state) {
            long[] kw = this.kw;
            long[] t = this.t;
            int[] mod9 = MOD9;
            int[] mod3 = MOD3;
            if (kw.length != 17) {
                throw new IllegalArgumentException();
            }
            if (t.length != 5) {
                throw new IllegalArgumentException();
            }
            long b0 = block[0];
            long b1 = block[1];
            long b2 = block[2];
            long b3 = block[3];
            long b4 = block[4];
            long b5 = block[5];
            long b6 = block[6];
            long b7 = block[7];
            for (int d = 17; d >= 1; d -= 2) {
                int dm9 = mod9[d];
                int dm3 = mod3[d];
                b0 -= kw[dm9 + 1];
                b1 -= kw[dm9 + 2];
                b2 -= kw[dm9 + 3];
                b3 -= kw[dm9 + 4];
                b4 -= kw[dm9 + 5];
                b5 -= kw[dm9 + 6] + t[dm3 + 1];
                b7 -= kw[dm9 + 8] + (long)d + 1L;
                b1 = ThreefishEngine.xorRotr(b1, 8, b6 -= kw[dm9 + 7] + t[dm3 + 2]);
                b6 -= b1;
                b7 = ThreefishEngine.xorRotr(b7, 35, b0);
                b0 -= b7;
                b5 = ThreefishEngine.xorRotr(b5, 56, b2);
                b2 -= b5;
                b3 = ThreefishEngine.xorRotr(b3, 22, b4);
                b1 = ThreefishEngine.xorRotr(b1, 25, b4 -= b3);
                b4 -= b1;
                b3 = ThreefishEngine.xorRotr(b3, 29, b6);
                b6 -= b3;
                b5 = ThreefishEngine.xorRotr(b5, 39, b0);
                b0 -= b5;
                b7 = ThreefishEngine.xorRotr(b7, 43, b2);
                b1 = ThreefishEngine.xorRotr(b1, 13, b2 -= b7);
                b2 -= b1;
                b7 = ThreefishEngine.xorRotr(b7, 50, b4);
                b4 -= b7;
                b5 = ThreefishEngine.xorRotr(b5, 10, b6);
                b6 -= b5;
                b3 = ThreefishEngine.xorRotr(b3, 17, b0);
                b1 = ThreefishEngine.xorRotr(b1, 39, b0 -= b3);
                b0 -= b1;
                b3 = ThreefishEngine.xorRotr(b3, 30, b2);
                b2 -= b3;
                b5 = ThreefishEngine.xorRotr(b5, 34, b4);
                b4 -= b5;
                b7 = ThreefishEngine.xorRotr(b7, 24, b6);
                b6 -= b7;
                b0 -= kw[dm9];
                b1 -= kw[dm9 + 1];
                b2 -= kw[dm9 + 2];
                b3 -= kw[dm9 + 3];
                b4 -= kw[dm9 + 4];
                b5 -= kw[dm9 + 5] + t[dm3];
                b7 -= kw[dm9 + 7] + (long)d;
                b1 = ThreefishEngine.xorRotr(b1, 44, b6 -= kw[dm9 + 6] + t[dm3 + 1]);
                b6 -= b1;
                b7 = ThreefishEngine.xorRotr(b7, 9, b0);
                b0 -= b7;
                b5 = ThreefishEngine.xorRotr(b5, 54, b2);
                b2 -= b5;
                b3 = ThreefishEngine.xorRotr(b3, 56, b4);
                b1 = ThreefishEngine.xorRotr(b1, 17, b4 -= b3);
                b4 -= b1;
                b3 = ThreefishEngine.xorRotr(b3, 49, b6);
                b6 -= b3;
                b5 = ThreefishEngine.xorRotr(b5, 36, b0);
                b0 -= b5;
                b7 = ThreefishEngine.xorRotr(b7, 39, b2);
                b1 = ThreefishEngine.xorRotr(b1, 33, b2 -= b7);
                b2 -= b1;
                b7 = ThreefishEngine.xorRotr(b7, 27, b4);
                b4 -= b7;
                b5 = ThreefishEngine.xorRotr(b5, 14, b6);
                b6 -= b5;
                b3 = ThreefishEngine.xorRotr(b3, 42, b0);
                b1 = ThreefishEngine.xorRotr(b1, 46, b0 -= b3);
                b0 -= b1;
                b3 = ThreefishEngine.xorRotr(b3, 36, b2);
                b2 -= b3;
                b5 = ThreefishEngine.xorRotr(b5, 19, b4);
                b4 -= b5;
                b7 = ThreefishEngine.xorRotr(b7, 37, b6);
                b6 -= b7;
            }
            b0 -= kw[0];
            b1 -= kw[1];
            b2 -= kw[2];
            b3 -= kw[3];
            b4 -= kw[4];
            b5 -= kw[5] + t[0];
            b6 -= kw[6] + t[1];
            b7 -= kw[7];
            state[0] = b0;
            state[1] = b1;
            state[2] = b2;
            state[3] = b3;
            state[4] = b4;
            state[5] = b5;
            state[6] = b6;
            state[7] = b7;
        }
    }

    private static abstract class ThreefishCipher {
        protected final long[] t;
        protected final long[] kw;

        protected ThreefishCipher(long[] kw, long[] t) {
            this.kw = kw;
            this.t = t;
        }

        abstract void encryptBlock(long[] var1, long[] var2);

        abstract void decryptBlock(long[] var1, long[] var2);
    }
}

