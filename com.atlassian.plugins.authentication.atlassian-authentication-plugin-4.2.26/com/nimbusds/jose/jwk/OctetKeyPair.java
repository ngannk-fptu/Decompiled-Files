/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.AsymmetricJWK;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.CurveBasedJWK;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMetadata;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.ThumbprintUtils;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public class OctetKeyPair
extends JWK
implements AsymmetricJWK,
CurveBasedJWK {
    private static final long serialVersionUID = 1L;
    public static final Set<Curve> SUPPORTED_CURVES = Collections.unmodifiableSet(new HashSet<Curve>(Arrays.asList(Curve.Ed25519, Curve.Ed448, Curve.X25519, Curve.X448)));
    private final Curve crv;
    private final Base64URL x;
    private final byte[] decodedX;
    private final Base64URL d;
    private final byte[] decodedD;

    public OctetKeyPair(Curve crv, Base64URL x, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        super(KeyType.OKP, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
        if (crv == null) {
            throw new IllegalArgumentException("The curve must not be null");
        }
        if (!SUPPORTED_CURVES.contains(crv)) {
            throw new IllegalArgumentException("Unknown / unsupported curve: " + crv);
        }
        this.crv = crv;
        if (x == null) {
            throw new IllegalArgumentException("The 'x' parameter must not be null");
        }
        this.x = x;
        this.decodedX = x.decode();
        this.d = null;
        this.decodedD = null;
    }

    public OctetKeyPair(Curve crv, Base64URL x, Base64URL d, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        super(KeyType.OKP, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
        if (crv == null) {
            throw new IllegalArgumentException("The curve must not be null");
        }
        if (!SUPPORTED_CURVES.contains(crv)) {
            throw new IllegalArgumentException("Unknown / unsupported curve: " + crv);
        }
        this.crv = crv;
        if (x == null) {
            throw new IllegalArgumentException("The 'x' parameter must not be null");
        }
        this.x = x;
        this.decodedX = x.decode();
        if (d == null) {
            throw new IllegalArgumentException("The 'd' parameter must not be null");
        }
        this.d = d;
        this.decodedD = d.decode();
    }

    @Override
    public Curve getCurve() {
        return this.crv;
    }

    public Base64URL getX() {
        return this.x;
    }

    public byte[] getDecodedX() {
        return (byte[])this.decodedX.clone();
    }

    public Base64URL getD() {
        return this.d;
    }

    public byte[] getDecodedD() {
        return this.decodedD == null ? null : (byte[])this.decodedD.clone();
    }

    @Override
    public PublicKey toPublicKey() throws JOSEException {
        throw new JOSEException("Export to java.security.PublicKey not supported");
    }

    @Override
    public PrivateKey toPrivateKey() throws JOSEException {
        throw new JOSEException("Export to java.security.PrivateKey not supported");
    }

    @Override
    public KeyPair toKeyPair() throws JOSEException {
        throw new JOSEException("Export to java.security.KeyPair not supported");
    }

    @Override
    public boolean matches(X509Certificate cert) {
        return false;
    }

    @Override
    public LinkedHashMap<String, ?> getRequiredParams() {
        LinkedHashMap<String, String> requiredParams = new LinkedHashMap<String, String>();
        requiredParams.put("crv", this.crv.toString());
        requiredParams.put("kty", this.getKeyType().getValue());
        requiredParams.put("x", this.x.toString());
        return requiredParams;
    }

    @Override
    public boolean isPrivate() {
        return this.d != null;
    }

    @Override
    public OctetKeyPair toPublicJWK() {
        return new OctetKeyPair(this.getCurve(), this.getX(), this.getKeyUse(), this.getKeyOperations(), this.getAlgorithm(), this.getKeyID(), this.getX509CertURL(), this.getX509CertThumbprint(), this.getX509CertSHA256Thumbprint(), this.getX509CertChain(), this.getKeyStore());
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        o.put("crv", this.crv.toString());
        o.put("x", this.x.toString());
        if (this.d != null) {
            o.put("d", this.d.toString());
        }
        return o;
    }

    @Override
    public int size() {
        return ByteUtils.bitLength(this.x.decode());
    }

    public static OctetKeyPair parse(String s) throws ParseException {
        return OctetKeyPair.parse(JSONObjectUtils.parse(s));
    }

    public static OctetKeyPair parse(JSONObject jsonObject) throws ParseException {
        Curve crv;
        if (!KeyType.OKP.equals(JWKMetadata.parseKeyType(jsonObject))) {
            throw new ParseException("The key type \"kty\" must be OKP", 0);
        }
        try {
            crv = Curve.parse(JSONObjectUtils.getString(jsonObject, "crv"));
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        Base64URL x = JSONObjectUtils.getBase64URL(jsonObject, "x");
        Base64URL d = JSONObjectUtils.getBase64URL(jsonObject, "d");
        try {
            if (d == null) {
                return new OctetKeyPair(crv, x, JWKMetadata.parseKeyUse(jsonObject), JWKMetadata.parseKeyOperations(jsonObject), JWKMetadata.parseAlgorithm(jsonObject), JWKMetadata.parseKeyID(jsonObject), JWKMetadata.parseX509CertURL(jsonObject), JWKMetadata.parseX509CertThumbprint(jsonObject), JWKMetadata.parseX509CertSHA256Thumbprint(jsonObject), JWKMetadata.parseX509CertChain(jsonObject), null);
            }
            return new OctetKeyPair(crv, x, d, JWKMetadata.parseKeyUse(jsonObject), JWKMetadata.parseKeyOperations(jsonObject), JWKMetadata.parseAlgorithm(jsonObject), JWKMetadata.parseKeyID(jsonObject), JWKMetadata.parseX509CertURL(jsonObject), JWKMetadata.parseX509CertThumbprint(jsonObject), JWKMetadata.parseX509CertSHA256Thumbprint(jsonObject), JWKMetadata.parseX509CertChain(jsonObject), null);
        }
        catch (IllegalArgumentException ex) {
            throw new ParseException(ex.getMessage(), 0);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OctetKeyPair)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        OctetKeyPair that = (OctetKeyPair)o;
        return Objects.equals(this.crv, that.crv) && Objects.equals(this.x, that.x) && Arrays.equals(this.decodedX, that.decodedX) && Objects.equals(this.d, that.d) && Arrays.equals(this.decodedD, that.decodedD);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(super.hashCode(), this.crv, this.x, this.d);
        result = 31 * result + Arrays.hashCode(this.decodedX);
        result = 31 * result + Arrays.hashCode(this.decodedD);
        return result;
    }

    public static class Builder {
        private final Curve crv;
        private final Base64URL x;
        private Base64URL d;
        private KeyUse use;
        private Set<KeyOperation> ops;
        private Algorithm alg;
        private String kid;
        private URI x5u;
        @Deprecated
        private Base64URL x5t;
        private Base64URL x5t256;
        private List<Base64> x5c;
        private KeyStore ks;

        public Builder(Curve crv, Base64URL x) {
            if (crv == null) {
                throw new IllegalArgumentException("The curve must not be null");
            }
            this.crv = crv;
            if (x == null) {
                throw new IllegalArgumentException("The 'x' coordinate must not be null");
            }
            this.x = x;
        }

        public Builder(OctetKeyPair okpJWK) {
            this.crv = okpJWK.crv;
            this.x = okpJWK.x;
            this.d = okpJWK.d;
            this.use = okpJWK.getKeyUse();
            this.ops = okpJWK.getKeyOperations();
            this.alg = okpJWK.getAlgorithm();
            this.kid = okpJWK.getKeyID();
            this.x5u = okpJWK.getX509CertURL();
            this.x5t = okpJWK.getX509CertThumbprint();
            this.x5t256 = okpJWK.getX509CertSHA256Thumbprint();
            this.x5c = okpJWK.getX509CertChain();
            this.ks = okpJWK.getKeyStore();
        }

        public Builder d(Base64URL d) {
            this.d = d;
            return this;
        }

        public Builder keyUse(KeyUse use) {
            this.use = use;
            return this;
        }

        public Builder keyOperations(Set<KeyOperation> ops) {
            this.ops = ops;
            return this;
        }

        public Builder algorithm(Algorithm alg) {
            this.alg = alg;
            return this;
        }

        public Builder keyID(String kid) {
            this.kid = kid;
            return this;
        }

        public Builder keyIDFromThumbprint() throws JOSEException {
            return this.keyIDFromThumbprint("SHA-256");
        }

        public Builder keyIDFromThumbprint(String hashAlg) throws JOSEException {
            LinkedHashMap<String, String> requiredParams = new LinkedHashMap<String, String>();
            requiredParams.put("crv", this.crv.toString());
            requiredParams.put("kty", KeyType.OKP.getValue());
            requiredParams.put("x", this.x.toString());
            this.kid = ThumbprintUtils.compute(hashAlg, requiredParams).toString();
            return this;
        }

        public Builder x509CertURL(URI x5u) {
            this.x5u = x5u;
            return this;
        }

        @Deprecated
        public Builder x509CertThumbprint(Base64URL x5t) {
            this.x5t = x5t;
            return this;
        }

        public Builder x509CertSHA256Thumbprint(Base64URL x5t256) {
            this.x5t256 = x5t256;
            return this;
        }

        public Builder x509CertChain(List<Base64> x5c) {
            this.x5c = x5c;
            return this;
        }

        public Builder keyStore(KeyStore keyStore) {
            this.ks = keyStore;
            return this;
        }

        public OctetKeyPair build() {
            try {
                if (this.d == null) {
                    return new OctetKeyPair(this.crv, this.x, this.use, this.ops, this.alg, this.kid, this.x5u, this.x5t, this.x5t256, this.x5c, this.ks);
                }
                return new OctetKeyPair(this.crv, this.x, this.d, this.use, this.ops, this.alg, this.kid, this.x5u, this.x5t, this.x5t256, this.x5c, this.ks);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }
}

