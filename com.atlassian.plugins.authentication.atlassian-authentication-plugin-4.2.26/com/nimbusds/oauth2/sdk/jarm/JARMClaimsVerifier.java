/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.jarm;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ClockSkewAware;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.nimbusds.jwt.util.DateUtils;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.validators.BadJWTExceptions;
import java.util.Date;
import java.util.List;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class JARMClaimsVerifier
implements JWTClaimsSetVerifier,
ClockSkewAware {
    private final Issuer expectedIssuer;
    private final ClientID expectedClientID;
    private int maxClockSkew;

    public JARMClaimsVerifier(Issuer issuer, ClientID clientID, int maxClockSkew) {
        if (issuer == null) {
            throw new IllegalArgumentException("The expected ID token issuer must not be null");
        }
        this.expectedIssuer = issuer;
        if (clientID == null) {
            throw new IllegalArgumentException("The client ID must not be null");
        }
        this.expectedClientID = clientID;
        this.setMaxClockSkew(maxClockSkew);
    }

    public Issuer getExpectedIssuer() {
        return this.expectedIssuer;
    }

    public ClientID getClientID() {
        return this.expectedClientID;
    }

    @Override
    public int getMaxClockSkew() {
        return this.maxClockSkew;
    }

    @Override
    public void setMaxClockSkew(int maxClockSkew) {
        if (maxClockSkew < 0) {
            throw new IllegalArgumentException("The max clock skew must be zero or positive");
        }
        this.maxClockSkew = maxClockSkew;
    }

    public void verify(JWTClaimsSet claimsSet, SecurityContext ctx) throws BadJWTException {
        String tokenIssuer = claimsSet.getIssuer();
        if (tokenIssuer == null) {
            throw BadJWTExceptions.MISSING_ISS_CLAIM_EXCEPTION;
        }
        if (!this.expectedIssuer.getValue().equals(tokenIssuer)) {
            throw new BadJWTException("Unexpected JWT issuer: " + tokenIssuer);
        }
        List<String> tokenAudience = claimsSet.getAudience();
        if (tokenAudience == null || tokenAudience.isEmpty()) {
            throw BadJWTExceptions.MISSING_AUD_CLAIM_EXCEPTION;
        }
        if (!tokenAudience.contains(this.expectedClientID.getValue())) {
            throw new BadJWTException("Unexpected JWT audience: " + tokenAudience);
        }
        Date exp = claimsSet.getExpirationTime();
        if (exp == null) {
            throw BadJWTExceptions.MISSING_EXP_CLAIM_EXCEPTION;
        }
        Date nowRef = new Date();
        if (!DateUtils.isAfter(exp, nowRef, this.maxClockSkew)) {
            throw BadJWTExceptions.EXPIRED_EXCEPTION;
        }
    }
}

