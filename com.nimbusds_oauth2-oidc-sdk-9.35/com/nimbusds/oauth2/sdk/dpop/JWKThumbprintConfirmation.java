/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JOSEException
 *  com.nimbusds.jose.jwk.JWK
 *  com.nimbusds.jose.util.Base64URL
 *  com.nimbusds.jwt.JWTClaimsSet
 *  com.nimbusds.jwt.JWTClaimsSet$Builder
 *  net.jcip.annotations.Immutable
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.oauth2.sdk.dpop;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public final class JWKThumbprintConfirmation {
    private final Base64URL jkt;

    public JWKThumbprintConfirmation(Base64URL jkt) {
        if (jkt == null) {
            throw new IllegalArgumentException("The JWK thumbprint must not be null");
        }
        this.jkt = jkt;
    }

    public Base64URL getValue() {
        return this.jkt;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        Map.Entry<String, JSONObject> cnfClaim = this.toJWTClaim();
        jsonObject.put((Object)cnfClaim.getKey(), (Object)cnfClaim.getValue());
        return jsonObject;
    }

    public Map.Entry<String, JSONObject> toJWTClaim() {
        JSONObject cnf = new JSONObject();
        cnf.put((Object)"jkt", (Object)this.jkt.toString());
        return new AbstractMap.SimpleImmutableEntry<String, JSONObject>("cnf", cnf);
    }

    public JWTClaimsSet applyTo(JWTClaimsSet jwtClaimsSet) {
        Map.Entry<String, JSONObject> cnfClaim = this.toJWTClaim();
        return new JWTClaimsSet.Builder(jwtClaimsSet).claim(cnfClaim.getKey(), (Object)cnfClaim.getValue()).build();
    }

    public String toString() {
        return this.toJSONObject().toJSONString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JWKThumbprintConfirmation)) {
            return false;
        }
        JWKThumbprintConfirmation that = (JWKThumbprintConfirmation)o;
        return this.jkt.equals((Object)that.jkt);
    }

    public int hashCode() {
        return Objects.hash(this.jkt);
    }

    public static JWKThumbprintConfirmation parse(JWTClaimsSet jwtClaimsSet) {
        Map jsonObjectClaim;
        try {
            jsonObjectClaim = jwtClaimsSet.getJSONObjectClaim("cnf");
        }
        catch (java.text.ParseException e) {
            return null;
        }
        if (jsonObjectClaim == null) {
            return null;
        }
        return JWKThumbprintConfirmation.parseFromConfirmationJSONObject(new JSONObject(jsonObjectClaim));
    }

    public static JWKThumbprintConfirmation parse(JSONObject jsonObject) {
        if (!jsonObject.containsKey((Object)"cnf")) {
            return null;
        }
        try {
            return JWKThumbprintConfirmation.parseFromConfirmationJSONObject(JSONObjectUtils.getJSONObject(jsonObject, "cnf"));
        }
        catch (ParseException e) {
            return null;
        }
    }

    public static JWKThumbprintConfirmation parseFromConfirmationJSONObject(JSONObject cnf) {
        if (cnf == null) {
            return null;
        }
        try {
            String jktString = JSONObjectUtils.getString(cnf, "jkt");
            return new JWKThumbprintConfirmation(new Base64URL(jktString));
        }
        catch (ParseException e) {
            return null;
        }
    }

    public static JWKThumbprintConfirmation of(JWK jwk) throws JOSEException {
        return new JWKThumbprintConfirmation(jwk.computeThumbprint());
    }
}

