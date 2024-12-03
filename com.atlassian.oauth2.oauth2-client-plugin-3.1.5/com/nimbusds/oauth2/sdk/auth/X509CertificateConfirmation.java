/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.auth;

import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.X509CertUtils;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.security.cert.X509Certificate;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public final class X509CertificateConfirmation {
    private final Base64URL x5tS256;

    public X509CertificateConfirmation(Base64URL x5tS256) {
        if (x5tS256 == null) {
            throw new IllegalArgumentException("The X.509 certificate thumbprint must not be null");
        }
        this.x5tS256 = x5tS256;
    }

    public Base64URL getValue() {
        return this.x5tS256;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        Map.Entry<String, JSONObject> cnfClaim = this.toJWTClaim();
        jsonObject.put(cnfClaim.getKey(), cnfClaim.getValue());
        return jsonObject;
    }

    public Map.Entry<String, JSONObject> toJWTClaim() {
        JSONObject cnf = new JSONObject();
        cnf.put("x5t#S256", this.x5tS256.toString());
        return new AbstractMap.SimpleImmutableEntry<String, JSONObject>("cnf", cnf);
    }

    public JWTClaimsSet applyTo(JWTClaimsSet jwtClaimsSet) {
        Map.Entry<String, JSONObject> cnfClaim = this.toJWTClaim();
        return new JWTClaimsSet.Builder(jwtClaimsSet).claim(cnfClaim.getKey(), cnfClaim.getValue()).build();
    }

    public String toString() {
        return this.toJSONObject().toJSONString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof X509CertificateConfirmation)) {
            return false;
        }
        X509CertificateConfirmation that = (X509CertificateConfirmation)o;
        return this.x5tS256.equals(that.x5tS256);
    }

    public int hashCode() {
        return Objects.hash(this.x5tS256);
    }

    public static X509CertificateConfirmation parse(JWTClaimsSet jwtClaimsSet) {
        Map<String, Object> jsonObjectClaim;
        try {
            jsonObjectClaim = jwtClaimsSet.getJSONObjectClaim("cnf");
        }
        catch (java.text.ParseException e) {
            return null;
        }
        if (jsonObjectClaim == null) {
            return null;
        }
        return X509CertificateConfirmation.parseFromConfirmationJSONObject(new JSONObject(jsonObjectClaim));
    }

    public static X509CertificateConfirmation parse(JSONObject jsonObject) {
        if (!jsonObject.containsKey("cnf")) {
            return null;
        }
        try {
            return X509CertificateConfirmation.parseFromConfirmationJSONObject(JSONObjectUtils.getJSONObject(jsonObject, "cnf"));
        }
        catch (ParseException e) {
            return null;
        }
    }

    public static X509CertificateConfirmation parseFromConfirmationJSONObject(JSONObject cnf) {
        if (cnf == null) {
            return null;
        }
        try {
            String x5tString = JSONObjectUtils.getString(cnf, "x5t#S256");
            return new X509CertificateConfirmation(new Base64URL(x5tString));
        }
        catch (ParseException e) {
            return null;
        }
    }

    public static X509CertificateConfirmation of(X509Certificate x509Cert) {
        return new X509CertificateConfirmation(X509CertUtils.computeSHA256Thumbprint(x509Cert));
    }
}

