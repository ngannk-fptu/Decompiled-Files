/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.dpop.verifiers;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPIssuer;
import com.nimbusds.oauth2.sdk.dpop.verifiers.DPoPProofContext;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.oauth2.sdk.util.URIUtils;
import com.nimbusds.oauth2.sdk.util.singleuse.AlreadyUsedException;
import com.nimbusds.oauth2.sdk.util.singleuse.SingleUseChecker;
import java.net.URI;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
class DPoPProofClaimsSetVerifier
extends DefaultJWTClaimsVerifier<DPoPProofContext> {
    private final long maxClockSkewSeconds;
    private final SingleUseChecker<Map.Entry<DPoPIssuer, JWTID>> singleUseChecker;

    public DPoPProofClaimsSetVerifier(String acceptedMethod, URI acceptedURI, long maxClockSkewSeconds, boolean requireATH, SingleUseChecker<Map.Entry<DPoPIssuer, JWTID>> singleUseChecker) {
        super(new JWTClaimsSet.Builder().claim("htm", acceptedMethod).claim("htu", URIUtils.getBaseURI(acceptedURI).toString()).build(), new HashSet<String>(requireATH ? Arrays.asList("jti", "iat", "ath") : Arrays.asList("jti", "iat")));
        this.maxClockSkewSeconds = maxClockSkewSeconds;
        this.singleUseChecker = singleUseChecker;
    }

    @Override
    public void verify(JWTClaimsSet claimsSet, DPoPProofContext context) throws BadJWTException {
        super.verify(claimsSet, context);
        Date iat = claimsSet.getIssueTime();
        Date now = new Date();
        Date maxPast = new Date(now.getTime() - this.maxClockSkewSeconds * 1000L);
        Date maxAhead = new Date(now.getTime() + this.maxClockSkewSeconds * 1000L);
        if (iat.before(maxPast)) {
            throw new BadJWTException("The JWT iat claim is behind the current time by more than " + this.maxClockSkewSeconds + " seconds");
        }
        if (iat.after(maxAhead)) {
            throw new BadJWTException("The JWT iat claim is ahead of the current time by more than " + this.maxClockSkewSeconds + " seconds");
        }
        if (this.singleUseChecker != null) {
            JWTID jti = new JWTID(claimsSet.getJWTID());
            try {
                this.singleUseChecker.markAsUsed(new AbstractMap.SimpleImmutableEntry<DPoPIssuer, JWTID>(context.getIssuer(), jti));
            }
            catch (AlreadyUsedException e) {
                throw new BadJWTException("The jti was used before: " + jti);
            }
        }
        if (this.getRequiredClaims().contains("ath")) {
            Base64URL ath;
            try {
                ath = new Base64URL(claimsSet.getStringClaim("ath"));
            }
            catch (ParseException e) {
                throw new BadJWTException("Invalid ath claim: " + e.getMessage(), e);
            }
            context.setAccessTokenHash(ath);
        }
    }
}

