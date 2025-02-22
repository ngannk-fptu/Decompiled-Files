/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.MaxBytesExceededException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.SkippingStreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

public class Salsa20Engine
implements SkippingStreamCipher {
    public static final int DEFAULT_ROUNDS = 20;
    private static final int STATE_SIZE = 16;
    private static final int[] TAU_SIGMA = Pack.littleEndianToInt(Strings.toByteArray("expand 16-byte kexpand 32-byte k"), 0, 8);
    protected int rounds;
    private int index = 0;
    protected int[] engineState = new int[16];
    protected int[] x = new int[16];
    private byte[] keyStream = new byte[64];
    private boolean initialised = false;
    private int cW0;
    private int cW1;
    private int cW2;

    protected void packTauOrSigma(int keyLength, int[] state, int stateOffset) {
        int tsOff = (keyLength - 16) / 4;
        state[stateOffset] = TAU_SIGMA[tsOff];
        state[stateOffset + 1] = TAU_SIGMA[tsOff + 1];
        state[stateOffset + 2] = TAU_SIGMA[tsOff + 2];
        state[stateOffset + 3] = TAU_SIGMA[tsOff + 3];
    }

    public Salsa20Engine() {
        this(20);
    }

    public Salsa20Engine(int rounds) {
        if (rounds <= 0 || (rounds & 1) != 0) {
            throw new IllegalArgumentException("'rounds' must be a positive, even number");
        }
        this.rounds = rounds;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) {
        if (!(params instanceof ParametersWithIV)) {
            throw new IllegalArgumentException(this.getAlgorithmName() + " Init parameters must include an IV");
        }
        ParametersWithIV ivParams = (ParametersWithIV)params;
        byte[] iv = ivParams.getIV();
        if (iv == null || iv.length != this.getNonceSize()) {
            throw new IllegalArgumentException(this.getAlgorithmName() + " requires exactly " + this.getNonceSize() + " bytes of IV");
        }
        CipherParameters keyParam = ivParams.getParameters();
        if (keyParam == null) {
            if (!this.initialised) {
                throw new IllegalStateException(this.getAlgorithmName() + " KeyParameter can not be null for first initialisation");
            }
            this.setKey(null, iv);
        } else if (keyParam instanceof KeyParameter) {
            byte[] key = ((KeyParameter)keyParam).getKey();
            this.setKey(key, iv);
            CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), key.length * 8, params, Utils.getPurpose(forEncryption)));
        } else {
            throw new IllegalArgumentException(this.getAlgorithmName() + " Init parameters must contain a KeyParameter (or null for re-init)");
        }
        this.reset();
        this.initialised = true;
    }

    protected int getNonceSize() {
        return 8;
    }

    @Override
    public String getAlgorithmName() {
        String name = "Salsa20";
        if (this.rounds != 20) {
            name = name + "/" + this.rounds;
        }
        return name;
    }

    @Override
    public byte returnByte(byte in) {
        if (this.limitExceeded()) {
            throw new MaxBytesExceededException("2^70 byte limit per IV; Change IV");
        }
        byte out = (byte)(this.keyStream[this.index] ^ in);
        this.index = this.index + 1 & 0x3F;
        if (this.index == 0) {
            this.advanceCounter();
            this.generateKeyStream(this.keyStream);
        }
        return out;
    }

    protected void advanceCounter(long diff) {
        int hi = (int)(diff >>> 32);
        int lo = (int)diff;
        if (hi > 0) {
            this.engineState[9] = this.engineState[9] + hi;
        }
        int oldState = this.engineState[8];
        this.engineState[8] = this.engineState[8] + lo;
        if (oldState != 0 && this.engineState[8] < oldState) {
            this.engineState[9] = this.engineState[9] + 1;
        }
    }

    protected void advanceCounter() {
        this.engineState[8] = this.engineState[8] + 1;
        if (this.engineState[8] == 0) {
            this.engineState[9] = this.engineState[9] + 1;
        }
    }

    protected void retreatCounter(long diff) {
        int hi = (int)(diff >>> 32);
        int lo = (int)diff;
        if (hi != 0) {
            if (((long)this.engineState[9] & 0xFFFFFFFFL) >= ((long)hi & 0xFFFFFFFFL)) {
                this.engineState[9] = this.engineState[9] - hi;
            } else {
                throw new IllegalStateException("attempt to reduce counter past zero.");
            }
        }
        if (((long)this.engineState[8] & 0xFFFFFFFFL) >= ((long)lo & 0xFFFFFFFFL)) {
            this.engineState[8] = this.engineState[8] - lo;
        } else if (this.engineState[9] != 0) {
            this.engineState[9] = this.engineState[9] - 1;
            this.engineState[8] = this.engineState[8] - lo;
        } else {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
    }

    protected void retreatCounter() {
        if (this.engineState[8] == 0 && this.engineState[9] == 0) {
            throw new IllegalStateException("attempt to reduce counter past zero.");
        }
        this.engineState[8] = this.engineState[8] - 1;
        if (this.engineState[8] == -1) {
            this.engineState[9] = this.engineState[9] - 1;
        }
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) {
        if (!this.initialised) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (inOff + len > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + len > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.limitExceeded(len)) {
            throw new MaxBytesExceededException("2^70 byte limit per IV would be exceeded; Change IV");
        }
        for (int i = 0; i < len; ++i) {
            out[i + outOff] = (byte)(this.keyStream[this.index] ^ in[i + inOff]);
            this.index = this.index + 1 & 0x3F;
            if (this.index != 0) continue;
            this.advanceCounter();
            this.generateKeyStream(this.keyStream);
        }
        return len;
    }

    @Override
    public long skip(long numberOfBytes) {
        if (numberOfBytes >= 0L) {
            long remaining = numberOfBytes;
            if (remaining >= 64L) {
                long count = remaining / 64L;
                this.advanceCounter(count);
                remaining -= count * 64L;
            }
            int oldIndex = this.index;
            this.index = this.index + (int)remaining & 0x3F;
            if (this.index < oldIndex) {
                this.advanceCounter();
            }
        } else {
            long remaining = -numberOfBytes;
            if (remaining >= 64L) {
                long count = remaining / 64L;
                this.retreatCounter(count);
                remaining -= count * 64L;
            }
            for (long i = 0L; i < remaining; ++i) {
                if (this.index == 0) {
                    this.retreatCounter();
                }
                this.index = this.index - 1 & 0x3F;
            }
        }
        this.generateKeyStream(this.keyStream);
        return numberOfBytes;
    }

    @Override
    public long seekTo(long position) {
        this.reset();
        return this.skip(position);
    }

    @Override
    public long getPosition() {
        return this.getCounter() * 64L + (long)this.index;
    }

    @Override
    public void reset() {
        this.index = 0;
        this.resetLimitCounter();
        this.resetCounter();
        this.generateKeyStream(this.keyStream);
    }

    protected long getCounter() {
        return (long)this.engineState[9] << 32 | (long)this.engineState[8] & 0xFFFFFFFFL;
    }

    protected void resetCounter() {
        this.engineState[9] = 0;
        this.engineState[8] = 0;
    }

    protected void setKey(byte[] keyBytes, byte[] ivBytes) {
        if (keyBytes != null) {
            if (keyBytes.length != 16 && keyBytes.length != 32) {
                throw new IllegalArgumentException(this.getAlgorithmName() + " requires 128 bit or 256 bit key");
            }
            int tsOff = (keyBytes.length - 16) / 4;
            this.engineState[0] = TAU_SIGMA[tsOff];
            this.engineState[5] = TAU_SIGMA[tsOff + 1];
            this.engineState[10] = TAU_SIGMA[tsOff + 2];
            this.engineState[15] = TAU_SIGMA[tsOff + 3];
            Pack.littleEndianToInt(keyBytes, 0, this.engineState, 1, 4);
            Pack.littleEndianToInt(keyBytes, keyBytes.length - 16, this.engineState, 11, 4);
        }
        Pack.littleEndianToInt(ivBytes, 0, this.engineState, 6, 2);
    }

    protected void generateKeyStream(byte[] output) {
        Salsa20Engine.salsaCore(this.rounds, this.engineState, this.x);
        Pack.intToLittleEndian(this.x, output, 0);
    }

    public static void salsaCore(int rounds, int[] input, int[] x) {
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
            x08 ^= Integers.rotateLeft((x04 ^= Integers.rotateLeft(x00 + x12, 7)) + x00, 9);
            x00 ^= Integers.rotateLeft((x12 ^= Integers.rotateLeft(x08 + x04, 13)) + x08, 18);
            x13 ^= Integers.rotateLeft((x09 ^= Integers.rotateLeft(x05 + x01, 7)) + x05, 9);
            x05 ^= Integers.rotateLeft((x01 ^= Integers.rotateLeft(x13 + x09, 13)) + x13, 18);
            x02 ^= Integers.rotateLeft((x14 ^= Integers.rotateLeft(x10 + x06, 7)) + x10, 9);
            x10 ^= Integers.rotateLeft((x06 ^= Integers.rotateLeft(x02 + x14, 13)) + x02, 18);
            x07 ^= Integers.rotateLeft((x03 ^= Integers.rotateLeft(x15 + x11, 7)) + x15, 9);
            x15 ^= Integers.rotateLeft((x11 ^= Integers.rotateLeft(x07 + x03, 13)) + x07, 18);
            x02 ^= Integers.rotateLeft((x01 ^= Integers.rotateLeft(x00 + x03, 7)) + x00, 9);
            x00 ^= Integers.rotateLeft((x03 ^= Integers.rotateLeft(x02 + x01, 13)) + x02, 18);
            x07 ^= Integers.rotateLeft((x06 ^= Integers.rotateLeft(x05 + x04, 7)) + x05, 9);
            x05 ^= Integers.rotateLeft((x04 ^= Integers.rotateLeft(x07 + x06, 13)) + x07, 18);
            x08 ^= Integers.rotateLeft((x11 ^= Integers.rotateLeft(x10 + x09, 7)) + x10, 9);
            x10 ^= Integers.rotateLeft((x09 ^= Integers.rotateLeft(x08 + x11, 13)) + x08, 18);
            x13 ^= Integers.rotateLeft((x12 ^= Integers.rotateLeft(x15 + x14, 7)) + x15, 9);
            x15 ^= Integers.rotateLeft((x14 ^= Integers.rotateLeft(x13 + x12, 13)) + x13, 18);
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

    private void resetLimitCounter() {
        this.cW0 = 0;
        this.cW1 = 0;
        this.cW2 = 0;
    }

    private boolean limitExceeded() {
        if (++this.cW0 == 0 && ++this.cW1 == 0) {
            return (++this.cW2 & 0x20) != 0;
        }
        return false;
    }

    private boolean limitExceeded(int len) {
        this.cW0 += len;
        if (this.cW0 < len && this.cW0 >= 0 && ++this.cW1 == 0) {
            return (++this.cW2 & 0x20) != 0;
        }
        return false;
    }
}

