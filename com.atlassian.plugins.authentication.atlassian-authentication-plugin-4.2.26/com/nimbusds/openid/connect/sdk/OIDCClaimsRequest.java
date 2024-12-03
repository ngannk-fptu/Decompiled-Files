/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.util.JSONArrayUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;
import com.nimbusds.openid.connect.sdk.OIDCScopeValue;
import com.nimbusds.openid.connect.sdk.assurance.claims.VerifiedClaimsSetRequest;
import com.nimbusds.openid.connect.sdk.claims.ClaimRequirement;
import com.nimbusds.openid.connect.sdk.claims.ClaimsSetRequest;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

@Immutable
public class OIDCClaimsRequest
implements JSONAware {
    private final ClaimsSetRequest idToken;
    private final ClaimsSetRequest userInfo;
    private final List<VerifiedClaimsSetRequest> idTokenVerified;
    private final List<VerifiedClaimsSetRequest> userInfoVerified;

    public OIDCClaimsRequest() {
        this(null, null, Collections.emptyList(), Collections.emptyList());
    }

    private OIDCClaimsRequest(ClaimsSetRequest idToken, ClaimsSetRequest userInfo, List<VerifiedClaimsSetRequest> idTokenVerified, List<VerifiedClaimsSetRequest> userInfoVerified) {
        this.idToken = idToken;
        this.userInfo = userInfo;
        if (idTokenVerified == null) {
            throw new IllegalArgumentException("The ID token verified claims set request list must not be null");
        }
        this.idTokenVerified = Collections.unmodifiableList(idTokenVerified);
        if (userInfoVerified == null) {
            throw new IllegalArgumentException("The UserInfo verified claims set request list must not be null");
        }
        this.userInfoVerified = Collections.unmodifiableList(userInfoVerified);
    }

    public OIDCClaimsRequest add(OIDCClaimsRequest other) {
        if (other == null) {
            return this;
        }
        LinkedList<ClaimsSetRequest.Entry> idTokenEntries = new LinkedList<ClaimsSetRequest.Entry>();
        if (this.idToken != null) {
            idTokenEntries.addAll(this.idToken.getEntries());
        }
        if (other.getIDTokenClaimsRequest() != null) {
            idTokenEntries.addAll(other.getIDTokenClaimsRequest().getEntries());
        }
        LinkedList<ClaimsSetRequest.Entry> userInfoEntries = new LinkedList<ClaimsSetRequest.Entry>();
        if (this.userInfo != null) {
            userInfoEntries.addAll(this.userInfo.getEntries());
        }
        if (other.getUserInfoClaimsRequest() != null) {
            userInfoEntries.addAll(other.getUserInfoClaimsRequest().getEntries());
        }
        LinkedList<VerifiedClaimsSetRequest> idTokenVerifiedList = new LinkedList<VerifiedClaimsSetRequest>(this.idTokenVerified);
        idTokenVerifiedList.addAll(other.getIDTokenVerifiedClaimsRequestList());
        LinkedList<VerifiedClaimsSetRequest> userInfoVerifiedList = new LinkedList<VerifiedClaimsSetRequest>(this.userInfoVerified);
        userInfoVerifiedList.addAll(other.getUserInfoVerifiedClaimsRequestList());
        return new OIDCClaimsRequest(idTokenEntries.isEmpty() ? null : new ClaimsSetRequest(idTokenEntries), userInfoEntries.isEmpty() ? null : new ClaimsSetRequest(userInfoEntries), idTokenVerifiedList, userInfoVerifiedList);
    }

    public ClaimsSetRequest getIDTokenClaimsRequest() {
        return this.idToken;
    }

    public OIDCClaimsRequest withIDTokenClaimsRequest(ClaimsSetRequest idToken) {
        return new OIDCClaimsRequest(idToken, this.getUserInfoClaimsRequest(), this.getIDTokenVerifiedClaimsRequestList(), this.getUserInfoVerifiedClaimsRequestList());
    }

    public ClaimsSetRequest getUserInfoClaimsRequest() {
        return this.userInfo;
    }

    public OIDCClaimsRequest withUserInfoClaimsRequest(ClaimsSetRequest userInfo) {
        return new OIDCClaimsRequest(this.getIDTokenClaimsRequest(), userInfo, this.getIDTokenVerifiedClaimsRequestList(), this.getUserInfoVerifiedClaimsRequestList());
    }

    public List<VerifiedClaimsSetRequest> getIDTokenVerifiedClaimsRequestList() {
        return this.idTokenVerified;
    }

    public OIDCClaimsRequest withIDTokenVerifiedClaimsRequestList(List<VerifiedClaimsSetRequest> idTokenVerifiedList) {
        return new OIDCClaimsRequest(this.getIDTokenClaimsRequest(), this.getUserInfoClaimsRequest(), idTokenVerifiedList != null ? idTokenVerifiedList : Collections.emptyList(), this.getUserInfoVerifiedClaimsRequestList());
    }

    public OIDCClaimsRequest withIDTokenVerifiedClaimsRequest(VerifiedClaimsSetRequest idTokenVerified) {
        return new OIDCClaimsRequest(this.getIDTokenClaimsRequest(), this.getUserInfoClaimsRequest(), idTokenVerified != null ? Collections.singletonList(idTokenVerified) : Collections.emptyList(), this.getUserInfoVerifiedClaimsRequestList());
    }

    public List<VerifiedClaimsSetRequest> getUserInfoVerifiedClaimsRequestList() {
        return this.userInfoVerified;
    }

    public OIDCClaimsRequest withUserInfoVerifiedClaimsRequestList(List<VerifiedClaimsSetRequest> userInfoVerifiedList) {
        return new OIDCClaimsRequest(this.getIDTokenClaimsRequest(), this.getUserInfoClaimsRequest(), this.getIDTokenVerifiedClaimsRequestList(), userInfoVerifiedList != null ? userInfoVerifiedList : Collections.emptyList());
    }

    public OIDCClaimsRequest withUserInfoVerifiedClaimsRequest(VerifiedClaimsSetRequest userInfoVerified) {
        return new OIDCClaimsRequest(this.getIDTokenClaimsRequest(), this.getUserInfoClaimsRequest(), this.getIDTokenVerifiedClaimsRequestList(), userInfoVerified != null ? Collections.singletonList(userInfoVerified) : Collections.emptyList());
    }

    private static JSONObject addVerified(List<VerifiedClaimsSetRequest> verified, JSONObject containingJSONObject) {
        if (verified != null) {
            if (verified.size() == 1 && verified.get(0) != null) {
                JSONObject out = new JSONObject();
                if (containingJSONObject != null) {
                    out.putAll(containingJSONObject);
                }
                out.put("verified_claims", verified.get(0).toJSONObject());
                return out;
            }
            if (verified.size() > 1) {
                JSONObject out = new JSONObject();
                if (containingJSONObject != null) {
                    out.putAll(containingJSONObject);
                }
                JSONArray jsonArray = new JSONArray();
                for (VerifiedClaimsSetRequest verifiedClaims : verified) {
                    jsonArray.add(verifiedClaims.toJSONObject());
                }
                out.put("verified_claims", jsonArray);
                return out;
            }
        }
        return containingJSONObject;
    }

    public JSONObject toJSONObject() {
        JSONObject o = new JSONObject();
        JSONObject idTokenJSONObject = null;
        if (this.idToken != null) {
            idTokenJSONObject = this.idToken.toJSONObject();
        }
        if ((idTokenJSONObject = OIDCClaimsRequest.addVerified(this.idTokenVerified, idTokenJSONObject)) != null && !idTokenJSONObject.isEmpty()) {
            o.put("id_token", idTokenJSONObject);
        }
        JSONObject userInfoJSONObject = null;
        if (this.userInfo != null) {
            userInfoJSONObject = this.userInfo.toJSONObject();
        }
        if ((userInfoJSONObject = OIDCClaimsRequest.addVerified(this.userInfoVerified, userInfoJSONObject)) != null && !userInfoJSONObject.isEmpty()) {
            o.put("userinfo", userInfoJSONObject);
        }
        return o;
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public String toString() {
        return this.toJSONString();
    }

    public static OIDCClaimsRequest resolve(ResponseType responseType, Scope scope) {
        return OIDCClaimsRequest.resolve(responseType, scope, Collections.emptyMap());
    }

    public static OIDCClaimsRequest resolve(ResponseType responseType, Scope scope, Map<Scope.Value, Set<String>> customClaims) {
        boolean switchToIDToken;
        OIDCClaimsRequest claimsRequest = new OIDCClaimsRequest();
        if (scope == null) {
            return claimsRequest;
        }
        LinkedList<ClaimsSetRequest.Entry> entries = new LinkedList<ClaimsSetRequest.Entry>();
        for (Scope.Value value : scope) {
            Set<String> claimNames;
            if (value.equals(OIDCScopeValue.PROFILE)) {
                entries.addAll(OIDCScopeValue.PROFILE.toClaimsSetRequestEntries());
                continue;
            }
            if (value.equals(OIDCScopeValue.EMAIL)) {
                entries.addAll(OIDCScopeValue.EMAIL.toClaimsSetRequestEntries());
                continue;
            }
            if (value.equals(OIDCScopeValue.PHONE)) {
                entries.addAll(OIDCScopeValue.PHONE.toClaimsSetRequestEntries());
                continue;
            }
            if (value.equals(OIDCScopeValue.ADDRESS)) {
                entries.addAll(OIDCScopeValue.ADDRESS.toClaimsSetRequestEntries());
                continue;
            }
            if (customClaims == null || !customClaims.containsKey(value) || (claimNames = customClaims.get(value)) == null || claimNames.isEmpty()) continue;
            for (String claimName : claimNames) {
                entries.add(new ClaimsSetRequest.Entry(claimName).withClaimRequirement(ClaimRequirement.VOLUNTARY));
            }
        }
        if (entries.isEmpty()) {
            return claimsRequest;
        }
        ClaimsSetRequest claimsSetRequest = new ClaimsSetRequest(entries);
        boolean bl = switchToIDToken = responseType.contains(OIDCResponseTypeValue.ID_TOKEN) && !responseType.contains(ResponseType.Value.CODE) && !responseType.contains(ResponseType.Value.TOKEN);
        if (switchToIDToken) {
            return claimsRequest.withIDTokenClaimsRequest(claimsSetRequest);
        }
        return claimsRequest.withUserInfoClaimsRequest(claimsSetRequest);
    }

    public static OIDCClaimsRequest resolve(ResponseType responseType, Scope scope, OIDCClaimsRequest claimsRequest) {
        return OIDCClaimsRequest.resolve(responseType, scope, claimsRequest, Collections.emptyMap());
    }

    public static OIDCClaimsRequest resolve(ResponseType responseType, Scope scope, OIDCClaimsRequest claimsRequest, Map<Scope.Value, Set<String>> customClaims) {
        return OIDCClaimsRequest.resolve(responseType, scope, customClaims).add(claimsRequest);
    }

    public static OIDCClaimsRequest resolve(AuthenticationRequest authRequest) {
        return OIDCClaimsRequest.resolve(authRequest.getResponseType(), authRequest.getScope(), authRequest.getOIDCClaims());
    }

    private static VerifiedClaimsSetRequest parseVerifiedClaimsSetRequest(JSONObject jsonObject, int position) throws ParseException {
        try {
            return VerifiedClaimsSetRequest.parse(jsonObject);
        }
        catch (ParseException e) {
            throw new ParseException("Invalid verified claims request" + (position > -1 ? " at position " + position : "") + ": " + e.getMessage());
        }
    }

    private static List<VerifiedClaimsSetRequest> parseVerified(JSONObject containingJSONObject) throws ParseException {
        if (!containingJSONObject.containsKey("verified_claims")) {
            return Collections.emptyList();
        }
        if (containingJSONObject.get("verified_claims") instanceof JSONObject) {
            JSONObject vo = JSONObjectUtils.getJSONObject(containingJSONObject, "verified_claims");
            return Collections.singletonList(OIDCClaimsRequest.parseVerifiedClaimsSetRequest(vo, -1));
        }
        JSONArray va = JSONObjectUtils.getJSONArray(containingJSONObject, "verified_claims");
        LinkedList<VerifiedClaimsSetRequest> out = new LinkedList<VerifiedClaimsSetRequest>();
        int pos = 0;
        for (JSONObject vo : JSONArrayUtils.toJSONObjectList(va)) {
            out.add(OIDCClaimsRequest.parseVerifiedClaimsSetRequest(vo, pos++));
        }
        return out;
    }

    public static OIDCClaimsRequest parse(JSONObject jsonObject) throws ParseException {
        JSONObject userInfoObject;
        OIDCClaimsRequest claimsRequest = new OIDCClaimsRequest();
        JSONObject idTokenObject = JSONObjectUtils.getJSONObject(jsonObject, "id_token", null);
        if (idTokenObject != null) {
            ClaimsSetRequest csr = ClaimsSetRequest.parse(idTokenObject);
            if (!csr.getEntries().isEmpty()) {
                claimsRequest = claimsRequest.withIDTokenClaimsRequest(csr);
            }
            claimsRequest = claimsRequest.withIDTokenVerifiedClaimsRequestList(OIDCClaimsRequest.parseVerified(idTokenObject));
        }
        if ((userInfoObject = JSONObjectUtils.getJSONObject(jsonObject, "userinfo", null)) != null) {
            ClaimsSetRequest csr = ClaimsSetRequest.parse(userInfoObject);
            if (!csr.getEntries().isEmpty()) {
                claimsRequest = claimsRequest.withUserInfoClaimsRequest(ClaimsSetRequest.parse(userInfoObject));
            }
            claimsRequest = claimsRequest.withUserInfoVerifiedClaimsRequestList(OIDCClaimsRequest.parseVerified(userInfoObject));
        }
        return claimsRequest;
    }

    public static OIDCClaimsRequest parse(String json) throws ParseException {
        return OIDCClaimsRequest.parse(JSONObjectUtils.parse(json));
    }
}

