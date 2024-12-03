/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMSIVModeCipher;
import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.Tables4kGCMMultiplier;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class GCMSIVBlockCipher
implements GCMSIVModeCipher {
    private static final int BUFLEN = 16;
    private static final int HALFBUFLEN = 8;
    private static final int NONCELEN = 12;
    private static final int MAX_DATALEN = 0x7FFFFFE7;
    private static final byte MASK = -128;
    private static final byte ADD = -31;
    private static final int INIT = 1;
    private static final int AEAD_COMPLETE = 2;
    private final BlockCipher theCipher;
    private final GCMMultiplier theMultiplier;
    private final byte[] theGHash = new byte[16];
    private final byte[] theReverse = new byte[16];
    private final GCMSIVHasher theAEADHasher;
    private final GCMSIVHasher theDataHasher;
    private GCMSIVCache thePlain;
    private GCMSIVCache theEncData;
    private boolean forEncryption;
    private byte[] theInitialAEAD;
    private byte[] theNonce;
    private int theFlags;
    private byte[] macBlock = new byte[16];

    public static GCMSIVModeCipher newInstance(BlockCipher cipher) {
        return new GCMSIVBlockCipher(cipher);
    }

    public GCMSIVBlockCipher() {
        this(AESEngine.newInstance());
    }

    public GCMSIVBlockCipher(BlockCipher pCipher) {
        this(pCipher, new Tables4kGCMMultiplier());
    }

    public GCMSIVBlockCipher(BlockCipher pCipher, GCMMultiplier pMultiplier) {
        if (pCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("Cipher required with a block size of 16.");
        }
        this.theCipher = pCipher;
        this.theMultiplier = pMultiplier;
        this.theAEADHasher = new GCMSIVHasher();
        this.theDataHasher = new GCMSIVHasher();
    }

    @Override
    public BlockCipher getUnderlyingCipher() {
        return this.theCipher;
    }

    @Override
    public void init(boolean pEncrypt, CipherParameters cipherParameters) throws IllegalArgumentException {
        byte[] myInitialAEAD = null;
        byte[] myNonce = null;
        KeyParameter myKey = null;
        if (cipherParameters instanceof AEADParameters) {
            AEADParameters myAEAD = (AEADParameters)cipherParameters;
            myInitialAEAD = myAEAD.getAssociatedText();
            myNonce = myAEAD.getNonce();
            myKey = myAEAD.getKey();
        } else if (cipherParameters instanceof ParametersWithIV) {
            ParametersWithIV myParms = (ParametersWithIV)cipherParameters;
            myNonce = myParms.getIV();
            myKey = (KeyParameter)myParms.getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to GCM-SIV");
        }
        if (myNonce == null || myNonce.length != 12) {
            throw new IllegalArgumentException("Invalid nonce");
        }
        if (myKey == null || myKey.getKeyLength() != 16 && myKey.getKeyLength() != 32) {
            throw new IllegalArgumentException("Invalid key");
        }
        this.forEncryption = pEncrypt;
        this.theInitialAEAD = myInitialAEAD;
        this.theNonce = myNonce;
        this.deriveKeys(myKey);
        this.resetStreams();
    }

    @Override
    public String getAlgorithmName() {
        return this.theCipher.getAlgorithmName() + "-GCM-SIV";
    }

    private void checkAEADStatus(int pLen) {
        if ((this.theFlags & 1) == 0) {
            throw new IllegalStateException("Cipher is not initialised");
        }
        if ((this.theFlags & 2) != 0) {
            throw new IllegalStateException("AEAD data cannot be processed after ordinary data");
        }
        if (this.theAEADHasher.getBytesProcessed() + Long.MIN_VALUE > (long)(0x7FFFFFE7 - pLen) + Long.MIN_VALUE) {
            throw new IllegalStateException("AEAD byte count exceeded");
        }
    }

    private void checkStatus(int pLen) {
        if ((this.theFlags & 1) == 0) {
            throw new IllegalStateException("Cipher is not initialised");
        }
        if ((this.theFlags & 2) == 0) {
            this.theAEADHasher.completeHash();
            this.theFlags |= 2;
        }
        long dataLimit = 0x7FFFFFE7L;
        long currBytes = this.thePlain.size();
        if (!this.forEncryption) {
            dataLimit += 16L;
            currBytes = this.theEncData.size();
        }
        if (currBytes + Long.MIN_VALUE > dataLimit - (long)pLen + Long.MIN_VALUE) {
            throw new IllegalStateException("byte count exceeded");
        }
    }

    @Override
    public void processAADByte(byte pByte) {
        this.checkAEADStatus(1);
        this.theAEADHasher.updateHash(pByte);
    }

    @Override
    public void processAADBytes(byte[] pData, int pOffset, int pLen) {
        this.checkAEADStatus(pLen);
        GCMSIVBlockCipher.checkBuffer(pData, pOffset, pLen, false);
        this.theAEADHasher.updateHash(pData, pOffset, pLen);
    }

    @Override
    public int processByte(byte pByte, byte[] pOutput, int pOutOffset) throws DataLengthException {
        this.checkStatus(1);
        if (this.forEncryption) {
            this.thePlain.write(pByte);
            this.theDataHasher.updateHash(pByte);
        } else {
            this.theEncData.write(pByte);
        }
        return 0;
    }

    @Override
    public int processBytes(byte[] pData, int pOffset, int pLen, byte[] pOutput, int pOutOffset) throws DataLengthException {
        this.checkStatus(pLen);
        GCMSIVBlockCipher.checkBuffer(pData, pOffset, pLen, false);
        if (this.forEncryption) {
            this.thePlain.write(pData, pOffset, pLen);
            this.theDataHasher.updateHash(pData, pOffset, pLen);
        } else {
            this.theEncData.write(pData, pOffset, pLen);
        }
        return 0;
    }

    @Override
    public int doFinal(byte[] pOutput, int pOffset) throws IllegalStateException, InvalidCipherTextException {
        this.checkStatus(0);
        GCMSIVBlockCipher.checkBuffer(pOutput, pOffset, this.getOutputSize(0), true);
        if (this.forEncryption) {
            byte[] myTag = this.calculateTag();
            int myDataLen = 16 + this.encryptPlain(myTag, pOutput, pOffset);
            System.arraycopy(myTag, 0, pOutput, pOffset + this.thePlain.size(), 16);
            System.arraycopy(myTag, 0, this.macBlock, 0, this.macBlock.length);
            this.resetStreams();
            return myDataLen;
        }
        this.decryptPlain();
        int myDataLen = this.thePlain.size();
        byte[] mySrc = this.thePlain.getBuffer();
        System.arraycopy(mySrc, 0, pOutput, pOffset, myDataLen);
        this.resetStreams();
        return myDataLen;
    }

    @Override
    public byte[] getMac() {
        return Arrays.clone(this.macBlock);
    }

    @Override
    public int getUpdateOutputSize(int pLen) {
        return 0;
    }

    @Override
    public int getOutputSize(int pLen) {
        if (this.forEncryption) {
            return pLen + this.thePlain.size() + 16;
        }
        int myCurr = pLen + this.theEncData.size();
        return myCurr > 16 ? myCurr - 16 : 0;
    }

    @Override
    public void reset() {
        this.resetStreams();
    }

    private void resetStreams() {
        if (this.thePlain != null) {
            this.thePlain.clearBuffer();
        }
        this.theAEADHasher.reset();
        this.theDataHasher.reset();
        this.thePlain = new GCMSIVCache();
        this.theEncData = this.forEncryption ? null : new GCMSIVCache();
        this.theFlags &= 0xFFFFFFFD;
        Arrays.fill(this.theGHash, (byte)0);
        if (this.theInitialAEAD != null) {
            this.theAEADHasher.updateHash(this.theInitialAEAD, 0, this.theInitialAEAD.length);
        }
    }

    private static int bufLength(byte[] pBuffer) {
        return pBuffer == null ? 0 : pBuffer.length;
    }

    private static void checkBuffer(byte[] pBuffer, int pOffset, int pLen, boolean pOutput) {
        boolean badLen;
        int myBufLen = GCMSIVBlockCipher.bufLength(pBuffer);
        int myLast = pOffset + pLen;
        boolean bl = badLen = pLen < 0 || pOffset < 0 || myLast < 0;
        if (badLen || myLast > myBufLen) {
            throw pOutput ? new OutputLengthException("Output buffer too short.") : new DataLengthException("Input buffer too short.");
        }
    }

    private int encryptPlain(byte[] pCounter, byte[] pTarget, int pOffset) {
        byte[] mySrc = this.thePlain.getBuffer();
        byte[] myCounter = Arrays.clone(pCounter);
        myCounter[15] = (byte)(myCounter[15] | 0xFFFFFF80);
        byte[] myMask = new byte[16];
        int myRemaining = this.thePlain.size();
        int myOff = 0;
        while (myRemaining > 0) {
            this.theCipher.processBlock(myCounter, 0, myMask, 0);
            int myLen = Math.min(16, myRemaining);
            GCMSIVBlockCipher.xorBlock(myMask, mySrc, myOff, myLen);
            System.arraycopy(myMask, 0, pTarget, pOffset + myOff, myLen);
            myRemaining -= myLen;
            myOff += myLen;
            GCMSIVBlockCipher.incrementCounter(myCounter);
        }
        return this.thePlain.size();
    }

    private void decryptPlain() throws InvalidCipherTextException {
        byte[] mySrc = this.theEncData.getBuffer();
        int myRemaining = this.theEncData.size() - 16;
        if (myRemaining < 0) {
            throw new InvalidCipherTextException("Data too short");
        }
        byte[] myExpected = Arrays.copyOfRange(mySrc, myRemaining, myRemaining + 16);
        byte[] myCounter = Arrays.clone(myExpected);
        myCounter[15] = (byte)(myCounter[15] | 0xFFFFFF80);
        byte[] myMask = new byte[16];
        int myOff = 0;
        while (myRemaining > 0) {
            this.theCipher.processBlock(myCounter, 0, myMask, 0);
            int myLen = Math.min(16, myRemaining);
            GCMSIVBlockCipher.xorBlock(myMask, mySrc, myOff, myLen);
            this.thePlain.write(myMask, 0, myLen);
            this.theDataHasher.updateHash(myMask, 0, myLen);
            myRemaining -= myLen;
            myOff += myLen;
            GCMSIVBlockCipher.incrementCounter(myCounter);
        }
        byte[] myTag = this.calculateTag();
        if (!Arrays.constantTimeAreEqual(myTag, myExpected)) {
            this.reset();
            throw new InvalidCipherTextException("mac check failed");
        }
        System.arraycopy(myTag, 0, this.macBlock, 0, this.macBlock.length);
    }

    private byte[] calculateTag() {
        this.theDataHasher.completeHash();
        byte[] myPolyVal = this.completePolyVal();
        byte[] myResult = new byte[16];
        for (int i = 0; i < 12; ++i) {
            int n = i;
            myPolyVal[n] = (byte)(myPolyVal[n] ^ this.theNonce[i]);
        }
        myPolyVal[15] = (byte)(myPolyVal[15] & 0xFFFFFF7F);
        this.theCipher.processBlock(myPolyVal, 0, myResult, 0);
        return myResult;
    }

    private byte[] completePolyVal() {
        byte[] myResult = new byte[16];
        this.gHashLengths();
        GCMSIVBlockCipher.fillReverse(this.theGHash, 0, 16, myResult);
        return myResult;
    }

    private void gHashLengths() {
        byte[] myIn = new byte[16];
        Pack.longToBigEndian(8L * this.theDataHasher.getBytesProcessed(), myIn, 0);
        Pack.longToBigEndian(8L * this.theAEADHasher.getBytesProcessed(), myIn, 8);
        this.gHASH(myIn);
    }

    private void gHASH(byte[] pNext) {
        GCMSIVBlockCipher.xorBlock(this.theGHash, pNext);
        this.theMultiplier.multiplyH(this.theGHash);
    }

    private static void fillReverse(byte[] pInput, int pOffset, int pLength, byte[] pOutput) {
        int i = 0;
        int j = 15;
        while (i < pLength) {
            pOutput[j] = pInput[pOffset + i];
            ++i;
            --j;
        }
    }

    private static void xorBlock(byte[] pLeft, byte[] pRight) {
        for (int i = 0; i < 16; ++i) {
            int n = i;
            pLeft[n] = (byte)(pLeft[n] ^ pRight[i]);
        }
    }

    private static void xorBlock(byte[] pLeft, byte[] pRight, int pOffset, int pLength) {
        for (int i = 0; i < pLength; ++i) {
            int n = i;
            pLeft[n] = (byte)(pLeft[n] ^ pRight[i + pOffset]);
        }
    }

    private static void incrementCounter(byte[] pCounter) {
        int i = 0;
        while (i < 4) {
            int n = i++;
            pCounter[n] = (byte)(pCounter[n] + 1);
            if (pCounter[n] != 0) break;
        }
    }

    private static void mulX(byte[] pValue) {
        int myMask = 0;
        for (int i = 0; i < 16; ++i) {
            byte myValue = pValue[i];
            pValue[i] = (byte)(myValue >> 1 & 0x7F | myMask);
            myMask = (myValue & 1) == 0 ? 0 : -128;
        }
        if (myMask != 0) {
            pValue[0] = (byte)(pValue[0] ^ 0xFFFFFFE1);
        }
    }

    private void deriveKeys(KeyParameter pKey) {
        byte[] myIn = new byte[16];
        byte[] myOut = new byte[16];
        byte[] myResult = new byte[16];
        byte[] myEncKey = new byte[pKey.getKeyLength()];
        System.arraycopy(this.theNonce, 0, myIn, 4, 12);
        this.theCipher.init(true, pKey);
        int myOff = 0;
        this.theCipher.processBlock(myIn, 0, myOut, 0);
        System.arraycopy(myOut, 0, myResult, myOff, 8);
        myIn[0] = (byte)(myIn[0] + 1);
        this.theCipher.processBlock(myIn, 0, myOut, 0);
        System.arraycopy(myOut, 0, myResult, myOff += 8, 8);
        myIn[0] = (byte)(myIn[0] + 1);
        myOff = 0;
        this.theCipher.processBlock(myIn, 0, myOut, 0);
        System.arraycopy(myOut, 0, myEncKey, myOff, 8);
        myIn[0] = (byte)(myIn[0] + 1);
        this.theCipher.processBlock(myIn, 0, myOut, 0);
        System.arraycopy(myOut, 0, myEncKey, myOff += 8, 8);
        if (myEncKey.length == 32) {
            myIn[0] = (byte)(myIn[0] + 1);
            this.theCipher.processBlock(myIn, 0, myOut, 0);
            System.arraycopy(myOut, 0, myEncKey, myOff += 8, 8);
            myIn[0] = (byte)(myIn[0] + 1);
            this.theCipher.processBlock(myIn, 0, myOut, 0);
            System.arraycopy(myOut, 0, myEncKey, myOff += 8, 8);
        }
        this.theCipher.init(true, new KeyParameter(myEncKey));
        GCMSIVBlockCipher.fillReverse(myResult, 0, 16, myOut);
        GCMSIVBlockCipher.mulX(myOut);
        this.theMultiplier.init(myOut);
        this.theFlags |= 1;
    }

    private static class GCMSIVCache
    extends ByteArrayOutputStream {
        GCMSIVCache() {
        }

        byte[] getBuffer() {
            return this.buf;
        }

        void clearBuffer() {
            Arrays.fill(this.getBuffer(), (byte)0);
        }
    }

    private class GCMSIVHasher {
        private final byte[] theBuffer = new byte[16];
        private final byte[] theByte = new byte[1];
        private int numActive;
        private long numHashed;

        private GCMSIVHasher() {
        }

        long getBytesProcessed() {
            return this.numHashed;
        }

        void reset() {
            this.numActive = 0;
            this.numHashed = 0L;
        }

        void updateHash(byte pByte) {
            this.theByte[0] = pByte;
            this.updateHash(this.theByte, 0, 1);
        }

        void updateHash(byte[] pBuffer, int pOffset, int pLen) {
            int mySpace = 16 - this.numActive;
            int numProcessed = 0;
            int myRemaining = pLen;
            if (this.numActive > 0 && pLen >= mySpace) {
                System.arraycopy(pBuffer, pOffset, this.theBuffer, this.numActive, mySpace);
                GCMSIVBlockCipher.fillReverse(this.theBuffer, 0, 16, GCMSIVBlockCipher.this.theReverse);
                GCMSIVBlockCipher.this.gHASH(GCMSIVBlockCipher.this.theReverse);
                numProcessed += mySpace;
                myRemaining -= mySpace;
                this.numActive = 0;
            }
            while (myRemaining >= 16) {
                GCMSIVBlockCipher.fillReverse(pBuffer, pOffset + numProcessed, 16, GCMSIVBlockCipher.this.theReverse);
                GCMSIVBlockCipher.this.gHASH(GCMSIVBlockCipher.this.theReverse);
                numProcessed += 16;
                myRemaining -= 16;
            }
            if (myRemaining > 0) {
                System.arraycopy(pBuffer, pOffset + numProcessed, this.theBuffer, this.numActive, myRemaining);
                this.numActive += myRemaining;
            }
            this.numHashed += (long)pLen;
        }

        void completeHash() {
            if (this.numActive > 0) {
                Arrays.fill(GCMSIVBlockCipher.this.theReverse, (byte)0);
                GCMSIVBlockCipher.fillReverse(this.theBuffer, 0, this.numActive, GCMSIVBlockCipher.this.theReverse);
                GCMSIVBlockCipher.this.gHASH(GCMSIVBlockCipher.this.theReverse);
            }
        }
    }
}

