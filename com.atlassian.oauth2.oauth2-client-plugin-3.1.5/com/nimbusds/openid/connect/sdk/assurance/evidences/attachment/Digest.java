/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences.attachment;

import com.nimbusds.jose.util.Base64;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.assurance.evidences.attachment.HashAlgorithm;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public final class Digest {
    private final HashAlgorithm alg;
    private final Base64 value;

    public Digest(HashAlgorithm alg, Base64 value) {
        Objects.requireNonNull(alg);
        this.alg = alg;
        Objects.requireNonNull(value);
        this.value = value;
    }

    public HashAlgorithm getHashAlgorithm() {
        return this.alg;
    }

    public Base64 getValue() {
        return this.value;
    }

    public boolean matches(Base64 content) throws NoSuchAlgorithmException {
        Digest computed = Digest.compute(this.getHashAlgorithm(), content);
        return this.equals(computed);
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("alg", this.getHashAlgorithm().getValue());
        jsonObject.put("value", this.getValue().toString());
        return jsonObject;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Digest)) {
            return false;
        }
        Digest digest = (Digest)o;
        return this.alg.equals(digest.alg) && this.getValue().equals(digest.getValue());
    }

    public int hashCode() {
        return Objects.hash(this.alg, this.getValue());
    }

    public static Digest compute(HashAlgorithm alg, Base64 content) throws NoSuchAlgorithmException {
        return Digest.compute(alg, content.decode());
    }

    public static Digest compute(HashAlgorithm alg, byte[] content) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(alg.getValue().toUpperCase());
        byte[] hash = md.digest(content);
        return new Digest(alg, Base64.encode(hash));
    }

    public static Digest parse(JSONObject jsonObject) throws ParseException {
        HashAlgorithm alg = new HashAlgorithm(JSONObjectUtils.getString(jsonObject, "alg"));
        Base64 value = new Base64(JSONObjectUtils.getString(jsonObject, "value"));
        return new Digest(alg, value);
    }
}

