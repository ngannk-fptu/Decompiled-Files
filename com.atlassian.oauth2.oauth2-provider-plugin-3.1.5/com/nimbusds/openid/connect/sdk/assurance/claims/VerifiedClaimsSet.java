/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.claims;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.assurance.IdentityVerification;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.PersonClaims;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

public class VerifiedClaimsSet
implements JSONAware {
    public static final String VERIFICATION_ELEMENT = "verification";
    public static final String CLAIMS_ELEMENT = "claims";
    private final IdentityVerification identityVerification;
    private final ClaimsSet claimsSet;

    public VerifiedClaimsSet(IdentityVerification verification, ClaimsSet claims) {
        if (verification == null) {
            throw new IllegalArgumentException("The verification must not be null");
        }
        this.identityVerification = verification;
        if (claims == null) {
            throw new IllegalArgumentException("The claims must not be null");
        }
        this.claimsSet = claims;
    }

    public IdentityVerification getVerification() {
        return this.identityVerification;
    }

    public PersonClaims getClaimsSet() {
        return new PersonClaims(this.claimsSet.toJSONObject());
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put(VERIFICATION_ELEMENT, this.identityVerification.toJSONObject());
        o.put(CLAIMS_ELEMENT, this.claimsSet.toJSONObject());
        return o;
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public static VerifiedClaimsSet parse(JSONObject jsonObject) throws ParseException {
        return new VerifiedClaimsSet(IdentityVerification.parse(JSONObjectUtils.getJSONObject(jsonObject, VERIFICATION_ELEMENT)), new PersonClaims(JSONObjectUtils.getJSONObject(jsonObject, CLAIMS_ELEMENT)));
    }
}

