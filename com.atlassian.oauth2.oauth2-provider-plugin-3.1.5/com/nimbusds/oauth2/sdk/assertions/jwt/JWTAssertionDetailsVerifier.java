/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.assertions.jwt;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public class JWTAssertionDetailsVerifier
extends DefaultJWTClaimsVerifier {
    private static final BadJWTException MISSING_EXP_CLAIM_EXCEPTION = new BadJWTException("Missing JWT expiration claim");
    private static final BadJWTException MISSING_AUD_CLAIM_EXCEPTION = new BadJWTException("Missing JWT audience claim");
    private static final BadJWTException MISSING_SUB_CLAIM_EXCEPTION = new BadJWTException("Missing JWT subject claim");
    private static final BadJWTException MISSING_ISS_CLAIM_EXCEPTION = new BadJWTException("Missing JWT issuer claim");
    private final Set<Audience> expectedAudience;

    public JWTAssertionDetailsVerifier(Set<Audience> expectedAudience) {
        if (CollectionUtils.isEmpty(expectedAudience)) {
            throw new IllegalArgumentException("The expected audience set must not be null or empty");
        }
        this.expectedAudience = expectedAudience;
    }

    public Set<Audience> getExpectedAudience() {
        return this.expectedAudience;
    }

    @Override
    public void verify(JWTClaimsSet claimsSet, SecurityContext securityContext) throws BadJWTException {
        super.verify(claimsSet, null);
        if (claimsSet.getExpirationTime() == null) {
            throw MISSING_EXP_CLAIM_EXCEPTION;
        }
        if (claimsSet.getAudience() == null || claimsSet.getAudience().isEmpty()) {
            throw MISSING_AUD_CLAIM_EXCEPTION;
        }
        boolean audMatch = false;
        for (String aud : claimsSet.getAudience()) {
            if (aud == null || aud.isEmpty() || !this.expectedAudience.contains(new Audience(aud))) continue;
            audMatch = true;
        }
        if (!audMatch) {
            throw new BadJWTException("Invalid JWT audience claim, expected " + this.expectedAudience);
        }
        if (claimsSet.getIssuer() == null) {
            throw MISSING_ISS_CLAIM_EXCEPTION;
        }
        if (claimsSet.getSubject() == null) {
            throw MISSING_SUB_CLAIM_EXCEPTION;
        }
    }
}

