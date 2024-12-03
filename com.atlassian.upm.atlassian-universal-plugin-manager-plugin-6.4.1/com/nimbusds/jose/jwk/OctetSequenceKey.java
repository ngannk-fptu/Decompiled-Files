/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMetadata;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.SecretJWK;
import com.nimbusds.jose.jwk.ThumbprintUtils;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.IntegerOverflowException;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.net.URI;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public final class OctetSequenceKey
extends JWK
implements SecretJWK {
    private static final long serialVersionUID = 1L;
    private final Base64URL k;

    public OctetSequenceKey(Base64URL k, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        super(KeyType.OCT, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
        if (k == null) {
            throw new IllegalArgumentException("The key value must not be null");
        }
        this.k = k;
    }

    public Base64URL getKeyValue() {
        return this.k;
    }

    public byte[] toByteArray() {
        return this.getKeyValue().decode();
    }

    @Override
    public SecretKey toSecretKey() {
        return this.toSecretKey("NONE");
    }

    public SecretKey toSecretKey(String jcaAlg) {
        return new SecretKeySpec(this.toByteArray(), jcaAlg);
    }

    @Override
    public LinkedHashMap<String, ?> getRequiredParams() {
        LinkedHashMap<String, String> requiredParams = new LinkedHashMap<String, String>();
        requiredParams.put("k", this.k.toString());
        requiredParams.put("kty", this.getKeyType().toString());
        return requiredParams;
    }

    @Override
    public boolean isPrivate() {
        return true;
    }

    @Override
    public OctetSequenceKey toPublicJWK() {
        return null;
    }

    @Override
    public int size() {
        try {
            return ByteUtils.safeBitLength(this.k.decode());
        }
        catch (IntegerOverflowException e) {
            throw new ArithmeticException(e.getMessage());
        }
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        o.put((Object)"k", (Object)this.k.toString());
        return o;
    }

    public static OctetSequenceKey parse(String s) throws ParseException {
        return OctetSequenceKey.parse(JSONObjectUtils.parse(s));
    }

    public static OctetSequenceKey parse(JSONObject jsonObject) throws ParseException {
        if (!KeyType.OCT.equals(JWKMetadata.parseKeyType(jsonObject))) {
            throw new ParseException("The key type \"kty\" must be oct", 0);
        }
        Base64URL k = JSONObjectUtils.getBase64URL(jsonObject, "k");
        try {
            return new OctetSequenceKey(k, JWKMetadata.parseKeyUse(jsonObject), JWKMetadata.parseKeyOperations(jsonObject), JWKMetadata.parseAlgorithm(jsonObject), JWKMetadata.parseKeyID(jsonObject), JWKMetadata.parseX509CertURL(jsonObject), JWKMetadata.parseX509CertThumbprint(jsonObject), JWKMetadata.parseX509CertSHA256Thumbprint(jsonObject), JWKMetadata.parseX509CertChain(jsonObject), null);
        }
        catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    public static OctetSequenceKey load(KeyStore keyStore, String alias, char[] pin) throws KeyStoreException, JOSEException {
        Key key;
        try {
            key = keyStore.getKey(alias, pin);
        }
        catch (NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new JOSEException("Couldn't retrieve secret key (bad pin?): " + e.getMessage(), e);
        }
        if (!(key instanceof SecretKey)) {
            return null;
        }
        return new Builder((SecretKey)key).keyID(alias).keyStore(keyStore).build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OctetSequenceKey)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        OctetSequenceKey that = (OctetSequenceKey)o;
        return Objects.equals(this.k, that.k);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.k);
    }

    public static class Builder {
        private final Base64URL k;
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

        public Builder(Base64URL k) {
            if (k == null) {
                throw new IllegalArgumentException("The key value must not be null");
            }
            this.k = k;
        }

        public Builder(byte[] key) {
            this(Base64URL.encode(key));
            if (key.length == 0) {
                throw new IllegalArgumentException("The key must have a positive length");
            }
        }

        public Builder(SecretKey secretKey) {
            this(secretKey.getEncoded());
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
            requiredParams.put("k", this.k.toString());
            requiredParams.put("kty", KeyType.OCT.getValue());
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

        public OctetSequenceKey build() {
            try {
                return new OctetSequenceKey(this.k, this.use, this.ops, this.alg, this.kid, this.x5u, this.x5t, this.x5t256, this.x5c, this.ks);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }
}

