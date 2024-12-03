/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.validators;

import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jwt.proc.ClockSkewAware;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;

public abstract class AbstractJWTValidator
implements ClockSkewAware {
    public static final int DEFAULT_MAX_CLOCK_SKEW = 60;
    private final Issuer expectedIssuer;
    private final ClientID clientID;
    private final JWSKeySelector jwsKeySelector;
    private final JWEKeySelector jweKeySelector;
    private int maxClockSkew = 60;

    public AbstractJWTValidator(Issuer expectedIssuer, ClientID clientID, JWSKeySelector jwsKeySelector, JWEKeySelector jweKeySelector) {
        if (expectedIssuer == null) {
            throw new IllegalArgumentException("The expected token issuer must not be null");
        }
        this.expectedIssuer = expectedIssuer;
        if (clientID == null) {
            throw new IllegalArgumentException("The client ID must not be null");
        }
        this.clientID = clientID;
        this.jwsKeySelector = jwsKeySelector;
        this.jweKeySelector = jweKeySelector;
    }

    public Issuer getExpectedIssuer() {
        return this.expectedIssuer;
    }

    public ClientID getClientID() {
        return this.clientID;
    }

    public JWSKeySelector getJWSKeySelector() {
        return this.jwsKeySelector;
    }

    public JWEKeySelector getJWEKeySelector() {
        return this.jweKeySelector;
    }

    @Override
    public int getMaxClockSkew() {
        return this.maxClockSkew;
    }

    @Override
    public void setMaxClockSkew(int maxClockSkew) {
        this.maxClockSkew = maxClockSkew;
    }
}

