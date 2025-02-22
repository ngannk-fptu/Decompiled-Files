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
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class Blake2sDigest
implements ExtendedDigest {
    private static final int[] blake2s_IV = new int[]{1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225};
    private static final byte[][] blake2s_sigma = new byte[][]{{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15}, {14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3}, {11, 8, 12, 0, 5, 2, 15, 13, 10, 14, 3, 6, 7, 1, 9, 4}, {7, 9, 3, 1, 13, 12, 11, 14, 2, 6, 5, 10, 4, 0, 15, 8}, {9, 0, 5, 7, 2, 4, 10, 15, 14, 1, 11, 12, 6, 8, 3, 13}, {2, 12, 6, 10, 0, 11, 8, 3, 4, 13, 7, 5, 15, 14, 1, 9}, {12, 5, 1, 15, 14, 13, 4, 10, 0, 7, 6, 3, 9, 2, 8, 11}, {13, 11, 7, 14, 12, 1, 3, 9, 5, 0, 15, 4, 8, 6, 2, 10}, {6, 15, 14, 9, 11, 3, 0, 8, 12, 2, 13, 7, 1, 4, 10, 5}, {10, 2, 8, 4, 7, 6, 1, 5, 15, 11, 9, 14, 3, 12, 13, 0}};
    private static final int ROUNDS = 10;
    private static final int BLOCK_LENGTH_BYTES = 64;
    private int digestLength = 32;
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
    private int[] internalState = new int[16];
    private int[] chainValue = null;
    private int t0 = 0;
    private int t1 = 0;
    private int f0 = 0;
    private int f1 = 0;
    private final CryptoServicePurpose purpose;

    public Blake2sDigest() {
        this(256, CryptoServicePurpose.ANY);
    }

    public Blake2sDigest(int digestSize) {
        this(digestSize, CryptoServicePurpose.ANY);
    }

    public Blake2sDigest(Blake2sDigest digest) {
        this.bufferPos = digest.bufferPos;
        this.buffer = Arrays.clone(digest.buffer);
        this.keyLength = digest.keyLength;
        this.key = Arrays.clone(digest.key);
        this.digestLength = digest.digestLength;
        this.internalState = Arrays.clone(digest.internalState);
        this.chainValue = Arrays.clone(digest.chainValue);
        this.t0 = digest.t0;
        this.t1 = digest.t1;
        this.f0 = digest.f0;
        this.salt = Arrays.clone(digest.salt);
        this.personalization = Arrays.clone(digest.personalization);
        this.fanout = digest.fanout;
        this.depth = digest.depth;
        this.leafLength = digest.leafLength;
        this.nodeOffset = digest.nodeOffset;
        this.nodeDepth = digest.nodeDepth;
        this.innerHashLength = digest.innerHashLength;
        this.purpose = digest.purpose;
    }

    public Blake2sDigest(int digestBits, CryptoServicePurpose purpose) {
        if (digestBits < 8 || digestBits > 256 || digestBits % 8 != 0) {
            throw new IllegalArgumentException("BLAKE2s digest bit length must be a multiple of 8 and not greater than 256");
        }
        this.digestLength = digestBits / 8;
        this.purpose = purpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, digestBits, purpose));
        this.init(null, null, null);
    }

    public Blake2sDigest(byte[] key) {
        this(key, CryptoServicePurpose.ANY);
    }

    public Blake2sDigest(byte[] key, CryptoServicePurpose purpose) {
        this.purpose = purpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, key.length * 8, purpose));
        this.init(null, null, key);
    }

    public Blake2sDigest(byte[] key, int digestBytes, byte[] salt, byte[] personalization) {
        this(key, digestBytes, salt, personalization, CryptoServicePurpose.ANY);
    }

    public Blake2sDigest(byte[] key, int digestBytes, byte[] salt, byte[] personalization, CryptoServicePurpose purpose) {
        if (digestBytes < 1 || digestBytes > 32) {
            throw new IllegalArgumentException("Invalid digest length (required: 1 - 32)");
        }
        this.digestLength = digestBytes;
        this.purpose = purpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, digestBytes * 8, purpose));
        this.init(salt, personalization, key);
    }

    Blake2sDigest(int digestBytes, byte[] key, byte[] salt, byte[] personalization, long offset, CryptoServicePurpose purpose) {
        this.digestLength = digestBytes;
        this.nodeOffset = offset;
        this.purpose = purpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, digestBytes * 8, purpose));
        this.init(salt, personalization, key);
    }

    Blake2sDigest(int digestBytes, int hashLength, long offset) {
        this(digestBytes, hashLength, offset, CryptoServicePurpose.ANY);
    }

    Blake2sDigest(int digestBytes, int hashLength, long offset, CryptoServicePurpose purpose) {
        this.digestLength = digestBytes;
        this.nodeOffset = offset;
        this.fanout = 0;
        this.depth = 0;
        this.leafLength = hashLength;
        this.innerHashLength = hashLength;
        this.nodeDepth = 0;
        this.purpose = purpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, digestBytes * 8, purpose));
        this.init(null, null, null);
    }

    Blake2sDigest(byte[] key, byte[] param) {
        this.purpose = CryptoServicePurpose.ANY;
        this.digestLength = param[0];
        this.keyLength = param[1];
        this.fanout = param[2];
        this.depth = param[3];
        this.leafLength = Pack.littleEndianToInt(param, 4);
        this.nodeOffset |= (long)Pack.littleEndianToInt(param, 8);
        this.nodeDepth = param[14];
        this.innerHashLength = param[15];
        byte[] salt = new byte[8];
        byte[] personalization = new byte[8];
        System.arraycopy(param, 16, salt, 0, 8);
        System.arraycopy(param, 24, personalization, 0, 8);
        this.init(salt, personalization, key);
    }

    private void init(byte[] salt, byte[] personalization, byte[] key) {
        this.buffer = new byte[64];
        if (key != null && key.length > 0) {
            this.keyLength = key.length;
            if (this.keyLength > 32) {
                throw new IllegalArgumentException("Keys > 32 bytes are not supported");
            }
            this.key = new byte[this.keyLength];
            System.arraycopy(key, 0, this.key, 0, this.keyLength);
            System.arraycopy(key, 0, this.buffer, 0, this.keyLength);
            this.bufferPos = 64;
        }
        if (this.chainValue == null) {
            this.chainValue = new int[8];
            this.chainValue[0] = blake2s_IV[0] ^ (this.digestLength | this.keyLength << 8 | (this.fanout << 16 | this.depth << 24));
            this.chainValue[1] = blake2s_IV[1] ^ this.leafLength;
            int nofHi = (int)(this.nodeOffset >> 32);
            int nofLo = (int)this.nodeOffset;
            this.chainValue[2] = blake2s_IV[2] ^ nofLo;
            this.chainValue[3] = blake2s_IV[3] ^ (nofHi | this.nodeDepth << 16 | this.innerHashLength << 24);
            this.chainValue[4] = blake2s_IV[4];
            this.chainValue[5] = blake2s_IV[5];
            if (salt != null) {
                if (salt.length != 8) {
                    throw new IllegalArgumentException("Salt length must be exactly 8 bytes");
                }
                this.salt = new byte[8];
                System.arraycopy(salt, 0, this.salt, 0, salt.length);
                this.chainValue[4] = this.chainValue[4] ^ Pack.littleEndianToInt(salt, 0);
                this.chainValue[5] = this.chainValue[5] ^ Pack.littleEndianToInt(salt, 4);
            }
            this.chainValue[6] = blake2s_IV[6];
            this.chainValue[7] = blake2s_IV[7];
            if (personalization != null) {
                if (personalization.length != 8) {
                    throw new IllegalArgumentException("Personalization length must be exactly 8 bytes");
                }
                this.personalization = new byte[8];
                System.arraycopy(personalization, 0, this.personalization, 0, personalization.length);
                this.chainValue[6] = this.chainValue[6] ^ Pack.littleEndianToInt(personalization, 0);
                this.chainValue[7] = this.chainValue[7] ^ Pack.littleEndianToInt(personalization, 4);
            }
        }
    }

    private void initializeInternalState() {
        System.arraycopy(this.chainValue, 0, this.internalState, 0, this.chainValue.length);
        System.arraycopy(blake2s_IV, 0, this.internalState, this.chainValue.length, 4);
        this.internalState[12] = this.t0 ^ blake2s_IV[4];
        this.internalState[13] = this.t1 ^ blake2s_IV[5];
        this.internalState[14] = this.f0 ^ blake2s_IV[6];
        this.internalState[15] = this.f1 ^ blake2s_IV[7];
    }

    @Override
    public void update(byte b) {
        int remainingLength = 64 - this.bufferPos;
        if (remainingLength == 0) {
            this.t0 += 64;
            if (this.t0 == 0) {
                ++this.t1;
            }
            this.compress(this.buffer, 0);
            Arrays.fill(this.buffer, (byte)0);
            this.buffer[0] = b;
            this.bufferPos = 1;
        } else {
            this.buffer[this.bufferPos] = b;
            ++this.bufferPos;
        }
    }

    @Override
    public void update(byte[] message, int offset, int len) {
        int messagePos;
        if (message == null || len == 0) {
            return;
        }
        int remainingLength = 0;
        if (this.bufferPos != 0) {
            remainingLength = 64 - this.bufferPos;
            if (remainingLength < len) {
                System.arraycopy(message, offset, this.buffer, this.bufferPos, remainingLength);
                this.t0 += 64;
                if (this.t0 == 0) {
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
        int blockWiseLastPos = offset + len - 64;
        for (messagePos = offset + remainingLength; messagePos < blockWiseLastPos; messagePos += 64) {
            this.t0 += 64;
            if (this.t0 == 0) {
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
        this.f0 = -1;
        if (this.isLastNode) {
            this.f1 = -1;
        }
        this.t0 += this.bufferPos;
        if (this.t0 < 0 && this.bufferPos > -this.t0) {
            ++this.t1;
        }
        this.compress(this.buffer, 0);
        Arrays.fill(this.buffer, (byte)0);
        Arrays.fill(this.internalState, 0);
        int full = this.digestLength >>> 2;
        int partial = this.digestLength & 3;
        Pack.intToLittleEndian(this.chainValue, 0, full, out, outOffset);
        if (partial > 0) {
            byte[] bytes = new byte[4];
            Pack.intToLittleEndian(this.chainValue[full], bytes, 0);
            System.arraycopy(bytes, 0, out, outOffset + this.digestLength - partial, partial);
        }
        Arrays.fill(this.chainValue, 0);
        this.reset();
        return this.digestLength;
    }

    @Override
    public void reset() {
        this.bufferPos = 0;
        this.f0 = 0;
        this.f1 = 0;
        this.t0 = 0;
        this.t1 = 0;
        this.isLastNode = false;
        this.chainValue = null;
        Arrays.fill(this.buffer, (byte)0);
        if (this.key != null) {
            System.arraycopy(this.key, 0, this.buffer, 0, this.key.length);
            this.bufferPos = 64;
        }
        this.init(this.salt, this.personalization, this.key);
    }

    private void compress(byte[] message, int messagePos) {
        this.initializeInternalState();
        int[] m = new int[16];
        Pack.littleEndianToInt(message, messagePos, m);
        for (int round = 0; round < 10; ++round) {
            this.G(m[blake2s_sigma[round][0]], m[blake2s_sigma[round][1]], 0, 4, 8, 12);
            this.G(m[blake2s_sigma[round][2]], m[blake2s_sigma[round][3]], 1, 5, 9, 13);
            this.G(m[blake2s_sigma[round][4]], m[blake2s_sigma[round][5]], 2, 6, 10, 14);
            this.G(m[blake2s_sigma[round][6]], m[blake2s_sigma[round][7]], 3, 7, 11, 15);
            this.G(m[blake2s_sigma[round][8]], m[blake2s_sigma[round][9]], 0, 5, 10, 15);
            this.G(m[blake2s_sigma[round][10]], m[blake2s_sigma[round][11]], 1, 6, 11, 12);
            this.G(m[blake2s_sigma[round][12]], m[blake2s_sigma[round][13]], 2, 7, 8, 13);
            this.G(m[blake2s_sigma[round][14]], m[blake2s_sigma[round][15]], 3, 4, 9, 14);
        }
        for (int offset = 0; offset < this.chainValue.length; ++offset) {
            this.chainValue[offset] = this.chainValue[offset] ^ this.internalState[offset] ^ this.internalState[offset + 8];
        }
    }

    private void G(int m1, int m2, int posA, int posB, int posC, int posD) {
        this.internalState[posA] = this.internalState[posA] + this.internalState[posB] + m1;
        this.internalState[posD] = Integers.rotateRight(this.internalState[posD] ^ this.internalState[posA], 16);
        this.internalState[posC] = this.internalState[posC] + this.internalState[posD];
        this.internalState[posB] = Integers.rotateRight(this.internalState[posB] ^ this.internalState[posC], 12);
        this.internalState[posA] = this.internalState[posA] + this.internalState[posB] + m2;
        this.internalState[posD] = Integers.rotateRight(this.internalState[posD] ^ this.internalState[posA], 8);
        this.internalState[posC] = this.internalState[posC] + this.internalState[posD];
        this.internalState[posB] = Integers.rotateRight(this.internalState[posB] ^ this.internalState[posC], 7);
    }

    protected void setAsLastNode() {
        this.isLastNode = true;
    }

    @Override
    public String getAlgorithmName() {
        return "BLAKE2s";
    }

    @Override
    public int getDigestSize() {
        return this.digestLength;
    }

    @Override
    public int getByteLength() {
        return 64;
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

