/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.ciba;

import com.nimbusds.oauth2.sdk.ciba.CIBARequest;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.JWTID;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CIBASignedRequestClaimsSet
extends ClaimsSet {
    public static final String REQUEST_CLAIM_NAME = "request";
    public static final String IAT_CLAIM_NAME = "iat";
    public static final String NBF_CLAIM_NAME = "nbf";
    public static final String EXP_CLAIM_NAME = "exp";
    public static final String JTI_CLAIM_NAME = "jti";
    private static final Set<String> STD_CLAIM_NAMES;

    public static Set<String> getStandardClaimNames() {
        return STD_CLAIM_NAMES;
    }

    public CIBASignedRequestClaimsSet(CIBARequest cibaPlainRequest, Issuer iss, Audience aud, Date iat, Date nbf, Date exp, JWTID jti) {
        if (cibaPlainRequest.isSigned()) {
            throw new IllegalArgumentException("The CIBA request must be plain");
        }
        for (Map.Entry claim : cibaPlainRequest.toJWTClaimsSet().getClaims().entrySet()) {
            this.setClaim((String)claim.getKey(), claim.getValue());
        }
        this.setIssuer(Objects.requireNonNull(iss));
        this.setAudience(Objects.requireNonNull(aud));
        this.setDateClaim(IAT_CLAIM_NAME, Objects.requireNonNull(iat));
        this.setDateClaim(NBF_CLAIM_NAME, Objects.requireNonNull(nbf));
        this.setDateClaim(EXP_CLAIM_NAME, Objects.requireNonNull(exp));
        this.setClaim(JTI_CLAIM_NAME, jti.getValue());
    }

    static {
        HashSet<String> claimNames = new HashSet<String>(ClaimsSet.getStandardClaimNames());
        claimNames.add(REQUEST_CLAIM_NAME);
        claimNames.add("iss");
        claimNames.add("aud");
        claimNames.add(IAT_CLAIM_NAME);
        claimNames.add(NBF_CLAIM_NAME);
        claimNames.add(EXP_CLAIM_NAME);
        claimNames.add(JTI_CLAIM_NAME);
        STD_CLAIM_NAMES = Collections.unmodifiableSet(claimNames);
    }
}

