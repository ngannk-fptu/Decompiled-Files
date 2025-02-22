/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.ECParameterTable;
import java.io.Serializable;
import java.security.spec.ECParameterSpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public final class Curve
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final Curve P_256 = new Curve("P-256", "secp256r1", "1.2.840.10045.3.1.7");
    public static final Curve SECP256K1 = new Curve("secp256k1", "secp256k1", "1.3.132.0.10");
    @Deprecated
    public static final Curve P_256K = new Curve("P-256K", "secp256k1", "1.3.132.0.10");
    public static final Curve P_384 = new Curve("P-384", "secp384r1", "1.3.132.0.34");
    public static final Curve P_521 = new Curve("P-521", "secp521r1", "1.3.132.0.35");
    public static final Curve Ed25519 = new Curve("Ed25519", "Ed25519", null);
    public static final Curve Ed448 = new Curve("Ed448", "Ed448", null);
    public static final Curve X25519 = new Curve("X25519", "X25519", null);
    public static final Curve X448 = new Curve("X448", "X448", null);
    private final String name;
    private final String stdName;
    private final String oid;

    public Curve(String name) {
        this(name, null, null);
    }

    public Curve(String name, String stdName, String oid) {
        if (name == null) {
            throw new IllegalArgumentException("The JOSE cryptographic curve name must not be null");
        }
        this.name = name;
        this.stdName = stdName;
        this.oid = oid;
    }

    public String getName() {
        return this.name;
    }

    public String getStdName() {
        return this.stdName;
    }

    public String getOID() {
        return this.oid;
    }

    public ECParameterSpec toECParameterSpec() {
        return ECParameterTable.get(this);
    }

    public String toString() {
        return this.getName();
    }

    public boolean equals(Object object) {
        return object instanceof Curve && this.toString().equals(object.toString());
    }

    public static Curve parse(String s) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("The cryptographic curve string must not be null or empty");
        }
        if (s.equals(P_256.getName())) {
            return P_256;
        }
        if (s.equals(P_256K.getName())) {
            return P_256K;
        }
        if (s.equals(SECP256K1.getName())) {
            return SECP256K1;
        }
        if (s.equals(P_384.getName())) {
            return P_384;
        }
        if (s.equals(P_521.getName())) {
            return P_521;
        }
        if (s.equals(Ed25519.getName())) {
            return Ed25519;
        }
        if (s.equals(Ed448.getName())) {
            return Ed448;
        }
        if (s.equals(X25519.getName())) {
            return X25519;
        }
        if (s.equals(X448.getName())) {
            return X448;
        }
        return new Curve(s);
    }

    public static Curve forStdName(String stdName) {
        if ("secp256r1".equals(stdName) || "prime256v1".equals(stdName)) {
            return P_256;
        }
        if ("secp256k1".equals(stdName)) {
            return SECP256K1;
        }
        if ("secp384r1".equals(stdName)) {
            return P_384;
        }
        if ("secp521r1".equals(stdName)) {
            return P_521;
        }
        if (Ed25519.getStdName().equals(stdName)) {
            return Ed25519;
        }
        if (Ed448.getStdName().equals(stdName)) {
            return Ed448;
        }
        if (X25519.getStdName().equals(stdName)) {
            return X25519;
        }
        if (X448.getStdName().equals(stdName)) {
            return X448;
        }
        return null;
    }

    public static Curve forOID(String oid) {
        if (P_256.getOID().equals(oid)) {
            return P_256;
        }
        if (SECP256K1.getOID().equals(oid)) {
            return SECP256K1;
        }
        if (P_384.getOID().equals(oid)) {
            return P_384;
        }
        if (P_521.getOID().equals(oid)) {
            return P_521;
        }
        return null;
    }

    public static Set<Curve> forJWSAlgorithm(JWSAlgorithm alg) {
        if (JWSAlgorithm.ES256.equals(alg)) {
            return Collections.singleton(P_256);
        }
        if (JWSAlgorithm.ES256K.equals(alg)) {
            return Collections.singleton(SECP256K1);
        }
        if (JWSAlgorithm.ES384.equals(alg)) {
            return Collections.singleton(P_384);
        }
        if (JWSAlgorithm.ES512.equals(alg)) {
            return Collections.singleton(P_521);
        }
        if (JWSAlgorithm.EdDSA.equals(alg)) {
            return Collections.unmodifiableSet(new HashSet<Curve>(Arrays.asList(Ed25519, Ed448)));
        }
        return null;
    }

    public static Curve forECParameterSpec(ECParameterSpec spec) {
        return ECParameterTable.get(spec);
    }
}

