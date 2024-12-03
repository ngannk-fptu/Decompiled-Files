/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEProvider;
import com.nimbusds.jose.jca.JWEJCAContext;
import java.util.Collections;
import java.util.Set;

abstract class BaseJWEProvider
implements JWEProvider {
    private final Set<JWEAlgorithm> algs;
    private final Set<EncryptionMethod> encs;
    private final JWEJCAContext jcaContext = new JWEJCAContext();

    public BaseJWEProvider(Set<JWEAlgorithm> algs, Set<EncryptionMethod> encs) {
        if (algs == null) {
            throw new IllegalArgumentException("The supported JWE algorithm set must not be null");
        }
        this.algs = Collections.unmodifiableSet(algs);
        if (encs == null) {
            throw new IllegalArgumentException("The supported encryption methods must not be null");
        }
        this.encs = encs;
    }

    @Override
    public Set<JWEAlgorithm> supportedJWEAlgorithms() {
        return this.algs;
    }

    @Override
    public Set<EncryptionMethod> supportedEncryptionMethods() {
        return this.encs;
    }

    @Override
    public JWEJCAContext getJCAContext() {
        return this.jcaContext;
    }
}

