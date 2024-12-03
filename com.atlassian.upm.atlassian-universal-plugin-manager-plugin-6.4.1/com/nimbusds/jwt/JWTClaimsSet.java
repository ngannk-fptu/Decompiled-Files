/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONArray
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.jwt;

import com.nimbusds.jose.util.DateUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSetTransformer;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@Immutable
public final class JWTClaimsSet
implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String ISSUER_CLAIM = "iss";
    private static final String SUBJECT_CLAIM = "sub";
    private static final String AUDIENCE_CLAIM = "aud";
    private static final String EXPIRATION_TIME_CLAIM = "exp";
    private static final String NOT_BEFORE_CLAIM = "nbf";
    private static final String ISSUED_AT_CLAIM = "iat";
    private static final String JWT_ID_CLAIM = "jti";
    private static final Set<String> REGISTERED_CLAIM_NAMES;
    private final Map<String, Object> claims = new LinkedHashMap<String, Object>();

    private JWTClaimsSet(Map<String, Object> claims) {
        this.claims.putAll(claims);
    }

    public static Set<String> getRegisteredNames() {
        return REGISTERED_CLAIM_NAMES;
    }

    public String getIssuer() {
        try {
            return this.getStringClaim(ISSUER_CLAIM);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public String getSubject() {
        try {
            return this.getStringClaim(SUBJECT_CLAIM);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public List<String> getAudience() {
        List<String> aud;
        Object audValue = this.getClaim(AUDIENCE_CLAIM);
        if (audValue instanceof String) {
            return Collections.singletonList((String)audValue);
        }
        try {
            aud = this.getStringListClaim(AUDIENCE_CLAIM);
        }
        catch (ParseException e) {
            return Collections.emptyList();
        }
        return aud != null ? Collections.unmodifiableList(aud) : Collections.emptyList();
    }

    public Date getExpirationTime() {
        try {
            return this.getDateClaim(EXPIRATION_TIME_CLAIM);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public Date getNotBeforeTime() {
        try {
            return this.getDateClaim(NOT_BEFORE_CLAIM);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public Date getIssueTime() {
        try {
            return this.getDateClaim(ISSUED_AT_CLAIM);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public String getJWTID() {
        try {
            return this.getStringClaim(JWT_ID_CLAIM);
        }
        catch (ParseException e) {
            return null;
        }
    }

    public Object getClaim(String name) {
        return this.claims.get(name);
    }

    public String getStringClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null || value instanceof String) {
            return (String)value;
        }
        throw new ParseException("The \"" + name + "\" claim is not a String", 0);
    }

    public String[] getStringArrayClaim(String name) throws ParseException {
        List list;
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        try {
            list = (List)this.getClaim(name);
        }
        catch (ClassCastException e) {
            throw new ParseException("The \"" + name + "\" claim is not a list / JSON array", 0);
        }
        String[] stringArray = new String[list.size()];
        for (int i = 0; i < stringArray.length; ++i) {
            try {
                stringArray[i] = (String)list.get(i);
                continue;
            }
            catch (ClassCastException e) {
                throw new ParseException("The \"" + name + "\" claim is not a list / JSON array of strings", 0);
            }
        }
        return stringArray;
    }

    public List<String> getStringListClaim(String name) throws ParseException {
        String[] stringArray = this.getStringArrayClaim(name);
        if (stringArray == null) {
            return null;
        }
        return Collections.unmodifiableList(Arrays.asList(stringArray));
    }

    public URI getURIClaim(String name) throws ParseException {
        String uriString = this.getStringClaim(name);
        if (uriString == null) {
            return null;
        }
        try {
            return new URI(uriString);
        }
        catch (URISyntaxException e) {
            throw new ParseException("The \"" + name + "\" claim is not a URI: " + e.getMessage(), 0);
        }
    }

    public Boolean getBooleanClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null || value instanceof Boolean) {
            return (Boolean)value;
        }
        throw new ParseException("The \"" + name + "\" claim is not a Boolean", 0);
    }

    public Integer getIntegerClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        throw new ParseException("The \"" + name + "\" claim is not an Integer", 0);
    }

    public Long getLongClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number)value).longValue();
        }
        throw new ParseException("The \"" + name + "\" claim is not a Number", 0);
    }

    public Date getDateClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return (Date)value;
        }
        if (value instanceof Number) {
            return DateUtils.fromSecondsSinceEpoch(((Number)value).longValue());
        }
        throw new ParseException("The \"" + name + "\" claim is not a Date", 0);
    }

    public Float getFloatClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return Float.valueOf(((Number)value).floatValue());
        }
        throw new ParseException("The \"" + name + "\" claim is not a Float", 0);
    }

    public Double getDoubleClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number)value).doubleValue();
        }
        throw new ParseException("The \"" + name + "\" claim is not a Double", 0);
    }

    public JSONObject getJSONObjectClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof JSONObject) {
            return (JSONObject)value;
        }
        if (value instanceof Map) {
            JSONObject jsonObject = new JSONObject();
            Map map = (Map)value;
            for (Map.Entry entry : map.entrySet()) {
                if (!(entry.getKey() instanceof String)) continue;
                jsonObject.put((Object)((String)entry.getKey()), entry.getValue());
            }
            return jsonObject;
        }
        throw new ParseException("The \"" + name + "\" claim is not a JSON object or Map", 0);
    }

    public Map<String, Object> getClaims() {
        return Collections.unmodifiableMap(this.claims);
    }

    public JSONObject toJSONObject() {
        return this.toJSONObject(false);
    }

    public JSONObject toJSONObject(boolean includeClaimsWithNullValues) {
        JSONObject o = new JSONObject();
        for (Map.Entry<String, Object> claim : this.claims.entrySet()) {
            if (claim.getValue() instanceof Date) {
                Date dateValue = (Date)claim.getValue();
                o.put((Object)claim.getKey(), (Object)DateUtils.toSecondsSinceEpoch(dateValue));
                continue;
            }
            if (AUDIENCE_CLAIM.equals(claim.getKey())) {
                List<String> audList = this.getAudience();
                if (audList != null && !audList.isEmpty()) {
                    if (audList.size() == 1) {
                        o.put((Object)AUDIENCE_CLAIM, (Object)audList.get(0));
                        continue;
                    }
                    JSONArray audArray = new JSONArray();
                    audArray.addAll(audList);
                    o.put((Object)AUDIENCE_CLAIM, (Object)audArray);
                    continue;
                }
                if (!includeClaimsWithNullValues) continue;
                o.put((Object)AUDIENCE_CLAIM, null);
                continue;
            }
            if (claim.getValue() != null) {
                o.put((Object)claim.getKey(), claim.getValue());
                continue;
            }
            if (!includeClaimsWithNullValues) continue;
            o.put((Object)claim.getKey(), null);
        }
        return o;
    }

    public String toString() {
        return this.toJSONObject().toJSONString();
    }

    public <T> T toType(JWTClaimsSetTransformer<T> transformer) {
        return transformer.transform(this);
    }

    public static JWTClaimsSet parse(JSONObject json) throws ParseException {
        Builder builder = new Builder();
        for (String name : json.keySet()) {
            if (name.equals(ISSUER_CLAIM)) {
                builder.issuer(JSONObjectUtils.getString(json, ISSUER_CLAIM));
                continue;
            }
            if (name.equals(SUBJECT_CLAIM)) {
                builder.subject(JSONObjectUtils.getString(json, SUBJECT_CLAIM));
                continue;
            }
            if (name.equals(AUDIENCE_CLAIM)) {
                Object audValue = json.get((Object)AUDIENCE_CLAIM);
                if (audValue instanceof String) {
                    ArrayList<String> singleAud = new ArrayList<String>();
                    singleAud.add(JSONObjectUtils.getString(json, AUDIENCE_CLAIM));
                    builder.audience(singleAud);
                    continue;
                }
                if (audValue instanceof List) {
                    builder.audience(JSONObjectUtils.getStringList(json, AUDIENCE_CLAIM));
                    continue;
                }
                if (audValue != null) continue;
                builder.audience((String)null);
                continue;
            }
            if (name.equals(EXPIRATION_TIME_CLAIM)) {
                builder.expirationTime(new Date(JSONObjectUtils.getLong(json, EXPIRATION_TIME_CLAIM) * 1000L));
                continue;
            }
            if (name.equals(NOT_BEFORE_CLAIM)) {
                builder.notBeforeTime(new Date(JSONObjectUtils.getLong(json, NOT_BEFORE_CLAIM) * 1000L));
                continue;
            }
            if (name.equals(ISSUED_AT_CLAIM)) {
                builder.issueTime(new Date(JSONObjectUtils.getLong(json, ISSUED_AT_CLAIM) * 1000L));
                continue;
            }
            if (name.equals(JWT_ID_CLAIM)) {
                builder.jwtID(JSONObjectUtils.getString(json, JWT_ID_CLAIM));
                continue;
            }
            builder.claim(name, json.get((Object)name));
        }
        return builder.build();
    }

    public static JWTClaimsSet parse(String s) throws ParseException {
        return JWTClaimsSet.parse(JSONObjectUtils.parse(s));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JWTClaimsSet)) {
            return false;
        }
        JWTClaimsSet that = (JWTClaimsSet)o;
        return Objects.equals(this.claims, that.claims);
    }

    public int hashCode() {
        return Objects.hash(this.claims);
    }

    static {
        HashSet<String> n = new HashSet<String>();
        n.add(ISSUER_CLAIM);
        n.add(SUBJECT_CLAIM);
        n.add(AUDIENCE_CLAIM);
        n.add(EXPIRATION_TIME_CLAIM);
        n.add(NOT_BEFORE_CLAIM);
        n.add(ISSUED_AT_CLAIM);
        n.add(JWT_ID_CLAIM);
        REGISTERED_CLAIM_NAMES = Collections.unmodifiableSet(n);
    }

    public static class Builder {
        private final Map<String, Object> claims = new LinkedHashMap<String, Object>();

        public Builder() {
        }

        public Builder(JWTClaimsSet jwtClaimsSet) {
            this.claims.putAll(jwtClaimsSet.claims);
        }

        public Builder issuer(String iss) {
            this.claims.put(JWTClaimsSet.ISSUER_CLAIM, iss);
            return this;
        }

        public Builder subject(String sub) {
            this.claims.put(JWTClaimsSet.SUBJECT_CLAIM, sub);
            return this;
        }

        public Builder audience(List<String> aud) {
            this.claims.put(JWTClaimsSet.AUDIENCE_CLAIM, aud);
            return this;
        }

        public Builder audience(String aud) {
            if (aud == null) {
                this.claims.put(JWTClaimsSet.AUDIENCE_CLAIM, null);
            } else {
                this.claims.put(JWTClaimsSet.AUDIENCE_CLAIM, Collections.singletonList(aud));
            }
            return this;
        }

        public Builder expirationTime(Date exp) {
            this.claims.put(JWTClaimsSet.EXPIRATION_TIME_CLAIM, exp);
            return this;
        }

        public Builder notBeforeTime(Date nbf) {
            this.claims.put(JWTClaimsSet.NOT_BEFORE_CLAIM, nbf);
            return this;
        }

        public Builder issueTime(Date iat) {
            this.claims.put(JWTClaimsSet.ISSUED_AT_CLAIM, iat);
            return this;
        }

        public Builder jwtID(String jti) {
            this.claims.put(JWTClaimsSet.JWT_ID_CLAIM, jti);
            return this;
        }

        public Builder claim(String name, Object value) {
            this.claims.put(name, value);
            return this;
        }

        public Map<String, Object> getClaims() {
            return Collections.unmodifiableMap(this.claims);
        }

        public JWTClaimsSet build() {
            return new JWTClaimsSet(this.claims);
        }
    }
}

