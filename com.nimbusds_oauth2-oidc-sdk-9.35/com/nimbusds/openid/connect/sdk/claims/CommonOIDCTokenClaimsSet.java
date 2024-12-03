/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.openid.connect.sdk.claims.CommonClaimsSet;
import com.nimbusds.openid.connect.sdk.claims.SessionID;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minidev.json.JSONObject;

abstract class CommonOIDCTokenClaimsSet
extends CommonClaimsSet {
    public static final String SID_CLAIM_NAME = "sid";
    private static final Set<String> STD_CLAIM_NAMES;

    public static Set<String> getStandardClaimNames() {
        return STD_CLAIM_NAMES;
    }

    protected CommonOIDCTokenClaimsSet() {
    }

    protected CommonOIDCTokenClaimsSet(JSONObject jsonObject) {
        super(jsonObject);
    }

    public SessionID getSessionID() {
        String val = this.getStringClaim(SID_CLAIM_NAME);
        return val != null ? new SessionID(val) : null;
    }

    public void setSessionID(SessionID sid) {
        this.setClaim(SID_CLAIM_NAME, sid != null ? sid.getValue() : null);
    }

    static {
        HashSet<String> claimNames = new HashSet<String>(CommonClaimsSet.getStandardClaimNames());
        claimNames.add(SID_CLAIM_NAME);
        STD_CLAIM_NAMES = Collections.unmodifiableSet(claimNames);
    }
}

