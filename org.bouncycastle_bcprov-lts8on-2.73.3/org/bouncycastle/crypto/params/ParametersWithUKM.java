/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ParametersWithUKM
implements CipherParameters {
    private byte[] ukm;
    private CipherParameters parameters;

    public ParametersWithUKM(CipherParameters parameters, byte[] ukm) {
        this(parameters, ukm, 0, ukm.length);
    }

    public ParametersWithUKM(CipherParameters parameters, byte[] ukm, int ukmOff, int ukmLen) {
        this.ukm = new byte[ukmLen];
        this.parameters = parameters;
        System.arraycopy(ukm, ukmOff, this.ukm, 0, ukmLen);
    }

    public byte[] getUKM() {
        return this.ukm;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}

