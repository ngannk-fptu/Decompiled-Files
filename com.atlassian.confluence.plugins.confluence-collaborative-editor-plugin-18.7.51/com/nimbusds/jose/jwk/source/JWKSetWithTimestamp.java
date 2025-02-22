/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk.source;

import com.nimbusds.jose.jwk.JWKSet;
import java.util.Date;
import net.jcip.annotations.Immutable;

@Deprecated
@Immutable
public final class JWKSetWithTimestamp {
    private final JWKSet jwkSet;
    private final Date timestamp;

    public JWKSetWithTimestamp(JWKSet jwkSet) {
        this(jwkSet, new Date());
    }

    public JWKSetWithTimestamp(JWKSet jwkSet, Date timestamp) {
        if (jwkSet == null) {
            throw new IllegalArgumentException("The JWK set must not be null");
        }
        this.jwkSet = jwkSet;
        if (timestamp == null) {
            throw new IllegalArgumentException("The timestamp must not null");
        }
        this.timestamp = timestamp;
    }

    public JWKSet getJWKSet() {
        return this.jwkSet;
    }

    public Date getDate() {
        return this.timestamp;
    }
}

