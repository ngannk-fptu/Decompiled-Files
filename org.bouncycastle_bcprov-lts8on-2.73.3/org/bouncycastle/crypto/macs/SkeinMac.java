/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.SkeinEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.SkeinParameters;

public class SkeinMac
implements Mac {
    public static final int SKEIN_256 = 256;
    public static final int SKEIN_512 = 512;
    public static final int SKEIN_1024 = 1024;
    private SkeinEngine engine;

    public SkeinMac(int stateSizeBits, int digestSizeBits) {
        this.engine = new SkeinEngine(stateSizeBits, digestSizeBits);
    }

    public SkeinMac(SkeinMac mac) {
        this.engine = new SkeinEngine(mac.engine);
    }

    @Override
    public String getAlgorithmName() {
        return "Skein-MAC-" + this.engine.getBlockSize() * 8 + "-" + this.engine.getOutputSize() * 8;
    }

    @Override
    public void init(CipherParameters params) throws IllegalArgumentException {
        SkeinParameters skeinParameters;
        if (params instanceof SkeinParameters) {
            skeinParameters = (SkeinParameters)params;
        } else if (params instanceof KeyParameter) {
            skeinParameters = new SkeinParameters.Builder().setKey(((KeyParameter)params).getKey()).build();
        } else {
            throw new IllegalArgumentException("Invalid parameter passed to Skein MAC init - " + params.getClass().getName());
        }
        if (skeinParameters.getKey() == null) {
            throw new IllegalArgumentException("Skein MAC requires a key parameter.");
        }
        this.engine.init(skeinParameters);
    }

    @Override
    public int getMacSize() {
        return this.engine.getOutputSize();
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

