/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.core.reader;

import com.atlassian.jwt.core.Clock;
import com.atlassian.jwt.core.SystemClock;
import com.atlassian.jwt.core.reader.NimbusJwtReader;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import java.security.interfaces.RSAPublicKey;

public class NimbusRsJwtReader
extends NimbusJwtReader {
    public NimbusRsJwtReader(String issuer, RSAPublicKey publicKey) {
        this(issuer, publicKey, SystemClock.getInstance());
    }

    public NimbusRsJwtReader(String issuer, RSAPublicKey publicKey, Clock clock) {
        super(issuer, new RSASSAVerifier(publicKey), clock);
    }
}

