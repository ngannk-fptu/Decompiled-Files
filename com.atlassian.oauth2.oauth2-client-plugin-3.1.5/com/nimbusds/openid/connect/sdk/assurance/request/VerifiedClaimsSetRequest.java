/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.request;

import com.nimbusds.langtag.LangTag;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.oauth2.sdk.util.MapUtils;
import com.nimbusds.openid.connect.sdk.assurance.request.MinimalVerificationSpec;
import com.nimbusds.openid.connect.sdk.assurance.request.VerificationSpec;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSetRequest;
import java.util.Collection;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class VerifiedClaimsSetRequest
extends ClaimsSetRequest {
    private final VerificationSpec verification;

    public VerifiedClaimsSetRequest() {
        this.verification = new MinimalVerificationSpec();
    }

    public VerifiedClaimsSetRequest(Collection<ClaimsSetRequest.Entry> entries, VerificationSpec verification) {
        super(entries);
        if (verification == null) {
            throw new IllegalArgumentException("The verification element must not be null");
        }
        this.verification = verification;
    }

    public VerificationSpec getVerification() {
        return this.verification;
    }

    public VerifiedClaimsSetRequest withVerification(VerificationSpec verification) {
        return new VerifiedClaimsSetRequest(this.getEntries(), verification);
    }

    @Override
    public VerifiedClaimsSetRequest add(String claimName) {
        VerifiedClaimsSetRequest csr = this.add(new ClaimsSetRequest.Entry(claimName));
        return new VerifiedClaimsSetRequest(csr.getEntries(), this.getVerification());
    }

    @Override
    public VerifiedClaimsSetRequest add(ClaimsSetRequest.Entry entry) {
        ClaimsSetRequest csr = super.add(entry);
        return new VerifiedClaimsSetRequest(csr.getEntries(), this.getVerification());
    }

    @Override
    public VerifiedClaimsSetRequest delete(String claimName, LangTag langTag) {
        ClaimsSetRequest csr = super.delete(claimName, langTag);
        return new VerifiedClaimsSetRequest(csr.getEntries(), this.getVerification());
    }

    @Override
    public VerifiedClaimsSetRequest delete(String claimName) {
        ClaimsSetRequest csr = super.delete(claimName);
        return new VerifiedClaimsSetRequest(csr.getEntries(), this.getVerification());
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        o.put("verification", this.getVerification().toJSONObject());
        JSONObject claims = super.toJSONObject();
        if (MapUtils.isEmpty(claims)) {
            throw new IllegalStateException("Empty verified claims object");
        }
        o.put("claims", claims);
        return o;
    }

    public static VerifiedClaimsSetRequest parse(JSONObject jsonObject) throws ParseException {
        MinimalVerificationSpec verification = MinimalVerificationSpec.parse(JSONObjectUtils.getJSONObject(jsonObject, "verification"));
        JSONObject claimsJSONObject = JSONObjectUtils.getJSONObject(jsonObject, "claims", new JSONObject());
        if (claimsJSONObject.isEmpty()) {
            throw new ParseException("Empty verified claims object");
        }
        return new VerifiedClaimsSetRequest(ClaimsSetRequest.parse(claimsJSONObject).getEntries(), verification);
    }

    public static VerifiedClaimsSetRequest parse(String json) throws ParseException {
        return VerifiedClaimsSetRequest.parse(JSONObjectUtils.parse(json));
    }
}

