/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.util.Arrays;

public final class KDFCounterParameters
implements DerivationParameters {
    private byte[] ki;
    private byte[] fixedInputDataCounterPrefix;
    private byte[] fixedInputDataCounterSuffix;
    private int r;

    public KDFCounterParameters(byte[] ki, byte[] fixedInputDataCounterSuffix, int r) {
        this(ki, null, fixedInputDataCounterSuffix, r);
    }

    public KDFCounterParameters(byte[] ki, byte[] fixedInputDataCounterPrefix, byte[] fixedInputDataCounterSuffix, int r) {
        if (ki == null) {
            throw new IllegalArgumentException("A KDF requires Ki (a seed) as input");
        }
        this.ki = Arrays.clone(ki);
        this.fixedInputDataCounterPrefix = fixedInputDataCounterPrefix == null ? new byte[0] : Arrays.clone(fixedInputDataCounterPrefix);
        this.fixedInputDataCounterSuffix = fixedInputDataCounterSuffix == null ? new byte[0] : Arrays.clone(fixedInputDataCounterSuffix);
        if (r != 8 && r != 16 && r != 24 && r != 32) {
            throw new IllegalArgumentException("Length of counter should be 8, 16, 24 or 32");
        }
        this.r = r;
    }

    public byte[] getKI() {
        return this.ki;
    }

    public byte[] getFixedInputData() {
        return Arrays.clone(this.fixedInputDataCounterSuffix);
    }

    public byte[] getFixedInputDataCounterPrefix() {
        return Arrays.clone(this.fixedInputDataCounterPrefix);
    }

    public byte[] getFixedInputDataCounterSuffix() {
        return Arrays.clone(this.fixedInputDataCounterSuffix);
    }

    public int getR() {
        return this.r;
    }
}

