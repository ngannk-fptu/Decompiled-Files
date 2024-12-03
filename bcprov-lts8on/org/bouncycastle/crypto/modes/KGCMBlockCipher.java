/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.KCTRBlockCipher;
import org.bouncycastle.crypto.modes.kgcm.KGCMMultiplier;
import org.bouncycastle.crypto.modes.kgcm.Tables16kKGCMMultiplier_512;
import org.bouncycastle.crypto.modes.kgcm.Tables4kKGCMMultiplier_128;
import org.bouncycastle.crypto.modes.kgcm.Tables8kKGCMMultiplier_256;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class KGCMBlockCipher
implements AEADBlockCipher {
    private static final int MIN_MAC_BITS = 64;
    private BlockCipher engine;
    private BufferedBlockCipher ctrEngine;
    private int macSize;
    private boolean forEncryption;
    private byte[] initialAssociatedText;
    private byte[] macBlock;
    private byte[] iv;
    private KGCMMultiplier multiplier;
    private long[] b;
    private final int blockSize;
    private ExposedByteArrayOutputStream associatedText = new ExposedByteArrayOutputStream();
    private ExposedByteArrayOutputStream data = new ExposedByteArrayOutputStream();

    private static KGCMMultiplier createDefaultMultiplier(int blockSize) {
        switch (blockSize) {
            case 16: {
                return new Tables4kKGCMMultiplier_128();
            }
            case 32: {
                return new Tables8kKGCMMultiplier_256();
            }
            case 64: {
                return new Tables16kKGCMMultiplier_512();
            }
        }
        throw new IllegalArgumentException("Only 128, 256, and 512 -bit block sizes supported");
    }

    public KGCMBlockCipher(BlockCipher dstu7624Engine) {
        this.engine = dstu7624Engine;
        this.ctrEngine = new DefaultBufferedBlockCipher(new KCTRBlockCipher(this.engine));
        this.macSize = -1;
        this.blockSize = this.engine.getBlockSize();
        this.initialAssociatedText = new byte[this.blockSize];
        this.iv = new byte[this.blockSize];
        this.multiplier = KGCMBlockCipher.createDefaultMultiplier(this.blockSize);
        this.b = new long[this.blockSize >>> 3];
        this.macBlock = null;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        KeyParameter engineParam;
        this.forEncryption = forEncryption;
        if (params instanceof AEADParameters) {
            AEADParameters param = (AEADParameters)params;
            byte[] iv = param.getNonce();
            int diff = this.iv.length - iv.length;
            Arrays.fill(this.iv, (byte)0);
            System.arraycopy(iv, 0, this.iv, diff, iv.length);
            this.initialAssociatedText = param.getAssociatedText();
            int macSizeBits = param.getMacSize();
            if (macSizeBits < 64 || macSizeBits > this.blockSize << 3 || (macSizeBits & 7) != 0) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + macSizeBits);
            }
            this.macSize = macSizeBits >>> 3;
            engineParam = param.getKey();
            if (this.initialAssociatedText != null) {
                this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
            }
        } else if (params instanceof ParametersWithIV) {
            ParametersWithIV param = (ParametersWithIV)params;
            byte[] iv = param.getIV();
            int diff = this.iv.length - iv.length;
            Arrays.fill(this.iv, (byte)0);
            System.arraycopy(iv, 0, this.iv, diff, iv.length);
            this.initialAssociatedText = null;
            this.macSize = this.blockSize;
            engineParam = (KeyParameter)param.getParameters();
        } else {
            throw new IllegalArgumentException("Invalid parameter passed");
        }
        this.macBlock = new byte[this.blockSize];
        this.ctrEngine.init(true, new ParametersWithIV(engineParam, this.iv));
        this.engine.init(true, engineParam);
    }

    @Override
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName() + "/KGCM";
    }

    @Override
    public BlockCipher getUnderlyingCipher() {
        return this.engine;
    }

    @Override
    public void processAADByte(byte in) {
        this.associatedText.write(in);
    }

    @Override
    public void processAADBytes(byte[] in, int inOff, int len) {
        this.associatedText.write(in, inOff, len);
    }

    private void processAAD(byte[] authText, int authOff, int len) {
        int end = authOff + len;
        for (int pos = authOff; pos < end; pos += this.blockSize) {
            KGCMBlockCipher.xorWithInput(this.b, authText, pos);
            this.multiplier.multiplyH(this.b);
        }
    }

    @Override
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        this.data.write(in);
        return 0;
    }

    @Override
    public int processBytes(byte[] in, int inOff, int inLen, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (in.length < inOff + inLen) {
            throw new DataLengthException("input buffer too short");
        }
        this.data.write(in, inOff, inLen);
        return 0;
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        int resultLen;
        int len = this.data.size();
        if (!this.forEncryption && len < this.macSize) {
            throw new InvalidCipherTextException("data too short");
        }
        byte[] temp = new byte[this.blockSize];
        this.engine.processBlock(temp, 0, temp, 0);
        long[] H = new long[this.blockSize >>> 3];
        Pack.littleEndianToLong(temp, 0, H);
        this.multiplier.init(H);
        Arrays.fill(temp, (byte)0);
        Arrays.fill(H, 0L);
        int lenAAD = this.associatedText.size();
        if (lenAAD > 0) {
            this.processAAD(this.associatedText.getBuffer(), 0, lenAAD);
        }
        if (this.forEncryption) {
            if (out.length - outOff - this.macSize < len) {
                throw new OutputLengthException("Output buffer too short");
            }
            resultLen = this.ctrEngine.processBytes(this.data.getBuffer(), 0, len, out, outOff);
            resultLen += this.ctrEngine.doFinal(out, outOff + resultLen);
            this.calculateMac(out, outOff, len, lenAAD);
        } else {
            int ctLen = len - this.macSize;
            if (out.length - outOff < ctLen) {
                throw new OutputLengthException("Output buffer too short");
            }
            this.calculateMac(this.data.getBuffer(), 0, ctLen, lenAAD);
            resultLen = this.ctrEngine.processBytes(this.data.getBuffer(), 0, ctLen, out, outOff);
            resultLen += this.ctrEngine.doFinal(out, outOff + resultLen);
        }
        if (this.macBlock == null) {
            throw new IllegalStateException("mac is not calculated");
        }
        if (this.forEncryption) {
            System.arraycopy(this.macBlock, 0, out, outOff + resultLen, this.macSize);
            this.reset();
            return resultLen + this.macSize;
        }
        byte[] mac = new byte[this.macSize];
        System.arraycopy(this.data.getBuffer(), len - this.macSize, mac, 0, this.macSize);
        byte[] calculatedMac = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, calculatedMac, 0, this.macSize);
        if (!Arrays.constantTimeAreEqual(mac, calculatedMac)) {
            throw new InvalidCipherTextException("mac verification failed");
        }
        this.reset();
        return resultLen;
    }

    @Override
    public byte[] getMac() {
        byte[] mac = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, mac, 0, this.macSize);
        return mac;
    }

    @Override
    public int getUpdateOutputSize(int len) {
        return 0;
    }

    @Override
    public int getOutputSize(int len) {
        int totalData = len + this.data.size();
        if (this.forEncryption) {
            return totalData + this.macSize;
        }
        return totalData < this.macSize ? 0 : totalData - this.macSize;
    }

    @Override
    public void reset() {
        Arrays.fill(this.b, 0L);
        this.engine.reset();
        this.data.reset();
        this.associatedText.reset();
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    private void calculateMac(byte[] input, int inOff, int len, int lenAAD) {
        int end = inOff + len;
        for (int pos = inOff; pos < end; pos += this.blockSize) {
            KGCMBlockCipher.xorWithInput(this.b, input, pos);
            this.multiplier.multiplyH(this.b);
        }
        long lambda_o = ((long)lenAAD & 0xFFFFFFFFL) << 3;
        long lambda_c = ((long)len & 0xFFFFFFFFL) << 3;
        this.b[0] = this.b[0] ^ lambda_o;
        int n = this.blockSize >>> 4;
        this.b[n] = this.b[n] ^ lambda_c;
        this.macBlock = Pack.longToLittleEndian(this.b);
        this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
    }

    private static void xorWithInput(long[] z, byte[] buf, int off) {
        int i = 0;
        while (i < z.length) {
            int n = i++;
            z[n] = z[n] ^ Pack.littleEndianToLong(buf, off);
            off += 8;
        }
    }

    private static class ExposedByteArrayOutputStream
    extends ByteArrayOutputStream {
        public byte[] getBuffer() {
            return this.buf;
        }
    }
}

