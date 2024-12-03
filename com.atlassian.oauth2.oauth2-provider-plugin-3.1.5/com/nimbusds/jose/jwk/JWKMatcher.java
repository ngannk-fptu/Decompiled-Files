/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.CurveBasedJWK;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.X509CertUtils;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public class JWKMatcher {
    private final Set<KeyType> types;
    private final Set<KeyUse> uses;
    private final Set<KeyOperation> ops;
    private final Set<Algorithm> algs;
    private final Set<String> ids;
    private final boolean hasUse;
    private final boolean hasID;
    private final boolean privateOnly;
    private final boolean publicOnly;
    private final int minSizeBits;
    private final int maxSizeBits;
    private final Set<Integer> sizesBits;
    private final Set<Curve> curves;
    private final Set<Base64URL> x5tS256s;
    private final boolean hasX5C;

    @Deprecated
    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean privateOnly, boolean publicOnly) {
        this(types, uses, ops, algs, ids, privateOnly, publicOnly, 0, 0);
    }

    @Deprecated
    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean privateOnly, boolean publicOnly, int minSizeBits, int maxSizeBits) {
        this(types, uses, ops, algs, ids, privateOnly, publicOnly, minSizeBits, maxSizeBits, null);
    }

    @Deprecated
    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean privateOnly, boolean publicOnly, int minSizeBits, int maxSizeBits, Set<Curve> curves) {
        this(types, uses, ops, algs, ids, privateOnly, publicOnly, minSizeBits, maxSizeBits, null, curves);
    }

    @Deprecated
    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean privateOnly, boolean publicOnly, int minSizeBits, int maxSizeBits, Set<Integer> sizesBits, Set<Curve> curves) {
        this(types, uses, ops, algs, ids, false, false, privateOnly, publicOnly, minSizeBits, maxSizeBits, sizesBits, curves);
    }

    @Deprecated
    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean hasUse, boolean hasID, boolean privateOnly, boolean publicOnly, int minSizeBits, int maxSizeBits, Set<Integer> sizesBits, Set<Curve> curves) {
        this(types, uses, ops, algs, ids, hasUse, hasID, privateOnly, publicOnly, minSizeBits, maxSizeBits, sizesBits, curves, null);
    }

    @Deprecated
    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean hasUse, boolean hasID, boolean privateOnly, boolean publicOnly, int minSizeBits, int maxSizeBits, Set<Integer> sizesBits, Set<Curve> curves, Set<Base64URL> x5tS256s) {
        this(types, uses, ops, algs, ids, hasUse, hasID, privateOnly, publicOnly, minSizeBits, maxSizeBits, sizesBits, curves, x5tS256s, false);
    }

    public JWKMatcher(Set<KeyType> types, Set<KeyUse> uses, Set<KeyOperation> ops, Set<Algorithm> algs, Set<String> ids, boolean hasUse, boolean hasID, boolean privateOnly, boolean publicOnly, int minSizeBits, int maxSizeBits, Set<Integer> sizesBits, Set<Curve> curves, Set<Base64URL> x5tS256s, boolean hasX5C) {
        this.types = types;
        this.uses = uses;
        this.ops = ops;
        this.algs = algs;
        this.ids = ids;
        this.hasUse = hasUse;
        this.hasID = hasID;
        this.privateOnly = privateOnly;
        this.publicOnly = publicOnly;
        this.minSizeBits = minSizeBits;
        this.maxSizeBits = maxSizeBits;
        this.sizesBits = sizesBits;
        this.curves = curves;
        this.x5tS256s = x5tS256s;
        this.hasX5C = hasX5C;
    }

    public static JWKMatcher forJWEHeader(JWEHeader jweHeader) {
        return new Builder().keyType(KeyType.forAlgorithm(jweHeader.getAlgorithm())).keyID(jweHeader.getKeyID()).keyUses(KeyUse.ENCRYPTION, null).algorithms(jweHeader.getAlgorithm(), null).build();
    }

    public static JWKMatcher forJWSHeader(JWSHeader jwsHeader) {
        JWSAlgorithm algorithm = jwsHeader.getAlgorithm();
        if (JWSAlgorithm.Family.RSA.contains(algorithm) || JWSAlgorithm.Family.EC.contains(algorithm)) {
            return new Builder().keyType(KeyType.forAlgorithm(algorithm)).keyID(jwsHeader.getKeyID()).keyUses(KeyUse.SIGNATURE, null).algorithms(algorithm, null).x509CertSHA256Thumbprint(jwsHeader.getX509CertSHA256Thumbprint()).build();
        }
        if (JWSAlgorithm.Family.HMAC_SHA.contains(algorithm)) {
            return new Builder().keyType(KeyType.forAlgorithm(algorithm)).keyID(jwsHeader.getKeyID()).privateOnly(true).algorithms(algorithm, null).build();
        }
        if (JWSAlgorithm.Family.ED.contains(algorithm)) {
            return new Builder().keyType(KeyType.forAlgorithm(algorithm)).keyID(jwsHeader.getKeyID()).keyUses(KeyUse.SIGNATURE, null).algorithms(algorithm, null).curves(Curve.forJWSAlgorithm(algorithm)).build();
        }
        return null;
    }

    public Set<KeyType> getKeyTypes() {
        return this.types;
    }

    public Set<KeyUse> getKeyUses() {
        return this.uses;
    }

    public Set<KeyOperation> getKeyOperations() {
        return this.ops;
    }

    public Set<Algorithm> getAlgorithms() {
        return this.algs;
    }

    public Set<String> getKeyIDs() {
        return this.ids;
    }

    public boolean hasKeyUse() {
        return this.hasUse;
    }

    public boolean hasKeyID() {
        return this.hasID;
    }

    public boolean isPrivateOnly() {
        return this.privateOnly;
    }

    public boolean isPublicOnly() {
        return this.publicOnly;
    }

    @Deprecated
    public int getMinSize() {
        return this.getMinKeySize();
    }

    public int getMinKeySize() {
        return this.minSizeBits;
    }

    @Deprecated
    public int getMaxSize() {
        return this.getMaxKeySize();
    }

    public int getMaxKeySize() {
        return this.maxSizeBits;
    }

    public Set<Integer> getKeySizes() {
        return this.sizesBits;
    }

    public Set<Curve> getCurves() {
        return this.curves;
    }

    public Set<Base64URL> getX509CertSHA256Thumbprints() {
        return this.x5tS256s;
    }

    public boolean hasX509CertChain() {
        return this.hasX5C;
    }

    public boolean matches(JWK key) {
        if (this.hasUse && key.getKeyUse() == null) {
            return false;
        }
        if (this.hasID && (key.getKeyID() == null || key.getKeyID().trim().isEmpty())) {
            return false;
        }
        if (this.privateOnly && !key.isPrivate()) {
            return false;
        }
        if (this.publicOnly && key.isPrivate()) {
            return false;
        }
        if (this.types != null && !this.types.contains(key.getKeyType())) {
            return false;
        }
        if (this.uses != null && !this.uses.contains(key.getKeyUse())) {
            return false;
        }
        if (!(this.ops == null || this.ops.contains(null) && key.getKeyOperations() == null || key.getKeyOperations() != null && this.ops.containsAll(key.getKeyOperations()))) {
            return false;
        }
        if (this.algs != null && !this.algs.contains(key.getAlgorithm())) {
            return false;
        }
        if (this.ids != null && !this.ids.contains(key.getKeyID())) {
            return false;
        }
        if (this.minSizeBits > 0 && key.size() < this.minSizeBits) {
            return false;
        }
        if (this.maxSizeBits > 0 && key.size() > this.maxSizeBits) {
            return false;
        }
        if (this.sizesBits != null && !this.sizesBits.contains(key.size())) {
            return false;
        }
        if (this.curves != null) {
            if (!(key instanceof CurveBasedJWK)) {
                return false;
            }
            CurveBasedJWK curveBasedJWK = (CurveBasedJWK)((Object)key);
            if (!this.curves.contains(curveBasedJWK.getCurve())) {
                return false;
            }
        }
        if (this.x5tS256s != null) {
            boolean matchingCertFound = false;
            if (key.getX509CertChain() != null && !key.getX509CertChain().isEmpty()) {
                try {
                    X509Certificate cert = X509CertUtils.parseWithException(key.getX509CertChain().get(0).decode());
                    matchingCertFound = this.x5tS256s.contains(X509CertUtils.computeSHA256Thumbprint(cert));
                }
                catch (CertificateException cert) {
                    // empty catch block
                }
            }
            boolean matchingX5T256Found = this.x5tS256s.contains(key.getX509CertSHA256Thumbprint());
            if (!matchingCertFound && !matchingX5T256Found) {
                return false;
            }
        }
        if (this.hasX5C) {
            return key.getX509CertChain() != null && !key.getX509CertChain().isEmpty();
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        JWKMatcher.append(sb, "kty", this.types);
        JWKMatcher.append(sb, "use", this.uses);
        JWKMatcher.append(sb, "key_ops", this.ops);
        JWKMatcher.append(sb, "alg", this.algs);
        JWKMatcher.append(sb, "kid", this.ids);
        if (this.hasUse) {
            sb.append("has_use=true ");
        }
        if (this.hasID) {
            sb.append("has_id=true ");
        }
        if (this.privateOnly) {
            sb.append("private_only=true ");
        }
        if (this.publicOnly) {
            sb.append("public_only=true ");
        }
        if (this.minSizeBits > 0) {
            sb.append("min_size=" + this.minSizeBits + " ");
        }
        if (this.maxSizeBits > 0) {
            sb.append("max_size=" + this.maxSizeBits + " ");
        }
        JWKMatcher.append(sb, "size", this.sizesBits);
        JWKMatcher.append(sb, "crv", this.curves);
        JWKMatcher.append(sb, "x5t#S256", this.x5tS256s);
        if (this.hasX5C) {
            sb.append("has_x5c=true");
        }
        return sb.toString().trim();
    }

    private static void append(StringBuilder sb, String key, Set<?> values) {
        if (values != null) {
            sb.append(key);
            sb.append('=');
            if (values.size() == 1) {
                Object value = values.iterator().next();
                if (value == null) {
                    sb.append("ANY");
                } else {
                    sb.append(value.toString().trim());
                }
            } else {
                sb.append(values.toString().trim());
            }
            sb.append(' ');
        }
    }

    public static class Builder {
        private Set<KeyType> types;
        private Set<KeyUse> uses;
        private Set<KeyOperation> ops;
        private Set<Algorithm> algs;
        private Set<String> ids;
        private boolean hasUse = false;
        private boolean hasID = false;
        private boolean privateOnly = false;
        private boolean publicOnly = false;
        private int minSizeBits = 0;
        private int maxSizeBits = 0;
        private Set<Integer> sizesBits;
        private Set<Curve> curves;
        private Set<Base64URL> x5tS256s;
        private boolean hasX5C = false;

        public Builder keyType(KeyType kty) {
            this.types = kty == null ? null : new HashSet<KeyType>(Collections.singletonList(kty));
            return this;
        }

        public Builder keyTypes(KeyType ... types) {
            this.keyTypes(new LinkedHashSet<KeyType>(Arrays.asList(types)));
            return this;
        }

        public Builder keyTypes(Set<KeyType> types) {
            this.types = types;
            return this;
        }

        public Builder keyUse(KeyUse use) {
            this.uses = use == null ? null : new HashSet<KeyUse>(Collections.singletonList(use));
            return this;
        }

        public Builder keyUses(KeyUse ... uses) {
            this.keyUses(new LinkedHashSet<KeyUse>(Arrays.asList(uses)));
            return this;
        }

        public Builder keyUses(Set<KeyUse> uses) {
            this.uses = uses;
            return this;
        }

        public Builder keyOperation(KeyOperation op) {
            this.ops = op == null ? null : new HashSet<KeyOperation>(Collections.singletonList(op));
            return this;
        }

        public Builder keyOperations(KeyOperation ... ops) {
            this.keyOperations(new LinkedHashSet<KeyOperation>(Arrays.asList(ops)));
            return this;
        }

        public Builder keyOperations(Set<KeyOperation> ops) {
            this.ops = ops;
            return this;
        }

        public Builder algorithm(Algorithm alg) {
            this.algs = alg == null ? null : new HashSet<Algorithm>(Collections.singletonList(alg));
            return this;
        }

        public Builder algorithms(Algorithm ... algs) {
            this.algorithms(new LinkedHashSet<Algorithm>(Arrays.asList(algs)));
            return this;
        }

        public Builder algorithms(Set<Algorithm> algs) {
            this.algs = algs;
            return this;
        }

        public Builder keyID(String id) {
            this.ids = id == null ? null : new HashSet<String>(Collections.singletonList(id));
            return this;
        }

        public Builder keyIDs(String ... ids) {
            this.keyIDs(new LinkedHashSet<String>(Arrays.asList(ids)));
            return this;
        }

        public Builder keyIDs(Set<String> ids) {
            this.ids = ids;
            return this;
        }

        public Builder hasKeyUse(boolean hasUse) {
            this.hasUse = hasUse;
            return this;
        }

        public Builder hasKeyID(boolean hasID) {
            this.hasID = hasID;
            return this;
        }

        public Builder privateOnly(boolean privateOnly) {
            this.privateOnly = privateOnly;
            return this;
        }

        public Builder publicOnly(boolean publicOnly) {
            this.publicOnly = publicOnly;
            return this;
        }

        public Builder minKeySize(int minSizeBits) {
            this.minSizeBits = minSizeBits;
            return this;
        }

        public Builder maxKeySize(int maxSizeBits) {
            this.maxSizeBits = maxSizeBits;
            return this;
        }

        public Builder keySize(int keySizeBits) {
            this.sizesBits = keySizeBits <= 0 ? null : Collections.singleton(keySizeBits);
            return this;
        }

        public Builder keySizes(int ... keySizesBits) {
            LinkedHashSet<Integer> sizesSet = new LinkedHashSet<Integer>();
            for (int keySize : keySizesBits) {
                sizesSet.add(keySize);
            }
            this.keySizes(sizesSet);
            return this;
        }

        public Builder keySizes(Set<Integer> keySizesBits) {
            this.sizesBits = keySizesBits;
            return this;
        }

        public Builder curve(Curve curve) {
            this.curves = curve == null ? null : Collections.singleton(curve);
            return this;
        }

        public Builder curves(Curve ... curves) {
            this.curves(new LinkedHashSet<Curve>(Arrays.asList(curves)));
            return this;
        }

        public Builder curves(Set<Curve> curves) {
            this.curves = curves;
            return this;
        }

        public Builder x509CertSHA256Thumbprint(Base64URL x5tS256) {
            this.x5tS256s = x5tS256 == null ? null : Collections.singleton(x5tS256);
            return this;
        }

        public Builder x509CertSHA256Thumbprints(Base64URL ... x5tS256s) {
            return this.x509CertSHA256Thumbprints(new LinkedHashSet<Base64URL>(Arrays.asList(x5tS256s)));
        }

        public Builder x509CertSHA256Thumbprints(Set<Base64URL> x5tS256s) {
            this.x5tS256s = x5tS256s;
            return this;
        }

        public Builder hasX509CertChain(boolean hasX5C) {
            this.hasX5C = hasX5C;
            return this;
        }

        public JWKMatcher build() {
            return new JWKMatcher(this.types, this.uses, this.ops, this.algs, this.ids, this.hasUse, this.hasID, this.privateOnly, this.publicOnly, this.minSizeBits, this.maxSizeBits, this.sizesBits, this.curves, this.x5tS256s, this.hasX5C);
        }
    }
}

