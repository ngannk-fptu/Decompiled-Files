/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONAware
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.Requirement;
import java.io.Serializable;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

@Immutable
public final class KeyType
implements JSONAware,
Serializable {
    private static final long serialVersionUID = 1L;
    private final String value;
    private final Requirement requirement;
    public static final KeyType EC = new KeyType("EC", Requirement.RECOMMENDED);
    public static final KeyType RSA = new KeyType("RSA", Requirement.REQUIRED);
    public static final KeyType OCT = new KeyType("oct", Requirement.OPTIONAL);
    public static final KeyType OKP = new KeyType("OKP", Requirement.OPTIONAL);

    public KeyType(String value, Requirement req) {
        if (value == null) {
            throw new IllegalArgumentException("The key type value must not be null");
        }
        this.value = value;
        this.requirement = req;
    }

    public String getValue() {
        return this.value;
    }

    public Requirement getRequirement() {
        return this.requirement;
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public boolean equals(Object object) {
        return object != null && object instanceof KeyType && this.toString().equals(object.toString());
    }

    public String toString() {
        return this.value;
    }

    public String toJSONString() {
        return "\"" + JSONObject.escape((String)this.value) + '\"';
    }

    public static KeyType parse(String s) {
        if (s == null) {
            throw new IllegalArgumentException("The key type to parse must not be null");
        }
        if (s.equals(EC.getValue())) {
            return EC;
        }
        if (s.equals(RSA.getValue())) {
            return RSA;
        }
        if (s.equals(OCT.getValue())) {
            return OCT;
        }
        if (s.equals(OKP.getValue())) {
            return OKP;
        }
        return new KeyType(s, null);
    }

    public static KeyType forAlgorithm(Algorithm alg) {
        if (alg == null) {
            return null;
        }
        if (JWSAlgorithm.Family.RSA.contains(alg)) {
            return RSA;
        }
        if (JWSAlgorithm.Family.EC.contains(alg)) {
            return EC;
        }
        if (JWSAlgorithm.Family.HMAC_SHA.contains(alg)) {
            return OCT;
        }
        if (JWEAlgorithm.Family.RSA.contains(alg)) {
            return RSA;
        }
        if (JWEAlgorithm.Family.ECDH_ES.contains(alg)) {
            return EC;
        }
        if (JWEAlgorithm.DIR.equals(alg)) {
            return OCT;
        }
        if (JWEAlgorithm.Family.AES_GCM_KW.contains(alg)) {
            return OCT;
        }
        if (JWEAlgorithm.Family.AES_KW.contains(alg)) {
            return OCT;
        }
        if (JWEAlgorithm.Family.PBES2.contains(alg)) {
            return OCT;
        }
        if (JWSAlgorithm.Family.ED.contains(alg)) {
            return OKP;
        }
        return null;
    }
}

