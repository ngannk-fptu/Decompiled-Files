/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.validators;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.ClockSkewAware;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.nimbusds.jwt.util.DateUtils;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.openid.connect.sdk.Nonce;
import com.nimbusds.openid.connect.sdk.validators.BadJWTExceptions;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class IDTokenClaimsVerifier
implements JWTClaimsSetVerifier,
ClockSkewAware {
    private final Issuer expectedIssuer;
    private final ClientID expectedClientID;
    private final Nonce expectedNonce;
    private int maxClockSkew;

    public IDTokenClaimsVerifier(Issuer issuer, ClientID clientID, Nonce nonce, int maxClockSkew) {
        if (issuer == null) {
            throw new IllegalArgumentException("The expected ID token issuer must not be null");
        }
        this.expectedIssuer = issuer;
        if (clientID == null) {
            throw new IllegalArgumentException("The client ID must not be null");
        }
        this.expectedClientID = clientID;
        this.expectedNonce = nonce;
        this.setMaxClockSkew(maxClockSkew);
    }

    public Issuer getExpectedIssuer() {
        return this.expectedIssuer;
    }

    public ClientID getClientID() {
        return this.expectedClientID;
    }

    public Nonce getExpectedNonce() {
        return this.expectedNonce;
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
        Date exp;
        String tokenIssuer = claimsSet.getIssuer();
        if (tokenIssuer == null) {
            throw BadJWTExceptions.MISSING_ISS_CLAIM_EXCEPTION;
        }
        if (!this.expectedIssuer.getValue().equals(tokenIssuer)) {
            throw new BadJWTException("Unexpected JWT issuer: " + tokenIssuer);
        }
        if (claimsSet.getSubject() == null) {
            throw BadJWTExceptions.MISSING_SUB_CLAIM_EXCEPTION;
        }
        List<String> tokenAudience = claimsSet.getAudience();
        if (CollectionUtils.isEmpty(tokenAudience)) {
            throw BadJWTExceptions.MISSING_AUD_CLAIM_EXCEPTION;
        }
        if (!tokenAudience.contains(this.expectedClientID.getValue())) {
            throw new BadJWTException("Unexpected JWT audience: " + tokenAudience);
        }
        if (tokenAudience.size() > 1) {
            String tokenAzp;
            try {
                tokenAzp = claimsSet.getStringClaim("azp");
            }
            catch (ParseException e) {
                throw new BadJWTException("Invalid JWT authorized party (azp) claim: " + e.getMessage());
            }
            if (tokenAzp == null) {
                throw new BadJWTException("JWT authorized party (azp) claim required when multiple (aud) audiences present");
            }
            if (!this.expectedClientID.getValue().equals(tokenAzp)) {
                throw new BadJWTException("Unexpected JWT authorized party (azp) claim: " + tokenAzp);
            }
        }
        if ((exp = claimsSet.getExpirationTime()) == null) {
            throw BadJWTExceptions.MISSING_EXP_CLAIM_EXCEPTION;
        }
        Date iat = claimsSet.getIssueTime();
        if (iat == null) {
            throw BadJWTExceptions.MISSING_IAT_CLAIM_EXCEPTION;
        }
        Date nowRef = new Date();
        if (!DateUtils.isAfter(exp, nowRef, this.maxClockSkew)) {
            throw BadJWTExceptions.EXPIRED_EXCEPTION;
        }
        if (!iat.equals(nowRef) && !DateUtils.isBefore(iat, nowRef, this.maxClockSkew)) {
            throw BadJWTExceptions.IAT_CLAIM_AHEAD_EXCEPTION;
        }
        if (this.expectedNonce != null) {
            String tokenNonce;
            try {
                tokenNonce = claimsSet.getStringClaim("nonce");
            }
            catch (ParseException e) {
                throw new BadJWTException("Invalid JWT nonce (nonce) claim: " + e.getMessage());
            }
            if (tokenNonce == null) {
                throw BadJWTExceptions.MISSING_NONCE_CLAIM_EXCEPTION;
            }
            if (!this.expectedNonce.getValue().equals(tokenNonce)) {
                throw new BadJWTException("Unexpected JWT nonce (nonce) claim: " + tokenNonce);
            }
        }
    }
}

