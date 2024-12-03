/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.AsymmetricJWK;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMetadata;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.ThumbprintUtils;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.ByteUtils;
import com.nimbusds.jose.util.IntegerOverflowException;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.io.Serializable;
import java.math.BigInteger;
import java.net.URI;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAMultiPrimePrivateCrtKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAMultiPrimePrivateCrtKeySpec;
import java.security.spec.RSAOtherPrimeInfo;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

@Immutable
public final class RSAKey
extends JWK
implements AsymmetricJWK {
    private static final long serialVersionUID = 1L;
    private final Base64URL n;
    private final Base64URL e;
    private final Base64URL d;
    private final Base64URL p;
    private final Base64URL q;
    private final Base64URL dp;
    private final Base64URL dq;
    private final Base64URL qi;
    private final List<OtherPrimesInfo> oth;
    private final PrivateKey privateKey;

    public RSAKey(Base64URL n, Base64URL e, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(n, e, null, null, null, null, null, null, null, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }

    public RSAKey(Base64URL n, Base64URL e, Base64URL d, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(n, e, d, null, null, null, null, null, null, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
        if (d == null) {
            throw new IllegalArgumentException("The private exponent must not be null");
        }
    }

    public RSAKey(Base64URL n, Base64URL e, Base64URL p, Base64URL q, Base64URL dp, Base64URL dq, Base64URL qi, List<OtherPrimesInfo> oth, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(n, e, null, p, q, dp, dq, qi, oth, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
        if (p == null) {
            throw new IllegalArgumentException("The first prime factor must not be null");
        }
        if (q == null) {
            throw new IllegalArgumentException("The second prime factor must not be null");
        }
        if (dp == null) {
            throw new IllegalArgumentException("The first factor CRT exponent must not be null");
        }
        if (dq == null) {
            throw new IllegalArgumentException("The second factor CRT exponent must not be null");
        }
        if (qi == null) {
            throw new IllegalArgumentException("The first CRT coefficient must not be null");
        }
    }

    @Deprecated
    public RSAKey(Base64URL n, Base64URL e, Base64URL d, Base64URL p, Base64URL q, Base64URL dp, Base64URL dq, Base64URL qi, List<OtherPrimesInfo> oth, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c) {
        this(n, e, d, p, q, dp, dq, qi, oth, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, null);
    }

    public RSAKey(Base64URL n, Base64URL e, Base64URL d, Base64URL p, Base64URL q, Base64URL dp, Base64URL dq, Base64URL qi, List<OtherPrimesInfo> oth, PrivateKey prv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        super(KeyType.RSA, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
        if (n == null) {
            throw new IllegalArgumentException("The modulus value must not be null");
        }
        this.n = n;
        if (e == null) {
            throw new IllegalArgumentException("The public exponent value must not be null");
        }
        this.e = e;
        if (this.getParsedX509CertChain() != null && !this.matches(this.getParsedX509CertChain().get(0))) {
            throw new IllegalArgumentException("The public subject key info of the first X.509 certificate in the chain must match the JWK type and public parameters");
        }
        this.d = d;
        if (p != null && q != null && dp != null && dq != null && qi != null) {
            this.p = p;
            this.q = q;
            this.dp = dp;
            this.dq = dq;
            this.qi = qi;
            this.oth = oth != null ? Collections.unmodifiableList(oth) : Collections.emptyList();
        } else if (p == null && q == null && dp == null && dq == null && qi == null && oth == null) {
            this.p = null;
            this.q = null;
            this.dp = null;
            this.dq = null;
            this.qi = null;
            this.oth = Collections.emptyList();
        } else {
            if (p != null || q != null || dp != null || dq != null || qi != null) {
                if (p == null) {
                    throw new IllegalArgumentException("Incomplete second private (CRT) representation: The first prime factor must not be null");
                }
                if (q == null) {
                    throw new IllegalArgumentException("Incomplete second private (CRT) representation: The second prime factor must not be null");
                }
                if (dp == null) {
                    throw new IllegalArgumentException("Incomplete second private (CRT) representation: The first factor CRT exponent must not be null");
                }
                if (dq == null) {
                    throw new IllegalArgumentException("Incomplete second private (CRT) representation: The second factor CRT exponent must not be null");
                }
                throw new IllegalArgumentException("Incomplete second private (CRT) representation: The first CRT coefficient must not be null");
            }
            this.p = null;
            this.q = null;
            this.dp = null;
            this.dq = null;
            this.qi = null;
            this.oth = Collections.emptyList();
        }
        this.privateKey = prv;
    }

    public RSAKey(RSAPublicKey pub, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(Base64URL.encode(pub.getModulus()), Base64URL.encode(pub.getPublicExponent()), use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }

    public RSAKey(RSAPublicKey pub, RSAPrivateKey priv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(Base64URL.encode(pub.getModulus()), Base64URL.encode(pub.getPublicExponent()), Base64URL.encode(priv.getPrivateExponent()), use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }

    public RSAKey(RSAPublicKey pub, RSAPrivateCrtKey priv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(Base64URL.encode(pub.getModulus()), Base64URL.encode(pub.getPublicExponent()), Base64URL.encode(priv.getPrivateExponent()), Base64URL.encode(priv.getPrimeP()), Base64URL.encode(priv.getPrimeQ()), Base64URL.encode(priv.getPrimeExponentP()), Base64URL.encode(priv.getPrimeExponentQ()), Base64URL.encode(priv.getCrtCoefficient()), null, null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }

    public RSAKey(RSAPublicKey pub, RSAMultiPrimePrivateCrtKey priv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(Base64URL.encode(pub.getModulus()), Base64URL.encode(pub.getPublicExponent()), Base64URL.encode(priv.getPrivateExponent()), Base64URL.encode(priv.getPrimeP()), Base64URL.encode(priv.getPrimeQ()), Base64URL.encode(priv.getPrimeExponentP()), Base64URL.encode(priv.getPrimeExponentQ()), Base64URL.encode(priv.getCrtCoefficient()), OtherPrimesInfo.toList(priv.getOtherPrimeInfo()), null, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }

    public RSAKey(RSAPublicKey pub, PrivateKey priv, KeyUse use, Set<KeyOperation> ops, Algorithm alg, String kid, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, KeyStore ks) {
        this(Base64URL.encode(pub.getModulus()), Base64URL.encode(pub.getPublicExponent()), null, null, null, null, null, null, null, priv, use, ops, alg, kid, x5u, x5t, x5t256, x5c, ks);
    }

    public Base64URL getModulus() {
        return this.n;
    }

    public Base64URL getPublicExponent() {
        return this.e;
    }

    public Base64URL getPrivateExponent() {
        return this.d;
    }

    public Base64URL getFirstPrimeFactor() {
        return this.p;
    }

    public Base64URL getSecondPrimeFactor() {
        return this.q;
    }

    public Base64URL getFirstFactorCRTExponent() {
        return this.dp;
    }

    public Base64URL getSecondFactorCRTExponent() {
        return this.dq;
    }

    public Base64URL getFirstCRTCoefficient() {
        return this.qi;
    }

    public List<OtherPrimesInfo> getOtherPrimes() {
        return this.oth;
    }

    public RSAPublicKey toRSAPublicKey() throws JOSEException {
        BigInteger modulus = this.n.decodeToBigInteger();
        BigInteger exponent = this.e.decodeToBigInteger();
        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey)factory.generatePublic(spec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    public RSAPrivateKey toRSAPrivateKey() throws JOSEException {
        RSAPrivateKeySpec spec;
        if (this.d == null) {
            return null;
        }
        BigInteger modulus = this.n.decodeToBigInteger();
        BigInteger privateExponent = this.d.decodeToBigInteger();
        if (this.p == null) {
            spec = new RSAPrivateKeySpec(modulus, privateExponent);
        } else {
            BigInteger publicExponent = this.e.decodeToBigInteger();
            BigInteger primeP = this.p.decodeToBigInteger();
            BigInteger primeQ = this.q.decodeToBigInteger();
            BigInteger primeExponentP = this.dp.decodeToBigInteger();
            BigInteger primeExponentQ = this.dq.decodeToBigInteger();
            BigInteger crtCoefficient = this.qi.decodeToBigInteger();
            if (this.oth != null && !this.oth.isEmpty()) {
                RSAOtherPrimeInfo[] otherInfo = new RSAOtherPrimeInfo[this.oth.size()];
                for (int i = 0; i < this.oth.size(); ++i) {
                    OtherPrimesInfo opi = this.oth.get(i);
                    BigInteger otherPrime = opi.getPrimeFactor().decodeToBigInteger();
                    BigInteger otherPrimeExponent = opi.getFactorCRTExponent().decodeToBigInteger();
                    BigInteger otherCrtCoefficient = opi.getFactorCRTCoefficient().decodeToBigInteger();
                    otherInfo[i] = new RSAOtherPrimeInfo(otherPrime, otherPrimeExponent, otherCrtCoefficient);
                }
                spec = new RSAMultiPrimePrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient, otherInfo);
            } else {
                spec = new RSAPrivateCrtKeySpec(modulus, publicExponent, privateExponent, primeP, primeQ, primeExponentP, primeExponentQ, crtCoefficient);
            }
        }
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey)factory.generatePrivate(spec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new JOSEException(e.getMessage(), e);
        }
    }

    @Override
    public PublicKey toPublicKey() throws JOSEException {
        return this.toRSAPublicKey();
    }

    @Override
    public PrivateKey toPrivateKey() throws JOSEException {
        RSAPrivateKey prv = this.toRSAPrivateKey();
        if (prv != null) {
            return prv;
        }
        return this.privateKey;
    }

    @Override
    public KeyPair toKeyPair() throws JOSEException {
        return new KeyPair(this.toRSAPublicKey(), this.toPrivateKey());
    }

    @Override
    public boolean matches(X509Certificate cert) {
        RSAPublicKey certRSAKey;
        try {
            certRSAKey = (RSAPublicKey)this.getParsedX509CertChain().get(0).getPublicKey();
        }
        catch (ClassCastException ex) {
            return false;
        }
        if (!this.e.decodeToBigInteger().equals(certRSAKey.getPublicExponent())) {
            return false;
        }
        return this.n.decodeToBigInteger().equals(certRSAKey.getModulus());
    }

    @Override
    public LinkedHashMap<String, ?> getRequiredParams() {
        LinkedHashMap<String, String> requiredParams = new LinkedHashMap<String, String>();
        requiredParams.put("e", this.e.toString());
        requiredParams.put("kty", this.getKeyType().getValue());
        requiredParams.put("n", this.n.toString());
        return requiredParams;
    }

    @Override
    public boolean isPrivate() {
        return this.d != null || this.p != null || this.privateKey != null;
    }

    @Override
    public int size() {
        try {
            return ByteUtils.safeBitLength(this.n.decode());
        }
        catch (IntegerOverflowException e) {
            throw new ArithmeticException(e.getMessage());
        }
    }

    @Override
    public RSAKey toPublicJWK() {
        return new RSAKey(this.getModulus(), this.getPublicExponent(), this.getKeyUse(), this.getKeyOperations(), this.getAlgorithm(), this.getKeyID(), this.getX509CertURL(), this.getX509CertThumbprint(), this.getX509CertSHA256Thumbprint(), this.getX509CertChain(), this.getKeyStore());
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        o.put("n", this.n.toString());
        o.put("e", this.e.toString());
        if (this.d != null) {
            o.put("d", this.d.toString());
        }
        if (this.p != null) {
            o.put("p", this.p.toString());
        }
        if (this.q != null) {
            o.put("q", this.q.toString());
        }
        if (this.dp != null) {
            o.put("dp", this.dp.toString());
        }
        if (this.dq != null) {
            o.put("dq", this.dq.toString());
        }
        if (this.qi != null) {
            o.put("qi", this.qi.toString());
        }
        if (this.oth != null && !this.oth.isEmpty()) {
            JSONArray a = new JSONArray();
            for (OtherPrimesInfo other : this.oth) {
                JSONObject oo = new JSONObject();
                oo.put("r", other.r.toString());
                oo.put("d", other.d.toString());
                oo.put("t", other.t.toString());
                a.add(oo);
            }
            o.put("oth", a);
        }
        return o;
    }

    public static RSAKey parse(String s) throws ParseException {
        return RSAKey.parse(JSONObjectUtils.parse(s));
    }

    public static RSAKey parse(JSONObject jsonObject) throws ParseException {
        JSONArray arr;
        if (!KeyType.RSA.equals(JWKMetadata.parseKeyType(jsonObject))) {
            throw new ParseException("The key type \"kty\" must be RSA", 0);
        }
        Base64URL n = JSONObjectUtils.getBase64URL(jsonObject, "n");
        Base64URL e = JSONObjectUtils.getBase64URL(jsonObject, "e");
        Base64URL d = JSONObjectUtils.getBase64URL(jsonObject, "d");
        Base64URL p = JSONObjectUtils.getBase64URL(jsonObject, "p");
        Base64URL q = JSONObjectUtils.getBase64URL(jsonObject, "q");
        Base64URL dp = JSONObjectUtils.getBase64URL(jsonObject, "dp");
        Base64URL dq = JSONObjectUtils.getBase64URL(jsonObject, "dq");
        Base64URL qi = JSONObjectUtils.getBase64URL(jsonObject, "qi");
        ArrayList<OtherPrimesInfo> oth = null;
        if (jsonObject.containsKey("oth") && (arr = JSONObjectUtils.getJSONArray(jsonObject, "oth")) != null) {
            oth = new ArrayList<OtherPrimesInfo>(arr.size());
            for (Object o : arr) {
                if (!(o instanceof JSONObject)) continue;
                JSONObject otherJson = (JSONObject)o;
                Base64URL r = JSONObjectUtils.getBase64URL(otherJson, "r");
                Base64URL odq = JSONObjectUtils.getBase64URL(otherJson, "dq");
                Base64URL t = JSONObjectUtils.getBase64URL(otherJson, "t");
                try {
                    oth.add(new OtherPrimesInfo(r, odq, t));
                }
                catch (IllegalArgumentException iae) {
                    throw new ParseException(iae.getMessage(), 0);
                }
            }
        }
        try {
            return new RSAKey(n, e, d, p, q, dp, dq, qi, oth, null, JWKMetadata.parseKeyUse(jsonObject), JWKMetadata.parseKeyOperations(jsonObject), JWKMetadata.parseAlgorithm(jsonObject), JWKMetadata.parseKeyID(jsonObject), JWKMetadata.parseX509CertURL(jsonObject), JWKMetadata.parseX509CertThumbprint(jsonObject), JWKMetadata.parseX509CertSHA256Thumbprint(jsonObject), JWKMetadata.parseX509CertChain(jsonObject), null);
        }
        catch (IllegalArgumentException ex) {
            throw new ParseException(ex.getMessage(), 0);
        }
    }

    public static RSAKey parse(X509Certificate cert) throws JOSEException {
        if (!(cert.getPublicKey() instanceof RSAPublicKey)) {
            throw new JOSEException("The public key of the X.509 certificate is not RSA");
        }
        RSAPublicKey publicKey = (RSAPublicKey)cert.getPublicKey();
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            return new Builder(publicKey).keyUse(KeyUse.from(cert)).keyID(cert.getSerialNumber().toString(10)).x509CertChain(Collections.singletonList(Base64.encode(cert.getEncoded()))).x509CertSHA256Thumbprint(Base64URL.encode(sha256.digest(cert.getEncoded()))).build();
        }
        catch (NoSuchAlgorithmException e) {
            throw new JOSEException("Couldn't encode x5t parameter: " + e.getMessage(), e);
        }
        catch (CertificateEncodingException e) {
            throw new JOSEException("Couldn't encode x5c parameter: " + e.getMessage(), e);
        }
    }

    public static RSAKey load(KeyStore keyStore, String alias, char[] pin) throws KeyStoreException, JOSEException {
        Key key;
        Certificate cert = keyStore.getCertificate(alias);
        if (!(cert instanceof X509Certificate)) {
            return null;
        }
        X509Certificate x509Cert = (X509Certificate)cert;
        if (!(x509Cert.getPublicKey() instanceof RSAPublicKey)) {
            throw new JOSEException("Couldn't load RSA JWK: The key algorithm is not RSA");
        }
        RSAKey rsaJWK = RSAKey.parse(x509Cert);
        rsaJWK = new Builder(rsaJWK).keyID(alias).keyStore(keyStore).build();
        try {
            key = keyStore.getKey(alias, pin);
        }
        catch (NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new JOSEException("Couldn't retrieve private RSA key (bad pin?): " + e.getMessage(), e);
        }
        if (key instanceof RSAPrivateKey) {
            return new Builder(rsaJWK).privateKey((RSAPrivateKey)key).build();
        }
        if (key instanceof PrivateKey && "RSA".equalsIgnoreCase(key.getAlgorithm())) {
            return new Builder(rsaJWK).privateKey((PrivateKey)key).build();
        }
        return rsaJWK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RSAKey)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        RSAKey rsaKey = (RSAKey)o;
        return Objects.equals(this.n, rsaKey.n) && Objects.equals(this.e, rsaKey.e) && Objects.equals(this.d, rsaKey.d) && Objects.equals(this.p, rsaKey.p) && Objects.equals(this.q, rsaKey.q) && Objects.equals(this.dp, rsaKey.dp) && Objects.equals(this.dq, rsaKey.dq) && Objects.equals(this.qi, rsaKey.qi) && Objects.equals(this.oth, rsaKey.oth) && Objects.equals(this.privateKey, rsaKey.privateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.n, this.e, this.d, this.p, this.q, this.dp, this.dq, this.qi, this.oth, this.privateKey);
    }

    public static class Builder {
        private final Base64URL n;
        private final Base64URL e;
        private Base64URL d;
        private Base64URL p;
        private Base64URL q;
        private Base64URL dp;
        private Base64URL dq;
        private Base64URL qi;
        private List<OtherPrimesInfo> oth;
        private PrivateKey priv;
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

        public Builder(Base64URL n, Base64URL e) {
            if (n == null) {
                throw new IllegalArgumentException("The modulus value must not be null");
            }
            this.n = n;
            if (e == null) {
                throw new IllegalArgumentException("The public exponent value must not be null");
            }
            this.e = e;
        }

        public Builder(RSAPublicKey pub) {
            this.n = Base64URL.encode(pub.getModulus());
            this.e = Base64URL.encode(pub.getPublicExponent());
        }

        public Builder(RSAKey rsaJWK) {
            this.n = rsaJWK.n;
            this.e = rsaJWK.e;
            this.d = rsaJWK.d;
            this.p = rsaJWK.p;
            this.q = rsaJWK.q;
            this.dp = rsaJWK.dp;
            this.dq = rsaJWK.dq;
            this.qi = rsaJWK.qi;
            this.oth = rsaJWK.oth;
            this.priv = rsaJWK.privateKey;
            this.use = rsaJWK.getKeyUse();
            this.ops = rsaJWK.getKeyOperations();
            this.alg = rsaJWK.getAlgorithm();
            this.kid = rsaJWK.getKeyID();
            this.x5u = rsaJWK.getX509CertURL();
            this.x5t = rsaJWK.getX509CertThumbprint();
            this.x5t256 = rsaJWK.getX509CertSHA256Thumbprint();
            this.x5c = rsaJWK.getX509CertChain();
            this.ks = rsaJWK.getKeyStore();
        }

        public Builder privateExponent(Base64URL d) {
            this.d = d;
            return this;
        }

        public Builder privateKey(RSAPrivateKey priv) {
            if (priv instanceof RSAPrivateCrtKey) {
                return this.privateKey((RSAPrivateCrtKey)priv);
            }
            if (priv instanceof RSAMultiPrimePrivateCrtKey) {
                return this.privateKey((RSAMultiPrimePrivateCrtKey)priv);
            }
            this.d = Base64URL.encode(priv.getPrivateExponent());
            return this;
        }

        public Builder privateKey(PrivateKey priv) {
            if (priv instanceof RSAPrivateKey) {
                return this.privateKey((RSAPrivateKey)priv);
            }
            if (!"RSA".equalsIgnoreCase(priv.getAlgorithm())) {
                throw new IllegalArgumentException("The private key algorithm must be RSA");
            }
            this.priv = priv;
            return this;
        }

        public Builder firstPrimeFactor(Base64URL p) {
            this.p = p;
            return this;
        }

        public Builder secondPrimeFactor(Base64URL q) {
            this.q = q;
            return this;
        }

        public Builder firstFactorCRTExponent(Base64URL dp) {
            this.dp = dp;
            return this;
        }

        public Builder secondFactorCRTExponent(Base64URL dq) {
            this.dq = dq;
            return this;
        }

        public Builder firstCRTCoefficient(Base64URL qi) {
            this.qi = qi;
            return this;
        }

        public Builder otherPrimes(List<OtherPrimesInfo> oth) {
            this.oth = oth;
            return this;
        }

        public Builder privateKey(RSAPrivateCrtKey priv) {
            this.d = Base64URL.encode(priv.getPrivateExponent());
            this.p = Base64URL.encode(priv.getPrimeP());
            this.q = Base64URL.encode(priv.getPrimeQ());
            this.dp = Base64URL.encode(priv.getPrimeExponentP());
            this.dq = Base64URL.encode(priv.getPrimeExponentQ());
            this.qi = Base64URL.encode(priv.getCrtCoefficient());
            return this;
        }

        public Builder privateKey(RSAMultiPrimePrivateCrtKey priv) {
            this.d = Base64URL.encode(priv.getPrivateExponent());
            this.p = Base64URL.encode(priv.getPrimeP());
            this.q = Base64URL.encode(priv.getPrimeQ());
            this.dp = Base64URL.encode(priv.getPrimeExponentP());
            this.dq = Base64URL.encode(priv.getPrimeExponentQ());
            this.qi = Base64URL.encode(priv.getCrtCoefficient());
            this.oth = OtherPrimesInfo.toList(priv.getOtherPrimeInfo());
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
            requiredParams.put("e", this.e.toString());
            requiredParams.put("kty", KeyType.RSA.getValue());
            requiredParams.put("n", this.n.toString());
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

        public RSAKey build() {
            try {
                return new RSAKey(this.n, this.e, this.d, this.p, this.q, this.dp, this.dq, this.qi, this.oth, this.priv, this.use, this.ops, this.alg, this.kid, this.x5u, this.x5t, this.x5t256, this.x5c, this.ks);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
    }

    @Immutable
    public static class OtherPrimesInfo
    implements Serializable {
        private static final long serialVersionUID = 1L;
        private final Base64URL r;
        private final Base64URL d;
        private final Base64URL t;

        public OtherPrimesInfo(Base64URL r, Base64URL d, Base64URL t) {
            if (r == null) {
                throw new IllegalArgumentException("The prime factor must not be null");
            }
            this.r = r;
            if (d == null) {
                throw new IllegalArgumentException("The factor CRT exponent must not be null");
            }
            this.d = d;
            if (t == null) {
                throw new IllegalArgumentException("The factor CRT coefficient must not be null");
            }
            this.t = t;
        }

        public OtherPrimesInfo(RSAOtherPrimeInfo oth) {
            this.r = Base64URL.encode(oth.getPrime());
            this.d = Base64URL.encode(oth.getExponent());
            this.t = Base64URL.encode(oth.getCrtCoefficient());
        }

        public Base64URL getPrimeFactor() {
            return this.r;
        }

        public Base64URL getFactorCRTExponent() {
            return this.d;
        }

        public Base64URL getFactorCRTCoefficient() {
            return this.t;
        }

        public static List<OtherPrimesInfo> toList(RSAOtherPrimeInfo[] othArray) {
            ArrayList<OtherPrimesInfo> list = new ArrayList<OtherPrimesInfo>();
            if (othArray == null) {
                return list;
            }
            for (RSAOtherPrimeInfo oth : othArray) {
                list.add(new OtherPrimesInfo(oth));
            }
            return list;
        }
    }
}

