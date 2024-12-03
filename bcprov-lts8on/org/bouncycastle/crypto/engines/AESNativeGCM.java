/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMModeCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.dispose.NativeDisposer;
import org.bouncycastle.util.dispose.NativeReference;

class AESNativeGCM
implements GCMModeCipher {
    private GCMRefWrapper refWrapper;
    private int macSize = 0;
    private byte[] nonce;
    private byte[] lastKey;
    private byte[] initialAssociatedText;
    private boolean forEncryption = false;
    private boolean initialised = false;
    private byte[] keptMac = null;
    private boolean finalCalled = false;

    AESNativeGCM() {
    }

    @Override
    public BlockCipher getUnderlyingCipher() {
        MultiBlockCipher engine = AESEngine.newInstance();
        if (this.lastKey != null) {
            engine.init(true, new KeyParameter(this.lastKey));
        }
        return engine;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        KeyParameter keyParam;
        this.forEncryption = forEncryption;
        byte[] newNonce = null;
        this.keptMac = null;
        if (params instanceof AEADParameters) {
            AEADParameters param = (AEADParameters)params;
            newNonce = param.getNonce();
            this.initialAssociatedText = param.getAssociatedText();
            int macSizeBits = param.getMacSize();
            if (macSizeBits < 32 || macSizeBits > 128 || macSizeBits % 8 != 0) {
                throw new IllegalArgumentException("invalid value for MAC size: " + macSizeBits);
            }
            this.macSize = macSizeBits;
            keyParam = param.getKey();
        } else if (params instanceof ParametersWithIV) {
            ParametersWithIV param = (ParametersWithIV)params;
            newNonce = param.getIV();
            this.initialAssociatedText = null;
            this.macSize = 128;
            keyParam = (KeyParameter)param.getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to GCM");
        }
        if (newNonce == null || newNonce.length < 12) {
            throw new IllegalArgumentException("IV must be at least 12 bytes");
        }
        if (forEncryption && this.nonce != null && Arrays.areEqual(this.nonce, newNonce)) {
            if (keyParam == null) {
                throw new IllegalArgumentException("cannot reuse nonce for GCM encryption");
            }
            if (this.lastKey != null && Arrays.areEqual(this.lastKey, keyParam.getKey())) {
                throw new IllegalArgumentException("cannot reuse nonce for GCM encryption");
            }
        }
        this.nonce = newNonce;
        if (keyParam != null) {
            this.lastKey = keyParam.getKey();
        }
        switch (this.lastKey.length) {
            case 16: 
            case 24: 
            case 32: {
                break;
            }
            default: {
                throw new IllegalStateException("key must be only 16,24,or 32 bytes long.");
            }
        }
        this.initRef(this.lastKey.length);
        AESNativeGCM.initNative(this.refWrapper.getReference(), forEncryption, this.lastKey, this.nonce, this.initialAssociatedText, this.macSize);
        this.finalCalled = false;
        this.initialised = true;
    }

    private void initRef(int keySize) {
        this.refWrapper = new GCMRefWrapper(AESNativeGCM.makeInstance(keySize, this.forEncryption));
    }

    @Override
    public String getAlgorithmName() {
        return "AES/GCM";
    }

    @Override
    public void processAADByte(byte in) {
        AESNativeGCM.processAADByte(this.refWrapper.getReference(), in);
    }

    @Override
    public void processAADBytes(byte[] in, int inOff, int len) {
        if (this.refWrapper == null) {
            throw new IllegalStateException("GCM is uninitialized");
        }
        AESNativeGCM.processAADBytes(this.refWrapper.getReference(), in, inOff, len);
    }

    @Override
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
        if (this.refWrapper == null) {
            throw new IllegalStateException("GCM is uninitialized");
        }
        return AESNativeGCM.processByte(this.refWrapper.getReference(), in, out, outOff);
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        if (this.refWrapper == null) {
            throw new IllegalStateException("GCM is uninitialized");
        }
        return AESNativeGCM.processBytes(this.refWrapper.getReference(), in, inOff, len, out, outOff);
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        this.checkStatus();
        int len = AESNativeGCM.doFinal(this.refWrapper.getReference(), out, outOff);
        this.resetKeepMac();
        return len;
    }

    @Override
    public byte[] getMac() {
        if (this.keptMac != null) {
            return Arrays.clone(this.keptMac);
        }
        return AESNativeGCM.getMac(this.refWrapper.getReference());
    }

    @Override
    public int getUpdateOutputSize(int len) {
        return AESNativeGCM.getUpdateOutputSize(this.refWrapper.getReference(), len);
    }

    @Override
    public int getOutputSize(int len) {
        return AESNativeGCM.getOutputSize(this.refWrapper.getReference(), len);
    }

    @Override
    public void reset() {
        if (this.refWrapper == null) {
            return;
        }
        this.reset(this.refWrapper.getReference());
        this.initialised = false;
    }

    private void resetKeepMac() {
        if (this.refWrapper == null) {
            return;
        }
        this.keptMac = this.getMac();
        this.reset(this.refWrapper.getReference());
        this.initialised = false;
    }

    private void checkStatus() {
        if (!this.initialised) {
            if (this.forEncryption) {
                throw new IllegalStateException("GCM cipher cannot be reused for encryption");
            }
            throw new IllegalStateException("GCM cipher needs to be initialised");
        }
    }

    private native void reset(long var1);

    static native void initNative(long var0, boolean var2, byte[] var3, byte[] var4, byte[] var5, int var6);

    static native long makeInstance(int var0, boolean var1);

    static native void dispose(long var0);

    private static native void processAADByte(long var0, byte var2);

    private static native void processAADBytes(long var0, byte[] var2, int var3, int var4);

    private static native int processByte(long var0, byte var2, byte[] var3, int var4);

    private static native int processBytes(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6);

    private static native int doFinal(long var0, byte[] var2, int var3);

    private static native int getUpdateOutputSize(long var0, int var2);

    private static native int getOutputSize(long var0, int var2);

    public static native byte[] getMac(long var0);

    void setBlocksRemainingDown(long value) {
        this.setBlocksRemainingDown(this.refWrapper.getReference(), value);
    }

    private native void setBlocksRemainingDown(long var1, long var3);

    public String toString() {
        if (this.lastKey != null) {
            return "GCM[Native](AES[Native](" + this.lastKey.length * 8 + "))";
        }
        return "GCM[Native](AES[Native](not initialized))";
    }

    private static class Disposer
    extends NativeDisposer {
        Disposer(long ref) {
            super(ref);
        }

        @Override
        protected void dispose(long reference) {
            AESNativeGCM.dispose(reference);
        }
    }

    private static class GCMRefWrapper
    extends NativeReference {
        public GCMRefWrapper(long reference) {
            super(reference, "GCM");
        }

        @Override
        public Runnable createAction() {
            return new Disposer(this.reference);
        }
    }
}

