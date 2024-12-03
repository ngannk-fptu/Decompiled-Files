/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CTRModeCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.dispose.NativeDisposer;
import org.bouncycastle.util.dispose.NativeReference;

public class AESNativeCTR
implements CTRModeCipher {
    private CTRRefWrapper referenceWrapper = null;
    private int keyLen;
    private byte[] lastKey;

    @Override
    public BlockCipher getUnderlyingCipher() {
        MultiBlockCipher engine = AESEngine.newInstance();
        if (this.lastKey != null) {
            engine.init(true, new KeyParameter(this.lastKey));
        }
        return engine;
    }

    @Override
    public int getBlockSize() {
        return 16;
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.referenceWrapper == null) {
            throw new IllegalStateException("not initialized");
        }
        return AESNativeCTR.processBytes(this.referenceWrapper.getReference(), in, inOff, this.getBlockSize(), out, outOff);
    }

    @Override
    public int getMultiBlockSize() {
        return AESNativeCTR.getMultiBlockSize(this.referenceWrapper.getReference());
    }

    @Override
    public int processBlocks(byte[] in, int inOff, int blockCount, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        int extent = this.getBlockSize() * blockCount;
        if (this.referenceWrapper == null) {
            throw new IllegalStateException("not initialized");
        }
        return AESNativeCTR.processBytes(this.referenceWrapper.getReference(), in, inOff, extent, out, outOff);
    }

    @Override
    public long skip(long numberOfBytes) {
        if (this.referenceWrapper == null) {
            throw new IllegalStateException("not initialized");
        }
        return AESNativeCTR.skip(this.referenceWrapper.getReference(), numberOfBytes);
    }

    @Override
    public long seekTo(long position) {
        if (this.referenceWrapper == null) {
            throw new IllegalStateException("not initialized");
        }
        return AESNativeCTR.seekTo(this.referenceWrapper.getReference(), position);
    }

    @Override
    public long getPosition() {
        if (this.referenceWrapper == null) {
            throw new IllegalStateException("not initialized");
        }
        return AESNativeCTR.getPosition(this.referenceWrapper.getReference());
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        if (params instanceof ParametersWithIV) {
            int maxCounterSize;
            ParametersWithIV ivParam = (ParametersWithIV)params;
            byte[] iv = ivParam.getIV();
            int blockSize = this.getBlockSize();
            int n = maxCounterSize = 8 > blockSize / 2 ? blockSize / 2 : 8;
            if (blockSize - iv.length > maxCounterSize) {
                throw new IllegalArgumentException("CTR mode requires IV of at least: " + (blockSize - maxCounterSize) + " bytes.");
            }
            if (this.referenceWrapper == null) {
                this.referenceWrapper = new CTRRefWrapper(AESNativeCTR.makeCTRInstance());
            }
            if (ivParam.getParameters() == null) {
                AESNativeCTR.init(this.referenceWrapper.getReference(), null, iv);
            } else {
                byte[] key = ((KeyParameter)ivParam.getParameters()).getKey();
                switch (key.length) {
                    case 16: 
                    case 24: 
                    case 32: {
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("invalid key length, key must be 16,24 or 32 bytes");
                    }
                }
                AESNativeCTR.init(this.referenceWrapper.getReference(), key, iv);
                this.lastKey = Arrays.clone(key);
                this.keyLen = key.length * 8;
            }
        } else {
            throw new IllegalArgumentException("CTR mode requires ParametersWithIV");
        }
        this.reset();
    }

    static native long makeCTRInstance();

    @Override
    public String getAlgorithmName() {
        return "AES/CTR";
    }

    @Override
    public byte returnByte(byte in) {
        if (this.referenceWrapper == null) {
            throw new IllegalStateException("not initialized");
        }
        return AESNativeCTR.returnByte(this.referenceWrapper.getReference(), in);
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        if (this.referenceWrapper == null) {
            throw new IllegalStateException("not initialized");
        }
        return AESNativeCTR.processBytes(this.referenceWrapper.getReference(), in, inOff, len, out, outOff);
    }

    @Override
    public void reset() {
        if (this.referenceWrapper == null) {
            return;
        }
        AESNativeCTR.reset(this.referenceWrapper.getReference());
    }

    private static native long getPosition(long var0);

    private static native int getMultiBlockSize(long var0);

    private static native long skip(long var0, long var2);

    private static native long seekTo(long var0, long var2);

    static native void init(long var0, byte[] var2, byte[] var3);

    private static native byte returnByte(long var0, byte var2);

    private static native int processBytes(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6);

    private static native void reset(long var0);

    static native void dispose(long var0);

    public String toString() {
        if (this.keyLen > 0) {
            return "CTR[Native](AES[Native](" + this.keyLen + "))";
        }
        return "CTR[Native](AES[Native](not initialized))";
    }

    private static class CTRRefWrapper
    extends NativeReference {
        public CTRRefWrapper(long reference) {
            super(reference, "CTR");
        }

        @Override
        public Runnable createAction() {
            return new Disposer(this.reference);
        }
    }

    private static class Disposer
    extends NativeDisposer {
        Disposer(long ref) {
            super(ref);
        }

        @Override
        protected void dispose(long reference) {
            AESNativeCTR.dispose(reference);
        }
    }
}

