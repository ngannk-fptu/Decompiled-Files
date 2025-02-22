/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.util.Arrays;

public final class KDFFeedbackParameters
implements DerivationParameters {
    private static final int UNUSED_R = -1;
    private final byte[] ki;
    private final byte[] iv;
    private final boolean useCounter;
    private final int r;
    private final byte[] fixedInputData;

    private KDFFeedbackParameters(byte[] ki, byte[] iv, byte[] fixedInputData, int r, boolean useCounter) {
        if (ki == null) {
            throw new IllegalArgumentException("A KDF requires Ki (a seed) as input");
        }
        this.ki = Arrays.clone(ki);
        this.fixedInputData = fixedInputData == null ? new byte[0] : Arrays.clone(fixedInputData);
        this.r = r;
        this.iv = iv == null ? new byte[0] : Arrays.clone(iv);
        this.useCounter = useCounter;
    }

    public static KDFFeedbackParameters createWithCounter(byte[] ki, byte[] iv, byte[] fixedInputData, int r) {
        if (r != 8 && r != 16 && r != 24 && r != 32) {
            throw new IllegalArgumentException("Length of counter should be 8, 16, 24 or 32");
        }
        return new KDFFeedbackParameters(ki, iv, fixedInputData, r, true);
    }

    public static KDFFeedbackParameters createWithoutCounter(byte[] ki, byte[] iv, byte[] fixedInputData) {
        return new KDFFeedbackParameters(ki, iv, fixedInputData, -1, false);
    }

    public byte[] getKI() {
        return this.ki;
    }

    public byte[] getIV() {
        return this.iv;
    }

    public boolean useCounter() {
        return this.useCounter;
    }

    public int getR() {
        return this.r;
    }

    public byte[] getFixedInputData() {
        return Arrays.clone(this.fixedInputData);
    }
}

