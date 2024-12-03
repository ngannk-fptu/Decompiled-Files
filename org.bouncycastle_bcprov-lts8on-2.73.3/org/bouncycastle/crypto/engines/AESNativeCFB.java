/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.modes.CFBModeCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.dispose.NativeDisposer;
import org.bouncycastle.util.dispose.NativeReference;

class AESNativeCFB
implements CFBModeCipher {
    private final int bitSize;
    private CFBRefWrapper referenceWrapper;
    private byte[] oldKey;
    private byte[] oldIv;
    private boolean encrypting;

    public AESNativeCFB() {
        this(128);
    }

    public AESNativeCFB(int bitSize) {
        this.bitSize = bitSize;
        switch (bitSize) {
            case 128: {
                break;
            }
            default: {
                throw new IllegalArgumentException("native feedback bit size can only be 128");
            }
        }
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        boolean oldEncrypting = this.encrypting;
        this.encrypting = forEncryption;
        byte[] key = null;
        byte[] iv = null;
        if (params instanceof ParametersWithIV) {
            ParametersWithIV ivParam = (ParametersWithIV)params;
            iv = ivParam.getIV();
            if (iv.length > this.getBlockSize() || iv.length < 1) {
                throw new IllegalArgumentException("initialisation vector must be between one and block size length");
            }
            if (iv.length < this.getBlockSize()) {
                byte[] newIv = new byte[this.getBlockSize()];
                System.arraycopy(iv, 0, newIv, newIv.length - iv.length, iv.length);
                iv = newIv;
            }
            this.oldIv = Arrays.clone(iv);
            if (ivParam.getParameters() != null) {
                key = ((KeyParameter)ivParam.getParameters()).getKey();
            }
            if (key != null) {
                oldEncrypting = this.encrypting;
                this.oldKey = Arrays.clone(key);
            } else {
                key = this.oldKey;
            }
        } else if (params instanceof KeyParameter) {
            key = ((KeyParameter)params).getKey();
            this.oldKey = Arrays.clone(key);
            iv = this.oldIv;
        }
        if (key == null && oldEncrypting != this.encrypting) {
            throw new IllegalArgumentException("cannot change encrypting state without providing key.");
        }
        if (iv == null) {
            throw new IllegalArgumentException("iv is null");
        }
        switch (key.length) {
            case 16: 
            case 24: 
            case 32: {
                break;
            }
            default: {
                throw new IllegalStateException("key must be only 16,24,or 32 bytes long.");
            }
        }
        this.referenceWrapper = new CFBRefWrapper(AESNativeCFB.makeNative(this.encrypting, key.length));
        this.init(this.referenceWrapper.getReference(), key, iv);
    }

    @Override
    public String getAlgorithmName() {
        return "AES/CFB";
    }

    @Override
    public byte returnByte(byte in) {
        return AESNativeCFB.processByte(this.referenceWrapper.getReference(), in);
    }

    @Override
    public int processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) throws DataLengthException {
        if (this.referenceWrapper == null) {
            throw new IllegalStateException("not initialized");
        }
        return AESNativeCFB.processBytes(this.referenceWrapper.getReference(), in, inOff, len, out, outOff);
    }

    @Override
    public int getBlockSize() {
        return this.bitSize / 8;
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.referenceWrapper == null) {
            throw new IllegalStateException("not initialized");
        }
        return AESNativeCFB.processBytes(this.referenceWrapper.getReference(), in, inOff, this.getBlockSize(), out, outOff);
    }

    @Override
    public void reset() {
        if (this.referenceWrapper == null) {
            return;
        }
        AESNativeCFB.reset(this.referenceWrapper.getReference());
    }

    @Override
    public int getMultiBlockSize() {
        return AESNativeCFB.getNativeMultiBlockSize();
    }

    @Override
    public int processBlocks(byte[] in, int inOff, int blockCount, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.referenceWrapper == null) {
            throw new IllegalStateException("CFB engine not initialized");
        }
        return this.processBytes(in, inOff, blockCount * this.getBlockSize(), out, outOff);
    }

    private static native byte processByte(long var0, byte var2);

    private static native int processBytes(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6) throws DataLengthException;

    static native long makeNative(boolean var0, int var1);

    native void init(long var1, byte[] var3, byte[] var4);

    static native void dispose(long var0);

    static native int getNativeMultiBlockSize();

    private static native void reset(long var0);

    public String toString() {
        if (this.oldKey != null) {
            return "CFB[Native](AES[Native](" + this.oldKey.length * 8 + "))";
        }
        return "CFB[Native](AES[Native](not initialized))";
    }

    private static class CFBRefWrapper
    extends NativeReference {
        public CFBRefWrapper(long reference) {
            super(reference, "CFB");
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
            AESNativeCFB.dispose(reference);
        }
    }
}

