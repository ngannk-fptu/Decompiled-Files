/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Longs;
import org.bouncycastle.util.Pack;

public class Argon2BytesGenerator {
    private static final int ARGON2_BLOCK_SIZE = 1024;
    private static final int ARGON2_QWORDS_IN_BLOCK = 128;
    private static final int ARGON2_ADDRESSES_IN_BLOCK = 128;
    private static final int ARGON2_PREHASH_DIGEST_LENGTH = 64;
    private static final int ARGON2_PREHASH_SEED_LENGTH = 72;
    private static final int ARGON2_SYNC_POINTS = 4;
    private static final int MIN_PARALLELISM = 1;
    private static final int MAX_PARALLELISM = 0x1000000;
    private static final int MIN_OUTLEN = 4;
    private static final int MIN_ITERATIONS = 1;
    private static final long M32L = 0xFFFFFFFFL;
    private static final byte[] ZERO_BYTES = new byte[4];
    private Argon2Parameters parameters;
    private Block[] memory;
    private int segmentLength;
    private int laneLength;

    public void init(Argon2Parameters parameters) {
        this.parameters = parameters;
        if (parameters.getLanes() < 1) {
            throw new IllegalStateException("lanes must be greater than 1");
        }
        if (parameters.getLanes() > 0x1000000) {
            throw new IllegalStateException("lanes must be less than 16777216");
        }
        if (parameters.getMemory() < 2 * parameters.getLanes()) {
            throw new IllegalStateException("memory is less than: " + 2 * parameters.getLanes() + " expected " + 2 * parameters.getLanes());
        }
        if (parameters.getIterations() < 1) {
            throw new IllegalStateException("iterations is less than: 1");
        }
        this.doInit(parameters);
    }

    public int generateBytes(char[] password, byte[] out) {
        return this.generateBytes(this.parameters.getCharToByteConverter().convert(password), out);
    }

    public int generateBytes(char[] password, byte[] out, int outOff, int outLen) {
        return this.generateBytes(this.parameters.getCharToByteConverter().convert(password), out, outOff, outLen);
    }

    public int generateBytes(byte[] password, byte[] out) {
        return this.generateBytes(password, out, 0, out.length);
    }

    public int generateBytes(byte[] password, byte[] out, int outOff, int outLen) {
        if (outLen < 4) {
            throw new IllegalStateException("output length less than 4");
        }
        byte[] tmpBlockBytes = new byte[1024];
        this.initialize(tmpBlockBytes, password, outLen);
        this.fillMemoryBlocks();
        this.digest(tmpBlockBytes, out, outOff, outLen);
        this.reset();
        return outLen;
    }

    private void reset() {
        if (null != this.memory) {
            for (int i = 0; i < this.memory.length; ++i) {
                Block b = this.memory[i];
                if (null == b) continue;
                b.clear();
            }
        }
    }

    private void doInit(Argon2Parameters parameters) {
        int memoryBlocks = parameters.getMemory();
        if (memoryBlocks < 8 * parameters.getLanes()) {
            memoryBlocks = 8 * parameters.getLanes();
        }
        this.segmentLength = memoryBlocks / (parameters.getLanes() * 4);
        this.laneLength = this.segmentLength * 4;
        memoryBlocks = this.segmentLength * (parameters.getLanes() * 4);
        this.initMemory(memoryBlocks);
    }

    private void initMemory(int memoryBlocks) {
        this.memory = new Block[memoryBlocks];
        for (int i = 0; i < this.memory.length; ++i) {
            this.memory[i] = new Block();
        }
    }

    private void fillMemoryBlocks() {
        FillBlock filler = new FillBlock();
        Position position = new Position();
        for (int pass = 0; pass < this.parameters.getIterations(); ++pass) {
            position.pass = pass;
            for (int slice = 0; slice < 4; ++slice) {
                position.slice = slice;
                int lane = 0;
                while (lane < this.parameters.getLanes()) {
                    position.lane = lane++;
                    this.fillSegment(filler, position);
                }
            }
        }
    }

    private void fillSegment(FillBlock filler, Position position) {
        Block addressBlock = null;
        Block inputBlock = null;
        boolean dataIndependentAddressing = this.isDataIndependentAddressing(position);
        int startingIndex = Argon2BytesGenerator.getStartingIndex(position);
        int currentOffset = position.lane * this.laneLength + position.slice * this.segmentLength + startingIndex;
        int prevOffset = this.getPrevOffset(currentOffset);
        if (dataIndependentAddressing) {
            addressBlock = filler.addressBlock.clear();
            inputBlock = filler.inputBlock.clear();
            this.initAddressBlocks(filler, position, inputBlock, addressBlock);
        }
        boolean withXor = this.isWithXor(position);
        for (int index = startingIndex; index < this.segmentLength; ++index) {
            long pseudoRandom;
            int refLane = this.getRefLane(position, pseudoRandom = this.getPseudoRandom(filler, index, addressBlock, inputBlock, prevOffset, dataIndependentAddressing));
            int refColumn = this.getRefColumn(position, index, pseudoRandom, refLane == position.lane);
            Block prevBlock = this.memory[prevOffset];
            Block refBlock = this.memory[this.laneLength * refLane + refColumn];
            Block currentBlock = this.memory[currentOffset];
            if (withXor) {
                filler.fillBlockWithXor(prevBlock, refBlock, currentBlock);
            } else {
                filler.fillBlock(prevBlock, refBlock, currentBlock);
            }
            prevOffset = currentOffset++;
        }
    }

    private boolean isDataIndependentAddressing(Position position) {
        return this.parameters.getType() == 1 || this.parameters.getType() == 2 && position.pass == 0 && position.slice < 2;
    }

    private void initAddressBlocks(FillBlock filler, Position position, Block inputBlock, Block addressBlock) {
        ((Block)inputBlock).v[0] = this.intToLong(position.pass);
        ((Block)inputBlock).v[1] = this.intToLong(position.lane);
        ((Block)inputBlock).v[2] = this.intToLong(position.slice);
        ((Block)inputBlock).v[3] = this.intToLong(this.memory.length);
        ((Block)inputBlock).v[4] = this.intToLong(this.parameters.getIterations());
        ((Block)inputBlock).v[5] = this.intToLong(this.parameters.getType());
        if (position.pass == 0 && position.slice == 0) {
            this.nextAddresses(filler, inputBlock, addressBlock);
        }
    }

    private boolean isWithXor(Position position) {
        return position.pass != 0 && this.parameters.getVersion() != 16;
    }

    private int getPrevOffset(int currentOffset) {
        if (currentOffset % this.laneLength == 0) {
            return currentOffset + this.laneLength - 1;
        }
        return currentOffset - 1;
    }

    private static int getStartingIndex(Position position) {
        if (position.pass == 0 && position.slice == 0) {
            return 2;
        }
        return 0;
    }

    private void nextAddresses(FillBlock filler, Block inputBlock, Block addressBlock) {
        long[] lArray = inputBlock.v;
        lArray[6] = lArray[6] + 1L;
        filler.fillBlock(inputBlock, addressBlock);
        filler.fillBlock(addressBlock, addressBlock);
    }

    private long getPseudoRandom(FillBlock filler, int index, Block addressBlock, Block inputBlock, int prevOffset, boolean dataIndependentAddressing) {
        if (dataIndependentAddressing) {
            int addressIndex = index % 128;
            if (addressIndex == 0) {
                this.nextAddresses(filler, inputBlock, addressBlock);
            }
            return addressBlock.v[addressIndex];
        }
        return this.memory[prevOffset].v[0];
    }

    private int getRefLane(Position position, long pseudoRandom) {
        int refLane = (int)((pseudoRandom >>> 32) % (long)this.parameters.getLanes());
        if (position.pass == 0 && position.slice == 0) {
            refLane = position.lane;
        }
        return refLane;
    }

    private int getRefColumn(Position position, int index, long pseudoRandom, boolean sameLane) {
        int referenceAreaSize;
        int startPosition;
        if (position.pass == 0) {
            startPosition = 0;
            referenceAreaSize = sameLane ? position.slice * this.segmentLength + index - 1 : position.slice * this.segmentLength + (index == 0 ? -1 : 0);
        } else {
            startPosition = (position.slice + 1) * this.segmentLength % this.laneLength;
            referenceAreaSize = sameLane ? this.laneLength - this.segmentLength + index - 1 : this.laneLength - this.segmentLength + (index == 0 ? -1 : 0);
        }
        long relativePosition = pseudoRandom & 0xFFFFFFFFL;
        relativePosition = relativePosition * relativePosition >>> 32;
        relativePosition = (long)(referenceAreaSize - 1) - ((long)referenceAreaSize * relativePosition >>> 32);
        return (int)((long)startPosition + relativePosition) % this.laneLength;
    }

    private void digest(byte[] tmpBlockBytes, byte[] out, int outOff, int outLen) {
        Block finalBlock = this.memory[this.laneLength - 1];
        for (int i = 1; i < this.parameters.getLanes(); ++i) {
            int lastBlockInLane = i * this.laneLength + (this.laneLength - 1);
            finalBlock.xorWith(this.memory[lastBlockInLane]);
        }
        finalBlock.toBytes(tmpBlockBytes);
        this.hash(tmpBlockBytes, out, outOff, outLen);
    }

    private void hash(byte[] input, byte[] out, int outOff, int outLen) {
        byte[] outLenBytes = new byte[4];
        Pack.intToLittleEndian(outLen, outLenBytes, 0);
        int blake2bLength = 64;
        if (outLen <= blake2bLength) {
            Blake2bDigest blake = new Blake2bDigest(outLen * 8);
            blake.update(outLenBytes, 0, outLenBytes.length);
            blake.update(input, 0, input.length);
            blake.doFinal(out, outOff);
        } else {
            Blake2bDigest digest = new Blake2bDigest(blake2bLength * 8);
            byte[] outBuffer = new byte[blake2bLength];
            digest.update(outLenBytes, 0, outLenBytes.length);
            digest.update(input, 0, input.length);
            digest.doFinal(outBuffer, 0);
            int halfLen = blake2bLength / 2;
            int outPos = outOff;
            System.arraycopy(outBuffer, 0, out, outPos, halfLen);
            outPos += halfLen;
            int r = (outLen + 31) / 32 - 2;
            int i = 2;
            while (i <= r) {
                digest.update(outBuffer, 0, outBuffer.length);
                digest.doFinal(outBuffer, 0);
                System.arraycopy(outBuffer, 0, out, outPos, halfLen);
                ++i;
                outPos += halfLen;
            }
            int lastLength = outLen - 32 * r;
            digest = new Blake2bDigest(lastLength * 8);
            digest.update(outBuffer, 0, outBuffer.length);
            digest.doFinal(out, outPos);
        }
    }

    private static void roundFunction(Block block, int v0, int v1, int v2, int v3, int v4, int v5, int v6, int v7, int v8, int v9, int v10, int v11, int v12, int v13, int v14, int v15) {
        long[] v = block.v;
        Argon2BytesGenerator.F(v, v0, v4, v8, v12);
        Argon2BytesGenerator.F(v, v1, v5, v9, v13);
        Argon2BytesGenerator.F(v, v2, v6, v10, v14);
        Argon2BytesGenerator.F(v, v3, v7, v11, v15);
        Argon2BytesGenerator.F(v, v0, v5, v10, v15);
        Argon2BytesGenerator.F(v, v1, v6, v11, v12);
        Argon2BytesGenerator.F(v, v2, v7, v8, v13);
        Argon2BytesGenerator.F(v, v3, v4, v9, v14);
    }

    private static void F(long[] v, int a, int b, int c, int d) {
        Argon2BytesGenerator.quarterRound(v, a, b, d, 32);
        Argon2BytesGenerator.quarterRound(v, c, d, b, 24);
        Argon2BytesGenerator.quarterRound(v, a, b, d, 16);
        Argon2BytesGenerator.quarterRound(v, c, d, b, 63);
    }

    private static void quarterRound(long[] v, int x, int y, int z, int s) {
        long a = v[x];
        long b = v[y];
        long c = v[z];
        a += b + 2L * (a & 0xFFFFFFFFL) * (b & 0xFFFFFFFFL);
        c = Longs.rotateRight(c ^ a, s);
        v[x] = a;
        v[z] = c;
    }

    private void initialize(byte[] tmpBlockBytes, byte[] password, int outputLength) {
        Blake2bDigest blake = new Blake2bDigest(512);
        int[] values = new int[]{this.parameters.getLanes(), outputLength, this.parameters.getMemory(), this.parameters.getIterations(), this.parameters.getVersion(), this.parameters.getType()};
        Pack.intToLittleEndian(values, tmpBlockBytes, 0);
        blake.update(tmpBlockBytes, 0, values.length * 4);
        Argon2BytesGenerator.addByteString(tmpBlockBytes, blake, password);
        Argon2BytesGenerator.addByteString(tmpBlockBytes, blake, this.parameters.getSalt());
        Argon2BytesGenerator.addByteString(tmpBlockBytes, blake, this.parameters.getSecret());
        Argon2BytesGenerator.addByteString(tmpBlockBytes, blake, this.parameters.getAdditional());
        byte[] initialHashWithZeros = new byte[72];
        blake.doFinal(initialHashWithZeros, 0);
        this.fillFirstBlocks(tmpBlockBytes, initialHashWithZeros);
    }

    private static void addByteString(byte[] tmpBlockBytes, Digest digest, byte[] octets) {
        if (null == octets) {
            digest.update(ZERO_BYTES, 0, 4);
            return;
        }
        Pack.intToLittleEndian(octets.length, tmpBlockBytes, 0);
        digest.update(tmpBlockBytes, 0, 4);
        digest.update(octets, 0, octets.length);
    }

    private void fillFirstBlocks(byte[] tmpBlockBytes, byte[] initialHashWithZeros) {
        byte[] initialHashWithOnes = new byte[72];
        System.arraycopy(initialHashWithZeros, 0, initialHashWithOnes, 0, 64);
        initialHashWithOnes[64] = 1;
        for (int i = 0; i < this.parameters.getLanes(); ++i) {
            Pack.intToLittleEndian(i, initialHashWithZeros, 68);
            Pack.intToLittleEndian(i, initialHashWithOnes, 68);
            this.hash(initialHashWithZeros, tmpBlockBytes, 0, 1024);
            this.memory[i * this.laneLength + 0].fromBytes(tmpBlockBytes);
            this.hash(initialHashWithOnes, tmpBlockBytes, 0, 1024);
            this.memory[i * this.laneLength + 1].fromBytes(tmpBlockBytes);
        }
    }

    private long intToLong(int x) {
        return (long)x & 0xFFFFFFFFL;
    }

    private static class Block {
        private static final int SIZE = 128;
        private final long[] v = new long[128];

        private Block() {
        }

        void fromBytes(byte[] input) {
            if (input.length < 1024) {
                throw new IllegalArgumentException("input shorter than blocksize");
            }
            Pack.littleEndianToLong(input, 0, this.v);
        }

        void toBytes(byte[] output) {
            if (output.length < 1024) {
                throw new IllegalArgumentException("output shorter than blocksize");
            }
            Pack.longToLittleEndian(this.v, output, 0);
        }

        private void copyBlock(Block other) {
            System.arraycopy(other.v, 0, this.v, 0, 128);
        }

        private void xor(Block b1, Block b2) {
            long[] v0 = this.v;
            long[] v1 = b1.v;
            long[] v2 = b2.v;
            for (int i = 0; i < 128; ++i) {
                v0[i] = v1[i] ^ v2[i];
            }
        }

        private void xorWith(Block b1) {
            long[] v0 = this.v;
            long[] v1 = b1.v;
            for (int i = 0; i < 128; ++i) {
                int n = i;
                v0[n] = v0[n] ^ v1[i];
            }
        }

        private void xorWith(Block b1, Block b2) {
            long[] v0 = this.v;
            long[] v1 = b1.v;
            long[] v2 = b2.v;
            for (int i = 0; i < 128; ++i) {
                int n = i;
                v0[n] = v0[n] ^ (v1[i] ^ v2[i]);
            }
        }

        public Block clear() {
            Arrays.fill(this.v, 0L);
            return this;
        }
    }

    private static class FillBlock {
        Block R = new Block();
        Block Z = new Block();
        Block addressBlock = new Block();
        Block inputBlock = new Block();

        private FillBlock() {
        }

        private void applyBlake() {
            int i;
            for (i = 0; i < 8; ++i) {
                int i16 = 16 * i;
                Argon2BytesGenerator.roundFunction(this.Z, i16, i16 + 1, i16 + 2, i16 + 3, i16 + 4, i16 + 5, i16 + 6, i16 + 7, i16 + 8, i16 + 9, i16 + 10, i16 + 11, i16 + 12, i16 + 13, i16 + 14, i16 + 15);
            }
            for (i = 0; i < 8; ++i) {
                int i2 = 2 * i;
                Argon2BytesGenerator.roundFunction(this.Z, i2, i2 + 1, i2 + 16, i2 + 17, i2 + 32, i2 + 33, i2 + 48, i2 + 49, i2 + 64, i2 + 65, i2 + 80, i2 + 81, i2 + 96, i2 + 97, i2 + 112, i2 + 113);
            }
        }

        private void fillBlock(Block Y, Block currentBlock) {
            this.Z.copyBlock(Y);
            this.applyBlake();
            currentBlock.xor(Y, this.Z);
        }

        private void fillBlock(Block X, Block Y, Block currentBlock) {
            this.R.xor(X, Y);
            this.Z.copyBlock(this.R);
            this.applyBlake();
            currentBlock.xor(this.R, this.Z);
        }

        private void fillBlockWithXor(Block X, Block Y, Block currentBlock) {
            this.R.xor(X, Y);
            this.Z.copyBlock(this.R);
            this.applyBlake();
            currentBlock.xorWith(this.R, this.Z);
        }
    }

    private static class Position {
        int pass;
        int lane;
        int slice;

        Position() {
        }
    }
}

