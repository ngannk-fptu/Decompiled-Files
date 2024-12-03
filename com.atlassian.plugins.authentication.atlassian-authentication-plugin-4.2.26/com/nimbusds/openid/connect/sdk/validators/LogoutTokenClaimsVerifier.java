/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.validators;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.validators.BadJWTExceptions;
import java.util.List;
import net.jcip.annotations.ThreadSafe;
import net.minidev.json.JSONObject;

@ThreadSafe
public class LogoutTokenClaimsVerifier
implements JWTClaimsSetVerifier {
    private final Issuer expectedIssuer;
    private final ClientID expectedClientID;

    public LogoutTokenClaimsVerifier(Issuer issuer, ClientID clientID) {
        if (issuer == null) {
            throw new IllegalArgumentException("The expected ID token issuer must not be null");
        }
        this.expectedIssuer = issuer;
        if (clientID == null) {
            throw new IllegalArgumentException("The client ID must not be null");
        }
        this.expectedClientID = clientID;
    }

    public Issuer getExpectedIssuer() {
        return this.expectedIssuer;
    }

    public ClientID getClientID() {
        return this.expectedClientID;
    }

    public void verify(JWTClaimsSet claimsSet, SecurityContext ctx) throws BadJWTException {
        try {
            JSONObject events = claimsSet.getJSONObjectClaim("events");
            if (events == null) {
                throw new BadJWTException("Missing JWT events (events) claim");
            }
            if (JSONObjectUtils.getJSONObject(events, "http://schemas.openid.net/event/backchannel-logout", null) == null) {
                throw new BadJWTException("Missing event type, required http://schemas.openid.net/event/backchannel-logout");
            }
        }
        catch (ParseException | java.text.ParseException e) {
            throw new BadJWTException("Invalid JWT events (events) claim");
        }
        String tokenIssuer = claimsSet.getIssuer();
        if (tokenIssuer == null) {
            throw BadJWTExceptions.MISSING_ISS_CLAIM_EXCEPTION;
        }
        if (!this.getExpectedIssuer().getValue().equals(tokenIssuer)) {
            throw new BadJWTException("Unexpected JWT issuer: " + tokenIssuer);
        }
        List<String> tokenAudience = claimsSet.getAudience();
        if (tokenAudience == null || tokenAudience.isEmpty()) {
            throw BadJWTExceptions.MISSING_AUD_CLAIM_EXCEPTION;
        }
        if (!tokenAudience.contains(this.expectedClientID.getValue())) {
            throw new BadJWTException("Unexpected JWT audience: " + tokenAudience);
        }
        if (claimsSet.getIssueTime() == null) {
            throw BadJWTExceptions.MISSING_IAT_CLAIM_EXCEPTION;
        }
        if (claimsSet.getJWTID() == null) {
            throw new BadJWTException("Missing JWT ID (jti) claim");
        }
        try {
            if (claimsSet.getSubject() == null && claimsSet.getStringClaim("sid") == null) {
                throw new BadJWTException("Missing subject (sub) and / or session ID (sid) claim(s)");
            }
        }
        catch (java.text.ParseException e) {
            throw new BadJWTException("Invalid session ID (sid) claim");
        }
        if (claimsSet.getClaim("nonce") != null) {
            throw new BadJWTException("Found illegal nonce (nonce) claim");
        }
    }
}

