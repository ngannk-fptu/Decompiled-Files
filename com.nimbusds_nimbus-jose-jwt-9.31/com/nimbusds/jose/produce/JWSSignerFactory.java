/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.produce;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSProvider;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.jwk.JWK;

public interface JWSSignerFactory
extends JWSProvider {
    public JWSSigner createJWSSigner(JWK var1) throws JOSEException;

    public JWSSigner createJWSSigner(JWK var1, JWSAlgorithm var2) throws JOSEException;
}

