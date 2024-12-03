/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.type.TypeReference
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.fasterxml.jackson.databind.ObjectReader
 *  com.fasterxml.jackson.databind.node.ObjectNode
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microsoft.aad.msal4j.MsalClientException;
import com.microsoft.aad.msal4j.RequestedClaim;
import com.microsoft.aad.msal4j.RequestedClaimAdditionalInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ClaimsRequest {
    List<RequestedClaim> idTokenRequestedClaims = new ArrayList<RequestedClaim>();
    List<RequestedClaim> userInfoRequestedClaims = new ArrayList<RequestedClaim>();
    List<RequestedClaim> accessTokenRequestedClaims = new ArrayList<RequestedClaim>();

    public void requestClaimInIdToken(String claim, RequestedClaimAdditionalInfo requestedClaimAdditionalInfo) {
        this.idTokenRequestedClaims.add(new RequestedClaim(claim, requestedClaimAdditionalInfo));
    }

    protected void requestClaimInUserInfo(String claim, RequestedClaimAdditionalInfo requestedClaimAdditionalInfo) {
        this.userInfoRequestedClaims.add(new RequestedClaim(claim, requestedClaimAdditionalInfo));
    }

    protected void requestClaimInAccessToken(String claim, RequestedClaimAdditionalInfo requestedClaimAdditionalInfo) {
        this.accessTokenRequestedClaims.add(new RequestedClaim(claim, requestedClaimAdditionalInfo));
    }

    public String formatAsJSONString() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        if (!this.idTokenRequestedClaims.isEmpty()) {
            rootNode.set("id_token", (JsonNode)this.convertClaimsToObjectNode(this.idTokenRequestedClaims));
        }
        if (!this.userInfoRequestedClaims.isEmpty()) {
            rootNode.set("userinfo", (JsonNode)this.convertClaimsToObjectNode(this.userInfoRequestedClaims));
        }
        if (!this.accessTokenRequestedClaims.isEmpty()) {
            rootNode.set("access_token", (JsonNode)this.convertClaimsToObjectNode(this.accessTokenRequestedClaims));
        }
        return mapper.valueToTree((Object)rootNode).toString();
    }

    private ObjectNode convertClaimsToObjectNode(List<RequestedClaim> claims) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode claimsNode = mapper.createObjectNode();
        for (RequestedClaim claim : claims) {
            claimsNode.setAll((ObjectNode)mapper.valueToTree((Object)claim));
        }
        return claimsNode;
    }

    public static ClaimsRequest formatAsClaimsRequest(String claims) {
        try {
            ClaimsRequest cr = new ClaimsRequest();
            ObjectMapper mapper = new ObjectMapper();
            ObjectReader reader = mapper.readerFor((TypeReference)new TypeReference<List<String>>(){});
            JsonNode jsonClaims = mapper.readTree(claims);
            ClaimsRequest.addClaimsFromJsonNode(jsonClaims.get("id_token"), "id_token", cr, reader);
            ClaimsRequest.addClaimsFromJsonNode(jsonClaims.get("userinfo"), "userinfo", cr, reader);
            ClaimsRequest.addClaimsFromJsonNode(jsonClaims.get("access_token"), "access_token", cr, reader);
            return cr;
        }
        catch (IOException e) {
            throw new MsalClientException("Could not convert string to ClaimsRequest: " + e.getMessage(), "invalid_json");
        }
    }

    private static void addClaimsFromJsonNode(JsonNode claims, String group, ClaimsRequest cr, ObjectReader reader) throws IOException {
        if (claims != null) {
            Iterator claimsIterator = claims.fieldNames();
            while (claimsIterator.hasNext()) {
                String claim = (String)claimsIterator.next();
                Boolean essential = null;
                String value = null;
                List values = null;
                RequestedClaimAdditionalInfo claimInfo = null;
                if (claims.get(claim).has("essential")) {
                    essential = claims.get(claim).get("essential").asBoolean();
                }
                if (claims.get(claim).has("value")) {
                    value = claims.get(claim).get("value").textValue();
                }
                if (claims.get(claim).has("values")) {
                    values = (List)reader.readValue(claims.get(claim).get("values"));
                }
                if (essential != null || value != null || values != null) {
                    claimInfo = new RequestedClaimAdditionalInfo(essential == null ? false : essential, value, values);
                }
                if (group.equals("id_token")) {
                    cr.requestClaimInIdToken(claim, claimInfo);
                }
                if (group.equals("userinfo")) {
                    cr.requestClaimInUserInfo(claim, claimInfo);
                }
                if (!group.equals("access_token")) continue;
                cr.requestClaimInAccessToken(claim, claimInfo);
            }
        }
    }

    public List<RequestedClaim> getIdTokenRequestedClaims() {
        return this.idTokenRequestedClaims;
    }

    public void setIdTokenRequestedClaims(List<RequestedClaim> idTokenRequestedClaims) {
        this.idTokenRequestedClaims = idTokenRequestedClaims;
    }
}

