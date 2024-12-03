/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCModeCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.dispose.NativeDisposer;
import org.bouncycastle.util.dispose.NativeReference;

class AESNativeCBC
implements CBCModeCipher {
    private CBCRefWrapper referenceWrapper;
    byte[] IV = new byte[16];
    byte[] oldKey;
    int keySize;
    private boolean encrypting;

    AESNativeCBC() {
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) throws IllegalArgumentException {
        boolean oldEncrypting = this.encrypting;
        this.encrypting = forEncryption;
        if (params instanceof ParametersWithIV) {
            ParametersWithIV ivParam = (ParametersWithIV)params;
            byte[] iv = ivParam.getIV();
            if (iv.length != this.getBlockSize()) {
                throw new IllegalArgumentException("initialisation vector must be the same length as block size");
            }
            System.arraycopy(iv, 0, this.IV, 0, iv.length);
            this.reset();
            if (ivParam.getParameters() != null) {
                this.init((KeyParameter)ivParam.getParameters());
            } else {
                if (oldEncrypting != this.encrypting) {
                    throw new IllegalArgumentException("cannot change encrypting state without providing key");
                }
                if (this.oldKey == null) {
                    throw new IllegalStateException("IV change attempted but not previously initialized with a key");
                }
                this.init(new KeyParameter(this.oldKey));
            }
        } else {
            this.reset();
            if (params != null) {
                this.init((KeyParameter)params);
            } else {
                if (oldEncrypting != this.encrypting) {
                    throw new IllegalArgumentException("cannot change encrypting state without providing key.");
                }
                if (this.oldKey == null) {
                    throw new IllegalStateException("IV change attempted but not previously initialized with a key");
                }
                this.init(new KeyParameter(this.oldKey));
            }
        }
    }

    private void init(KeyParameter parameters) {
        byte[] key = parameters.getKey();
        switch (key.length) {
            case 16: 
            case 24: 
            case 32: {
                break;
            }
            default: {
                throw new IllegalArgumentException("key must be only 16,24,or 32 bytes long.");
            }
        }
        this.referenceWrapper = new CBCRefWrapper(AESNativeCBC.makeNative(key.length, this.encrypting));
        if (this.referenceWrapper.getReference() == 0L) {
            throw new IllegalStateException("Native CBC native instance returned a null pointer.");
        }
        this.oldKey = Arrays.clone(key);
        this.init(this.referenceWrapper.getReference(), key, this.IV);
        this.keySize = key.length * 8;
    }

    @Override
    public String getAlgorithmName() {
        return "AES/CBC";
    }

    @Override
    public int getBlockSize() {
        return AESNativeCBC.getBlockSize(0L);
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.referenceWrapper == null) {
            throw new IllegalStateException("not initialized");
        }
        return AESNativeCBC.process(this.referenceWrapper.getReference(), in, inOff, 1, out, outOff);
    }

    @Override
    public void reset() {
        if (this.referenceWrapper == null) {
            return;
        }
        AESNativeCBC.reset(this.referenceWrapper.getReference());
    }

    @Override
    public int getMultiBlockSize() {
        return AESNativeCBC.getMultiBlockSize(0L);
    }

    @Override
    public int processBlocks(byte[] in, int inOff, int blockCount, byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        if (this.referenceWrapper == null) {
            throw new IllegalStateException("not initialized");
        }
        return AESNativeCBC.process(this.referenceWrapper.getReference(), in, inOff, blockCount, out, outOff);
    }

    private static native int process(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6);

    private static native int getMultiBlockSize(long var0);

    private static native int getBlockSize(long var0);

    static native long makeNative(int var0, boolean var1);

    native void init(long var1, byte[] var3, byte[] var4);

    static native void dispose(long var0);

    private static native void reset(long var0);

    @Override
    public BlockCipher getUnderlyingCipher() {
        MultiBlockCipher eng = AESEngine.newInstance();
        eng.init(this.encrypting, new KeyParameter(this.oldKey));
        return eng;
    }

    public String toString() {
        return "CBC[Native](AES[Native](" + this.keySize + ")";
    }

    private class CBCRefWrapper
    extends NativeReference {
        public CBCRefWrapper(long reference) {
            super(reference, "CBC");
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
            Arrays.clear(AESNativeCBC.this.oldKey);
            Arrays.clear(AESNativeCBC.this.IV);
            AESNativeCBC.dispose(reference);
        }
    }
}

