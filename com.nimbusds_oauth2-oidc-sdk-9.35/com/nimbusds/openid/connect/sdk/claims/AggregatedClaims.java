/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.JWT
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.jwt.JWT;
import com.nimbusds.openid.connect.sdk.claims.ExternalClaims;
import java.util.Map;
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
            claimNamesObject.put((Object)name, (Object)this.getSourceID());
        }
        if (jsonObject.containsKey((Object)"_claim_names")) {
            ((JSONObject)jsonObject.get((Object)"_claim_names")).putAll((Map)claimNamesObject);
        } else {
            jsonObject.put((Object)"_claim_names", (Object)claimNamesObject);
        }
        JSONObject sourceSpec = new JSONObject();
        sourceSpec.put((Object)"JWT", (Object)this.getClaimsJWT().serialize());
        JSONObject claimSourcesObject = new JSONObject();
        claimSourcesObject.put((Object)this.getSourceID(), (Object)sourceSpec);
        if (jsonObject.containsKey((Object)"_claim_sources")) {
            ((JSONObject)jsonObject.get((Object)"_claim_sources")).putAll((Map)claimSourcesObject);
        } else {
            jsonObject.put((Object)"_claim_sources", (Object)claimSourcesObject);
        }
    }
}

