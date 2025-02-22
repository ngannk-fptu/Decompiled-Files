/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.util.Arrays;

public class IESKEMParameterSpec
implements AlgorithmParameterSpec {
    private final byte[] recipientInfo;
    private final boolean usePointCompression;

    public IESKEMParameterSpec(byte[] recipientInfo) {
        this(recipientInfo, false);
    }

    public IESKEMParameterSpec(byte[] recipientInfo, boolean usePointCompression) {
        this.recipientInfo = Arrays.clone(recipientInfo);
        this.usePointCompression = usePointCompression;
    }

    public byte[] getRecipientInfo() {
        return Arrays.clone(this.recipientInfo);
    }

    public boolean hasUsePointCompression() {
        return this.usePointCompression;
    }
}

