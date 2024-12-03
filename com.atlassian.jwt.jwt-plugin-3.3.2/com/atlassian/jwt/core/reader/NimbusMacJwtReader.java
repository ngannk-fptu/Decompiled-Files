/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jwt.core.reader;

import com.atlassian.jwt.core.Clock;
import com.atlassian.jwt.core.SystemClock;
import com.atlassian.jwt.core.reader.NimbusJwtReader;
import com.atlassian.jwt.exception.JwtMalformedSharedSecretException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;

public class NimbusMacJwtReader
extends NimbusJwtReader {
    public NimbusMacJwtReader(String issuer, String sharedSecret) {
        this(issuer, sharedSecret, SystemClock.getInstance());
    }

    public NimbusMacJwtReader(String issuer, String sharedSecret, Clock clock) {
        super(issuer, NimbusMacJwtReader.createMACVerifier(sharedSecret), clock);
    }

    private static MACVerifier createMACVerifier(String sharedSecret) {
        try {
            return new MACVerifier(sharedSecret);
        }
        catch (JOSEException e) {
            throw new JwtMalformedSharedSecretException("Failed to create MAC verifier with the provided secret key", e);
        }
    }
}

