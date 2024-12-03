/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.claims.ExternalClaims;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import net.minidev.json.JSONObject;

public class DistributedClaims
extends ExternalClaims {
    private final URI sourceEndpoint;
    private final AccessToken accessToken;

    public DistributedClaims(Set<String> names, URI sourceEndpoint, AccessToken accessToken) {
        this(UUID.randomUUID().toString(), names, sourceEndpoint, accessToken);
    }

    public DistributedClaims(String sourceID, Set<String> names, URI sourceEndpoint, AccessToken accessToken) {
        super(sourceID, names);
        if (sourceEndpoint == null) {
            throw new IllegalArgumentException("The claims source URI must not be null");
        }
        this.sourceEndpoint = sourceEndpoint;
        this.accessToken = accessToken;
    }

    public URI getSourceEndpoint() {
        return this.sourceEndpoint;
    }

    public AccessToken getAccessToken() {
        return this.accessToken;
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
        sourceSpec.put("endpoint", this.getSourceEndpoint().toString());
        if (this.getAccessToken() != null) {
            sourceSpec.put("access_token", this.getAccessToken().getValue());
        }
        JSONObject claimSourcesObject = new JSONObject();
        claimSourcesObject.put(this.getSourceID(), sourceSpec);
        if (jsonObject.containsKey("_claim_sources")) {
            ((JSONObject)jsonObject.get("_claim_sources")).putAll(claimSourcesObject);
        } else {
            jsonObject.put("_claim_sources", claimSourcesObject);
        }
    }
}

