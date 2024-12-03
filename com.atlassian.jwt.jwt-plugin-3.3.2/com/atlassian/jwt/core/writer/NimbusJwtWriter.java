/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 */
package com.atlassian.jwt.core.writer;

import com.atlassian.jwt.SigningAlgorithm;
import com.atlassian.jwt.core.NimbusUtil;
import com.atlassian.jwt.exception.JwtSigningException;
import com.atlassian.jwt.writer.JwtWriter;
import com.google.common.annotations.VisibleForTesting;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import javax.annotation.Nonnull;

public class NimbusJwtWriter
implements JwtWriter {
    private final JWSAlgorithm algorithm;
    private final JWSSigner signer;
    private static final String JWT = "JWT";

    public NimbusJwtWriter(SigningAlgorithm algorithm, JWSSigner signer) {
        this(NimbusUtil.asNimbusJWSAlgorithm(algorithm), signer);
    }

    protected NimbusJwtWriter(JWSAlgorithm algorithm, JWSSigner signer) {
        this.algorithm = algorithm;
        this.signer = signer;
    }

    @Override
    @Nonnull
    public String jsonToJwt(@Nonnull String json) throws JwtSigningException {
        return this.generateJwsObject(json).serialize();
    }

    @VisibleForTesting
    JWSObject generateJwsObject(String payload) {
        JWSHeader header = new JWSHeader.Builder(this.algorithm).type(new JOSEObjectType(JWT)).build();
        JWSObject jwsObject = new JWSObject(header, new Payload(payload));
        try {
            jwsObject.sign(this.signer);
        }
        catch (JOSEException e) {
            throw new JwtSigningException(e);
        }
        return jwsObject;
    }
}

