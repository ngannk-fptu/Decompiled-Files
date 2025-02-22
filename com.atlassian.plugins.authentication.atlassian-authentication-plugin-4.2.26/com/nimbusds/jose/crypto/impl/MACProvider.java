/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.impl.AlgorithmSupportMessage;
import com.nimbusds.jose.crypto.impl.BaseJWSProvider;
import com.nimbusds.jose.util.StandardCharset;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public abstract class MACProvider
extends BaseJWSProvider {
    public static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS;
    private final byte[] secret;

    protected static String getJCAAlgorithmName(JWSAlgorithm alg) throws JOSEException {
        if (alg.equals(JWSAlgorithm.HS256)) {
            return "HMACSHA256";
        }
        if (alg.equals(JWSAlgorithm.HS384)) {
            return "HMACSHA384";
        }
        if (alg.equals(JWSAlgorithm.HS512)) {
            return "HMACSHA512";
        }
        throw new JOSEException(AlgorithmSupportMessage.unsupportedJWSAlgorithm(alg, SUPPORTED_ALGORITHMS));
    }

    protected MACProvider(byte[] secret, Set<JWSAlgorithm> supportedAlgs) throws KeyLengthException {
        super(supportedAlgs);
        if (secret.length < 32) {
            throw new KeyLengthException("The secret length must be at least 256 bits");
        }
        this.secret = secret;
    }

    public SecretKey getSecretKey() {
        return new SecretKeySpec(this.secret, "MAC");
    }

    public byte[] getSecret() {
        return this.secret;
    }

    public String getSecretString() {
        return new String(this.secret, StandardCharset.UTF_8);
    }

    static {
        LinkedHashSet<JWSAlgorithm> algs = new LinkedHashSet<JWSAlgorithm>();
        algs.add(JWSAlgorithm.HS256);
        algs.add(JWSAlgorithm.HS384);
        algs.add(JWSAlgorithm.HS512);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
    }
}

