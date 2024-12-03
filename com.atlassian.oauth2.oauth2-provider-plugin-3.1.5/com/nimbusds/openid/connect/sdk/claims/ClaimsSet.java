/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.claims;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.util.DateUtils;
import com.nimbusds.langtag.LangTag;
import com.nimbusds.langtag.LangTagUtils;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

public class ClaimsSet
implements JSONAware {
    public static final String ISS_CLAIM_NAME = "iss";
    public static final String AUD_CLAIM_NAME = "aud";
    private static final Set<String> STD_CLAIM_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("iss", "aud")));
    protected final JSONObject claims;

    public static Set<String> getStandardClaimNames() {
        return STD_CLAIM_NAMES;
    }

    public ClaimsSet() {
        this.claims = new JSONObject();
    }

    public ClaimsSet(JSONObject jsonObject) {
        if (jsonObject == null) {
            throw new IllegalArgumentException("The JSON object must not be null");
        }
        this.claims = jsonObject;
    }

    public void putAll(ClaimsSet other) {
        this.putAll(other.claims);
    }

    public void putAll(Map<String, Object> claims) {
        this.claims.putAll(claims);
    }

    public Object getClaim(String name) {
        return this.claims.get(name);
    }

    public <T> T getClaim(String name, Class<T> clazz) {
        try {
            return JSONObjectUtils.getGeneric(this.claims, name, clazz);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public <T> Map<LangTag, T> getLangTaggedClaim(String name, Class<T> clazz) {
        Map<LangTag, Object> matches = LangTagUtils.find(name, this.claims);
        HashMap<LangTag, T> out = new HashMap<LangTag, T>();
        for (Map.Entry<LangTag, Object> entry : matches.entrySet()) {
            LangTag langTag = entry.getKey();
            String compositeKey = name + (langTag != null ? "#" + langTag : "");
            try {
                out.put(langTag, JSONObjectUtils.getGeneric(this.claims, compositeKey, clazz));
            }
            catch (ParseException parseException) {}
        }
        return out;
    }

    public void setClaim(String name, Object value) {
        if (value != null) {
            this.claims.put(name, value);
        } else {
            this.claims.remove(name);
        }
    }

    public void setClaim(String name, Object value, LangTag langTag) {
        String keyName = langTag != null ? name + "#" + langTag : name;
        this.setClaim(keyName, value);
    }

    public String getStringClaim(String name) {
        try {
            return JSONObjectUtils.getString(this.claims, name, null);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public String getStringClaim(String name, LangTag langTag) {
        return langTag == null ? this.getStringClaim(name) : this.getStringClaim(name + '#' + langTag);
    }

    public Boolean getBooleanClaim(String name) {
        try {
            return JSONObjectUtils.getBoolean(this.claims, name);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public Number getNumberClaim(String name) {
        try {
            return JSONObjectUtils.getNumber(this.claims, name);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public URL getURLClaim(String name) {
        try {
            return JSONObjectUtils.getURL(this.claims, name);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setURLClaim(String name, URL value) {
        if (value != null) {
            this.setClaim(name, value.toString());
        } else {
            this.claims.remove(name);
        }
    }

    public URI getURIClaim(String name) {
        try {
            return JSONObjectUtils.getURI(this.claims, name, null);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public void setURIClaim(String name, URI value) {
        if (value != null) {
            this.setClaim(name, value.toString());
        } else {
            this.claims.remove(name);
        }
    }

    public Date getDateClaim(String name) {
        try {
            return DateUtils.fromSecondsSinceEpoch(JSONObjectUtils.getNumber(this.claims, name).longValue());
        }
        catch (Exception e) {
            return null;
        }
    }

    public void setDateClaim(String name, Date value) {
        if (value != null) {
            this.setClaim(name, DateUtils.toSecondsSinceEpoch(value));
        } else {
            this.claims.remove(name);
        }
    }

    public List<String> getStringListClaim(String name) {
        try {
            return JSONObjectUtils.getStringList(this.claims, name);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public JSONObject getJSONObjectClaim(String name) {
        try {
            return JSONObjectUtils.getJSONObject(this.claims, name);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public Issuer getIssuer() {
        String iss = this.getStringClaim(ISS_CLAIM_NAME);
        return iss != null ? new Issuer(iss) : null;
    }

    public void setIssuer(Issuer iss) {
        if (iss != null) {
            this.setClaim(ISS_CLAIM_NAME, iss.getValue());
        } else {
            this.setClaim(ISS_CLAIM_NAME, null);
        }
    }

    public List<Audience> getAudience() {
        if (this.getClaim(AUD_CLAIM_NAME) instanceof String) {
            return new Audience(this.getStringClaim(AUD_CLAIM_NAME)).toSingleAudienceList();
        }
        List<String> rawList = this.getStringListClaim(AUD_CLAIM_NAME);
        if (rawList == null) {
            return null;
        }
        ArrayList<Audience> audList = new ArrayList<Audience>(rawList.size());
        for (String s : rawList) {
            audList.add(new Audience(s));
        }
        return audList;
    }

    public void setAudience(Audience aud) {
        if (aud != null) {
            this.setAudience(aud.toSingleAudienceList());
        } else {
            this.setClaim(AUD_CLAIM_NAME, null);
        }
    }

    public void setAudience(List<Audience> audList) {
        if (audList != null) {
            this.setClaim(AUD_CLAIM_NAME, Audience.toStringList(audList));
        } else {
            this.setClaim(AUD_CLAIM_NAME, null);
        }
    }

    public JSONObject toJSONObject() {
        JSONObject out = new JSONObject();
        out.putAll(this.claims);
        return out;
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toJSONString();
    }

    public JWTClaimsSet toJWTClaimsSet() throws ParseException {
        try {
            return JWTClaimsSet.parse(this.claims.toJSONString());
        }
        catch (java.text.ParseException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClaimsSet)) {
            return false;
        }
        ClaimsSet claimsSet = (ClaimsSet)o;
        return this.claims.equals(claimsSet.claims);
    }

    public int hashCode() {
        return Objects.hash(this.claims);
    }
}

