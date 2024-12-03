/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JOSEObjectType
 *  com.nimbusds.jose.proc.JWEKeySelector
 *  com.nimbusds.jose.proc.JWSKeySelector
 *  com.nimbusds.jwt.proc.ClockSkewAware
 */
package com.nimbusds.openid.connect.sdk.validators;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.proc.JWEKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jwt.proc.ClockSkewAware;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;

public abstract class AbstractJWTValidator
implements ClockSkewAware {
    public static final int DEFAULT_MAX_CLOCK_SKEW = 60;
    private final JOSEObjectType jwtType;
    private final Issuer expectedIssuer;
    private final ClientID clientID;
    private final JWSKeySelector jwsKeySelector;
    private final JWEKeySelector jweKeySelector;
    private int maxClockSkew = 60;

    @Deprecated
    public AbstractJWTValidator(Issuer expectedIssuer, ClientID clientID, JWSKeySelector jwsKeySelector, JWEKeySelector jweKeySelector) {
        this(null, expectedIssuer, clientID, jwsKeySelector, jweKeySelector);
    }

    public AbstractJWTValidator(JOSEObjectType jwtType, Issuer expectedIssuer, ClientID clientID, JWSKeySelector jwsKeySelector, JWEKeySelector jweKeySelector) {
        this.jwtType = jwtType;
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

    public JOSEObjectType getExpectedJWTType() {
        return this.jwtType;
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

    public int getMaxClockSkew() {
        return this.maxClockSkew;
    }

    public void setMaxClockSkew(int maxClockSkew) {
        this.maxClockSkew = maxClockSkew;
    }
}

