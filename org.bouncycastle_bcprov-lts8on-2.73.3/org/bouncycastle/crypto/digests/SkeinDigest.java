/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.SkeinEngine;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.util.Memoable;

public class SkeinDigest
implements ExtendedDigest,
Memoable {
    public static final int SKEIN_256 = 256;
    public static final int SKEIN_512 = 512;
    public static final int SKEIN_1024 = 1024;
    private final CryptoServicePurpose purpose;
    private SkeinEngine engine;

    public SkeinDigest(int stateSizeBits, int digestSizeBits) {
        this(stateSizeBits, digestSizeBits, CryptoServicePurpose.ANY);
    }

    public SkeinDigest(int stateSizeBits, int digestSizeBits, CryptoServicePurpose purpose) {
        this.engine = new SkeinEngine(stateSizeBits, digestSizeBits);
        this.purpose = purpose;
        this.init(null);
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.getDigestSize() * 4, purpose));
    }

    public SkeinDigest(SkeinDigest digest) {
        this.engine = new SkeinEngine(digest.engine);
        this.purpose = digest.purpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, digest.getDigestSize() * 4, this.purpose));
    }

    @Override
    public void reset(Memoable other) {
        SkeinDigest d = (SkeinDigest)other;
        this.engine.reset(d.engine);
    }

    @Override
    public Memoable copy() {
        return new SkeinDigest(this);
    }

    @Override
    public String getAlgorithmName() {
        return "Skein-" + this.engine.getBlockSize() * 8 + "-" + this.engine.getOutputSize() * 8;
    }

    @Override
    public int getDigestSize() {
        return this.engine.getOutputSize();
    }

    @Override
    public int getByteLength() {
        return this.engine.getBlockSize();
    }

    public void init(SkeinParameters params) {
        this.engine.init(params);
    }

    @Override
    public void reset() {
        this.engine.reset();
    }

    @Override
    public void update(byte in) {
        this.engine.update(in);
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        this.engine.update(in, inOff, len);
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        return this.engine.doFinal(out, outOff);
    }
}

