/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.impl.BaseJWSProvider;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class RSASSAProvider
extends BaseJWSProvider {
    public static final Set<JWSAlgorithm> SUPPORTED_ALGORITHMS;

    protected RSASSAProvider() {
        super(SUPPORTED_ALGORITHMS);
    }

    static {
        LinkedHashSet<JWSAlgorithm> algs = new LinkedHashSet<JWSAlgorithm>();
        algs.add(JWSAlgorithm.RS256);
        algs.add(JWSAlgorithm.RS384);
        algs.add(JWSAlgorithm.RS512);
        algs.add(JWSAlgorithm.PS256);
        algs.add(JWSAlgorithm.PS384);
        algs.add(JWSAlgorithm.PS512);
        SUPPORTED_ALGORITHMS = Collections.unmodifiableSet(algs);
    }
}

