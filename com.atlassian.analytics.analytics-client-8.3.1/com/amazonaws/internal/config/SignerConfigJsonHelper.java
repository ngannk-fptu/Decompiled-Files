/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.internal.config;

import com.amazonaws.internal.config.Builder;
import com.amazonaws.internal.config.SignerConfig;

public class SignerConfigJsonHelper
implements Builder<SignerConfig> {
    private String signerType;

    public SignerConfigJsonHelper() {
    }

    public SignerConfigJsonHelper(String signerType) {
        this.signerType = signerType;
    }

    public String getSignerType() {
        return this.signerType;
    }

    public void setSignerType(String signerType) {
        this.signerType = signerType;
    }

    @Override
    public SignerConfig build() {
        return new SignerConfig(this.signerType);
    }
}

