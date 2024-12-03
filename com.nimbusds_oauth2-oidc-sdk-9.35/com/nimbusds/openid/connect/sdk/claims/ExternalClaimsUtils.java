/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.claims;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minidev.json.JSONObject;

class ExternalClaimsUtils {
    static Map<String, JSONObject> getExternalClaimSources(JSONObject claims) {
        Object o = claims.get((Object)"_claim_sources");
        if (!(o instanceof JSONObject)) {
            return null;
        }
        JSONObject claimSources = (JSONObject)o;
        if (claimSources.isEmpty()) {
            return null;
        }
        HashMap<String, JSONObject> out = new HashMap<String, JSONObject>();
        for (Map.Entry en : claimSources.entrySet()) {
            String sourceID = (String)en.getKey();
            Object v = en.getValue();
            if (!(v instanceof JSONObject)) continue;
            JSONObject sourceSpec = (JSONObject)v;
            out.put(sourceID, sourceSpec);
        }
        if (out.isEmpty()) {
            return null;
        }
        return out;
    }

    static Set<String> getExternalClaimNamesForSource(JSONObject claims, String sourceID) {
        if (claims == null || sourceID == null) {
            return Collections.emptySet();
        }
        Object claimNamesObject = claims.get((Object)"_claim_names");
        if (!(claimNamesObject instanceof JSONObject)) {
            return Collections.emptySet();
        }
        JSONObject claimNamesJSONObject = (JSONObject)claimNamesObject;
        HashSet<String> claimNames = new HashSet<String>();
        for (Map.Entry en : claimNamesJSONObject.entrySet()) {
            if (sourceID.equals(en.getValue())) {
                claimNames.add((String)en.getKey());
            }
            if (!(en.getValue() instanceof List)) continue;
            for (Object item : (List)en.getValue()) {
                if (!(item instanceof String) || !sourceID.equals(item)) continue;
                claimNames.add((String)en.getKey());
            }
        }
        return claimNames;
    }

    private ExternalClaimsUtils() {
    }
}

