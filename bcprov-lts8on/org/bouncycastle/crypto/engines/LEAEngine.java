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

public class LEAEngine
implements BlockCipher {
    private static final int BASEROUNDS = 16;
    private static final int NUMWORDS = 4;
    private static final int NUMWORDS128 = 4;
    private static final int MASK128 = 3;
    private static final int NUMWORDS192 = 6;
    private static final int NUMWORDS256 = 8;
    private static final int MASK256 = 7;
    private static final int BLOCKSIZE = 16;
    private static final int KEY0 = 0;
    private static final int KEY1 = 1;
    private static final int KEY2 = 2;
    private static final int KEY3 = 3;
    private static final int KEY4 = 4;
    private static final int KEY5 = 5;
    private static final int ROT1 = 1;
    private static final int ROT3 = 3;
    private static final int ROT5 = 5;
    private static final int ROT6 = 6;
    private static final int ROT9 = 9;
    private static final int ROT11 = 11;
    private static final int ROT13 = 13;
    private static final int ROT17 = 17;
    private static final int[] DELTA = new int[]{-1007687205, 1147300610, 2044886154, 2027892972, 1902027934, -947529206, -531697110, -440137385};
    private final int[] theBlock = new int[4];
    private int theRounds;
    private int[][] theRoundKeys;
    private boolean forEncryption;

    @Override
    public void init(boolean pEncrypt, CipherParameters pParams) {
        if (!(pParams instanceof KeyParameter)) {
            throw new IllegalArgumentException("Invalid parameter passed to LEA init - " + pParams.getClass().getName());
        }
        byte[] myKey = ((KeyParameter)pParams).getKey();
        int myKeyLen = myKey.length;
        if ((myKeyLen << 1) % 16 != 0 || myKeyLen < 16 || myKeyLen > 32) {
            throw new IllegalArgumentException("KeyBitSize must be 128, 192 or 256");
        }
        this.forEncryption = pEncrypt;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), myKeyLen * 8, pParams, Utils.getPurpose(this.forEncryption)));
        this.generateRoundKeys(myKey);
    }

    @Override
    public void reset() {
    }

    @Override
    public String getAlgorithmName() {
        return "LEA";
    }

    @Override
    public int getBlockSize() {
        return 16;
    }

    @Override
    public int processBlock(byte[] pInput, int pInOff, byte[] pOutput, int pOutOff) {
        LEAEngine.checkBuffer(pInput, pInOff, false);
        LEAEngine.checkBuffer(pOutput, pOutOff, true);
        return this.forEncryption ? this.encryptBlock(pInput, pInOff, pOutput, pOutOff) : this.decryptBlock(pInput, pInOff, pOutput, pOutOff);
    }

    private static int bufLength(byte[] pBuffer) {
        return pBuffer == null ? 0 : pBuffer.length;
    }

    private static void checkBuffer(byte[] pBuffer, int pOffset, boolean pOutput) {
        boolean badLen;
        int myBufLen = LEAEngine.bufLength(pBuffer);
        int myLast = pOffset + 16;
        boolean bl = badLen = pOffset < 0 || myLast < 0;
        if (badLen || myLast > myBufLen) {
            throw pOutput ? new OutputLengthException("Output buffer too short.") : new DataLengthException("Input buffer too short.");
        }
    }

    private int encryptBlock(byte[] pInput, int pInOff, byte[] pOutput, int pOutOff) {
        Pack.littleEndianToInt(pInput, pInOff, this.theBlock, 0, 4);
        for (int i = 0; i < this.theRounds; ++i) {
            this.encryptRound(i);
        }
        Pack.intToLittleEndian(this.theBlock, pOutput, pOutOff);
        return 16;
    }

    private void encryptRound(int pRound) {
        int[] myKeys = this.theRoundKeys[pRound];
        int myIndex = (3 + pRound) % 4;
        int myNextIndex = LEAEngine.leftIndex(myIndex);
        this.theBlock[myIndex] = LEAEngine.ror32((this.theBlock[myNextIndex] ^ myKeys[4]) + (this.theBlock[myIndex] ^ myKeys[5]), 3);
        myIndex = myNextIndex;
        myNextIndex = LEAEngine.leftIndex(myIndex);
        this.theBlock[myIndex] = LEAEngine.ror32((this.theBlock[myNextIndex] ^ myKeys[2]) + (this.theBlock[myIndex] ^ myKeys[3]), 5);
        myIndex = myNextIndex;
        myNextIndex = LEAEngine.leftIndex(myIndex);
        this.theBlock[myIndex] = LEAEngine.rol32((this.theBlock[myNextIndex] ^ myKeys[0]) + (this.theBlock[myIndex] ^ myKeys[1]), 9);
    }

    private static int leftIndex(int pIndex) {
        return pIndex == 0 ? 3 : pIndex - 1;
    }

    private int decryptBlock(byte[] pInput, int pInOff, byte[] pOutput, int pOutOff) {
        Pack.littleEndianToInt(pInput, pInOff, this.theBlock, 0, 4);
        for (int i = this.theRounds - 1; i >= 0; --i) {
            this.decryptRound(i);
        }
        Pack.intToLittleEndian(this.theBlock, pOutput, pOutOff);
        return 16;
    }

    private void decryptRound(int pRound) {
        int[] myKeys = this.theRoundKeys[pRound];
        int myPrevIndex = pRound % 4;
        int myIndex = LEAEngine.rightIndex(myPrevIndex);
        this.theBlock[myIndex] = LEAEngine.ror32(this.theBlock[myIndex], 9) - (this.theBlock[myPrevIndex] ^ myKeys[0]) ^ myKeys[1];
        myPrevIndex = myIndex;
        myIndex = LEAEngine.rightIndex(myIndex);
        this.theBlock[myIndex] = LEAEngine.rol32(this.theBlock[myIndex], 5) - (this.theBlock[myPrevIndex] ^ myKeys[2]) ^ myKeys[3];
        myPrevIndex = myIndex;
        myIndex = LEAEngine.rightIndex(myIndex);
        this.theBlock[myIndex] = LEAEngine.rol32(this.theBlock[myIndex], 3) - (this.theBlock[myPrevIndex] ^ myKeys[4]) ^ myKeys[5];
    }

    private static int rightIndex(int pIndex) {
        return pIndex == 3 ? 0 : pIndex + 1;
    }

    private void generateRoundKeys(byte[] pKey) {
        this.theRounds = (pKey.length >> 1) + 16;
        this.theRoundKeys = new int[this.theRounds][6];
        int numWords = pKey.length / 4;
        int[] myT = new int[numWords];
        Pack.littleEndianToInt(pKey, 0, myT, 0, numWords);
        switch (numWords) {
            case 4: {
                this.generate128RoundKeys(myT);
                break;
            }
            case 6: {
                this.generate192RoundKeys(myT);
                break;
            }
            default: {
                this.generate256RoundKeys(myT);
            }
        }
    }

    private void generate128RoundKeys(int[] pWork) {
        for (int i = 0; i < this.theRounds; ++i) {
            int j;
            int myDelta = LEAEngine.rol32(DELTA[i & 3], i);
            pWork[j = 0] = LEAEngine.rol32(pWork[j++] + myDelta, 1);
            pWork[j] = LEAEngine.rol32(pWork[j] + LEAEngine.rol32(myDelta, j++), 3);
            pWork[j] = LEAEngine.rol32(pWork[j] + LEAEngine.rol32(myDelta, j++), 6);
            pWork[j] = LEAEngine.rol32(pWork[j] + LEAEngine.rol32(myDelta, j), 11);
            int[] myKeys = this.theRoundKeys[i];
            myKeys[0] = pWork[0];
            myKeys[1] = pWork[1];
            myKeys[2] = pWork[2];
            myKeys[3] = pWork[1];
            myKeys[4] = pWork[3];
            myKeys[5] = pWork[1];
        }
    }

    private void generate192RoundKeys(int[] pWork) {
        for (int i = 0; i < this.theRounds; ++i) {
            int j;
            int myDelta = LEAEngine.rol32(DELTA[i % 6], i);
            pWork[j = 0] = LEAEngine.rol32(pWork[j] + LEAEngine.rol32(myDelta, j++), 1);
            pWork[j] = LEAEngine.rol32(pWork[j] + LEAEngine.rol32(myDelta, j++), 3);
            pWork[j] = LEAEngine.rol32(pWork[j] + LEAEngine.rol32(myDelta, j++), 6);
            pWork[j] = LEAEngine.rol32(pWork[j] + LEAEngine.rol32(myDelta, j++), 11);
            pWork[j] = LEAEngine.rol32(pWork[j] + LEAEngine.rol32(myDelta, j++), 13);
            pWork[j] = LEAEngine.rol32(pWork[j] + LEAEngine.rol32(myDelta, j++), 17);
            System.arraycopy(pWork, 0, this.theRoundKeys[i], 0, j);
        }
    }

    private void generate256RoundKeys(int[] pWork) {
        int index = 0;
        for (int i = 0; i < this.theRounds; ++i) {
            int myDelta = LEAEngine.rol32(DELTA[i & 7], i);
            int[] myKeys = this.theRoundKeys[i];
            int j = 0;
            myKeys[j] = LEAEngine.rol32(pWork[index & 7] + myDelta, 1);
            pWork[index++ & 7] = myKeys[j++];
            myKeys[j] = LEAEngine.rol32(pWork[index & 7] + LEAEngine.rol32(myDelta, j), 3);
            pWork[index++ & 7] = myKeys[j++];
            myKeys[j] = LEAEngine.rol32(pWork[index & 7] + LEAEngine.rol32(myDelta, j), 6);
            pWork[index++ & 7] = myKeys[j++];
            myKeys[j] = LEAEngine.rol32(pWork[index & 7] + LEAEngine.rol32(myDelta, j), 11);
            pWork[index++ & 7] = myKeys[j++];
            myKeys[j] = LEAEngine.rol32(pWork[index & 7] + LEAEngine.rol32(myDelta, j), 13);
            pWork[index++ & 7] = myKeys[j++];
            myKeys[j] = LEAEngine.rol32(pWork[index & 7] + LEAEngine.rol32(myDelta, j), 17);
            pWork[index++ & 7] = myKeys[j];
        }
    }

    private static int rol32(int pValue, int pBits) {
        return pValue << pBits | pValue >>> 32 - pBits;
    }

    private static int ror32(int pValue, int pBits) {
        return pValue >>> pBits | pValue << 32 - pBits;
    }
}

