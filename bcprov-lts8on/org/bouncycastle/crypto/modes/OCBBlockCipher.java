/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import java.util.Vector;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.modes.OCBModeCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Longs;

public class OCBBlockCipher
implements OCBModeCipher {
    private static final int BLOCK_SIZE = 16;
    private BlockCipher hashCipher;
    private BlockCipher mainCipher;
    private boolean forEncryption;
    private int macSize;
    private byte[] initialAssociatedText;
    private Vector L;
    private byte[] L_Asterisk;
    private byte[] L_Dollar;
    private byte[] KtopInput = null;
    private byte[] Stretch = new byte[24];
    private byte[] OffsetMAIN_0 = new byte[16];
    private byte[] hashBlock;
    private byte[] mainBlock;
    private int hashBlockPos;
    private int mainBlockPos;
    private long hashBlockCount;
    private long mainBlockCount;
    private byte[] OffsetHASH;
    private byte[] Sum;
    private byte[] OffsetMAIN = new byte[16];
    private byte[] Checksum;
    private byte[] macBlock;

    public OCBBlockCipher(BlockCipher hashCipher, BlockCipher mainCipher) {
        if (hashCipher == null) {
            throw new IllegalArgumentException("'hashCipher' cannot be null");
        }
        if (hashCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("'hashCipher' must have a block size of 16");
        }
        if (mainCipher == null) {
            throw new IllegalArgumentException("'mainCipher' cannot be null");
        }
        if (mainCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("'mainCipher' must have a block size of 16");
        }
        if (!hashCipher.getAlgorithmName().equals(mainCipher.getAlgorithmName())) {
            throw new IllegalArgumentException("'hashCipher' and 'mainCipher' must be the same algorithm");
        }
        this.hashCipher = hashCipher;
        this.mainCipher = mainCipher;
    }

    @Override
    public BlockCipher getUnderlyingCipher() {
        return this.mainCipher;
    }

    @Override
    public String getAlgorithmName() {
        return this.mainCipher.getAlgorithmName() + "/OCB";
    }

    @Override
    public void init(boolean forEncryption, CipherParameters parameters) throws IllegalArgumentException {
        KeyParameter keyParameter;
        byte[] N;
        boolean oldForEncryption = this.forEncryption;
        this.forEncryption = forEncryption;
        this.macBlock = null;
        if (parameters instanceof AEADParameters) {
            AEADParameters aeadParameters = (AEADParameters)parameters;
            N = aeadParameters.getNonce();
            this.initialAssociatedText = aeadParameters.getAssociatedText();
            int macSizeBits = aeadParameters.getMacSize();
            if (macSizeBits < 64 || macSizeBits > 128 || macSizeBits % 8 != 0) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + macSizeBits);
            }
            this.macSize = macSizeBits / 8;
            keyParameter = aeadParameters.getKey();
        } else if (parameters instanceof ParametersWithIV) {
            ParametersWithIV parametersWithIV = (ParametersWithIV)parameters;
            N = parametersWithIV.getIV();
            this.initialAssociatedText = null;
            this.macSize = 16;
            keyParameter = (KeyParameter)parametersWithIV.getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to OCB");
        }
        this.hashBlock = new byte[16];
        this.mainBlock = new byte[forEncryption ? 16 : 16 + this.macSize];
        if (N == null) {
            N = new byte[]{};
        }
        if (N.length > 15) {
            throw new IllegalArgumentException("IV must be no more than 15 bytes");
        }
        if (keyParameter != null) {
            this.hashCipher.init(true, keyParameter);
            this.mainCipher.init(forEncryption, keyParameter);
            this.KtopInput = null;
        } else if (oldForEncryption != forEncryption) {
            throw new IllegalArgumentException("cannot change encrypting state without providing key.");
        }
        this.L_Asterisk = new byte[16];
        this.hashCipher.processBlock(this.L_Asterisk, 0, this.L_Asterisk, 0);
        this.L_Dollar = OCBBlockCipher.OCB_double(this.L_Asterisk);
        this.L = new Vector();
        this.L.addElement(OCBBlockCipher.OCB_double(this.L_Dollar));
        int bottom = this.processNonce(N);
        int bits = bottom % 8;
        int bytes = bottom / 8;
        if (bits == 0) {
            System.arraycopy(this.Stretch, bytes, this.OffsetMAIN_0, 0, 16);
        } else {
            for (int i = 0; i < 16; ++i) {
                int b1 = this.Stretch[bytes] & 0xFF;
                int b2 = this.Stretch[++bytes] & 0xFF;
                this.OffsetMAIN_0[i] = (byte)(b1 << bits | b2 >>> 8 - bits);
            }
        }
        this.hashBlockPos = 0;
        this.mainBlockPos = 0;
        this.hashBlockCount = 0L;
        this.mainBlockCount = 0L;
        this.OffsetHASH = new byte[16];
        this.Sum = new byte[16];
        System.arraycopy(this.OffsetMAIN_0, 0, this.OffsetMAIN, 0, 16);
        this.Checksum = new byte[16];
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    protected int processNonce(byte[] N) {
        byte[] nonce = new byte[16];
        System.arraycopy(N, 0, nonce, nonce.length - N.length, N.length);
        nonce[0] = (byte)(this.macSize << 4);
        int n = 15 - N.length;
        nonce[n] = (byte)(nonce[n] | 1);
        int bottom = nonce[15] & 0x3F;
        nonce[15] = (byte)(nonce[15] & 0xC0);
        if (this.KtopInput == null || !Arrays.areEqual(nonce, this.KtopInput)) {
            byte[] Ktop = new byte[16];
            this.KtopInput = nonce;
            this.hashCipher.processBlock(this.KtopInput, 0, Ktop, 0);
            System.arraycopy(Ktop, 0, this.Stretch, 0, 16);
            for (int i = 0; i < 8; ++i) {
                this.Stretch[16 + i] = (byte)(Ktop[i] ^ Ktop[i + 1]);
            }
        }
        return bottom;
    }

    @Override
    public byte[] getMac() {
        if (this.macBlock == null) {
            return new byte[this.macSize];
        }
        return Arrays.clone(this.macBlock);
    }

    @Override
    public int getOutputSize(int len) {
        int totalData = len + this.mainBlockPos;
        if (this.forEncryption) {
            return totalData + this.macSize;
        }
        return totalData < this.macSize ? 0 : totalData - this.macSize;
    }

    @Override
    public int getUpdateOutputSize(int len) {
        int totalData = len + this.mainBlockPos;
        if (!this.forEncryption) {
            if (totalData < this.macSize) {
                return 0;
            }
            totalData -= this.macSize;
        }
        return totalData - totalData % 16;
    }

    @Override
    public void processAADByte(byte input) {
        this.hashBlock[this.hashBlockPos] = input;
        if (++this.hashBlockPos == this.hashBlock.length) {
            this.processHashBlock();
        }
    }

    @Override
    public void processAADBytes(byte[] input, int off, int len) {
        for (int i = 0; i < len; ++i) {
            this.hashBlock[this.hashBlockPos] = input[off + i];
            if (++this.hashBlockPos != this.hashBlock.length) continue;
            this.processHashBlock();
        }
    }

    @Override
    public int processByte(byte input, byte[] output, int outOff) throws DataLengthException {
        this.mainBlock[this.mainBlockPos] = input;
        if (++this.mainBlockPos == this.mainBlock.length) {
            this.processMainBlock(output, outOff);
            return 16;
        }
        return 0;
    }

    @Override
    public int processBytes(byte[] input, int inOff, int len, byte[] output, int outOff) throws DataLengthException {
        if (input.length < inOff + len) {
            throw new DataLengthException("Input buffer too short");
        }
        int resultLen = 0;
        for (int i = 0; i < len; ++i) {
            this.mainBlock[this.mainBlockPos] = input[inOff + i];
            if (++this.mainBlockPos != this.mainBlock.length) continue;
            this.processMainBlock(output, outOff + resultLen);
            resultLen += 16;
        }
        return resultLen;
    }

    @Override
    public int doFinal(byte[] output, int outOff) throws IllegalStateException, InvalidCipherTextException {
        byte[] tag = null;
        if (!this.forEncryption) {
            if (this.mainBlockPos < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            this.mainBlockPos -= this.macSize;
            tag = new byte[this.macSize];
            System.arraycopy(this.mainBlock, this.mainBlockPos, tag, 0, this.macSize);
        }
        if (this.hashBlockPos > 0) {
            OCBBlockCipher.OCB_extend(this.hashBlock, this.hashBlockPos);
            this.updateHASH(this.L_Asterisk);
        }
        if (this.mainBlockPos > 0) {
            if (this.forEncryption) {
                OCBBlockCipher.OCB_extend(this.mainBlock, this.mainBlockPos);
                OCBBlockCipher.xor(this.Checksum, this.mainBlock);
            }
            OCBBlockCipher.xor(this.OffsetMAIN, this.L_Asterisk);
            byte[] Pad = new byte[16];
            this.hashCipher.processBlock(this.OffsetMAIN, 0, Pad, 0);
            OCBBlockCipher.xor(this.mainBlock, Pad);
            if (output.length < outOff + this.mainBlockPos) {
                throw new OutputLengthException("Output buffer too short");
            }
            System.arraycopy(this.mainBlock, 0, output, outOff, this.mainBlockPos);
            if (!this.forEncryption) {
                OCBBlockCipher.OCB_extend(this.mainBlock, this.mainBlockPos);
                OCBBlockCipher.xor(this.Checksum, this.mainBlock);
            }
        }
        OCBBlockCipher.xor(this.Checksum, this.OffsetMAIN);
        OCBBlockCipher.xor(this.Checksum, this.L_Dollar);
        this.hashCipher.processBlock(this.Checksum, 0, this.Checksum, 0);
        OCBBlockCipher.xor(this.Checksum, this.Sum);
        this.macBlock = new byte[this.macSize];
        System.arraycopy(this.Checksum, 0, this.macBlock, 0, this.macSize);
        int resultLen = this.mainBlockPos;
        if (this.forEncryption) {
            if (output.length < outOff + resultLen + this.macSize) {
                throw new OutputLengthException("Output buffer too short");
            }
            System.arraycopy(this.macBlock, 0, output, outOff + resultLen, this.macSize);
            resultLen += this.macSize;
        } else if (!Arrays.constantTimeAreEqual(this.macBlock, tag)) {
            throw new InvalidCipherTextException("mac check in OCB failed");
        }
        this.reset(false);
        return resultLen;
    }

    @Override
    public void reset() {
        this.reset(true);
    }

    protected void clear(byte[] bs) {
        if (bs != null) {
            Arrays.fill(bs, (byte)0);
        }
    }

    protected byte[] getLSub(int n) {
        while (n >= this.L.size()) {
            this.L.addElement(OCBBlockCipher.OCB_double((byte[])this.L.lastElement()));
        }
        return (byte[])this.L.elementAt(n);
    }

    protected void processHashBlock() {
        this.updateHASH(this.getLSub(OCBBlockCipher.OCB_ntz(++this.hashBlockCount)));
        this.hashBlockPos = 0;
    }

    protected void processMainBlock(byte[] output, int outOff) {
        if (output.length < outOff + 16) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.forEncryption) {
            OCBBlockCipher.xor(this.Checksum, this.mainBlock);
            this.mainBlockPos = 0;
        }
        OCBBlockCipher.xor(this.OffsetMAIN, this.getLSub(OCBBlockCipher.OCB_ntz(++this.mainBlockCount)));
        OCBBlockCipher.xor(this.mainBlock, this.OffsetMAIN);
        this.mainCipher.processBlock(this.mainBlock, 0, this.mainBlock, 0);
        OCBBlockCipher.xor(this.mainBlock, this.OffsetMAIN);
        System.arraycopy(this.mainBlock, 0, output, outOff, 16);
        if (!this.forEncryption) {
            OCBBlockCipher.xor(this.Checksum, this.mainBlock);
            System.arraycopy(this.mainBlock, 16, this.mainBlock, 0, this.macSize);
            this.mainBlockPos = this.macSize;
        }
    }

    protected void reset(boolean clearMac) {
        this.hashCipher.reset();
        this.mainCipher.reset();
        this.clear(this.hashBlock);
        this.clear(this.mainBlock);
        this.hashBlockPos = 0;
        this.mainBlockPos = 0;
        this.hashBlockCount = 0L;
        this.mainBlockCount = 0L;
        this.clear(this.OffsetHASH);
        this.clear(this.Sum);
        System.arraycopy(this.OffsetMAIN_0, 0, this.OffsetMAIN, 0, 16);
        this.clear(this.Checksum);
        if (clearMac) {
            this.macBlock = null;
        }
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    protected void updateHASH(byte[] LSub) {
        OCBBlockCipher.xor(this.OffsetHASH, LSub);
        OCBBlockCipher.xor(this.hashBlock, this.OffsetHASH);
        this.hashCipher.processBlock(this.hashBlock, 0, this.hashBlock, 0);
        OCBBlockCipher.xor(this.Sum, this.hashBlock);
    }

    protected static byte[] OCB_double(byte[] block) {
        byte[] result = new byte[16];
        int carry = OCBBlockCipher.shiftLeft(block, result);
        result[15] = (byte)(result[15] ^ 135 >>> (1 - carry << 3));
        return result;
    }

    protected static void OCB_extend(byte[] block, int pos) {
        block[pos] = -128;
        while (++pos < 16) {
            block[pos] = 0;
        }
    }

    protected static int OCB_ntz(long x) {
        return Longs.numberOfTrailingZeros(x);
    }

    protected static int shiftLeft(byte[] block, byte[] output) {
        int i = 16;
        int bit = 0;
        while (--i >= 0) {
            int b = block[i] & 0xFF;
            output[i] = (byte)(b << 1 | bit);
            bit = b >>> 7 & 1;
        }
        return bit;
    }

    protected static void xor(byte[] block, byte[] val) {
        for (int i = 15; i >= 0; --i) {
            int n = i;
            block[n] = (byte)(block[n] ^ val[i]);
        }
    }
}

