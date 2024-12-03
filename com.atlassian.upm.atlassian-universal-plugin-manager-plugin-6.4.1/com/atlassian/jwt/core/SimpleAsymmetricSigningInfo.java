/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.core;

import com.atlassian.jwt.AsymmetricSigningInfo;
import com.atlassian.jwt.SigningAlgorithm;
import java.security.interfaces.RSAPrivateKey;
import javax.annotation.Nonnull;

public class SimpleAsymmetricSigningInfo
implements AsymmetricSigningInfo {
    private final SigningAlgorithm signingAlgorithm;
    private final RSAPrivateKey privateKey;

    public SimpleAsymmetricSigningInfo(SigningAlgorithm signingAlgorithm, @Nonnull RSAPrivateKey privateKey) {
        this.signingAlgorithm = signingAlgorithm;
        this.privateKey = privateKey;
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return this.privateKey;
    }

    @Override
    public SigningAlgorithm getSigningAlgorithm() {
        return this.signingAlgorithm;
    }
}

