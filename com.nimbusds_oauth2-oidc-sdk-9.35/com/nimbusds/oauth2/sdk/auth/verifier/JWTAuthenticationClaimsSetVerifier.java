/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.proc.SecurityContext
 *  com.nimbusds.jwt.JWTClaimsSet
 *  com.nimbusds.jwt.proc.BadJWTException
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.auth.verifier;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.oauth2.sdk.assertions.jwt.JWTAssertionDetailsVerifier;
import com.nimbusds.oauth2.sdk.id.Audience;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
class JWTAuthenticationClaimsSetVerifier
extends JWTAssertionDetailsVerifier {
    private static final BadJWTException ISS_SUB_MISMATCH_EXCEPTION = new BadJWTException("Issuer and subject JWT claims don't match");

    public JWTAuthenticationClaimsSetVerifier(Set<Audience> expectedAudience) {
        super(expectedAudience);
    }

    @Override
    public void verify(JWTClaimsSet claimsSet, SecurityContext securityContext) throws BadJWTException {
        super.verify(claimsSet, securityContext);
        if (!claimsSet.getIssuer().equals(claimsSet.getSubject())) {
            throw ISS_SUB_MISMATCH_EXCEPTION;
        }
    }
}

