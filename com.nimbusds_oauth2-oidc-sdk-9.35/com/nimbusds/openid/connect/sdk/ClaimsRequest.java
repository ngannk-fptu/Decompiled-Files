/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.langtag.LangTag
 *  com.nimbusds.langtag.LangTagException
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONArray
 *  net.minidev.json.JSONAware
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.langtag.LangTag;
import com.nimbusds.langtag.LangTagException;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.util.JSONArrayUtils;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.OIDCResponseTypeValue;
import com.nimbusds.openid.connect.sdk.OIDCScopeValue;
import com.nimbusds.openid.connect.sdk.claims.ClaimRequirement;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

@Deprecated
public class ClaimsRequest
implements JSONAware {
    private final Map<Map.Entry<String, LangTag>, Entry> idTokenClaims = new HashMap<Map.Entry<String, LangTag>, Entry>();
    private final Map<Map.Entry<String, LangTag>, Entry> verifiedIDTokenClaims = new HashMap<Map.Entry<String, LangTag>, Entry>();
    private JSONObject idTokenClaimsVerification;
    private final Map<Map.Entry<String, LangTag>, Entry> userInfoClaims = new HashMap<Map.Entry<String, LangTag>, Entry>();
    private final Map<Map.Entry<String, LangTag>, Entry> verifiedUserInfoClaims = new HashMap<Map.Entry<String, LangTag>, Entry>();
    private JSONObject userInfoClaimsVerification;

    public void add(ClaimsRequest other) {
        if (other == null) {
            return;
        }
        this.idTokenClaims.putAll(other.idTokenClaims);
        this.verifiedIDTokenClaims.putAll(other.verifiedIDTokenClaims);
        this.idTokenClaimsVerification = other.idTokenClaimsVerification;
        this.userInfoClaims.putAll(other.userInfoClaims);
        this.verifiedUserInfoClaims.putAll(other.verifiedUserInfoClaims);
        this.userInfoClaimsVerification = other.userInfoClaimsVerification;
    }

    public void addIDTokenClaim(String claimName) {
        this.addIDTokenClaim(claimName, ClaimRequirement.VOLUNTARY);
    }

    public void addIDTokenClaim(String claimName, ClaimRequirement requirement) {
        this.addIDTokenClaim(claimName, requirement, null);
    }

    public void addIDTokenClaim(String claimName, ClaimRequirement requirement, LangTag langTag) {
        this.addIDTokenClaim(claimName, requirement, langTag, (String)null);
    }

    public void addIDTokenClaim(String claimName, ClaimRequirement requirement, LangTag langTag, String value) {
        this.addIDTokenClaim(new Entry(claimName, requirement, langTag, value));
    }

    public void addIDTokenClaim(String claimName, ClaimRequirement requirement, LangTag langTag, String value, Map<String, Object> additionalInformation) {
        this.addIDTokenClaim(new Entry(claimName, requirement, langTag, value, null, null, additionalInformation));
    }

    public void addIDTokenClaim(String claimName, ClaimRequirement requirement, LangTag langTag, List<String> values) {
        this.addIDTokenClaim(new Entry(claimName, requirement, langTag, values));
    }

    public void addIDTokenClaim(String claimName, ClaimRequirement requirement, LangTag langTag, List<String> values, Map<String, Object> additionalInformation) {
        this.addIDTokenClaim(new Entry(claimName, requirement, langTag, null, values, null, additionalInformation));
    }

    private static Map.Entry<String, LangTag> toKey(Entry entry) {
        return new AbstractMap.SimpleImmutableEntry<String, LangTag>(entry.getClaimName(), entry.getLangTag());
    }

    public void addIDTokenClaim(Entry entry) {
        this.idTokenClaims.put(ClaimsRequest.toKey(entry), entry);
    }

    public void addVerifiedIDTokenClaim(Entry entry) {
        this.verifiedIDTokenClaims.put(ClaimsRequest.toKey(entry), entry);
    }

    public void setIDTokenClaimsVerificationJSONObject(JSONObject jsonObject) {
        this.idTokenClaimsVerification = jsonObject;
    }

    public JSONObject getIDTokenClaimsVerificationJSONObject() {
        return this.idTokenClaimsVerification;
    }

    public Collection<Entry> getIDTokenClaims() {
        return Collections.unmodifiableCollection(this.idTokenClaims.values());
    }

    public Collection<Entry> getVerifiedIDTokenClaims() {
        return Collections.unmodifiableCollection(this.verifiedIDTokenClaims.values());
    }

    private static Set<String> getClaimNames(Map<Map.Entry<String, LangTag>, Entry> claims, boolean withLangTag) {
        HashSet<String> names = new HashSet<String>();
        for (Entry en : claims.values()) {
            names.add(en.getClaimName(withLangTag));
        }
        return Collections.unmodifiableSet(names);
    }

    public Set<String> getIDTokenClaimNames(boolean withLangTag) {
        return ClaimsRequest.getClaimNames(this.idTokenClaims, withLangTag);
    }

    public Set<String> getVerifiedIDTokenClaimNames(boolean withLangTag) {
        return ClaimsRequest.getClaimNames(this.verifiedIDTokenClaims, withLangTag);
    }

    private static Map.Entry<String, LangTag> toKey(String claimName, LangTag langTag) {
        return new AbstractMap.SimpleImmutableEntry<String, LangTag>(claimName, langTag);
    }

    public Entry removeIDTokenClaim(String claimName, LangTag langTag) {
        return this.idTokenClaims.remove(ClaimsRequest.toKey(claimName, langTag));
    }

    public Entry removeVerifiedIDTokenClaim(String claimName, LangTag langTag) {
        return this.verifiedIDTokenClaims.remove(ClaimsRequest.toKey(claimName, langTag));
    }

    private static Collection<Entry> removeClaims(Map<Map.Entry<String, LangTag>, Entry> claims, String claimName) {
        LinkedList<Entry> removedClaims = new LinkedList<Entry>();
        Iterator<Map.Entry<Map.Entry<String, LangTag>, Entry>> it = claims.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Map.Entry<String, LangTag>, Entry> reqEntry = it.next();
            if (!reqEntry.getKey().getKey().equals(claimName)) continue;
            removedClaims.add(reqEntry.getValue());
            it.remove();
        }
        return Collections.unmodifiableCollection(removedClaims);
    }

    public Collection<Entry> removeIDTokenClaims(String claimName) {
        return ClaimsRequest.removeClaims(this.idTokenClaims, claimName);
    }

    public Collection<Entry> removeVerifiedIDTokenClaims(String claimName) {
        return ClaimsRequest.removeClaims(this.verifiedIDTokenClaims, claimName);
    }

    public void addUserInfoClaim(String claimName) {
        this.addUserInfoClaim(claimName, ClaimRequirement.VOLUNTARY);
    }

    public void addUserInfoClaim(String claimName, ClaimRequirement requirement) {
        this.addUserInfoClaim(claimName, requirement, null);
    }

    public void addUserInfoClaim(String claimName, ClaimRequirement requirement, LangTag langTag) {
        this.addUserInfoClaim(claimName, requirement, langTag, (String)null);
    }

    public void addUserInfoClaim(String claimName, ClaimRequirement requirement, LangTag langTag, String value) {
        this.addUserInfoClaim(new Entry(claimName, requirement, langTag, value));
    }

    public void addUserInfoClaim(String claimName, ClaimRequirement requirement, LangTag langTag, String value, Map<String, Object> additionalInformation) {
        this.addUserInfoClaim(new Entry(claimName, requirement, langTag, value, null, null, additionalInformation));
    }

    public void addUserInfoClaim(String claimName, ClaimRequirement requirement, LangTag langTag, List<String> values) {
        this.addUserInfoClaim(new Entry(claimName, requirement, langTag, values));
    }

    public void addUserInfoClaim(String claimName, ClaimRequirement requirement, LangTag langTag, List<String> values, Map<String, Object> additionalInformation) {
        this.addUserInfoClaim(new Entry(claimName, requirement, langTag, null, values, null, additionalInformation));
    }

    public void addUserInfoClaim(Entry entry) {
        this.userInfoClaims.put(ClaimsRequest.toKey(entry), entry);
    }

    public void addVerifiedUserInfoClaim(Entry entry) {
        this.verifiedUserInfoClaims.put(ClaimsRequest.toKey(entry), entry);
    }

    public void setUserInfoClaimsVerificationJSONObject(JSONObject jsonObject) {
        this.userInfoClaimsVerification = jsonObject;
    }

    public JSONObject getUserInfoClaimsVerificationJSONObject() {
        return this.userInfoClaimsVerification;
    }

    public Collection<Entry> getUserInfoClaims() {
        return Collections.unmodifiableCollection(this.userInfoClaims.values());
    }

    public Collection<Entry> getVerifiedUserInfoClaims() {
        return Collections.unmodifiableCollection(this.verifiedUserInfoClaims.values());
    }

    public Set<String> getUserInfoClaimNames(boolean withLangTag) {
        return ClaimsRequest.getClaimNames(this.userInfoClaims, withLangTag);
    }

    public Set<String> getVerifiedUserInfoClaimNames(boolean withLangTag) {
        return ClaimsRequest.getClaimNames(this.verifiedUserInfoClaims, withLangTag);
    }

    public Entry removeUserInfoClaim(String claimName, LangTag langTag) {
        return this.userInfoClaims.remove(ClaimsRequest.toKey(claimName, langTag));
    }

    public Entry removeVerifiedUserInfoClaim(String claimName, LangTag langTag) {
        return this.verifiedUserInfoClaims.remove(ClaimsRequest.toKey(claimName, langTag));
    }

    public Collection<Entry> removeUserInfoClaims(String claimName) {
        return ClaimsRequest.removeClaims(this.userInfoClaims, claimName);
    }

    public Collection<Entry> removeVerifiedUserInfoClaims(String claimName) {
        return ClaimsRequest.removeClaims(this.verifiedUserInfoClaims, claimName);
    }

    public JSONObject toJSONObject() {
        JSONObject verifiedClaims;
        JSONObject o = new JSONObject();
        if (!this.getIDTokenClaims().isEmpty()) {
            o.put((Object)"id_token", (Object)Entry.toJSONObject(this.getIDTokenClaims()));
        }
        if (!this.getVerifiedIDTokenClaims().isEmpty()) {
            JSONObject idTokenObject = o.get((Object)"id_token") != null ? (JSONObject)o.get((Object)"id_token") : new JSONObject();
            verifiedClaims = new JSONObject();
            verifiedClaims.put((Object)"claims", (Object)Entry.toJSONObject(this.getVerifiedIDTokenClaims()));
            if (this.getIDTokenClaimsVerificationJSONObject() != null) {
                verifiedClaims.put((Object)"verification", (Object)this.getIDTokenClaimsVerificationJSONObject());
            }
            idTokenObject.put((Object)"verified_claims", (Object)verifiedClaims);
            o.put((Object)"id_token", (Object)idTokenObject);
        }
        if (!this.getUserInfoClaims().isEmpty()) {
            o.put((Object)"userinfo", (Object)Entry.toJSONObject(this.getUserInfoClaims()));
        }
        if (!this.getVerifiedUserInfoClaims().isEmpty()) {
            JSONObject userInfoObject = o.get((Object)"userinfo") != null ? (JSONObject)o.get((Object)"userinfo") : new JSONObject();
            verifiedClaims = new JSONObject();
            verifiedClaims.put((Object)"claims", (Object)Entry.toJSONObject(this.getVerifiedUserInfoClaims()));
            if (this.getUserInfoClaimsVerificationJSONObject() != null) {
                verifiedClaims.put((Object)"verification", (Object)this.getUserInfoClaimsVerificationJSONObject());
            }
            userInfoObject.put((Object)"verified_claims", (Object)verifiedClaims);
            o.put((Object)"userinfo", (Object)userInfoObject);
        }
        return o;
    }

    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public String toString() {
        return this.toJSONString();
    }

    public static ClaimsRequest resolve(ResponseType responseType, Scope scope) {
        return ClaimsRequest.resolve(responseType, scope, Collections.emptyMap());
    }

    public static ClaimsRequest resolve(ResponseType responseType, Scope scope, Map<Scope.Value, Set<String>> customClaims) {
        boolean switchToIDToken = responseType.contains(OIDCResponseTypeValue.ID_TOKEN) && !responseType.contains(ResponseType.Value.CODE) && !responseType.contains(ResponseType.Value.TOKEN);
        ClaimsRequest claimsRequest = new ClaimsRequest();
        if (scope == null) {
            return claimsRequest;
        }
        for (Scope.Value value : scope) {
            Set<Entry> entries;
            if (value.equals(OIDCScopeValue.PROFILE)) {
                entries = OIDCScopeValue.PROFILE.toClaimsRequestEntries();
            } else if (value.equals(OIDCScopeValue.EMAIL)) {
                entries = OIDCScopeValue.EMAIL.toClaimsRequestEntries();
            } else if (value.equals(OIDCScopeValue.PHONE)) {
                entries = OIDCScopeValue.PHONE.toClaimsRequestEntries();
            } else if (value.equals(OIDCScopeValue.ADDRESS)) {
                entries = OIDCScopeValue.ADDRESS.toClaimsRequestEntries();
            } else {
                Set<String> claimNames;
                if (customClaims == null || !customClaims.containsKey(value) || (claimNames = customClaims.get(value)) == null || claimNames.isEmpty()) continue;
                entries = new HashSet<Entry>();
                for (String claimName : claimNames) {
                    entries.add(new Entry(claimName, ClaimRequirement.VOLUNTARY));
                }
            }
            for (Entry en : entries) {
                if (switchToIDToken) {
                    claimsRequest.addIDTokenClaim(en);
                    continue;
                }
                claimsRequest.addUserInfoClaim(en);
            }
        }
        return claimsRequest;
    }

    public static ClaimsRequest resolve(ResponseType responseType, Scope scope, ClaimsRequest claimsRequest) {
        return ClaimsRequest.resolve(responseType, scope, claimsRequest, Collections.emptyMap());
    }

    public static ClaimsRequest resolve(ResponseType responseType, Scope scope, ClaimsRequest claimsRequest, Map<Scope.Value, Set<String>> customClaims) {
        ClaimsRequest mergedClaimsRequest = ClaimsRequest.resolve(responseType, scope, customClaims);
        mergedClaimsRequest.add(claimsRequest);
        return mergedClaimsRequest;
    }

    public static ClaimsRequest resolve(AuthenticationRequest authRequest) {
        return ClaimsRequest.resolve(authRequest.getResponseType(), authRequest.getScope(), authRequest.getClaims());
    }

    private static JSONObject parseFirstVerifiedClaimsObject(JSONObject containingObject) throws ParseException {
        List<JSONObject> elements;
        if (containingObject.get((Object)"verified_claims") instanceof JSONObject) {
            return JSONObjectUtils.getJSONObject(containingObject, "verified_claims");
        }
        if (containingObject.get((Object)"verified_claims") instanceof JSONArray && (elements = JSONArrayUtils.toJSONObjectList(JSONObjectUtils.getJSONArray(containingObject, "verified_claims"))).size() > 0) {
            return elements.get(0);
        }
        return null;
    }

    public static ClaimsRequest parse(JSONObject jsonObject) throws ParseException {
        ClaimsRequest claimsRequest;
        block14: {
            claimsRequest = new ClaimsRequest();
            try {
                JSONObject userInfoObject;
                JSONObject idTokenObject = JSONObjectUtils.getJSONObject(jsonObject, "id_token", null);
                if (idTokenObject != null) {
                    for (Entry entry : Entry.parseEntries(idTokenObject)) {
                        if ("verified_claims".equals(entry.getClaimName())) continue;
                        claimsRequest.addIDTokenClaim(entry);
                    }
                    JSONObject verifiedClaimsObject = ClaimsRequest.parseFirstVerifiedClaimsObject(idTokenObject);
                    if (verifiedClaimsObject != null) {
                        JSONObject claimsObject = JSONObjectUtils.getJSONObject(verifiedClaimsObject, "claims", null);
                        if (claimsObject != null) {
                            if (claimsObject.isEmpty()) {
                                String msg = "Invalid claims object: Empty verification claims object";
                                throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
                            }
                            for (Entry entry : Entry.parseEntries(claimsObject)) {
                                claimsRequest.addVerifiedIDTokenClaim(entry);
                            }
                        }
                        claimsRequest.setIDTokenClaimsVerificationJSONObject(JSONObjectUtils.getJSONObject(verifiedClaimsObject, "verification", null));
                    }
                }
                if ((userInfoObject = JSONObjectUtils.getJSONObject(jsonObject, "userinfo", null)) != null) {
                    for (Entry entry : Entry.parseEntries(userInfoObject)) {
                        if ("verified_claims".equals(entry.getClaimName())) continue;
                        claimsRequest.addUserInfoClaim(entry);
                    }
                    JSONObject verifiedClaimsObject = ClaimsRequest.parseFirstVerifiedClaimsObject(userInfoObject);
                    if (verifiedClaimsObject != null) {
                        JSONObject claimsObject = JSONObjectUtils.getJSONObject(verifiedClaimsObject, "claims", null);
                        if (claimsObject != null) {
                            if (claimsObject.isEmpty()) {
                                String msg = "Invalid claims object: Empty verification claims object";
                                throw new ParseException(msg, OAuth2Error.INVALID_REQUEST.appendDescription(": " + msg));
                            }
                            for (Entry entry : Entry.parseEntries(claimsObject)) {
                                claimsRequest.addVerifiedUserInfoClaim(entry);
                            }
                        }
                        claimsRequest.setUserInfoClaimsVerificationJSONObject(JSONObjectUtils.getJSONObject(verifiedClaimsObject, "verification", null));
                    }
                }
            }
            catch (Exception e) {
                if (!(e instanceof ParseException)) break block14;
                throw e;
            }
        }
        return claimsRequest;
    }

    public static ClaimsRequest parse(String json) throws ParseException {
        return ClaimsRequest.parse(JSONObjectUtils.parse(json));
    }

    @Immutable
    public static class Entry {
        private final String claimName;
        private final ClaimRequirement requirement;
        private final LangTag langTag;
        private final String value;
        private final List<String> values;
        private final String purpose;
        private final Map<String, Object> additionalInformation;

        public Entry(String claimName) {
            this(claimName, ClaimRequirement.VOLUNTARY, null, null, null, null, null);
        }

        @Deprecated
        public Entry(String claimName, LangTag langTag) {
            this(claimName, ClaimRequirement.VOLUNTARY, langTag, null, null);
        }

        @Deprecated
        public Entry(String claimName, ClaimRequirement requirement) {
            this(claimName, requirement, null, null, null);
        }

        @Deprecated
        public Entry(String claimName, ClaimRequirement requirement, LangTag langTag, String value) {
            this(claimName, requirement, langTag, value, null);
        }

        @Deprecated
        public Entry(String claimName, ClaimRequirement requirement, LangTag langTag, List<String> values) {
            this(claimName, requirement, langTag, null, values, null, null);
        }

        @Deprecated
        private Entry(String claimName, ClaimRequirement requirement, LangTag langTag, String value, List<String> values) {
            this(claimName, requirement, langTag, value, values, null, null);
        }

        private Entry(String claimName, ClaimRequirement requirement, LangTag langTag, String value, List<String> values, String purpose, Map<String, Object> additionalInformation) {
            if (claimName == null) {
                throw new IllegalArgumentException("The claim name must not be null");
            }
            this.claimName = claimName;
            if (requirement == null) {
                throw new IllegalArgumentException("The claim requirement must not be null");
            }
            this.requirement = requirement;
            this.langTag = langTag;
            if (value != null && values == null) {
                this.value = value;
                this.values = null;
            } else if (value == null && values != null) {
                this.value = null;
                this.values = values;
            } else if (value == null && values == null) {
                this.value = null;
                this.values = null;
            } else {
                throw new IllegalArgumentException("Either value or values must be specified, but not both");
            }
            this.purpose = purpose;
            this.additionalInformation = additionalInformation;
        }

        public String getClaimName() {
            return this.claimName;
        }

        public String getClaimName(boolean withLangTag) {
            if (withLangTag && this.langTag != null) {
                return this.claimName + "#" + this.langTag;
            }
            return this.claimName;
        }

        public Entry withClaimRequirement(ClaimRequirement requirement) {
            return new Entry(this.claimName, requirement, this.langTag, this.value, this.values, this.purpose, this.additionalInformation);
        }

        public ClaimRequirement getClaimRequirement() {
            return this.requirement;
        }

        public Entry withLangTag(LangTag langTag) {
            return new Entry(this.claimName, this.requirement, langTag, this.value, this.values, this.purpose, this.additionalInformation);
        }

        public LangTag getLangTag() {
            return this.langTag;
        }

        public Entry withValue(String value) {
            return new Entry(this.claimName, this.requirement, this.langTag, value, null, this.purpose, this.additionalInformation);
        }

        public String getValue() {
            return this.value;
        }

        public Entry withValues(List<String> values) {
            return new Entry(this.claimName, this.requirement, this.langTag, null, values, this.purpose, this.additionalInformation);
        }

        public List<String> getValues() {
            return this.values;
        }

        public Entry withPurpose(String purpose) {
            return new Entry(this.claimName, this.requirement, this.langTag, this.value, this.values, purpose, this.additionalInformation);
        }

        public String getPurpose() {
            return this.purpose;
        }

        public Entry withAdditionalInformation(Map<String, Object> additionalInformation) {
            return new Entry(this.claimName, this.requirement, this.langTag, this.value, this.values, this.purpose, additionalInformation);
        }

        public Map<String, Object> getAdditionalInformation() {
            return this.additionalInformation;
        }

        public static JSONObject toJSONObject(Collection<Entry> entries) {
            JSONObject o = new JSONObject();
            for (Entry entry : entries) {
                JSONObject entrySpec = null;
                if (entry.getValue() != null) {
                    entrySpec = new JSONObject();
                    entrySpec.put((Object)"value", (Object)entry.getValue());
                }
                if (entry.getValues() != null) {
                    entrySpec = new JSONObject();
                    entrySpec.put((Object)"values", entry.getValues());
                }
                if (entry.getClaimRequirement().equals((Object)ClaimRequirement.ESSENTIAL)) {
                    if (entrySpec == null) {
                        entrySpec = new JSONObject();
                    }
                    entrySpec.put((Object)"essential", (Object)true);
                }
                if (entry.getPurpose() != null) {
                    if (entrySpec == null) {
                        entrySpec = new JSONObject();
                    }
                    entrySpec.put((Object)"purpose", (Object)entry.getPurpose());
                }
                if (entry.getAdditionalInformation() != null) {
                    if (entrySpec == null) {
                        entrySpec = new JSONObject();
                    }
                    for (Map.Entry<String, Object> additionalInformationEntry : entry.getAdditionalInformation().entrySet()) {
                        entrySpec.put((Object)additionalInformationEntry.getKey(), additionalInformationEntry.getValue());
                    }
                }
                o.put((Object)entry.getClaimName(true), (Object)entrySpec);
            }
            return o;
        }

        public static Collection<Entry> parseEntries(JSONObject jsonObject) {
            LinkedList<Entry> entries = new LinkedList<Entry>();
            if (jsonObject.isEmpty()) {
                return entries;
            }
            for (Map.Entry member : jsonObject.entrySet()) {
                String claimName;
                LangTag langTag;
                block13: {
                    String claimNameWithOptLangTag = (String)member.getKey();
                    langTag = null;
                    if (claimNameWithOptLangTag.contains("#")) {
                        String[] parts = claimNameWithOptLangTag.split("#", 2);
                        claimName = parts[0];
                        try {
                            langTag = LangTag.parse((String)parts[1]);
                            break block13;
                        }
                        catch (LangTagException e) {
                            continue;
                        }
                    }
                    claimName = claimNameWithOptLangTag;
                }
                if (member.getValue() == null) {
                    entries.add(new Entry(claimName, langTag));
                    continue;
                }
                try {
                    Map<String, Object> additionalInformation;
                    boolean isEssential;
                    JSONObject entrySpec = (JSONObject)member.getValue();
                    ClaimRequirement requirement = ClaimRequirement.VOLUNTARY;
                    if (entrySpec.containsKey((Object)"essential") && (isEssential = ((Boolean)entrySpec.get((Object)"essential")).booleanValue())) {
                        requirement = ClaimRequirement.ESSENTIAL;
                    }
                    String purpose = null;
                    if (entrySpec.containsKey((Object)"purpose")) {
                        purpose = (String)entrySpec.get((Object)"purpose");
                    }
                    if (entrySpec.containsKey((Object)"value")) {
                        String expectedValue = (String)entrySpec.get((Object)"value");
                        additionalInformation = Entry.getAdditionalInformationFromClaim(entrySpec);
                        entries.add(new Entry(claimName, requirement, langTag, expectedValue, null, purpose, additionalInformation));
                        continue;
                    }
                    if (entrySpec.containsKey((Object)"values")) {
                        LinkedList<String> expectedValues = new LinkedList<String>();
                        for (Object v : (List)entrySpec.get((Object)"values")) {
                            expectedValues.add((String)v);
                        }
                        additionalInformation = Entry.getAdditionalInformationFromClaim(entrySpec);
                        entries.add(new Entry(claimName, requirement, langTag, null, expectedValues, purpose, additionalInformation));
                        continue;
                    }
                    Map<String, Object> additionalInformation2 = Entry.getAdditionalInformationFromClaim(entrySpec);
                    entries.add(new Entry(claimName, requirement, langTag, null, null, purpose, additionalInformation2));
                }
                catch (Exception exception) {}
            }
            return entries;
        }

        private static Map<String, Object> getAdditionalInformationFromClaim(JSONObject entrySpec) {
            HashSet<String> stdKeys = new HashSet<String>(Arrays.asList("essential", "value", "values", "purpose"));
            HashMap<String, Object> additionalClaimInformation = new HashMap<String, Object>();
            for (Map.Entry additionalClaimInformationEntry : entrySpec.entrySet()) {
                if (stdKeys.contains(additionalClaimInformationEntry.getKey())) continue;
                additionalClaimInformation.put((String)additionalClaimInformationEntry.getKey(), additionalClaimInformationEntry.getValue());
            }
            return additionalClaimInformation.isEmpty() ? null : additionalClaimInformation;
        }
    }
}

