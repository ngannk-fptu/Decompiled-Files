/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.jwt.JWT;
import com.nimbusds.openid.connect.sdk.claims.ExternalClaims;
import java.util.Set;
import java.util.UUID;
import net.minidev.json.JSONObject;

public class AggregatedClaims
extends ExternalClaims {
    private final JWT claimsJWT;

    public AggregatedClaims(Set<String> names, JWT claimsJWT) {
        this(UUID.randomUUID().toString(), names, claimsJWT);
    }

    public AggregatedClaims(String sourceID, Set<String> names, JWT claimsJWT) {
        super(sourceID, names);
        if (claimsJWT == null) {
            throw new IllegalArgumentException("The claims JWT must not be null");
        }
        this.claimsJWT = claimsJWT;
    }

    public JWT getClaimsJWT() {
        return this.claimsJWT;
    }

    @Override
    void mergeInto(JSONObject jsonObject) {
        JSONObject claimNamesObject = new JSONObject();
        for (String name : this.getNames()) {
            claimNamesObject.put(name, this.getSourceID());
        }
        if (jsonObject.containsKey("_claim_names")) {
            ((JSONObject)jsonObject.get("_claim_names")).putAll(claimNamesObject);
        } else {
            jsonObject.put("_claim_names", claimNamesObject);
        }
        JSONObject sourceSpec = new JSONObject();
        sourceSpec.put("JWT", this.getClaimsJWT().serialize());
        JSONObject claimSourcesObject = new JSONObject();
        claimSourcesObject.put(this.getSourceID(), sourceSpec);
        if (jsonObject.containsKey("_claim_sources")) {
            ((JSONObject)jsonObject.get("_claim_sources")).putAll(claimSourcesObject);
        } else {
            jsonObject.put("_claim_sources", claimSourcesObject);
        }
    }
}

