/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CCMModeCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.dispose.NativeDisposer;
import org.bouncycastle.util.dispose.NativeReference;

class AESNativeCCM
implements CCMModeCipher {
    private CCMRefWrapper refWrapper;
    private byte[] lastKey;
    private boolean forEncryption = false;
    private boolean initialised = false;
    private final ExposedByteArrayOutputStream associatedText = new ExposedByteArrayOutputStream();
    private final ExposedByteArrayOutputStream data = new ExposedByteArrayOutputStream();

    AESNativeCCM() {
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
        CipherParameters cipherParameters;
        int macSize;
        byte[] initialAssociatedText;
        byte[] nonce;
        CipherParameters param;
        this.forEncryption = forEncryption;
        CipherParameters keyParam = null;
        if (params instanceof AEADParameters) {
            param = (AEADParameters)params;
            nonce = ((AEADParameters)param).getNonce();
            initialAssociatedText = ((AEADParameters)param).getAssociatedText();
            macSize = this.getMacSize(forEncryption, ((AEADParameters)param).getMacSize());
            cipherParameters = ((AEADParameters)param).getKey();
        } else if (params instanceof ParametersWithIV) {
            param = (ParametersWithIV)params;
            nonce = ((ParametersWithIV)param).getIV();
            initialAssociatedText = null;
            macSize = this.getMacSize(forEncryption, 64);
            cipherParameters = ((ParametersWithIV)param).getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to CCM");
        }
        if (cipherParameters != null) {
            keyParam = cipherParameters;
        }
        if (keyParam != null) {
            this.lastKey = ((KeyParameter)keyParam).getKey();
            if (this.lastKey == null) {
                throw new IllegalArgumentException("key was null");
            }
            this.initRef(this.lastKey.length);
        }
        int iatLen = initialAssociatedText != null ? initialAssociatedText.length : 0;
        AESNativeCCM.initNative(this.refWrapper.getReference(), forEncryption, this.lastKey, nonce, initialAssociatedText, iatLen, macSize * 8);
        this.reset();
        this.initialised = true;
    }

    private void initRef(int keySize) {
        this.refWrapper = new CCMRefWrapper(AESNativeCCM.makeInstance());
    }

    @Override
    public String getAlgorithmName() {
        return "AES/CCM";
    }

    @Override
    public void processAADByte(byte in) {
        this.associatedText.write(in);
    }

    @Override
    public void processAADBytes(byte[] in, int inOff, int len) {
        if (inOff < 0) {
            throw new IllegalArgumentException("offset is negative");
        }
        if (len < 0) {
            throw new IllegalArgumentException("len is negative");
        }
        if (in.length < inOff + len) {
            throw new IllegalArgumentException("array too short for offset + len");
        }
        this.associatedText.write(in, inOff, len);
    }

    @Override
    public int processByte(byte in, byte[] out, int outOff) throws DataLengthException {
        if (outOff < 0) {
            throw new IllegalArgumentException("offset is negative");
        }
        if (out != null && out.length < outOff) {
            throw new DataLengthException("offset past end");
        }
        this.data.write(in);
        return 0;
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        if (inOff < 0 || outOff < 0) {
            throw new IllegalArgumentException("offset is negative");
        }
        if (len < 0) {
            throw new IllegalArgumentException("len is negative");
        }
        if (in == null) {
            throw new NullPointerException("input was null");
        }
        if (in.length < inOff + len) {
            throw new DataLengthException("array too short for offset + len");
        }
        this.data.write(in, inOff, len);
        return 0;
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws IllegalStateException, InvalidCipherTextException {
        int len;
        try {
            this.checkStatus();
            if (out == null) {
                throw new NullPointerException("output was null");
            }
            if (outOff < 0) {
                throw new IllegalArgumentException("offset is negative");
            }
            byte[] input = this.data.toByteArray();
            byte[] aad = this.associatedText.toByteArray();
            if (this.getOutputSize(0) > out.length - outOff) {
                throw new OutputLengthException("output buffer too short");
            }
            len = AESNativeCCM.processPacket(this.refWrapper.getReference(), input, 0, input.length, aad, 0, aad.length, out, outOff);
            this.resetKeepMac();
        }
        catch (IllegalStateException e) {
            this.reset();
            throw e;
        }
        return len;
    }

    @Override
    public byte[] getMac() {
        return AESNativeCCM.getMac(this.refWrapper.getReference());
    }

    @Override
    public int getUpdateOutputSize(int len) {
        return 0;
    }

    @Override
    public int getOutputSize(int len) {
        return AESNativeCCM.getOutputSize(this.refWrapper.getReference(), len + this.data.size());
    }

    @Override
    public void reset() {
        if (this.refWrapper == null) {
            return;
        }
        this.associatedText.reset();
        this.data.reset();
        this.reset(this.refWrapper.getReference(), false);
    }

    private void resetKeepMac() {
        if (this.refWrapper == null) {
            return;
        }
        this.associatedText.reset();
        this.data.reset();
        this.reset(this.refWrapper.getReference(), true);
    }

    private void checkStatus() {
        if (!this.initialised) {
            if (this.forEncryption) {
                throw new IllegalStateException("CCM cipher cannot be reused for encryption");
            }
            throw new IllegalStateException("CCM cipher needs to be initialised");
        }
    }

    private native void reset(long var1, boolean var3);

    static native void initNative(long var0, boolean var2, byte[] var3, byte[] var4, byte[] var5, int var6, int var7);

    static native long makeInstance();

    static native void dispose(long var0);

    static native int getOutputSize(long var0, int var2);

    static native byte[] getMac(long var0);

    static native int processPacket(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7, byte[] var8, int var9);

    @Override
    public int processPacket(byte[] inBuf, int inOff, int length, byte[] outBuf, int outOff) throws InvalidCipherTextException {
        byte[] aad = this.associatedText.toByteArray();
        int result = AESNativeCCM.processPacket(this.refWrapper.getReference(), inBuf, inOff, length, aad, 0, aad.length, outBuf, outOff);
        this.reset();
        return result;
    }

    @Override
    public byte[] processPacket(byte[] input, int inOff, int length) throws InvalidCipherTextException {
        byte[] out = new byte[this.getOutputSize(length)];
        this.processPacket(input, inOff, length, out, 0);
        this.reset();
        return out;
    }

    public String toString() {
        if (this.lastKey != null) {
            return "CCM[Native](AES[Native](" + this.lastKey.length * 8 + "))";
        }
        return "CCM[Native](AES[Native](not initialized))";
    }

    private int getMacSize(boolean forEncryption, int requestedMacBits) {
        if (forEncryption && (requestedMacBits < 32 || requestedMacBits > 128 || 0 != (requestedMacBits & 0xF))) {
            throw new IllegalArgumentException("invalid value for MAC size");
        }
        return requestedMacBits >>> 3;
    }

    private class CCMRefWrapper
    extends NativeReference {
        public CCMRefWrapper(long reference) {
            super(reference, "CCM");
        }

        @Override
        public Runnable createAction() {
            return new Disposer(this.reference);
        }
    }

    private class Disposer
    extends NativeDisposer {
        Disposer(long ref) {
            super(ref);
        }

        @Override
        protected void dispose(long reference) {
            AESNativeCCM.this.data.reset();
            AESNativeCCM.this.associatedText.reset();
            AESNativeCCM.dispose(reference);
        }
    }

    private static class ExposedByteArrayOutputStream
    extends ByteArrayOutputStream {
        public byte[] getBuffer() {
            return this.buf;
        }
    }
}

