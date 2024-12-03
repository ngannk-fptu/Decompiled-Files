/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Longs;
import org.bouncycastle.util.Pack;

public class Blake2bDigest
implements ExtendedDigest {
    private static final long[] blake2b_IV = new long[]{7640891576956012808L, -4942790177534073029L, 4354685564936845355L, -6534734903238641935L, 5840696475078001361L, -7276294671716946913L, 2270897969802886507L, 6620516959819538809L};
    private static final byte[][] blake2b_sigma = new byte[][]{{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, {14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3}, {11, 8, 12, 0, 5, 2, 15, 13, 10, 14, 3, 6, 7, 1, 9, 4}, {7, 9, 3, 1, 13, 12, 11, 14, 2, 6, 5, 10, 4, 0, 15, 8}, {9, 0, 5, 7, 2, 4, 10, 15, 14, 1, 11, 12, 6, 8, 3, 13}, {2, 12, 6, 10, 0, 11, 8, 3, 4, 13, 7, 5, 15, 14, 1, 9}, {12, 5, 1, 15, 14, 13, 4, 10, 0, 7, 6, 3, 9, 2, 8, 11}, {13, 11, 7, 14, 12, 1, 3, 9, 5, 0, 15, 4, 8, 6, 2, 10}, {6, 15, 14, 9, 11, 3, 0, 8, 12, 2, 13, 7, 1, 4, 10, 5}, {10, 2, 8, 4, 7, 6, 1, 5, 15, 11, 9, 14, 3, 12, 13, 0}, {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, {14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3}};
    private static int ROUNDS = 12;
    private static final int BLOCK_LENGTH_BYTES = 128;
    private int digestLength = 64;
    private int keyLength = 0;
    private byte[] salt = null;
    private byte[] personalization = null;
    private byte[] key = null;
    private int fanout = 1;
    private int depth = 1;
    private int leafLength = 0;
    private long nodeOffset = 0L;
    private int nodeDepth = 0;
    private int innerHashLength = 0;
    private boolean isLastNode = false;
    private byte[] buffer = null;
    private int bufferPos = 0;
    private long[] internalState = new long[16];
    private long[] chainValue = null;
    private long t0 = 0L;
    private long t1 = 0L;
    private long f0 = 0L;
    private long f1 = 0L;
    private final CryptoServicePurpose purpose;

    public Blake2bDigest() {
        this(512, CryptoServicePurpose.ANY);
    }

    public Blake2bDigest(int digestSize) {
        this(digestSize, CryptoServicePurpose.ANY);
    }

    public Blake2bDigest(Blake2bDigest digest) {
        this.bufferPos = digest.bufferPos;
        this.buffer = Arrays.clone(digest.buffer);
        this.keyLength = digest.keyLength;
        this.key = Arrays.clone(digest.key);
        this.digestLength = digest.digestLength;
        this.chainValue = Arrays.clone(digest.chainValue);
        this.personalization = Arrays.clone(digest.personalization);
        this.salt = Arrays.clone(digest.salt);
        this.t0 = digest.t0;
        this.t1 = digest.t1;
        this.f0 = digest.f0;
        this.purpose = digest.purpose;
    }

    public Blake2bDigest(int digestSize, CryptoServicePurpose purpose) {
        this.purpose = purpose;
        if (digestSize < 8 || digestSize > 512 || digestSize % 8 != 0) {
            throw new IllegalArgumentException("BLAKE2b digest bit length must be a multiple of 8 and not greater than 512");
        }
        this.buffer = new byte[128];
        this.keyLength = 0;
        this.digestLength = digestSize / 8;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, digestSize, purpose));
        this.init();
    }

    public Blake2bDigest(byte[] key) {
        this(key, CryptoServicePurpose.ANY);
    }

    public Blake2bDigest(byte[] key, CryptoServicePurpose purpose) {
        this.buffer = new byte[128];
        if (key != null) {
            this.key = new byte[key.length];
            System.arraycopy(key, 0, this.key, 0, key.length);
            if (key.length > 64) {
                throw new IllegalArgumentException("Keys > 64 are not supported");
            }
            this.keyLength = key.length;
            System.arraycopy(key, 0, this.buffer, 0, key.length);
            this.bufferPos = 128;
        }
        this.purpose = purpose;
        this.digestLength = 64;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.digestLength * 8, purpose));
        this.init();
    }

    public Blake2bDigest(byte[] key, int digestLength, byte[] salt, byte[] personalization) {
        this(key, digestLength, salt, personalization, CryptoServicePurpose.ANY);
    }

    public Blake2bDigest(byte[] key, int digestLength, byte[] salt, byte[] personalization, CryptoServicePurpose purpose) {
        this.purpose = purpose;
        this.buffer = new byte[128];
        if (digestLength < 1 || digestLength > 64) {
            throw new IllegalArgumentException("Invalid digest length (required: 1 - 64)");
        }
        this.digestLength = digestLength;
        if (salt != null) {
            if (salt.length != 16) {
                throw new IllegalArgumentException("salt length must be exactly 16 bytes");
            }
            this.salt = new byte[16];
            System.arraycopy(salt, 0, this.salt, 0, salt.length);
        }
        if (personalization != null) {
            if (personalization.length != 16) {
                throw new IllegalArgumentException("personalization length must be exactly 16 bytes");
            }
            this.personalization = new byte[16];
            System.arraycopy(personalization, 0, this.personalization, 0, personalization.length);
        }
        if (key != null) {
            this.key = new byte[key.length];
            System.arraycopy(key, 0, this.key, 0, key.length);
            if (key.length > 64) {
                throw new IllegalArgumentException("Keys > 64 are not supported");
            }
            this.keyLength = key.length;
            System.arraycopy(key, 0, this.buffer, 0, key.length);
            this.bufferPos = 128;
        }
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, digestLength * 8, purpose));
        this.init();
    }

    public Blake2bDigest(byte[] key, byte[] param) {
        this.buffer = new byte[128];
        this.purpose = CryptoServicePurpose.ANY;
        this.digestLength = param[0];
        this.keyLength = param[1];
        this.fanout = param[2];
        this.depth = param[3];
        this.leafLength = Pack.littleEndianToInt(param, 4);
        this.nodeOffset |= (long)Pack.littleEndianToInt(param, 8);
        this.nodeDepth = param[16];
        this.innerHashLength = param[17];
        this.init();
    }

    private void init() {
        if (this.chainValue == null) {
            this.chainValue = new long[8];
            this.chainValue[0] = blake2b_IV[0] ^ (long)(this.digestLength | this.keyLength << 8 | (this.fanout << 16 | this.depth << 24 | this.leafLength << 32));
            this.chainValue[1] = blake2b_IV[1] ^ this.nodeOffset;
            this.chainValue[2] = blake2b_IV[2] ^ (long)(this.nodeDepth | this.innerHashLength << 8);
            this.chainValue[3] = blake2b_IV[3];
            this.chainValue[4] = blake2b_IV[4];
            this.chainValue[5] = blake2b_IV[5];
            if (this.salt != null) {
                this.chainValue[4] = this.chainValue[4] ^ Pack.littleEndianToLong(this.salt, 0);
                this.chainValue[5] = this.chainValue[5] ^ Pack.littleEndianToLong(this.salt, 8);
            }
            this.chainValue[6] = blake2b_IV[6];
            this.chainValue[7] = blake2b_IV[7];
            if (this.personalization != null) {
                this.chainValue[6] = this.chainValue[6] ^ Pack.littleEndianToLong(this.personalization, 0);
                this.chainValue[7] = this.chainValue[7] ^ Pack.littleEndianToLong(this.personalization, 8);
            }
        }
    }

    private void initializeInternalState() {
        System.arraycopy(this.chainValue, 0, this.internalState, 0, this.chainValue.length);
        System.arraycopy(blake2b_IV, 0, this.internalState, this.chainValue.length, 4);
        this.internalState[12] = this.t0 ^ blake2b_IV[4];
        this.internalState[13] = this.t1 ^ blake2b_IV[5];
        this.internalState[14] = this.f0 ^ blake2b_IV[6];
        this.internalState[15] = this.f1 ^ blake2b_IV[7];
    }

    @Override
    public void update(byte b) {
        int remainingLength = 0;
        remainingLength = 128 - this.bufferPos;
        if (remainingLength == 0) {
            this.t0 += 128L;
            if (this.t0 == 0L) {
                ++this.t1;
            }
        } else {
            this.buffer[this.bufferPos] = b;
            ++this.bufferPos;
            return;
        }
        this.compress(this.buffer, 0);
        Arrays.fill(this.buffer, (byte)0);
        this.buffer[0] = b;
        this.bufferPos = 1;
    }

    @Override
    public void update(byte[] message, int offset, int len) {
        int messagePos;
        if (message == null || len == 0) {
            return;
        }
        int remainingLength = 0;
        if (this.bufferPos != 0) {
            remainingLength = 128 - this.bufferPos;
            if (remainingLength < len) {
                System.arraycopy(message, offset, this.buffer, this.bufferPos, remainingLength);
                this.t0 += 128L;
                if (this.t0 == 0L) {
                    ++this.t1;
                }
                this.compress(this.buffer, 0);
                this.bufferPos = 0;
                Arrays.fill(this.buffer, (byte)0);
            } else {
                System.arraycopy(message, offset, this.buffer, this.bufferPos, len);
                this.bufferPos += len;
                return;
            }
        }
        int blockWiseLastPos = offset + len - 128;
        for (messagePos = offset + remainingLength; messagePos < blockWiseLastPos; messagePos += 128) {
            this.t0 += 128L;
            if (this.t0 == 0L) {
                ++this.t1;
            }
            this.compress(message, messagePos);
        }
        System.arraycopy(message, messagePos, this.buffer, 0, offset + len - messagePos);
        this.bufferPos += offset + len - messagePos;
    }

    @Override
    public int doFinal(byte[] out, int outOffset) {
        if (outOffset > out.length - this.digestLength) {
            throw new OutputLengthException("output buffer too short");
        }
        this.f0 = -1L;
        if (this.isLastNode) {
            this.f1 = -1L;
        }
        this.t0 += (long)this.bufferPos;
        if (this.bufferPos > 0 && this.t0 == 0L) {
            ++this.t1;
        }
        this.compress(this.buffer, 0);
        Arrays.fill(this.buffer, (byte)0);
        Arrays.fill(this.internalState, 0L);
        int full = this.digestLength >>> 3;
        int partial = this.digestLength & 7;
        Pack.longToLittleEndian(this.chainValue, 0, full, out, outOffset);
        if (partial > 0) {
            byte[] bytes = new byte[8];
            Pack.longToLittleEndian(this.chainValue[full], bytes, 0);
            System.arraycopy(bytes, 0, out, outOffset + this.digestLength - partial, partial);
        }
        Arrays.fill(this.chainValue, 0L);
        this.reset();
        return this.digestLength;
    }

    @Override
    public void reset() {
        this.bufferPos = 0;
        this.f0 = 0L;
        this.f1 = 0L;
        this.t0 = 0L;
        this.t1 = 0L;
        this.isLastNode = false;
        this.chainValue = null;
        Arrays.fill(this.buffer, (byte)0);
        if (this.key != null) {
            System.arraycopy(this.key, 0, this.buffer, 0, this.key.length);
            this.bufferPos = 128;
        }
        this.init();
    }

    private void compress(byte[] message, int messagePos) {
        this.initializeInternalState();
        long[] m = new long[16];
        Pack.littleEndianToLong(message, messagePos, m);
        for (int round = 0; round < ROUNDS; ++round) {
            this.G(m[blake2b_sigma[round][0]], m[blake2b_sigma[round][1]], 0, 4, 8, 12);
            this.G(m[blake2b_sigma[round][2]], m[blake2b_sigma[round][3]], 1, 5, 9, 13);
            this.G(m[blake2b_sigma[round][4]], m[blake2b_sigma[round][5]], 2, 6, 10, 14);
            this.G(m[blake2b_sigma[round][6]], m[blake2b_sigma[round][7]], 3, 7, 11, 15);
            this.G(m[blake2b_sigma[round][8]], m[blake2b_sigma[round][9]], 0, 5, 10, 15);
            this.G(m[blake2b_sigma[round][10]], m[blake2b_sigma[round][11]], 1, 6, 11, 12);
            this.G(m[blake2b_sigma[round][12]], m[blake2b_sigma[round][13]], 2, 7, 8, 13);
            this.G(m[blake2b_sigma[round][14]], m[blake2b_sigma[round][15]], 3, 4, 9, 14);
        }
        for (int offset = 0; offset < this.chainValue.length; ++offset) {
            this.chainValue[offset] = this.chainValue[offset] ^ this.internalState[offset] ^ this.internalState[offset + 8];
        }
    }

    private void G(long m1, long m2, int posA, int posB, int posC, int posD) {
        this.internalState[posA] = this.internalState[posA] + this.internalState[posB] + m1;
        this.internalState[posD] = Longs.rotateRight(this.internalState[posD] ^ this.internalState[posA], 32);
        this.internalState[posC] = this.internalState[posC] + this.internalState[posD];
        this.internalState[posB] = Longs.rotateRight(this.internalState[posB] ^ this.internalState[posC], 24);
        this.internalState[posA] = this.internalState[posA] + this.internalState[posB] + m2;
        this.internalState[posD] = Longs.rotateRight(this.internalState[posD] ^ this.internalState[posA], 16);
        this.internalState[posC] = this.internalState[posC] + this.internalState[posD];
        this.internalState[posB] = Longs.rotateRight(this.internalState[posB] ^ this.internalState[posC], 63);
    }

    protected void setAsLastNode() {
        this.isLastNode = true;
    }

    @Override
    public String getAlgorithmName() {
        return "BLAKE2b";
    }

    @Override
    public int getDigestSize() {
        return this.digestLength;
    }

    @Override
    public int getByteLength() {
        return 128;
    }

    public void clearKey() {
        if (this.key != null) {
            Arrays.fill(this.key, (byte)0);
            Arrays.fill(this.buffer, (byte)0);
        }
    }

    public void clearSalt() {
        if (this.salt != null) {
            Arrays.fill(this.salt, (byte)0);
        }
    }
}

