/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.openid.connect.sdk.claims.ExternalClaims;
import java.net.URI;
import java.util.Map;
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
            claimNamesObject.put((Object)name, (Object)this.getSourceID());
        }
        if (jsonObject.containsKey((Object)"_claim_names")) {
            ((JSONObject)jsonObject.get((Object)"_claim_names")).putAll((Map)claimNamesObject);
        } else {
            jsonObject.put((Object)"_claim_names", (Object)claimNamesObject);
        }
        JSONObject sourceSpec = new JSONObject();
        sourceSpec.put((Object)"endpoint", (Object)this.getSourceEndpoint().toString());
        if (this.getAccessToken() != null) {
            sourceSpec.put((Object)"access_token", (Object)this.getAccessToken().getValue());
        }
        JSONObject claimSourcesObject = new JSONObject();
        claimSourcesObject.put((Object)this.getSourceID(), (Object)sourceSpec);
        if (jsonObject.containsKey((Object)"_claim_sources")) {
            ((JSONObject)jsonObject.get((Object)"_claim_sources")).putAll((Map)claimSourcesObject);
        } else {
            jsonObject.put((Object)"_claim_sources", (Object)claimSourcesObject);
        }
    }
}

