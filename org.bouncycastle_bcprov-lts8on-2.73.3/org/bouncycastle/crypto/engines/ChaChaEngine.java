/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class ChaChaEngine
extends Salsa20Engine {
    public ChaChaEngine() {
    }

    public ChaChaEngine(int rounds) {
        super(rounds);
    }

    @Override
    public String getAlgorithmName() {
        return "ChaCha" + this.rounds;
    }

    @Override
    protected void advanceCounter(long diff) {
        int hi = (int)(diff >>> 32);
        int lo = (int)diff;
        if (hi > 0) {
            this.engineState[13] = this.engineState[13] + hi;
        }
        int oldState = this.engineState[12];
        this.engineState[12] = this.engineState[12] + lo;
        if (oldState != 0 && this.engineState[12] < oldState) {
            this.engineState[13] = this.engineState[13] + 1;
        }
    }

    @Override
    protected void advanceCounter() {
        this.engineState[12] = this.engineState[12] + 1;
        if (this.engineState[12] == 0) {
            this.engineState[13] = this.engineState[13] + 1;
        }
    }

    @Override
    protected void retreatCounter(long diff) {
        int hi = (int)(diff >>> 32);
        int lo = (int)diff;
        if (hi != 0) {
            if (((long)this.engineState[13] & 0xFFFFFFFFL) >= ((long)hi & 0xFFFFFFFFL)) {
                this.engineState[13] = this.engineState[13] - hi;
            } else {
                throw new IllegalStateException("attempt to reduce counter past zero.");
            }
        }
        if (((long)this.engineState[12] & 0xFFFFFFFFL) >= ((long)lo & 0xFFFFFFFFL)) {
            this.engineState[12] = this.engineState[12] - lo;
        } else if (this.engineState[13] != 0) {
            this.engineState[13] = this.engineState[13] - 1;
            this.engineState[12] = this.engineState[12] - lo;
        } else {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
    }

    @Override
    protected void retreatCounter() {
        if (this.engineState[12] == 0 && this.engineState[13] == 0) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
        this.engineState[12] = this.engineState[12] - 1;
        if (this.engineState[12] == -1) {
            this.engineState[13] = this.engineState[13] - 1;
        }
    }

    @Override
    protected long getCounter() {
        return (long)this.engineState[13] << 32 | (long)this.engineState[12] & 0xFFFFFFFFL;
    }

    @Override
    protected void resetCounter() {
        this.engineState[13] = 0;
        this.engineState[12] = 0;
    }

    @Override
    protected void setKey(byte[] keyBytes, byte[] ivBytes) {
        if (keyBytes != null) {
            if (keyBytes.length != 16 && keyBytes.length != 32) {
                throw new IllegalArgumentException(this.getAlgorithmName() + " requires 128 bit or 256 bit key");
            }
            this.packTauOrSigma(keyBytes.length, this.engineState, 0);
            Pack.littleEndianToInt(keyBytes, 0, this.engineState, 4, 4);
            Pack.littleEndianToInt(keyBytes, keyBytes.length - 16, this.engineState, 8, 4);
        }
        Pack.littleEndianToInt(ivBytes, 0, this.engineState, 14, 2);
    }

    @Override
    protected void generateKeyStream(byte[] output) {
        ChaChaEngine.chachaCore(this.rounds, this.engineState, this.x);
        Pack.intToLittleEndian(this.x, output, 0);
    }

    public static void chachaCore(int rounds, int[] input, int[] x) {
        if (input.length != 16) {
            throw new IllegalArgumentException();
        }
        if (x.length != 16) {
            throw new IllegalArgumentException();
        }
        if (rounds % 2 != 0) {
            throw new IllegalArgumentException("Number of rounds must be even");
        }
        int x00 = input[0];
        int x01 = input[1];
        int x02 = input[2];
        int x03 = input[3];
        int x04 = input[4];
        int x05 = input[5];
        int x06 = input[6];
        int x07 = input[7];
        int x08 = input[8];
        int x09 = input[9];
        int x10 = input[10];
        int x11 = input[11];
        int x12 = input[12];
        int x13 = input[13];
        int x14 = input[14];
        int x15 = input[15];
        for (int i = rounds; i > 0; i -= 2) {
            x12 = Integers.rotateLeft(x12 ^ (x00 += x04), 16);
            x04 = Integers.rotateLeft(x04 ^ (x08 += x12), 12);
            x12 = Integers.rotateLeft(x12 ^ (x00 += x04), 8);
            x04 = Integers.rotateLeft(x04 ^ (x08 += x12), 7);
            x13 = Integers.rotateLeft(x13 ^ (x01 += x05), 16);
            x05 = Integers.rotateLeft(x05 ^ (x09 += x13), 12);
            x13 = Integers.rotateLeft(x13 ^ (x01 += x05), 8);
            x05 = Integers.rotateLeft(x05 ^ (x09 += x13), 7);
            x14 = Integers.rotateLeft(x14 ^ (x02 += x06), 16);
            x06 = Integers.rotateLeft(x06 ^ (x10 += x14), 12);
            x14 = Integers.rotateLeft(x14 ^ (x02 += x06), 8);
            x06 = Integers.rotateLeft(x06 ^ (x10 += x14), 7);
            x15 = Integers.rotateLeft(x15 ^ (x03 += x07), 16);
            x07 = Integers.rotateLeft(x07 ^ (x11 += x15), 12);
            x15 = Integers.rotateLeft(x15 ^ (x03 += x07), 8);
            x07 = Integers.rotateLeft(x07 ^ (x11 += x15), 7);
            x15 = Integers.rotateLeft(x15 ^ (x00 += x05), 16);
            x05 = Integers.rotateLeft(x05 ^ (x10 += x15), 12);
            x15 = Integers.rotateLeft(x15 ^ (x00 += x05), 8);
            x05 = Integers.rotateLeft(x05 ^ (x10 += x15), 7);
            x12 = Integers.rotateLeft(x12 ^ (x01 += x06), 16);
            x06 = Integers.rotateLeft(x06 ^ (x11 += x12), 12);
            x12 = Integers.rotateLeft(x12 ^ (x01 += x06), 8);
            x06 = Integers.rotateLeft(x06 ^ (x11 += x12), 7);
            x13 = Integers.rotateLeft(x13 ^ (x02 += x07), 16);
            x07 = Integers.rotateLeft(x07 ^ (x08 += x13), 12);
            x13 = Integers.rotateLeft(x13 ^ (x02 += x07), 8);
            x07 = Integers.rotateLeft(x07 ^ (x08 += x13), 7);
            x14 = Integers.rotateLeft(x14 ^ (x03 += x04), 16);
            x04 = Integers.rotateLeft(x04 ^ (x09 += x14), 12);
            x14 = Integers.rotateLeft(x14 ^ (x03 += x04), 8);
            x04 = Integers.rotateLeft(x04 ^ (x09 += x14), 7);
        }
        x[0] = x00 + input[0];
        x[1] = x01 + input[1];
        x[2] = x02 + input[2];
        x[3] = x03 + input[3];
        x[4] = x04 + input[4];
        x[5] = x05 + input[5];
        x[6] = x06 + input[6];
        x[7] = x07 + input[7];
        x[8] = x08 + input[8];
        x[9] = x09 + input[9];
        x[10] = x10 + input[10];
        x[11] = x11 + input[11];
        x[12] = x12 + input[12];
        x[13] = x13 + input[13];
        x[14] = x14 + input[14];
        x[15] = x15 + input[15];
    }
}

