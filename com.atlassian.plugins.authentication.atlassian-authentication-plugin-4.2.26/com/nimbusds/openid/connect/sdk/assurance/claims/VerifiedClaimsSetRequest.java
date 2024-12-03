/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.claims;

import com.nimbusds.langtag.LangTag;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSetRequest;
import java.util.Collection;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class VerifiedClaimsSetRequest
extends ClaimsSetRequest {
    private final JSONObject verificationJSONObject;

    public VerifiedClaimsSetRequest() {
        this.verificationJSONObject = null;
    }

    public VerifiedClaimsSetRequest(Collection<ClaimsSetRequest.Entry> entries, JSONObject verificationJSONObject) {
        super(entries);
        this.verificationJSONObject = verificationJSONObject;
    }

    public JSONObject getVerificationJSONObject() {
        return this.verificationJSONObject;
    }

    public VerifiedClaimsSetRequest withVerificationJSONObject(JSONObject jsonObject) {
        return new VerifiedClaimsSetRequest(this.getEntries(), jsonObject);
    }

    @Override
    public VerifiedClaimsSetRequest add(String claimName) {
        VerifiedClaimsSetRequest csr = this.add(new ClaimsSetRequest.Entry(claimName));
        return new VerifiedClaimsSetRequest(csr.getEntries(), this.getVerificationJSONObject());
    }

    @Override
    public VerifiedClaimsSetRequest add(ClaimsSetRequest.Entry entry) {
        ClaimsSetRequest csr = super.add(entry);
        return new VerifiedClaimsSetRequest(csr.getEntries(), this.getVerificationJSONObject());
    }

    @Override
    public VerifiedClaimsSetRequest delete(String claimName, LangTag langTag) {
        ClaimsSetRequest csr = super.delete(claimName, langTag);
        return new VerifiedClaimsSetRequest(csr.getEntries(), this.getVerificationJSONObject());
    }

    @Override
    public VerifiedClaimsSetRequest delete(String claimName) {
        ClaimsSetRequest csr = super.delete(claimName);
        return new VerifiedClaimsSetRequest(csr.getEntries(), this.getVerificationJSONObject());
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject claims;
        JSONObject o = new JSONObject();
        if (this.verificationJSONObject != null && !this.verificationJSONObject.isEmpty()) {
            o.put("verification", this.verificationJSONObject);
        }
        if ((claims = super.toJSONObject()) != null && !claims.isEmpty()) {
            o.put("claims", claims);
        }
        return o;
    }

    public static VerifiedClaimsSetRequest parse(JSONObject jsonObject) throws ParseException {
        JSONObject verificationJSONObject = JSONObjectUtils.getJSONObject(jsonObject, "verification", null);
        JSONObject claimsJSONObject = JSONObjectUtils.getJSONObject(jsonObject, "claims", new JSONObject());
        if (claimsJSONObject.isEmpty()) {
            throw new ParseException("Empty verified claims object");
        }
        return new VerifiedClaimsSetRequest(ClaimsSetRequest.parse(claimsJSONObject).getEntries(), verificationJSONObject);
    }

    public static VerifiedClaimsSetRequest parse(String json) throws ParseException {
        return VerifiedClaimsSetRequest.parse(JSONObjectUtils.parse(json));
    }
}

