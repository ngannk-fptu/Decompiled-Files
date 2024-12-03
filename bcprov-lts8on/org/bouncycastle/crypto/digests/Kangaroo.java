/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public final class Kangaroo {
    private static final int DIGESTLEN = 32;

    static abstract class KangarooBase
    implements ExtendedDigest,
    Xof {
        private static final int BLKSIZE = 8192;
        private static final byte[] SINGLE = new byte[]{7};
        private static final byte[] INTERMEDIATE = new byte[]{11};
        private static final byte[] FINAL = new byte[]{-1, -1, 6};
        private static final byte[] FIRST = new byte[]{3, 0, 0, 0, 0, 0, 0, 0};
        private final byte[] singleByte = new byte[1];
        private final KangarooSponge theTree;
        private final KangarooSponge theLeaf;
        private final int theChainLen;
        private byte[] thePersonal;
        private boolean squeezing;
        private int theCurrNode;
        private int theProcessed;
        private final CryptoServicePurpose purpose;

        KangarooBase(int pStrength, int pRounds, int pLength, CryptoServicePurpose purpose) {
            this.theTree = new KangarooSponge(pStrength, pRounds);
            this.theLeaf = new KangarooSponge(pStrength, pRounds);
            this.theChainLen = pStrength >> 2;
            this.buildPersonal(null);
            this.purpose = purpose;
            CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, pStrength, purpose));
        }

        private void buildPersonal(byte[] pPersonal) {
            int myLen = pPersonal == null ? 0 : pPersonal.length;
            byte[] myEnc = KangarooBase.lengthEncode(myLen);
            this.thePersonal = pPersonal == null ? new byte[myLen + myEnc.length] : Arrays.copyOf(pPersonal, myLen + myEnc.length);
            System.arraycopy(myEnc, 0, this.thePersonal, myLen, myEnc.length);
        }

        @Override
        public int getByteLength() {
            return this.theTree.theRateBytes;
        }

        @Override
        public int getDigestSize() {
            return this.theChainLen >> 1;
        }

        public void init(KangarooParameters pParams) {
            this.buildPersonal(pParams.getPersonalisation());
            this.reset();
        }

        @Override
        public void update(byte pIn) {
            this.singleByte[0] = pIn;
            this.update(this.singleByte, 0, 1);
        }

        @Override
        public void update(byte[] pIn, int pInOff, int pLen) {
            this.processData(pIn, pInOff, pLen);
        }

        @Override
        public int doFinal(byte[] pOut, int pOutOffset) {
            return this.doFinal(pOut, pOutOffset, this.getDigestSize());
        }

        @Override
        public int doFinal(byte[] pOut, int pOutOffset, int pOutLen) {
            if (this.squeezing) {
                throw new IllegalStateException("Already outputting");
            }
            int length = this.doOutput(pOut, pOutOffset, pOutLen);
            this.reset();
            return length;
        }

        @Override
        public int doOutput(byte[] pOut, int pOutOffset, int pOutLen) {
            if (!this.squeezing) {
                this.switchToSqueezing();
            }
            if (pOutLen < 0) {
                throw new IllegalArgumentException("Invalid output length");
            }
            this.theTree.squeeze(pOut, pOutOffset, pOutLen);
            return pOutLen;
        }

        private void processData(byte[] pIn, int pInOffSet, int pLen) {
            int myDataLen;
            if (this.squeezing) {
                throw new IllegalStateException("attempt to absorb while squeezing");
            }
            KangarooSponge mySponge = this.theCurrNode == 0 ? this.theTree : this.theLeaf;
            int mySpace = 8192 - this.theProcessed;
            if (mySpace >= pLen) {
                mySponge.absorb(pIn, pInOffSet, pLen);
                this.theProcessed += pLen;
                return;
            }
            if (mySpace > 0) {
                mySponge.absorb(pIn, pInOffSet, mySpace);
                this.theProcessed += mySpace;
            }
            for (int myProcessed = mySpace; myProcessed < pLen; myProcessed += myDataLen) {
                if (this.theProcessed == 8192) {
                    this.switchLeaf(true);
                }
                myDataLen = Math.min(pLen - myProcessed, 8192);
                this.theLeaf.absorb(pIn, pInOffSet + myProcessed, myDataLen);
                this.theProcessed += myDataLen;
            }
        }

        @Override
        public void reset() {
            this.theTree.initSponge();
            this.theLeaf.initSponge();
            this.theCurrNode = 0;
            this.theProcessed = 0;
            this.squeezing = false;
        }

        private void switchLeaf(boolean pMoreToCome) {
            if (this.theCurrNode == 0) {
                this.theTree.absorb(KangarooBase.FIRST, 0, KangarooBase.FIRST.length);
            } else {
                this.theLeaf.absorb(KangarooBase.INTERMEDIATE, 0, KangarooBase.INTERMEDIATE.length);
                byte[] myHash = new byte[this.theChainLen];
                this.theLeaf.squeeze(myHash, 0, this.theChainLen);
                this.theTree.absorb(myHash, 0, this.theChainLen);
                this.theLeaf.initSponge();
            }
            if (pMoreToCome) {
                ++this.theCurrNode;
            }
            this.theProcessed = 0;
        }

        private void switchToSqueezing() {
            this.processData(this.thePersonal, 0, this.thePersonal.length);
            if (this.theCurrNode == 0) {
                this.switchSingle();
            } else {
                this.switchFinal();
            }
        }

        private void switchSingle() {
            this.theTree.absorb(KangarooBase.SINGLE, 0, 1);
            this.theTree.padAndSwitchToSqueezingPhase();
        }

        private void switchFinal() {
            this.switchLeaf(false);
            byte[] myLength = KangarooBase.lengthEncode(this.theCurrNode);
            this.theTree.absorb(myLength, 0, myLength.length);
            this.theTree.absorb(KangarooBase.FINAL, 0, KangarooBase.FINAL.length);
            this.theTree.padAndSwitchToSqueezingPhase();
        }

        private static byte[] lengthEncode(long strLen) {
            int n = 0;
            long v = strLen;
            if (v != 0L) {
                n = 1;
                while ((v >>= 8) != 0L) {
                    n = (byte)(n + 1);
                }
            }
            byte[] b = new byte[n + 1];
            b[n] = n;
            for (int i = 0; i < n; ++i) {
                b[i] = (byte)(strLen >> 8 * (n - i - 1));
            }
            return b;
        }
    }

    public static class KangarooParameters
    implements CipherParameters {
        private byte[] thePersonal;

        public byte[] getPersonalisation() {
            return Arrays.clone(this.thePersonal);
        }

        static /* synthetic */ byte[] access$002(KangarooParameters x0, byte[] x1) {
            x0.thePersonal = x1;
            return x1;
        }

        public static class Builder {
            private byte[] thePersonal;

            public Builder setPersonalisation(byte[] pPersonal) {
                this.thePersonal = Arrays.clone(pPersonal);
                return this;
            }

            public KangarooParameters build() {
                KangarooParameters myParams = new KangarooParameters();
                if (this.thePersonal != null) {
                    KangarooParameters.access$002(myParams, this.thePersonal);
                }
                return myParams;
            }
        }
    }

    private static class KangarooSponge {
        private static long[] KeccakRoundConstants = new long[]{1L, 32898L, -9223372036854742902L, -9223372034707259392L, 32907L, 0x80000001L, -9223372034707259263L, -9223372036854743031L, 138L, 136L, 0x80008009L, 0x8000000AL, 0x8000808BL, -9223372036854775669L, -9223372036854742903L, -9223372036854743037L, -9223372036854743038L, -9223372036854775680L, 32778L, -9223372034707292150L, -9223372034707259263L, -9223372036854742912L, 0x80000001L, -9223372034707259384L};
        private final int theRounds;
        private final int theRateBytes;
        private final long[] theState = new long[25];
        private final byte[] theQueue;
        private int bytesInQueue;
        private boolean squeezing;

        KangarooSponge(int pStrength, int pRounds) {
            this.theRateBytes = 1600 - (pStrength << 1) >> 3;
            this.theRounds = pRounds;
            this.theQueue = new byte[this.theRateBytes];
            this.initSponge();
        }

        private void initSponge() {
            Arrays.fill(this.theState, 0L);
            Arrays.fill(this.theQueue, (byte)0);
            this.bytesInQueue = 0;
            this.squeezing = false;
        }

        private void absorb(byte[] data, int off, int len) {
            if (this.squeezing) {
                throw new IllegalStateException("attempt to absorb while squeezing");
            }
            int count = 0;
            while (count < len) {
                if (this.bytesInQueue == 0 && count <= len - this.theRateBytes) {
                    do {
                        this.KangarooAbsorb(data, off + count);
                    } while ((count += this.theRateBytes) <= len - this.theRateBytes);
                    continue;
                }
                int partialBlock = Math.min(this.theRateBytes - this.bytesInQueue, len - count);
                System.arraycopy(data, off + count, this.theQueue, this.bytesInQueue, partialBlock);
                this.bytesInQueue += partialBlock;
                count += partialBlock;
                if (this.bytesInQueue != this.theRateBytes) continue;
                this.KangarooAbsorb(this.theQueue, 0);
                this.bytesInQueue = 0;
            }
        }

        private void padAndSwitchToSqueezingPhase() {
            for (int i = this.bytesInQueue; i < this.theRateBytes; ++i) {
                this.theQueue[i] = 0;
            }
            int n = this.theRateBytes - 1;
            this.theQueue[n] = (byte)(this.theQueue[n] ^ 0x80);
            this.KangarooAbsorb(this.theQueue, 0);
            this.KangarooExtract();
            this.bytesInQueue = this.theRateBytes;
            this.squeezing = true;
        }

        private void squeeze(byte[] output, int offset, int outputLength) {
            int partialBlock;
            if (!this.squeezing) {
                this.padAndSwitchToSqueezingPhase();
            }
            for (int i = 0; i < outputLength; i += partialBlock) {
                if (this.bytesInQueue == 0) {
                    this.KangarooPermutation();
                    this.KangarooExtract();
                    this.bytesInQueue = this.theRateBytes;
                }
                partialBlock = Math.min(this.bytesInQueue, outputLength - i);
                System.arraycopy(this.theQueue, this.theRateBytes - this.bytesInQueue, output, offset + i, partialBlock);
                this.bytesInQueue -= partialBlock;
            }
        }

        private void KangarooAbsorb(byte[] data, int off) {
            int count = this.theRateBytes >> 3;
            int offSet = off;
            int i = 0;
            while (i < count) {
                int n = i++;
                this.theState[n] = this.theState[n] ^ Pack.littleEndianToLong(data, offSet);
                offSet += 8;
            }
            this.KangarooPermutation();
        }

        private void KangarooExtract() {
            Pack.longToLittleEndian(this.theState, 0, this.theRateBytes >> 3, this.theQueue, 0);
        }

        private void KangarooPermutation() {
            long[] A = this.theState;
            long a00 = A[0];
            long a01 = A[1];
            long a02 = A[2];
            long a03 = A[3];
            long a04 = A[4];
            long a05 = A[5];
            long a06 = A[6];
            long a07 = A[7];
            long a08 = A[8];
            long a09 = A[9];
            long a10 = A[10];
            long a11 = A[11];
            long a12 = A[12];
            long a13 = A[13];
            long a14 = A[14];
            long a15 = A[15];
            long a16 = A[16];
            long a17 = A[17];
            long a18 = A[18];
            long a19 = A[19];
            long a20 = A[20];
            long a21 = A[21];
            long a22 = A[22];
            long a23 = A[23];
            long a24 = A[24];
            int myBase = KeccakRoundConstants.length - this.theRounds;
            for (int i = 0; i < this.theRounds; ++i) {
                long c0 = a00 ^ a05 ^ a10 ^ a15 ^ a20;
                long c1 = a01 ^ a06 ^ a11 ^ a16 ^ a21;
                long c2 = a02 ^ a07 ^ a12 ^ a17 ^ a22;
                long c3 = a03 ^ a08 ^ a13 ^ a18 ^ a23;
                long c4 = a04 ^ a09 ^ a14 ^ a19 ^ a24;
                long d1 = (c1 << 1 | c1 >>> -1) ^ c4;
                long d2 = (c2 << 1 | c2 >>> -1) ^ c0;
                long d3 = (c3 << 1 | c3 >>> -1) ^ c1;
                long d4 = (c4 << 1 | c4 >>> -1) ^ c2;
                long d0 = (c0 << 1 | c0 >>> -1) ^ c3;
                a00 ^= d1;
                a05 ^= d1;
                a10 ^= d1;
                a15 ^= d1;
                a20 ^= d1;
                a01 ^= d2;
                a06 ^= d2;
                a11 ^= d2;
                a16 ^= d2;
                a21 ^= d2;
                a02 ^= d3;
                a07 ^= d3;
                a12 ^= d3;
                a17 ^= d3;
                a22 ^= d3;
                a03 ^= d4;
                a08 ^= d4;
                a13 ^= d4;
                a18 ^= d4;
                a23 ^= d4;
                a04 ^= d0;
                a09 ^= d0;
                a14 ^= d0;
                a19 ^= d0;
                a24 ^= d0;
                c1 = a01 << 1 | a01 >>> 63;
                a01 = a06 << 44 | a06 >>> 20;
                a06 = a09 << 20 | a09 >>> 44;
                a09 = a22 << 61 | a22 >>> 3;
                a22 = a14 << 39 | a14 >>> 25;
                a14 = a20 << 18 | a20 >>> 46;
                a20 = a02 << 62 | a02 >>> 2;
                a02 = a12 << 43 | a12 >>> 21;
                a12 = a13 << 25 | a13 >>> 39;
                a13 = a19 << 8 | a19 >>> 56;
                a19 = a23 << 56 | a23 >>> 8;
                a23 = a15 << 41 | a15 >>> 23;
                a15 = a04 << 27 | a04 >>> 37;
                a04 = a24 << 14 | a24 >>> 50;
                a24 = a21 << 2 | a21 >>> 62;
                a21 = a08 << 55 | a08 >>> 9;
                a08 = a16 << 45 | a16 >>> 19;
                a16 = a05 << 36 | a05 >>> 28;
                a05 = a03 << 28 | a03 >>> 36;
                a03 = a18 << 21 | a18 >>> 43;
                a18 = a17 << 15 | a17 >>> 49;
                a17 = a11 << 10 | a11 >>> 54;
                a11 = a07 << 6 | a07 >>> 58;
                a07 = a10 << 3 | a10 >>> 61;
                a10 = c1;
                c0 = a00 ^ (a01 ^ 0xFFFFFFFFFFFFFFFFL) & a02;
                c1 = a01 ^ (a02 ^ 0xFFFFFFFFFFFFFFFFL) & a03;
                a02 ^= (a03 ^ 0xFFFFFFFFFFFFFFFFL) & a04;
                a03 ^= (a04 ^ 0xFFFFFFFFFFFFFFFFL) & a00;
                a04 ^= (a00 ^ 0xFFFFFFFFFFFFFFFFL) & a01;
                a00 = c0;
                a01 = c1;
                c0 = a05 ^ (a06 ^ 0xFFFFFFFFFFFFFFFFL) & a07;
                c1 = a06 ^ (a07 ^ 0xFFFFFFFFFFFFFFFFL) & a08;
                a07 ^= (a08 ^ 0xFFFFFFFFFFFFFFFFL) & a09;
                a08 ^= (a09 ^ 0xFFFFFFFFFFFFFFFFL) & a05;
                a09 ^= (a05 ^ 0xFFFFFFFFFFFFFFFFL) & a06;
                a05 = c0;
                a06 = c1;
                c0 = a10 ^ (a11 ^ 0xFFFFFFFFFFFFFFFFL) & a12;
                c1 = a11 ^ (a12 ^ 0xFFFFFFFFFFFFFFFFL) & a13;
                a12 ^= (a13 ^ 0xFFFFFFFFFFFFFFFFL) & a14;
                a13 ^= (a14 ^ 0xFFFFFFFFFFFFFFFFL) & a10;
                a14 ^= (a10 ^ 0xFFFFFFFFFFFFFFFFL) & a11;
                a10 = c0;
                a11 = c1;
                c0 = a15 ^ (a16 ^ 0xFFFFFFFFFFFFFFFFL) & a17;
                c1 = a16 ^ (a17 ^ 0xFFFFFFFFFFFFFFFFL) & a18;
                a17 ^= (a18 ^ 0xFFFFFFFFFFFFFFFFL) & a19;
                a18 ^= (a19 ^ 0xFFFFFFFFFFFFFFFFL) & a15;
                a19 ^= (a15 ^ 0xFFFFFFFFFFFFFFFFL) & a16;
                a15 = c0;
                a16 = c1;
                c0 = a20 ^ (a21 ^ 0xFFFFFFFFFFFFFFFFL) & a22;
                c1 = a21 ^ (a22 ^ 0xFFFFFFFFFFFFFFFFL) & a23;
                a22 ^= (a23 ^ 0xFFFFFFFFFFFFFFFFL) & a24;
                a23 ^= (a24 ^ 0xFFFFFFFFFFFFFFFFL) & a20;
                a24 ^= (a20 ^ 0xFFFFFFFFFFFFFFFFL) & a21;
                a20 = c0;
                a21 = c1;
                a00 ^= KeccakRoundConstants[myBase + i];
            }
            A[0] = a00;
            A[1] = a01;
            A[2] = a02;
            A[3] = a03;
            A[4] = a04;
            A[5] = a05;
            A[6] = a06;
            A[7] = a07;
            A[8] = a08;
            A[9] = a09;
            A[10] = a10;
            A[11] = a11;
            A[12] = a12;
            A[13] = a13;
            A[14] = a14;
            A[15] = a15;
            A[16] = a16;
            A[17] = a17;
            A[18] = a18;
            A[19] = a19;
            A[20] = a20;
            A[21] = a21;
            A[22] = a22;
            A[23] = a23;
            A[24] = a24;
        }
    }

    public static class KangarooTwelve
    extends KangarooBase {
        public KangarooTwelve() {
            this(32, CryptoServicePurpose.ANY);
        }

        public KangarooTwelve(int pLength, CryptoServicePurpose purpose) {
            super(128, 12, pLength, purpose);
        }

        public KangarooTwelve(CryptoServicePurpose purpose) {
            this(32, purpose);
        }

        @Override
        public String getAlgorithmName() {
            return "KangarooTwelve";
        }
    }

    public static class MarsupilamiFourteen
    extends KangarooBase {
        public MarsupilamiFourteen() {
            this(32, CryptoServicePurpose.ANY);
        }

        public MarsupilamiFourteen(int pLength, CryptoServicePurpose purpose) {
            super(256, 14, pLength, purpose);
        }

        public MarsupilamiFourteen(CryptoServicePurpose purpose) {
            this(32, purpose);
        }

        @Override
        public String getAlgorithmName() {
            return "MarsupilamiFourteen";
        }
    }
}

