/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import java.util.Iterator;
import java.util.Stack;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.crypto.params.Blake3Parameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class Blake3Digest
implements ExtendedDigest,
Memoable,
Xof {
    private static final String ERR_OUTPUTTING = "Already outputting";
    private static final int NUMWORDS = 8;
    private static final int ROUNDS = 7;
    private static final int BLOCKLEN = 64;
    private static final int CHUNKLEN = 1024;
    private static final int CHUNKSTART = 1;
    private static final int CHUNKEND = 2;
    private static final int PARENT = 4;
    private static final int ROOT = 8;
    private static final int KEYEDHASH = 16;
    private static final int DERIVECONTEXT = 32;
    private static final int DERIVEKEY = 64;
    private static final int CHAINING0 = 0;
    private static final int CHAINING1 = 1;
    private static final int CHAINING2 = 2;
    private static final int CHAINING3 = 3;
    private static final int CHAINING4 = 4;
    private static final int CHAINING5 = 5;
    private static final int CHAINING6 = 6;
    private static final int CHAINING7 = 7;
    private static final int IV0 = 8;
    private static final int IV1 = 9;
    private static final int IV2 = 10;
    private static final int IV3 = 11;
    private static final int COUNT0 = 12;
    private static final int COUNT1 = 13;
    private static final int DATALEN = 14;
    private static final int FLAGS = 15;
    private static final byte[] SIGMA = new byte[]{2, 6, 3, 10, 7, 0, 4, 13, 1, 11, 12, 5, 9, 14, 15, 8};
    private static final int[] IV = new int[]{1779033703, -1150833019, 1013904242, -1521486534, 1359893119, -1694144372, 528734635, 1541459225};
    private final byte[] theBuffer = new byte[64];
    private final int[] theK = new int[8];
    private final int[] theChaining = new int[8];
    private final int[] theV = new int[16];
    private final int[] theM = new int[16];
    private final byte[] theIndices = new byte[16];
    private final Stack theStack = new Stack();
    private final int theDigestLen;
    private boolean outputting;
    private long outputAvailable;
    private int theMode;
    private int theOutputMode;
    private int theOutputDataLen;
    private long theCounter;
    private int theCurrBytes;
    private int thePos;
    private final CryptoServicePurpose purpose;

    public Blake3Digest() {
        this(256);
    }

    public Blake3Digest(int pDigestSize) {
        this(pDigestSize > 100 ? pDigestSize : pDigestSize * 8, CryptoServicePurpose.ANY);
    }

    public Blake3Digest(int pDigestSize, CryptoServicePurpose purpose) {
        this.purpose = purpose;
        this.theDigestLen = pDigestSize / 8;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.getDigestSize() * 8, purpose));
        this.init(null);
    }

    public Blake3Digest(Blake3Digest pSource) {
        this.theDigestLen = pSource.theDigestLen;
        this.purpose = pSource.purpose;
        this.reset(pSource);
    }

    @Override
    public int getByteLength() {
        return 64;
    }

    @Override
    public String getAlgorithmName() {
        return "BLAKE3";
    }

    @Override
    public int getDigestSize() {
        return this.theDigestLen;
    }

    public void init(Blake3Parameters pParams) {
        byte[] myKey = pParams == null ? null : pParams.getKey();
        byte[] myContext = pParams == null ? null : pParams.getContext();
        this.reset();
        if (myKey != null) {
            this.initKey(myKey);
            Arrays.fill(myKey, (byte)0);
        } else if (myContext != null) {
            this.initNullKey();
            this.theMode = 32;
            this.update(myContext, 0, myContext.length);
            this.doFinal(this.theBuffer, 0);
            this.initKeyFromContext();
            this.reset();
        } else {
            this.initNullKey();
            this.theMode = 0;
        }
    }

    @Override
    public void update(byte b) {
        if (this.outputting) {
            throw new IllegalStateException(ERR_OUTPUTTING);
        }
        int blockLen = this.theBuffer.length;
        int remainingLength = blockLen - this.thePos;
        if (remainingLength == 0) {
            this.compressBlock(this.theBuffer, 0);
            Arrays.fill(this.theBuffer, (byte)0);
            this.thePos = 0;
        }
        this.theBuffer[this.thePos] = b;
        ++this.thePos;
    }

    @Override
    public void update(byte[] pMessage, int pOffset, int pLen) {
        int messagePos;
        if (pMessage == null || pLen == 0) {
            return;
        }
        if (this.outputting) {
            throw new IllegalStateException(ERR_OUTPUTTING);
        }
        int remainingLen = 0;
        if (this.thePos != 0) {
            remainingLen = 64 - this.thePos;
            if (remainingLen >= pLen) {
                System.arraycopy(pMessage, pOffset, this.theBuffer, this.thePos, pLen);
                this.thePos += pLen;
                return;
            }
            System.arraycopy(pMessage, pOffset, this.theBuffer, this.thePos, remainingLen);
            this.compressBlock(this.theBuffer, 0);
            this.thePos = 0;
            Arrays.fill(this.theBuffer, (byte)0);
        }
        int blockWiseLastPos = pOffset + pLen - 64;
        for (messagePos = pOffset + remainingLen; messagePos < blockWiseLastPos; messagePos += 64) {
            this.compressBlock(pMessage, messagePos);
        }
        int len = pLen - messagePos;
        System.arraycopy(pMessage, messagePos, this.theBuffer, 0, pOffset + len);
        this.thePos += pOffset + len;
    }

    @Override
    public int doFinal(byte[] pOutput, int pOutOffset) {
        return this.doFinal(pOutput, pOutOffset, this.getDigestSize());
    }

    @Override
    public int doFinal(byte[] pOut, int pOutOffset, int pOutLen) {
        int length = this.doOutput(pOut, pOutOffset, pOutLen);
        this.reset();
        return length;
    }

    @Override
    public int doOutput(byte[] pOut, int pOutOffset, int pOutLen) {
        int dataToCopy;
        if (pOutOffset > pOut.length - pOutLen) {
            throw new OutputLengthException("output buffer too short");
        }
        if (!this.outputting) {
            this.compressFinalBlock(this.thePos);
        }
        if (pOutLen < 0 || this.outputAvailable >= 0L && (long)pOutLen > this.outputAvailable) {
            throw new IllegalArgumentException("Insufficient bytes remaining");
        }
        int dataLeft = pOutLen;
        int outPos = pOutOffset;
        if (this.thePos < 64) {
            dataToCopy = Math.min(dataLeft, 64 - this.thePos);
            System.arraycopy(this.theBuffer, this.thePos, pOut, outPos, dataToCopy);
            this.thePos += dataToCopy;
            outPos += dataToCopy;
            dataLeft -= dataToCopy;
        }
        while (dataLeft > 0) {
            this.nextOutputBlock();
            dataToCopy = Math.min(dataLeft, 64);
            System.arraycopy(this.theBuffer, 0, pOut, outPos, dataToCopy);
            this.thePos += dataToCopy;
            outPos += dataToCopy;
            dataLeft -= dataToCopy;
        }
        this.outputAvailable -= (long)pOutLen;
        return pOutLen;
    }

    @Override
    public void reset() {
        this.resetBlockCount();
        this.thePos = 0;
        this.outputting = false;
        Arrays.fill(this.theBuffer, (byte)0);
    }

    @Override
    public void reset(Memoable pSource) {
        Blake3Digest mySource = (Blake3Digest)pSource;
        this.theCounter = mySource.theCounter;
        this.theCurrBytes = mySource.theCurrBytes;
        this.theMode = mySource.theMode;
        this.outputting = mySource.outputting;
        this.outputAvailable = mySource.outputAvailable;
        this.theOutputMode = mySource.theOutputMode;
        this.theOutputDataLen = mySource.theOutputDataLen;
        System.arraycopy(mySource.theChaining, 0, this.theChaining, 0, this.theChaining.length);
        System.arraycopy(mySource.theK, 0, this.theK, 0, this.theK.length);
        System.arraycopy(mySource.theM, 0, this.theM, 0, this.theM.length);
        this.theStack.clear();
        Iterator it = mySource.theStack.iterator();
        while (it.hasNext()) {
            this.theStack.push(Arrays.clone((int[])it.next()));
        }
        System.arraycopy(mySource.theBuffer, 0, this.theBuffer, 0, this.theBuffer.length);
        this.thePos = mySource.thePos;
    }

    @Override
    public Memoable copy() {
        return new Blake3Digest(this);
    }

    private void compressBlock(byte[] pMessage, int pMsgPos) {
        this.initChunkBlock(64, false);
        this.initM(pMessage, pMsgPos);
        this.compress();
        if (this.theCurrBytes == 0) {
            this.adjustStack();
        }
    }

    private void adjustStack() {
        for (long myCount = this.theCounter; myCount > 0L && (myCount & 1L) != 1L; myCount >>= 1) {
            int[] myLeft = (int[])this.theStack.pop();
            System.arraycopy(myLeft, 0, this.theM, 0, 8);
            System.arraycopy(this.theChaining, 0, this.theM, 8, 8);
            this.initParentBlock();
            this.compress();
        }
        this.theStack.push(Arrays.copyOf(this.theChaining, 8));
    }

    private void compressFinalBlock(int pDataLen) {
        this.initChunkBlock(pDataLen, true);
        this.initM(this.theBuffer, 0);
        this.compress();
        this.processStack();
    }

    private void processStack() {
        while (!this.theStack.isEmpty()) {
            int[] myLeft = (int[])this.theStack.pop();
            System.arraycopy(myLeft, 0, this.theM, 0, 8);
            System.arraycopy(this.theChaining, 0, this.theM, 8, 8);
            this.initParentBlock();
            if (this.theStack.isEmpty()) {
                this.setRoot();
            }
            this.compress();
        }
    }

    private void compress() {
        this.initIndices();
        for (int round = 0; round < 6; ++round) {
            this.performRound();
            this.permuteIndices();
        }
        this.performRound();
        this.adjustChaining();
    }

    private void performRound() {
        this.mixG(0, 0, 4, 8, 12);
        this.mixG(1, 1, 5, 9, 13);
        this.mixG(2, 2, 6, 10, 14);
        this.mixG(3, 3, 7, 11, 15);
        this.mixG(4, 0, 5, 10, 15);
        this.mixG(5, 1, 6, 11, 12);
        this.mixG(6, 2, 7, 8, 13);
        this.mixG(7, 3, 4, 9, 14);
    }

    private void initM(byte[] pMessage, int pMsgPos) {
        Pack.littleEndianToInt(pMessage, pMsgPos, this.theM);
    }

    private void adjustChaining() {
        if (this.outputting) {
            for (int i = 0; i < 8; ++i) {
                int n = i;
                this.theV[n] = this.theV[n] ^ this.theV[i + 8];
                int n2 = i + 8;
                this.theV[n2] = this.theV[n2] ^ this.theChaining[i];
            }
            Pack.intToLittleEndian(this.theV, this.theBuffer, 0);
            this.thePos = 0;
        } else {
            for (int i = 0; i < 8; ++i) {
                this.theChaining[i] = this.theV[i] ^ this.theV[i + 8];
            }
        }
    }

    private void mixG(int msgIdx, int posA, int posB, int posC, int posD) {
        int msg = msgIdx << 1;
        int n = posA;
        this.theV[n] = this.theV[n] + (this.theV[posB] + this.theM[this.theIndices[msg++]]);
        this.theV[posD] = Integers.rotateRight(this.theV[posD] ^ this.theV[posA], 16);
        int n2 = posC;
        this.theV[n2] = this.theV[n2] + this.theV[posD];
        this.theV[posB] = Integers.rotateRight(this.theV[posB] ^ this.theV[posC], 12);
        int n3 = posA;
        this.theV[n3] = this.theV[n3] + (this.theV[posB] + this.theM[this.theIndices[msg]]);
        this.theV[posD] = Integers.rotateRight(this.theV[posD] ^ this.theV[posA], 8);
        int n4 = posC;
        this.theV[n4] = this.theV[n4] + this.theV[posD];
        this.theV[posB] = Integers.rotateRight(this.theV[posB] ^ this.theV[posC], 7);
    }

    private void initIndices() {
        for (int i = 0; i < this.theIndices.length; i = (int)((byte)(i + 1))) {
            this.theIndices[i] = i;
        }
    }

    private void permuteIndices() {
        for (int i = 0; i < this.theIndices.length; i = (int)((byte)(i + 1))) {
            this.theIndices[i] = SIGMA[this.theIndices[i]];
        }
    }

    private void initNullKey() {
        System.arraycopy(IV, 0, this.theK, 0, 8);
    }

    private void initKey(byte[] pKey) {
        Pack.littleEndianToInt(pKey, 0, this.theK);
        this.theMode = 16;
    }

    private void initKeyFromContext() {
        System.arraycopy(this.theV, 0, this.theK, 0, 8);
        this.theMode = 64;
    }

    private void initChunkBlock(int pDataLen, boolean pFinal) {
        System.arraycopy(this.theCurrBytes == 0 ? this.theK : this.theChaining, 0, this.theV, 0, 8);
        System.arraycopy(IV, 0, this.theV, 8, 4);
        this.theV[12] = (int)this.theCounter;
        this.theV[13] = (int)(this.theCounter >> 32);
        this.theV[14] = pDataLen;
        this.theV[15] = this.theMode + (this.theCurrBytes == 0 ? 1 : 0) + (pFinal ? 2 : 0);
        this.theCurrBytes += pDataLen;
        if (this.theCurrBytes >= 1024) {
            this.incrementBlockCount();
            this.theV[15] = this.theV[15] | 2;
        }
        if (pFinal && this.theStack.isEmpty()) {
            this.setRoot();
        }
    }

    private void initParentBlock() {
        System.arraycopy(this.theK, 0, this.theV, 0, 8);
        System.arraycopy(IV, 0, this.theV, 8, 4);
        this.theV[12] = 0;
        this.theV[13] = 0;
        this.theV[14] = 64;
        this.theV[15] = this.theMode | 4;
    }

    private void nextOutputBlock() {
        ++this.theCounter;
        System.arraycopy(this.theChaining, 0, this.theV, 0, 8);
        System.arraycopy(IV, 0, this.theV, 8, 4);
        this.theV[12] = (int)this.theCounter;
        this.theV[13] = (int)(this.theCounter >> 32);
        this.theV[14] = this.theOutputDataLen;
        this.theV[15] = this.theOutputMode;
        this.compress();
    }

    private void incrementBlockCount() {
        ++this.theCounter;
        this.theCurrBytes = 0;
    }

    private void resetBlockCount() {
        this.theCounter = 0L;
        this.theCurrBytes = 0;
    }

    private void setRoot() {
        this.theV[15] = this.theV[15] | 8;
        this.theOutputMode = this.theV[15];
        this.theOutputDataLen = this.theV[14];
        this.theCounter = 0L;
        this.outputting = true;
        this.outputAvailable = -1L;
        System.arraycopy(this.theV, 0, this.theChaining, 0, 8);
    }
}

