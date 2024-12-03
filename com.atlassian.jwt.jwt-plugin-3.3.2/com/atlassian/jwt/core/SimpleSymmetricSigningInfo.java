/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.core;

import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.SymmetricSigningInfo;
import javax.annotation.Nonnull;

public class SimpleSymmetricSigningInfo
implements SymmetricSigningInfo {
    private final SigningAlgorithm signingAlgorithm;
    private final String sharedSecret;

    public SimpleSymmetricSigningInfo(SigningAlgorithm signingAlgorithm, @Nonnull String sharedSecret) {
        this.signingAlgorithm = signingAlgorithm;
        this.sharedSecret = sharedSecret;
    }

    @Override
    public SigningAlgorithm getSigningAlgorithm() {
        return this.signingAlgorithm;
    }

    @Override
    public String getSharedSecret() {
        return this.sharedSecret;
    }
}

