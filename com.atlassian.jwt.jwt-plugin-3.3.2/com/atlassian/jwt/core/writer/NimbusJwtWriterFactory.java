/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.core.writer;

import com.atlassian.jwt.AsymmetricSigningInfo;
import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.SymmetricSigningInfo;
import com.atlassian.jwt.core.writer.NimbusJwtWriter;
import com.atlassian.jwt.exception.JwtMalformedSharedSecretException;
import com.atlassian.jwt.writer.JwtWriter;
import com.atlassian.jwt.writer.JwtWriterFactory;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import java.security.PrivateKey;
import javax.annotation.Nonnull;

public class NimbusJwtWriterFactory
implements JwtWriterFactory {
    private NimbusJwtWriterFactoryHelper factoryHelper;

    public NimbusJwtWriterFactory() {
        this(new NimbusJwtWriterFactoryHelper());
    }

    public NimbusJwtWriterFactory(NimbusJwtWriterFactoryHelper factoryHelper) {
        this.factoryHelper = factoryHelper;
    }

    @Override
    @Nonnull
    public JwtWriter macSigningWriter(@Nonnull SigningAlgorithm algorithm, @Nonnull String sharedSecret) {
        return new NimbusJwtWriter(algorithm, (JWSSigner)NimbusJwtWriterFactory.createMACSigner(sharedSecret));
    }

    @Override
    @Nonnull
    public JwtWriter signingWriter(@Nonnull SymmetricSigningInfo signingInfo) {
        return this.factoryHelper.makeMacJwtWriter(signingInfo.getSigningAlgorithm(), NimbusJwtWriterFactory.createMACSigner(signingInfo.getSharedSecret()));
    }

    @Override
    @Nonnull
    public JwtWriter signingWriter(@Nonnull AsymmetricSigningInfo signingInfo) {
        return this.factoryHelper.makeRsJwtWriter(signingInfo.getSigningAlgorithm(), new RSASSASigner((PrivateKey)signingInfo.getPrivateKey(), true));
    }

    private static MACSigner createMACSigner(String sharedSecret) {
        try {
            return new MACSigner(sharedSecret);
        }
        catch (KeyLengthException e) {
            throw new JwtMalformedSharedSecretException("Failed to create MAC signer with the provided secret key", e);
        }
    }

    static class NimbusJwtWriterFactoryHelper {
        NimbusJwtWriterFactoryHelper() {
        }

        NimbusJwtWriter makeMacJwtWriter(SigningAlgorithm algorithm, MACSigner macSigner) {
            return new NimbusJwtWriter(algorithm, (JWSSigner)macSigner);
        }

        NimbusJwtWriter makeRsJwtWriter(SigningAlgorithm algorithm, RSASSASigner rsaSigner) {
            return new NimbusJwtWriter(algorithm, (JWSSigner)rsaSigner);
        }
    }
}

