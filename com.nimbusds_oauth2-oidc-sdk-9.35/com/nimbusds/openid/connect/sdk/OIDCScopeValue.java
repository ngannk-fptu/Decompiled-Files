/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.openid.connect.sdk.ClaimsRequest;
import com.nimbusds.openid.connect.sdk.claims.ClaimRequirement;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSetRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minidev.json.JSONObject;

public class OIDCScopeValue
extends Scope.Value {
    private static final long serialVersionUID = -652181533676125742L;
    public static final OIDCScopeValue OPENID = new OIDCScopeValue("openid", Scope.Value.Requirement.REQUIRED, new String[]{"sub"});
    public static final OIDCScopeValue PROFILE = new OIDCScopeValue("profile", new String[]{"name", "family_name", "given_name", "middle_name", "nickname", "preferred_username", "profile", "picture", "website", "gender", "birthdate", "zoneinfo", "locale", "updated_at"});
    public static final OIDCScopeValue EMAIL = new OIDCScopeValue("email", new String[]{"email", "email_verified"});
    public static final OIDCScopeValue ADDRESS = new OIDCScopeValue("address", new String[]{"address"});
    public static final OIDCScopeValue PHONE = new OIDCScopeValue("phone", new String[]{"phone_number", "phone_number_verified"});
    public static final OIDCScopeValue OFFLINE_ACCESS = new OIDCScopeValue("offline_access", null);
    private final Set<String> claims;

    public static OIDCScopeValue[] values() {
        return new OIDCScopeValue[]{OPENID, PROFILE, EMAIL, ADDRESS, PHONE, OFFLINE_ACCESS};
    }

    private OIDCScopeValue(String value, Scope.Value.Requirement requirement, String[] claims) {
        super(value, requirement);
        this.claims = claims != null ? Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList(claims))) : null;
    }

    private OIDCScopeValue(String value, String[] claims) {
        this(value, Scope.Value.Requirement.OPTIONAL, claims);
    }

    public Set<String> getClaimNames() {
        return this.claims;
    }

    public JSONObject toClaimsRequestJSONObject() {
        JSONObject req = new JSONObject();
        if (this.claims == null) {
            return null;
        }
        for (String claim : this.claims) {
            if (this.getRequirement() == Scope.Value.Requirement.REQUIRED) {
                JSONObject details = new JSONObject();
                details.put((Object)"essential", (Object)true);
                req.put((Object)claim, (Object)details);
                continue;
            }
            req.put((Object)claim, null);
        }
        return req;
    }

    @Deprecated
    public Set<ClaimsRequest.Entry> toClaimsRequestEntries() {
        HashSet<ClaimsRequest.Entry> entries = new HashSet<ClaimsRequest.Entry>();
        if (this == OPENID || this == OFFLINE_ACCESS) {
            return Collections.unmodifiableSet(entries);
        }
        for (String claimName : this.getClaimNames()) {
            entries.add(new ClaimsRequest.Entry(claimName).withClaimRequirement(ClaimRequirement.VOLUNTARY));
        }
        return Collections.unmodifiableSet(entries);
    }

    public List<ClaimsSetRequest.Entry> toClaimsSetRequestEntries() {
        LinkedList<ClaimsSetRequest.Entry> entries = new LinkedList<ClaimsSetRequest.Entry>();
        if (this == OPENID || this == OFFLINE_ACCESS) {
            return Collections.unmodifiableList(entries);
        }
        for (String claimName : this.getClaimNames()) {
            entries.add(new ClaimsSetRequest.Entry(claimName).withClaimRequirement(ClaimRequirement.VOLUNTARY));
        }
        return Collections.unmodifiableList(entries);
    }
}

