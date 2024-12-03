/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.entities;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.util.DateUtils;
import com.nimbusds.oauth2.sdk.id.Audience;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import net.jcip.annotations.Immutable;

@Immutable
public class EntityStatementClaimsVerifier
extends DefaultJWTClaimsVerifier {
    private final boolean isSelfIssued;

    public EntityStatementClaimsVerifier() {
        super(null, new HashSet<String>(Arrays.asList("iss", "sub", "iat", "exp", "jwks")));
        this.isSelfIssued = true;
    }

    public EntityStatementClaimsVerifier(Audience expectedAudience) {
        super(expectedAudience != null ? expectedAudience.getValue() : null, null, new HashSet<String>(Arrays.asList("iss", "sub", "iat", "exp")));
        this.isSelfIssued = false;
    }

    @Override
    public void verify(JWTClaimsSet claimsSet, SecurityContext context) throws BadJWTException {
        super.verify(claimsSet, context);
        if (this.isSelfIssued && !claimsSet.getIssuer().equals(claimsSet.getSubject())) {
            throw new BadJWTException("JWT not self-issued");
        }
        Date now = new Date();
        if (!DateUtils.isBefore(claimsSet.getIssueTime(), now, this.getMaxClockSkew())) {
            throw new BadJWTException("JWT issue time after current time");
        }
    }
}

