/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.KeyUseAndOpsConsistency;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.PEMEncodedKeyParser;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.ThumbprintUtils;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.X509CertChainUtils;
import com.nimbusds.jose.util.X509CertUtils;
import java.io.Serializable;
import java.net.URI;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.ECParameterSpec;
import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;

public abstract class JWK
implements JSONAware,
Serializable {
    private static final long serialVersionUID = 1L;
    public static final String MIME_TYPE = "application/jwk+json; charset=UTF-8";
    private final KeyType kty;
    private final KeyUse use;
    private final Set<KeyOperation> ops;
    private final Algorithm alg;
    private final String kid;
    private final URI x5u;
    @Deprecated
    private final Base64URL x5t;
    private Base64URL x5t256;
    private final List<Base64> x5c;
    private final List<X509Certificate> parsedX5c;
    private final KeyStore keyStore;

    protected JWK(KeyType kty, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        if (kty == null) {
            throw new IllegalArgumentException("The key type \"kty\" parameter must not be null");
        }
        this.kty = kty;
        if (!KeyUseAndOpsConsistency.areConsistent(use, ops)) {
            throw new IllegalArgumentException("The key use \"use\" and key options \"key_opts\" parameters are not consistent, see RFC 7517, section 4.3");
        }
        this.use = use;
        this.ops = ops;
        this.alg = alg;
        this.kid = kid;
        this.x5u = x5u;
        this.x5t = x5t;
        this.x5t256 = x5t256;
        if (x5c != null && x5c.isEmpty()) {
            throw new IllegalArgumentException("The X.509 certificate chain \"x5c\" must not be empty");
        }
        this.x5c = x5c;
        try {
            this.parsedX5c = X509CertChainUtils.parse(x5c);
        }
        catch (ParseException e) {
            throw new IllegalArgumentException("Invalid X.509 certificate chain \"x5c\": " + e.getMessage(), e);
        }
        this.keyStore = ks;
    }

    public KeyType getKeyType() {
        return this.kty;
    }

    public KeyUse getKeyUse() {
        return this.use;
    }

    public Set<KeyOperation> getKeyOperations() {
        return this.ops;
    }

    public Algorithm getAlgorithm() {
        return this.alg;
    }

    public String getKeyID() {
        return this.kid;
    }

    public URI getX509CertURL() {
        return this.x5u;
    }

    @Deprecated
    public Base64URL getX509CertThumbprint() {
        return this.x5t;
    }

    public Base64URL getX509CertSHA256Thumbprint() {
        return this.x5t256;
    }

    public List<Base64> getX509CertChain() {
        if (this.x5c == null) {
            return null;
        }
        return Collections.unmodifiableList(this.x5c);
    }

    public List<X509Certificate> getParsedX509CertChain() {
        if (this.parsedX5c == null) {
            return null;
        }
        return Collections.unmodifiableList(this.parsedX5c);
    }

    public KeyStore getKeyStore() {
        return this.keyStore;
    }

    public abstract LinkedHashMap<String, ?> getRequiredParams();

    public Base64URL computeThumbprint() throws JOSEException {
        return this.computeThumbprint("SHA-256");
    }

    public Base64URL computeThumbprint(String hashAlg) throws JOSEException {
        return ThumbprintUtils.compute(hashAlg, this);
    }

    public abstract boolean isPrivate();

    public abstract JWK toPublicJWK();

    public abstract int size();

    public RSAKey toRSAKey() {
        return (RSAKey)this;
    }

    public ECKey toECKey() {
        return (ECKey)this;
    }

    public OctetSequenceKey toOctetSequenceKey() {
        return (OctetSequenceKey)this;
    }

    public OctetKeyPair toOctetKeyPair() {
        return (OctetKeyPair)this;
    }

    public JSONObject toJSONObject() {
        JSONArray stringValues;
        JSONObject o = new JSONObject();
        o.put("kty", this.kty.getValue());
        if (this.use != null) {
            o.put("use", this.use.identifier());
        }
        if (this.ops != null) {
            stringValues = new JSONArray();
            for (KeyOperation op : this.ops) {
                stringValues.add(op.identifier());
            }
            o.put("key_ops", stringValues);
        }
        if (this.alg != null) {
            o.put("alg", this.alg.getName());
        }
        if (this.kid != null) {
            o.put("kid", this.kid);
        }
        if (this.x5u != null) {
            o.put("x5u", this.x5u.toString());
        }
        if (this.x5t != null) {
            o.put("x5t", this.x5t.toString());
        }
        if (this.x5t256 != null) {
            o.put("x5t#S256", this.x5t256.toString());
        }
        if (this.x5c != null) {
            stringValues = new JSONArray();
            for (Base64 base64 : this.x5c) {
                stringValues.add(base64.toString());
            }
            o.put("x5c", stringValues);
        }
        return o;
    }

    @Override
    public String toJSONString() {
        return this.toJSONObject().toString();
    }

    public String toString() {
        return this.toJSONObject().toString();
    }

    public static JWK parse(String s) throws ParseException {
        return JWK.parse(JSONObjectUtils.parse(s));
    }

    public static JWK parse(JSONObject jsonObject) throws ParseException {
        KeyType kty = KeyType.parse(JSONObjectUtils.getString(jsonObject, "kty"));
        if (kty == KeyType.EC) {
            return ECKey.parse(jsonObject);
        }
        if (kty == KeyType.RSA) {
            return RSAKey.parse(jsonObject);
        }
        if (kty == KeyType.OCT) {
            return OctetSequenceKey.parse(jsonObject);
        }
        if (kty == KeyType.OKP) {
            return OctetKeyPair.parse(jsonObject);
        }
        throw new ParseException("Unsupported key type \"kty\" parameter: " + kty, 0);
    }

    public static JWK parse(X509Certificate cert) throws JOSEException {
        if (cert.getPublicKey() instanceof RSAPublicKey) {
            return RSAKey.parse(cert);
        }
        if (cert.getPublicKey() instanceof ECPublicKey) {
            return ECKey.parse(cert);
        }
        throw new JOSEException("Unsupported public key algorithm: " + cert.getPublicKey().getAlgorithm());
    }

    public static JWK parseFromPEMEncodedX509Cert(String pemEncodedCert) throws JOSEException {
        X509Certificate cert = X509CertUtils.parse(pemEncodedCert);
        if (cert == null) {
            throw new JOSEException("Couldn't parse PEM-encoded X.509 certificate");
        }
        return JWK.parse(cert);
    }

    public static JWK load(KeyStore keyStore, String alias, char[] pin) throws KeyStoreException, JOSEException {
        Certificate cert = keyStore.getCertificate(alias);
        if (cert == null) {
            return OctetSequenceKey.load(keyStore, alias, pin);
        }
        if (cert.getPublicKey() instanceof RSAPublicKey) {
            return RSAKey.load(keyStore, alias, pin);
        }
        if (cert.getPublicKey() instanceof ECPublicKey) {
            return ECKey.load(keyStore, alias, pin);
        }
        throw new JOSEException("Unsupported public key algorithm: " + cert.getPublicKey().getAlgorithm());
    }

    public static JWK parseFromPEMEncodedObjects(String pemEncodedObjects) throws JOSEException {
        List<KeyPair> keys = PEMEncodedKeyParser.parseKeys(pemEncodedObjects);
        if (keys.isEmpty()) {
            throw new JOSEException("No PEM-encoded keys found");
        }
        KeyPair pair = JWK.mergeKeyPairs(keys);
        PublicKey publicKey = pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();
        if (publicKey == null) {
            throw new JOSEException("Missing PEM-encoded public key to construct JWK");
        }
        if (publicKey instanceof ECPublicKey) {
            ECPublicKey ecPubKey = (ECPublicKey)publicKey;
            ECParameterSpec pubParams = ecPubKey.getParams();
            if (privateKey instanceof ECPrivateKey) {
                JWK.validateEcCurves(ecPubKey, (ECPrivateKey)privateKey);
            }
            if (privateKey != null && !(privateKey instanceof ECPrivateKey)) {
                throw new JOSEException("Unsupported EC private key type: " + privateKey);
            }
            Curve curve = Curve.forECParameterSpec(pubParams);
            ECKey.Builder builder = new ECKey.Builder(curve, (ECPublicKey)publicKey);
            if (privateKey != null) {
                builder.privateKey((ECPrivateKey)privateKey);
            }
            return builder.build();
        }
        if (publicKey instanceof RSAPublicKey) {
            RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey)publicKey);
            if (privateKey instanceof RSAPrivateKey) {
                builder.privateKey((RSAPrivateKey)privateKey);
            } else if (privateKey != null) {
                throw new JOSEException("Unsupported RSA private key type: " + privateKey);
            }
            return builder.build();
        }
        throw new JOSEException("Unsupported algorithm of PEM-encoded key: " + publicKey.getAlgorithm());
    }

    private static void validateEcCurves(ECPublicKey publicKey, ECPrivateKey privateKey) throws JOSEException {
        ECParameterSpec pubParams = publicKey.getParams();
        ECParameterSpec privParams = privateKey.getParams();
        if (!pubParams.getCurve().equals(privParams.getCurve())) {
            throw new JOSEException("Public/private EC key curve mismatch: " + publicKey);
        }
        if (pubParams.getCofactor() != privParams.getCofactor()) {
            throw new JOSEException("Public/private EC key cofactor mismatch: " + publicKey);
        }
        if (!pubParams.getGenerator().equals(privParams.getGenerator())) {
            throw new JOSEException("Public/private EC key generator mismatch: " + publicKey);
        }
        if (!pubParams.getOrder().equals(privParams.getOrder())) {
            throw new JOSEException("Public/private EC key order mismatch: " + publicKey);
        }
    }

    private static KeyPair mergeKeyPairs(List<KeyPair> keys) throws JOSEException {
        KeyPair pair;
        if (keys.size() == 1) {
            pair = keys.get(0);
        } else if (keys.size() == 2) {
            pair = JWK.twoKeysToKeyPair(keys);
        } else {
            throw new JOSEException("Expected key or pair of PEM-encoded keys");
        }
        return pair;
    }

    private static KeyPair twoKeysToKeyPair(List<? extends KeyPair> keys) throws JOSEException {
        KeyPair key1 = keys.get(0);
        KeyPair key2 = keys.get(1);
        if (key1.getPublic() != null && key2.getPrivate() != null) {
            return new KeyPair(key1.getPublic(), key2.getPrivate());
        }
        if (key1.getPrivate() != null && key2.getPublic() != null) {
            return new KeyPair(key2.getPublic(), key1.getPrivate());
        }
        throw new JOSEException("Not a public/private key pair");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JWK)) {
            return false;
        }
        JWK jwk = (JWK)o;
        return Objects.equals(this.kty, jwk.kty) && Objects.equals(this.use, jwk.use) && Objects.equals(this.ops, jwk.ops) && Objects.equals(this.alg, jwk.alg) && Objects.equals(this.kid, jwk.kid) && Objects.equals(this.x5u, jwk.x5u) && Objects.equals(this.x5t, jwk.x5t) && Objects.equals(this.x5t256, jwk.x5t256) && Objects.equals(this.x5c, jwk.x5c) && Objects.equals(this.keyStore, jwk.keyStore);
    }

    public int hashCode() {
        return Objects.hash(this.kty, this.use, this.ops, this.alg, this.kid, this.x5u, this.x5t, this.x5t256, this.x5c, this.keyStore);
    }
}

