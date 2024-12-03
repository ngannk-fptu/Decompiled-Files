/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.util.Pack;

public class ChaCha7539Engine
extends Salsa20Engine {
    @Override
    public String getAlgorithmName() {
        return "ChaCha7539";
    }

    @Override
    protected int getNonceSize() {
        return 12;
    }

    @Override
    protected void advanceCounter(long diff) {
        int hi = (int)(diff >>> 32);
        int lo = (int)diff;
        if (hi > 0) {
            throw new IllegalStateException("attempt to increase counter past 2^32.");
        }
        int oldState = this.engineState[12];
        this.engineState[12] = this.engineState[12] + lo;
        if (oldState != 0 && this.engineState[12] < oldState) {
            throw new IllegalStateException("attempt to increase counter past 2^32.");
        }
    }

    @Override
    protected void advanceCounter() {
        this.engineState[12] = this.engineState[12] + 1;
        if (this.engineState[12] == 0) {
            throw new IllegalStateException("attempt to increase counter past 2^32.");
        }
    }

    @Override
    protected void retreatCounter(long diff) {
        int hi = (int)(diff >>> 32);
        int lo = (int)diff;
        if (hi != 0) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
        if (((long)this.engineState[12] & 0xFFFFFFFFL) < ((long)lo & 0xFFFFFFFFL)) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
        this.engineState[12] = this.engineState[12] - lo;
    }

    @Override
    protected void retreatCounter() {
        if (this.engineState[12] == 0) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
        this.engineState[12] = this.engineState[12] - 1;
    }

    @Override
    protected long getCounter() {
        return (long)this.engineState[12] & 0xFFFFFFFFL;
    }

    @Override
    protected void resetCounter() {
        this.engineState[12] = 0;
    }

    @Override
    protected void setKey(byte[] keyBytes, byte[] ivBytes) {
        if (keyBytes != null) {
            if (keyBytes.length != 32) {
                throw new IllegalArgumentException(this.getAlgorithmName() + " requires 256 bit key");
            }
            this.packTauOrSigma(keyBytes.length, this.engineState, 0);
            Pack.littleEndianToInt(keyBytes, 0, this.engineState, 4, 8);
        }
        Pack.littleEndianToInt(ivBytes, 0, this.engineState, 13, 3);
    }

    @Override
    protected void generateKeyStream(byte[] output) {
        ChaChaEngine.chachaCore(this.rounds, this.engineState, this.x);
        Pack.intToLittleEndian(this.x, output, 0);
    }
}

