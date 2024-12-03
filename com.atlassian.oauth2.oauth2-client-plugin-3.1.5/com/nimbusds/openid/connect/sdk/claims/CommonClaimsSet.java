/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import net.minidev.json.JSONObject;

public abstract class CommonClaimsSet
extends ClaimsSet {
    public static final String SUB_CLAIM_NAME = "sub";
    public static final String IAT_CLAIM_NAME = "iat";
    private static final Set<String> STD_CLAIM_NAMES;

    public static Set<String> getStandardClaimNames() {
        return STD_CLAIM_NAMES;
    }

    protected CommonClaimsSet() {
    }

    protected CommonClaimsSet(JSONObject jsonObject) {
        super(jsonObject);
    }

    public Subject getSubject() {
        String val = this.getStringClaim(SUB_CLAIM_NAME);
        return val != null ? new Subject(val) : null;
    }

    public Date getIssueTime() {
        return this.getDateClaim(IAT_CLAIM_NAME);
    }

    static {
        HashSet<String> claimNames = new HashSet<String>(ClaimsSet.getStandardClaimNames());
        claimNames.add(SUB_CLAIM_NAME);
        claimNames.add(IAT_CLAIM_NAME);
        STD_CLAIM_NAMES = Collections.unmodifiableSet(claimNames);
    }
}

