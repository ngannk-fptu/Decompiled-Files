/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jwt.JWT
 *  com.nimbusds.jwt.JWTClaimsSet
 *  com.nimbusds.jwt.JWTParser
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.token.TypelessAccessToken;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.assurance.claims.VerifiedClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.AggregatedClaims;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.DistributedClaims;
import com.nimbusds.openid.connect.sdk.claims.ExternalClaimsUtils;
import com.nimbusds.openid.connect.sdk.claims.PersonClaims;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minidev.json.JSONObject;

public class UserInfo
extends PersonClaims {
    public static final String SUB_CLAIM_NAME = "sub";
    public static final String VERIFIED_CLAIMS_CLAIM_NAME = "verified_claims";

    public static Set<String> getStandardClaimNames() {
        HashSet<String> names = new HashSet<String>(PersonClaims.getStandardClaimNames());
        names.add(SUB_CLAIM_NAME);
        names.add(VERIFIED_CLAIMS_CLAIM_NAME);
        return Collections.unmodifiableSet(names);
    }

    public UserInfo(Subject sub) {
        this.setClaim(SUB_CLAIM_NAME, sub.getValue());
    }

    public UserInfo(JSONObject jsonObject) {
        super(jsonObject);
        if (this.getStringClaim(SUB_CLAIM_NAME) == null) {
            throw new IllegalArgumentException("Missing or invalid \"sub\" claim");
        }
    }

    public UserInfo(JWTClaimsSet jwtClaimsSet) {
        this(JSONObjectUtils.toJSONObject(jwtClaimsSet));
    }

    public void putAll(UserInfo other) {
        Subject otherSubject = other.getSubject();
        if (otherSubject == null) {
            throw new IllegalArgumentException("The subject of the other UserInfo is missing");
        }
        if (!otherSubject.equals(this.getSubject())) {
            throw new IllegalArgumentException("The subject of the other UserInfo must be identical");
        }
        Set<AggregatedClaims> savedAggregatedClaims = this.getAggregatedClaims();
        Set<DistributedClaims> savedDistributedClaims = this.getDistributedClaims();
        Set<AggregatedClaims> otherAggregatedClaims = other.getAggregatedClaims();
        Set<DistributedClaims> otherDistributedClaims = other.getDistributedClaims();
        HashSet<String> externalSourceIDs = new HashSet<String>();
        if (savedAggregatedClaims != null) {
            for (AggregatedClaims ac : savedAggregatedClaims) {
                externalSourceIDs.add(ac.getSourceID());
            }
        }
        if (savedDistributedClaims != null) {
            for (DistributedClaims dc : savedDistributedClaims) {
                externalSourceIDs.add(dc.getSourceID());
            }
        }
        if (otherAggregatedClaims != null) {
            for (AggregatedClaims ac : otherAggregatedClaims) {
                if (!externalSourceIDs.contains(ac.getSourceID())) continue;
                throw new IllegalArgumentException("Aggregated claims source ID conflict: " + ac.getSourceID());
            }
        }
        if (otherDistributedClaims != null) {
            for (DistributedClaims dc : otherDistributedClaims) {
                if (!externalSourceIDs.contains(dc.getSourceID())) continue;
                throw new IllegalArgumentException("Distributed claims source ID conflict: " + dc.getSourceID());
            }
        }
        this.putAll((ClaimsSet)other);
        if (savedAggregatedClaims != null) {
            for (AggregatedClaims ac : savedAggregatedClaims) {
                this.addAggregatedClaims(ac);
            }
        }
        if (savedDistributedClaims != null) {
            for (DistributedClaims dc : savedDistributedClaims) {
                this.addDistributedClaims(dc);
            }
        }
    }

    public Subject getSubject() {
        return new Subject(this.getStringClaim(SUB_CLAIM_NAME));
    }

    public List<VerifiedClaimsSet> getVerifiedClaims() {
        Object value = this.getClaim(VERIFIED_CLAIMS_CLAIM_NAME);
        if (value instanceof JSONObject) {
            try {
                return Collections.singletonList(VerifiedClaimsSet.parse((JSONObject)value));
            }
            catch (ParseException e) {
                return null;
            }
        }
        if (value instanceof List) {
            List rawList = (List)value;
            if (rawList.isEmpty()) {
                return null;
            }
            LinkedList<VerifiedClaimsSet> list = new LinkedList<VerifiedClaimsSet>();
            for (Object item : rawList) {
                if (item instanceof JSONObject) {
                    try {
                        list.add(VerifiedClaimsSet.parse((JSONObject)item));
                        continue;
                    }
                    catch (ParseException e) {
                        return null;
                    }
                }
                return null;
            }
            return list;
        }
        return null;
    }

    public void setVerifiedClaims(VerifiedClaimsSet verifiedClaims) {
        if (verifiedClaims != null) {
            this.setClaim(VERIFIED_CLAIMS_CLAIM_NAME, verifiedClaims.toJSONObject());
        } else {
            this.setClaim(VERIFIED_CLAIMS_CLAIM_NAME, null);
        }
    }

    public void setVerifiedClaims(List<VerifiedClaimsSet> verifiedClaimsList) {
        if (verifiedClaimsList != null) {
            LinkedList<JSONObject> jsonObjects = new LinkedList<JSONObject>();
            for (VerifiedClaimsSet verifiedClaims : verifiedClaimsList) {
                if (verifiedClaims == null) continue;
                jsonObjects.add(verifiedClaims.toJSONObject());
            }
            this.setClaim(VERIFIED_CLAIMS_CLAIM_NAME, jsonObjects);
        } else {
            this.setClaim(VERIFIED_CLAIMS_CLAIM_NAME, null);
        }
    }

    public void addAggregatedClaims(AggregatedClaims aggregatedClaims) {
        if (aggregatedClaims == null) {
            return;
        }
        aggregatedClaims.mergeInto(this.claims);
    }

    public Set<AggregatedClaims> getAggregatedClaims() {
        Map<String, JSONObject> claimSources = ExternalClaimsUtils.getExternalClaimSources(this.claims);
        if (claimSources == null) {
            return null;
        }
        HashSet<AggregatedClaims> aggregatedClaimsSet = new HashSet<AggregatedClaims>();
        for (Map.Entry<String, JSONObject> en : claimSources.entrySet()) {
            JWT claimsJWT;
            String sourceID = en.getKey();
            JSONObject sourceSpec = en.getValue();
            Object jwtValue = sourceSpec.get((Object)"JWT");
            if (!(jwtValue instanceof String)) continue;
            try {
                claimsJWT = JWTParser.parse((String)((String)jwtValue));
            }
            catch (java.text.ParseException e) {
                continue;
            }
            Set<String> claimNames = ExternalClaimsUtils.getExternalClaimNamesForSource(this.claims, sourceID);
            if (claimNames.isEmpty()) continue;
            aggregatedClaimsSet.add(new AggregatedClaims(sourceID, claimNames, claimsJWT));
        }
        if (aggregatedClaimsSet.isEmpty()) {
            return null;
        }
        return aggregatedClaimsSet;
    }

    public void addDistributedClaims(DistributedClaims distributedClaims) {
        if (distributedClaims == null) {
            return;
        }
        distributedClaims.mergeInto(this.claims);
    }

    public Set<DistributedClaims> getDistributedClaims() {
        Map<String, JSONObject> claimSources = ExternalClaimsUtils.getExternalClaimSources(this.claims);
        if (claimSources == null) {
            return null;
        }
        HashSet<DistributedClaims> distributedClaimsSet = new HashSet<DistributedClaims>();
        for (Map.Entry<String, JSONObject> en : claimSources.entrySet()) {
            Set<String> claimNames;
            URI endpoint;
            String sourceID = en.getKey();
            JSONObject sourceSpec = en.getValue();
            Object endpointValue = sourceSpec.get((Object)"endpoint");
            if (!(endpointValue instanceof String)) continue;
            try {
                endpoint = new URI((String)endpointValue);
            }
            catch (URISyntaxException e) {
                continue;
            }
            TypelessAccessToken accessToken = null;
            Object accessTokenValue = sourceSpec.get((Object)"access_token");
            if (accessTokenValue instanceof String) {
                accessToken = new TypelessAccessToken((String)accessTokenValue);
            }
            if ((claimNames = ExternalClaimsUtils.getExternalClaimNamesForSource(this.claims, sourceID)).isEmpty()) continue;
            distributedClaimsSet.add(new DistributedClaims(sourceID, claimNames, endpoint, accessToken));
        }
        if (distributedClaimsSet.isEmpty()) {
            return null;
        }
        return distributedClaimsSet;
    }

    public static UserInfo parse(String json) throws ParseException {
        JSONObject jsonObject = JSONObjectUtils.parse(json);
        try {
            return new UserInfo(jsonObject);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}

