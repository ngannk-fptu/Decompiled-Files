/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.ChaCha7539Engine;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class ChaCha20Poly1305
implements AEADCipher {
    private static final int BUF_SIZE = 64;
    private static final int KEY_SIZE = 32;
    private static final int NONCE_SIZE = 12;
    private static final int MAC_SIZE = 16;
    private static final byte[] ZEROES = new byte[15];
    private static final long AAD_LIMIT = -1L;
    private static final long DATA_LIMIT = 274877906880L;
    private final ChaCha7539Engine chacha20;
    private final Mac poly1305;
    private final byte[] key = new byte[32];
    private final byte[] nonce = new byte[12];
    private final byte[] buf = new byte[80];
    private final byte[] mac = new byte[16];
    private byte[] initialAAD;
    private long aadCount;
    private long dataCount;
    private int state = 0;
    private int bufPos;

    public ChaCha20Poly1305() {
        this(new Poly1305());
    }

    public ChaCha20Poly1305(Mac poly1305) {
        if (null == poly1305) {
            throw new NullPointerException("'poly1305' cannot be null");
        }
        if (16 != poly1305.getMacSize()) {
            throw new IllegalArgumentException("'poly1305' must be a 128-bit MAC");
        }
        this.chacha20 = new ChaCha7539Engine();
        this.poly1305 = poly1305;
    }

    @Override
    public String getAlgorithmName() {
        return "ChaCha20Poly1305";
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        ParametersWithIV chacha20Params;
        byte[] initNonce;
        KeyParameter initKeyParam;
        if (params instanceof AEADParameters) {
            AEADParameters aeadParams = (AEADParameters)params;
            int macSizeBits = aeadParams.getMacSize();
            if (128 != macSizeBits) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + macSizeBits);
            }
            initKeyParam = aeadParams.getKey();
            initNonce = aeadParams.getNonce();
            chacha20Params = new ParametersWithIV(initKeyParam, initNonce);
            this.initialAAD = aeadParams.getAssociatedText();
        } else if (params instanceof ParametersWithIV) {
            ParametersWithIV ivParams = (ParametersWithIV)params;
            initKeyParam = (KeyParameter)ivParams.getParameters();
            initNonce = ivParams.getIV();
            chacha20Params = ivParams;
            this.initialAAD = null;
        } else {
            throw new IllegalArgumentException("invalid parameters passed to ChaCha20Poly1305");
        }
        if (null == initKeyParam) {
            if (0 == this.state) {
                throw new IllegalArgumentException("Key must be specified in initial init");
            }
        } else if (32 != initKeyParam.getKeyLength()) {
            throw new IllegalArgumentException("Key must be 256 bits");
        }
        if (null == initNonce || 12 != initNonce.length) {
            throw new IllegalArgumentException("Nonce must be 96 bits");
        }
        if (0 != this.state && forEncryption && Arrays.areEqual(this.nonce, initNonce) && (null == initKeyParam || Arrays.areEqual(this.key, initKeyParam.getKey()))) {
            throw new IllegalArgumentException("cannot reuse nonce for ChaCha20Poly1305 encryption");
        }
        if (null != initKeyParam) {
            initKeyParam.copyTo(this.key, 0, 32);
        }
        System.arraycopy(initNonce, 0, this.nonce, 0, 12);
        this.chacha20.init(true, chacha20Params);
        this.state = forEncryption ? 1 : 5;
        this.reset(true, false);
    }

    @Override
    public int getOutputSize(int len) {
        int total = Math.max(0, len) + this.bufPos;
        switch (this.state) {
            case 5: 
            case 6: 
            case 7: {
                return Math.max(0, total - 16);
            }
            case 1: 
            case 2: 
            case 3: {
                return total + 16;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public int getUpdateOutputSize(int len) {
        int total = Math.max(0, len) + this.bufPos;
        switch (this.state) {
            case 5: 
            case 6: 
            case 7: {
                total = Math.max(0, total - 16);
                break;
            }
            case 1: 
            case 2: 
            case 3: {
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return total - total % 64;
    }

    @Override
    public void processAADByte(byte in) {
        this.checkAAD();
        this.aadCount = this.incrementCount(this.aadCount, 1, -1L);
        this.poly1305.update(in);
    }

    @Override
    public void processAADBytes(byte[] in, int inOff, int len) {
        if (null == in) {
            throw new NullPointerException("'in' cannot be null");
        }
        if (inOff < 0) {
            throw new IllegalArgumentException("'inOff' cannot be negative");
        }
        if (len < 0) {
            throw new IllegalArgumentException("'len' cannot be negative");
        }
        if (inOff > in.length - len) {
            throw new DataLengthException("Input buffer too short");
        }
        this.checkAAD();
        if (len > 0) {
            this.aadCount = this.incrementCount(this.aadCount, len, -1L);
            this.poly1305.update(in, inOff, len);
        }
    }

    @Override
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
        this.checkData();
        switch (this.state) {
            case 7: {
                this.buf[this.bufPos] = in;
                if (++this.bufPos == this.buf.length) {
                    this.poly1305.update(this.buf, 0, 64);
                    this.processData(this.buf, 0, 64, out, outOff);
                    System.arraycopy(this.buf, 64, this.buf, 0, 16);
                    this.bufPos = 16;
                    return 64;
                }
                return 0;
            }
            case 3: {
                this.buf[this.bufPos] = in;
                if (++this.bufPos == 64) {
                    this.processData(this.buf, 0, 64, out, outOff);
                    this.poly1305.update(out, outOff, 64);
                    this.bufPos = 0;
                    return 64;
                }
                return 0;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        if (null == in) {
            throw new NullPointerException("'in' cannot be null");
        }
        if (null == out) {
            // empty if block
        }
        if (inOff < 0) {
            throw new IllegalArgumentException("'inOff' cannot be negative");
        }
        if (len < 0) {
            throw new IllegalArgumentException("'len' cannot be negative");
        }
        if (inOff > in.length - len) {
            throw new DataLengthException("Input buffer too short");
        }
        if (outOff < 0) {
            throw new IllegalArgumentException("'outOff' cannot be negative");
        }
        this.checkData();
        int resultLen = 0;
        switch (this.state) {
            case 7: {
                for (int i = 0; i < len; ++i) {
                    this.buf[this.bufPos] = in[inOff + i];
                    if (++this.bufPos != this.buf.length) continue;
                    this.poly1305.update(this.buf, 0, 64);
                    this.processData(this.buf, 0, 64, out, outOff + resultLen);
                    System.arraycopy(this.buf, 64, this.buf, 0, 16);
                    this.bufPos = 16;
                    resultLen += 64;
                }
                break;
            }
            case 3: {
                if (this.bufPos != 0) {
                    while (len > 0) {
                        --len;
                        this.buf[this.bufPos] = in[inOff++];
                        if (++this.bufPos != 64) continue;
                        this.processData(this.buf, 0, 64, out, outOff);
                        this.poly1305.update(out, outOff, 64);
                        this.bufPos = 0;
                        resultLen = 64;
                        break;
                    }
                }
                while (len >= 64) {
                    this.processData(in, inOff, 64, out, outOff + resultLen);
                    this.poly1305.update(out, outOff + resultLen, 64);
                    inOff += 64;
                    len -= 64;
                    resultLen += 64;
                }
                if (len <= 0) break;
                System.arraycopy(in, inOff, this.buf, 0, len);
                this.bufPos = len;
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return resultLen;
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        if (null == out) {
            throw new NullPointerException("'out' cannot be null");
        }
        if (outOff < 0) {
            throw new IllegalArgumentException("'outOff' cannot be negative");
        }
        this.checkData();
        Arrays.clear(this.mac);
        int resultLen = 0;
        switch (this.state) {
            case 7: {
                if (this.bufPos < 16) {
                    throw new InvalidCipherTextException("data too short");
                }
                resultLen = this.bufPos - 16;
                if (outOff > out.length - resultLen) {
                    throw new OutputLengthException("Output buffer too short");
                }
                if (resultLen > 0) {
                    this.poly1305.update(this.buf, 0, resultLen);
                    this.processData(this.buf, 0, resultLen, out, outOff);
                }
                this.finishData(8);
                if (Arrays.constantTimeAreEqual(16, this.mac, 0, this.buf, resultLen)) break;
                throw new InvalidCipherTextException("mac check in ChaCha20Poly1305 failed");
            }
            case 3: {
                resultLen = this.bufPos + 16;
                if (outOff > out.length - resultLen) {
                    throw new OutputLengthException("Output buffer too short");
                }
                if (this.bufPos > 0) {
                    this.processData(this.buf, 0, this.bufPos, out, outOff);
                    this.poly1305.update(out, outOff, this.bufPos);
                }
                this.finishData(4);
                System.arraycopy(this.mac, 0, out, outOff + this.bufPos, 16);
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        this.reset(false, true);
        return resultLen;
    }

    @Override
    public byte[] getMac() {
        return Arrays.clone(this.mac);
    }

    @Override
    public void reset() {
        this.reset(true, true);
    }

    private void checkAAD() {
        switch (this.state) {
            case 5: {
                this.state = 6;
                break;
            }
            case 1: {
                this.state = 2;
                break;
            }
            case 2: 
            case 6: {
                break;
            }
            case 4: {
                throw new IllegalStateException("ChaCha20Poly1305 cannot be reused for encryption");
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }

    private void checkData() {
        switch (this.state) {
            case 5: 
            case 6: {
                this.finishAAD(7);
                break;
            }
            case 1: 
            case 2: {
                this.finishAAD(3);
                break;
            }
            case 3: 
            case 7: {
                break;
            }
            case 4: {
                throw new IllegalStateException("ChaCha20Poly1305 cannot be reused for encryption");
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }

    private void finishAAD(int nextState) {
        this.padMAC(this.aadCount);
        this.state = nextState;
    }

    private void finishData(int nextState) {
        this.padMAC(this.dataCount);
        byte[] lengths = new byte[16];
        Pack.longToLittleEndian(this.aadCount, lengths, 0);
        Pack.longToLittleEndian(this.dataCount, lengths, 8);
        this.poly1305.update(lengths, 0, 16);
        this.poly1305.doFinal(this.mac, 0);
        this.state = nextState;
    }

    private long incrementCount(long count, int increment, long limit) {
        if (count + Long.MIN_VALUE > limit - (long)increment + Long.MIN_VALUE) {
            throw new IllegalStateException("Limit exceeded");
        }
        return count + (long)increment;
    }

    private void initMAC() {
        byte[] firstBlock = new byte[64];
        try {
            this.chacha20.processBytes(firstBlock, 0, 64, firstBlock, 0);
            this.poly1305.init(new KeyParameter(firstBlock, 0, 32));
        }
        finally {
            Arrays.clear(firstBlock);
        }
    }

    private void padMAC(long count) {
        int partial = (int)count & 0xF;
        if (0 != partial) {
            this.poly1305.update(ZEROES, 0, 16 - partial);
        }
    }

    private void processData(byte[] in, int inOff, int inLen, byte[] out, int outOff) {
        if (outOff > out.length - inLen) {
            throw new OutputLengthException("Output buffer too short");
        }
        this.chacha20.processBytes(in, inOff, inLen, out, outOff);
        this.dataCount = this.incrementCount(this.dataCount, inLen, 274877906880L);
    }

    private void reset(boolean clearMac, boolean resetCipher) {
        Arrays.clear(this.buf);
        if (clearMac) {
            Arrays.clear(this.mac);
        }
        this.aadCount = 0L;
        this.dataCount = 0L;
        this.bufPos = 0;
        switch (this.state) {
            case 1: 
            case 5: {
                break;
            }
            case 6: 
            case 7: 
            case 8: {
                this.state = 5;
                break;
            }
            case 2: 
            case 3: 
            case 4: {
                this.state = 4;
                return;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        if (resetCipher) {
            this.chacha20.reset();
        }
        this.initMAC();
        if (null != this.initialAAD) {
            this.processAADBytes(this.initialAAD, 0, this.initialAAD.length);
        }
    }

    private static final class State {
        static final int UNINITIALIZED = 0;
        static final int ENC_INIT = 1;
        static final int ENC_AAD = 2;
        static final int ENC_DATA = 3;
        static final int ENC_FINAL = 4;
        static final int DEC_INIT = 5;
        static final int DEC_AAD = 6;
        static final int DEC_DATA = 7;
        static final int DEC_FINAL = 8;

        private State() {
        }
    }
}

