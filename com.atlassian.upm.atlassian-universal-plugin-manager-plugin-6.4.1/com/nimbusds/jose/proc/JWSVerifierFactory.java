/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.proc;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSProvider;
import com.nimbusds.jose.JWSVerifier;
import java.security.Key;

public interface JWSVerifierFactory
extends JWSProvider {
    public JWSVerifier createJWSVerifier(JWSHeader var1, Key var2) throws JOSEException;
}

