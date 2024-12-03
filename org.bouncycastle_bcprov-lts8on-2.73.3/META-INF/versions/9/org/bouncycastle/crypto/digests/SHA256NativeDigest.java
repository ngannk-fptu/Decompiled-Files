/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.SavableDigest;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.dispose.NativeDisposer;
import org.bouncycastle.util.dispose.NativeReference;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
class SHA256NativeDigest
implements SavableDigest {
    private final CryptoServicePurpose purpose;
    protected DigestRefWrapper nativeRef = null;

    SHA256NativeDigest(CryptoServicePurpose purpose) {
        this.purpose = purpose;
        this.nativeRef = new DigestRefWrapper(SHA256NativeDigest.makeNative());
        this.reset();
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
    }

    SHA256NativeDigest() {
        this(CryptoServicePurpose.ANY);
    }

    SHA256NativeDigest(SHA256NativeDigest src) {
        this(CryptoServicePurpose.ANY);
        byte[] state = src.getEncodedState();
        SHA256NativeDigest.restoreFullState(this.nativeRef.getReference(), state, 0);
    }

    SHA256NativeDigest restoreState(byte[] state, int offset) {
        SHA256NativeDigest.restoreFullState(this.nativeRef.getReference(), state, offset);
        return this;
    }

    @Override
    public String getAlgorithmName() {
        return "SHA-256";
    }

    @Override
    public int getDigestSize() {
        return SHA256NativeDigest.getDigestSize(this.nativeRef.getReference());
    }

    @Override
    public void update(byte in) {
        SHA256NativeDigest.update(this.nativeRef.getReference(), in);
    }

    @Override
    public void update(byte[] input, int inOff, int len) {
        SHA256NativeDigest.update(this.nativeRef.getReference(), input, inOff, len);
    }

    @Override
    public int doFinal(byte[] output, int outOff) {
        return SHA256NativeDigest.doFinal(this.nativeRef.getReference(), output, outOff);
    }

    @Override
    public void reset() {
        SHA256NativeDigest.reset(this.nativeRef.getReference());
    }

    @Override
    public int getByteLength() {
        return SHA256NativeDigest.getByteLength(this.nativeRef.getReference());
    }

    @Override
    public Memoable copy() {
        return new SHA256NativeDigest(this);
    }

    @Override
    public void reset(Memoable other) {
        SHA256NativeDigest dig = (SHA256NativeDigest)other;
        SHA256NativeDigest.restoreFullState(this.nativeRef.getReference(), dig.getEncodedState(), 0);
    }

    @Override
    public byte[] getEncodedState() {
        int l = SHA256NativeDigest.encodeFullState(this.nativeRef.getReference(), null, 0);
        byte[] state = new byte[l];
        SHA256NativeDigest.encodeFullState(this.nativeRef.getReference(), state, 0);
        return state;
    }

    void restoreFullState(byte[] encoded, int offset) {
        SHA256NativeDigest.restoreFullState(this.nativeRef.getReference(), encoded, offset);
    }

    public String toString() {
        return "SHA256[Native]()";
    }

    static native long makeNative();

    static native void destroy(long var0);

    static native int getDigestSize(long var0);

    static native void update(long var0, byte var2);

    static native void update(long var0, byte[] var2, int var3, int var4);

    static native int doFinal(long var0, byte[] var2, int var3);

    static native void reset(long var0);

    static native int getByteLength(long var0);

    static native int encodeFullState(long var0, byte[] var2, int var3);

    static native void restoreFullState(long var0, byte[] var2, int var3);

    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, 256, this.purpose);
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class DigestRefWrapper
    extends NativeReference {
        public DigestRefWrapper(long reference) {
            super(reference, "SHA256");
        }

        @Override
        public Runnable createAction() {
            return new Disposer(this.reference);
        }
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class Disposer
    extends NativeDisposer {
        Disposer(long ref) {
            super(ref);
        }

        @Override
        protected void dispose(long reference) {
            SHA256NativeDigest.destroy(reference);
        }
    }
}

