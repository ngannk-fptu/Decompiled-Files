/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.DefaultMultiBlockCipher;
import org.bouncycastle.crypto.NativeBlockCipherProvider;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.AESNativeCBC;
import org.bouncycastle.crypto.engines.AESNativeCCM;
import org.bouncycastle.crypto.engines.AESNativeCFB;
import org.bouncycastle.crypto.engines.AESNativeCTR;
import org.bouncycastle.crypto.engines.AESNativeGCM;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CBCModeCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.CCMModeCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.CFBModeCipher;
import org.bouncycastle.crypto.modes.CTRModeCipher;
import org.bouncycastle.crypto.modes.EAXBlockCipher;
import org.bouncycastle.crypto.modes.EAXModeCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.GCMModeCipher;
import org.bouncycastle.crypto.modes.GCMSIVBlockCipher;
import org.bouncycastle.crypto.modes.GCMSIVModeCipher;
import org.bouncycastle.crypto.modes.NativeCCMProvider;
import org.bouncycastle.crypto.modes.NativeEAXProvider;
import org.bouncycastle.crypto.modes.NativeGCMSIVProvider;
import org.bouncycastle.crypto.modes.NativeOCBProvider;
import org.bouncycastle.crypto.modes.OCBBlockCipher;
import org.bouncycastle.crypto.modes.OCBModeCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.dispose.NativeDisposer;
import org.bouncycastle.util.dispose.NativeReference;

class AESNativeEngine
extends DefaultMultiBlockCipher
implements NativeBlockCipherProvider,
NativeCCMProvider,
NativeEAXProvider,
NativeOCBProvider,
NativeGCMSIVProvider {
    protected NativeReference wrapper = null;
    private int keyLen = 0;

    AESNativeEngine() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 256));
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        if (params instanceof KeyParameter) {
            byte[] key = ((KeyParameter)params).getKey();
            switch (key.length) {
                case 16: 
                case 24: 
                case 32: {
                    this.wrapper = new ECBNativeRef(AESNativeEngine.makeInstance(key.length, forEncryption));
                    break;
                }
                default: {
                    throw new IllegalArgumentException("key must be 16, 24 or 32 bytes");
                }
            }
            CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), key.length * 8, params, forEncryption ? CryptoServicePurpose.ENCRYPTION : CryptoServicePurpose.DECRYPTION));
            AESNativeEngine.init(this.wrapper.getReference(), key);
            this.keyLen = key.length * 8;
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to AES init - " + params.getClass().getName());
    }

    @Override
    public String getAlgorithmName() {
        return "AES";
    }

    @Override
    public int getBlockSize() {
        return AESNativeEngine.getBlockSize(0L);
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.wrapper == null) {
            throw new IllegalStateException("not initialized");
        }
        return AESNativeEngine.process(this.wrapper.getReference(), in, inOff, 1, out, outOff);
    }

    @Override
    public int getMultiBlockSize() {
        return AESNativeEngine.getMultiBlockSize(0L);
    }

    @Override
    public int processBlocks(byte[] in, int inOff, int blockCount, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.wrapper == null) {
            throw new IllegalStateException("not initialized");
        }
        return AESNativeEngine.process(this.wrapper.getReference(), in, inOff, blockCount, out, outOff);
    }

    @Override
    public void reset() {
        if (this.wrapper == null) {
            return;
        }
        AESNativeEngine.reset(this.wrapper.getReference());
    }

    private static native void reset(long var0);

    private static native int process(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6);

    private static native int getMultiBlockSize(long var0);

    private static native int getBlockSize(long var0);

    static native long makeInstance(int var0, boolean var1);

    static native void dispose(long var0);

    static native void init(long var0, byte[] var2);

    @Override
    public GCMModeCipher createGCM() {
        if (CryptoServicesRegistrar.hasEnabledService("AES/GCM")) {
            return new AESNativeGCM();
        }
        return new GCMBlockCipher(new AESEngine());
    }

    @Override
    public GCMSIVModeCipher createGCMSIV() {
        return new GCMSIVBlockCipher(AESEngine.newInstance());
    }

    @Override
    public CBCModeCipher createCBC() {
        if (CryptoServicesRegistrar.hasEnabledService("AES/CBC")) {
            return new AESNativeCBC();
        }
        return new CBCBlockCipher(new AESNativeEngine());
    }

    @Override
    public CFBModeCipher createCFB(int bitSize) {
        if (bitSize % 8 != 0 || bitSize == 0 || bitSize > 128) {
            throw new IllegalArgumentException("invalid CFB bitsize: " + bitSize);
        }
        if (CryptoServicesRegistrar.hasEnabledService("AES/CFB")) {
            return new AESNativeCFB(bitSize);
        }
        return new CFBBlockCipher(new AESNativeEngine(), bitSize);
    }

    @Override
    public CTRModeCipher createCTR() {
        if (CryptoServicesRegistrar.hasEnabledService("AES/CTR")) {
            return new AESNativeCTR();
        }
        return new SICBlockCipher(AESEngine.newInstance());
    }

    @Override
    public CCMModeCipher createCCM() {
        if (CryptoServicesRegistrar.hasEnabledService("AES/CCM")) {
            return new AESNativeCCM();
        }
        return new CCMBlockCipher(AESEngine.newInstance());
    }

    @Override
    public EAXModeCipher createEAX() {
        return new EAXBlockCipher(AESEngine.newInstance());
    }

    @Override
    public OCBModeCipher createOCB() {
        return new OCBBlockCipher(AESEngine.newInstance(), AESEngine.newInstance());
    }

    public String toString() {
        return "AES[Native](" + this.keyLen + ")";
    }

    private static class Disposer
    extends NativeDisposer {
        Disposer(long ref) {
            super(ref);
        }

        @Override
        protected void dispose(long reference) {
            AESNativeEngine.dispose(reference);
        }
    }

    private static class ECBNativeRef
    extends NativeReference {
        public ECBNativeRef(long reference) {
            super(reference, "ECB");
        }

        @Override
        protected Runnable createAction() {
            return new Disposer(this.reference);
        }
    }
}

