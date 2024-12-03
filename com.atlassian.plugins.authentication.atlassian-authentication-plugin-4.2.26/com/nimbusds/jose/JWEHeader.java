/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.CommonSEHeader;
import com.nimbusds.jose.CompressionAlgorithm;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.X509CertChainUtils;
import java.net.URI;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Immutable
public final class JWEHeader
extends CommonSEHeader {
    private static final long serialVersionUID = 1L;
    private static final Set<String> REGISTERED_PARAMETER_NAMES;
    private final EncryptionMethod enc;
    private final JWK epk;
    private final CompressionAlgorithm zip;
    private final Base64URL apu;
    private final Base64URL apv;
    private final Base64URL p2s;
    private final int p2c;
    private final Base64URL iv;
    private final Base64URL tag;

    public JWEHeader(JWEAlgorithm alg, EncryptionMethod enc) {
        this(alg, enc, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0, null, null, null, null);
    }

    public JWEHeader(Algorithm alg, EncryptionMethod enc, JOSEObjectType typ, String cty, Set<String> crit, URI jku, JWK jwk, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, String kid, JWK epk, CompressionAlgorithm zip, Base64URL apu, Base64URL apv, Base64URL p2s, int p2c, Base64URL iv, Base64URL tag, Map<String, Object> customParams, Base64URL parsedBase64URL) {
        super(alg, typ, cty, crit, jku, jwk, x5u, x5t, x5t256, x5c, kid, customParams, parsedBase64URL);
        if (alg.getName().equals(Algorithm.NONE.getName())) {
            throw new IllegalArgumentException("The JWE algorithm cannot be \"none\"");
        }
        if (enc == null) {
            throw new IllegalArgumentException("The encryption method \"enc\" parameter must not be null");
        }
        if (epk != null && epk.isPrivate()) {
            throw new IllegalArgumentException("Ephemeral public key should not be a private key");
        }
        this.enc = enc;
        this.epk = epk;
        this.zip = zip;
        this.apu = apu;
        this.apv = apv;
        this.p2s = p2s;
        this.p2c = p2c;
        this.iv = iv;
        this.tag = tag;
    }

    public JWEHeader(JWEHeader jweHeader) {
        this(jweHeader.getAlgorithm(), jweHeader.getEncryptionMethod(), jweHeader.getType(), jweHeader.getContentType(), jweHeader.getCriticalParams(), jweHeader.getJWKURL(), jweHeader.getJWK(), jweHeader.getX509CertURL(), jweHeader.getX509CertThumbprint(), jweHeader.getX509CertSHA256Thumbprint(), jweHeader.getX509CertChain(), jweHeader.getKeyID(), jweHeader.getEphemeralPublicKey(), jweHeader.getCompressionAlgorithm(), jweHeader.getAgreementPartyUInfo(), jweHeader.getAgreementPartyVInfo(), jweHeader.getPBES2Salt(), jweHeader.getPBES2Count(), jweHeader.getIV(), jweHeader.getAuthTag(), jweHeader.getCustomParams(), jweHeader.getParsedBase64URL());
    }

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    @Override
    public JWEAlgorithm getAlgorithm() {
        return (JWEAlgorithm)super.getAlgorithm();
    }

    public EncryptionMethod getEncryptionMethod() {
        return this.enc;
    }

    public JWK getEphemeralPublicKey() {
        return this.epk;
    }

    public CompressionAlgorithm getCompressionAlgorithm() {
        return this.zip;
    }

    public Base64URL getAgreementPartyUInfo() {
        return this.apu;
    }

    public Base64URL getAgreementPartyVInfo() {
        return this.apv;
    }

    public Base64URL getPBES2Salt() {
        return this.p2s;
    }

    public int getPBES2Count() {
        return this.p2c;
    }

    public Base64URL getIV() {
        return this.iv;
    }

    public Base64URL getAuthTag() {
        return this.tag;
    }

    @Override
    public Set<String> getIncludedParams() {
        Set<String> includedParameters = super.getIncludedParams();
        if (this.enc != null) {
            includedParameters.add("enc");
        }
        if (this.epk != null) {
            includedParameters.add("epk");
        }
        if (this.zip != null) {
            includedParameters.add("zip");
        }
        if (this.apu != null) {
            includedParameters.add("apu");
        }
        if (this.apv != null) {
            includedParameters.add("apv");
        }
        if (this.p2s != null) {
            includedParameters.add("p2s");
        }
        if (this.p2c > 0) {
            includedParameters.add("p2c");
        }
        if (this.iv != null) {
            includedParameters.add("iv");
        }
        if (this.tag != null) {
            includedParameters.add("tag");
        }
        return includedParameters;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        if (this.enc != null) {
            o.put("enc", this.enc.toString());
        }
        if (this.epk != null) {
            o.put("epk", this.epk.toJSONObject());
        }
        if (this.zip != null) {
            o.put("zip", this.zip.toString());
        }
        if (this.apu != null) {
            o.put("apu", this.apu.toString());
        }
        if (this.apv != null) {
            o.put("apv", this.apv.toString());
        }
        if (this.p2s != null) {
            o.put("p2s", this.p2s.toString());
        }
        if (this.p2c > 0) {
            o.put("p2c", this.p2c);
        }
        if (this.iv != null) {
            o.put("iv", this.iv.toString());
        }
        if (this.tag != null) {
            o.put("tag", this.tag.toString());
        }
        return o;
    }

    private static EncryptionMethod parseEncryptionMethod(JSONObject json) throws ParseException {
        return EncryptionMethod.parse(JSONObjectUtils.getString(json, "enc"));
    }

    public static JWEHeader parse(JSONObject jsonObject) throws ParseException {
        return JWEHeader.parse(jsonObject, null);
    }

    public static JWEHeader parse(JSONObject jsonObject, Base64URL parsedBase64URL) throws ParseException {
        Algorithm alg = Header.parseAlgorithm(jsonObject);
        if (!(alg instanceof JWEAlgorithm)) {
            throw new ParseException("The algorithm \"alg\" header parameter must be for encryption", 0);
        }
        EncryptionMethod enc = JWEHeader.parseEncryptionMethod(jsonObject);
        Builder header = new Builder((JWEAlgorithm)alg, enc).parsedBase64URL(parsedBase64URL);
        for (String name : jsonObject.keySet()) {
            if ("alg".equals(name) || "enc".equals(name)) continue;
            if ("typ".equals(name)) {
                String typValue = JSONObjectUtils.getString(jsonObject, name);
                if (typValue == null) continue;
                header = header.type(new JOSEObjectType(typValue));
                continue;
            }
            if ("cty".equals(name)) {
                header = header.contentType(JSONObjectUtils.getString(jsonObject, name));
                continue;
            }
            if ("crit".equals(name)) {
                List<String> critValues = JSONObjectUtils.getStringList(jsonObject, name);
                if (critValues == null) continue;
                header = header.criticalParams(new HashSet<String>(critValues));
                continue;
            }
            if ("jku".equals(name)) {
                header = header.jwkURL(JSONObjectUtils.getURI(jsonObject, name));
                continue;
            }
            if ("jwk".equals(name)) {
                JSONObject jwkObject = JSONObjectUtils.getJSONObject(jsonObject, name);
                if (jwkObject == null) continue;
                header = header.jwk(JWK.parse(jwkObject));
                continue;
            }
            if ("x5u".equals(name)) {
                header = header.x509CertURL(JSONObjectUtils.getURI(jsonObject, name));
                continue;
            }
            if ("x5t".equals(name)) {
                header = header.x509CertThumbprint(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
                continue;
            }
            if ("x5t#S256".equals(name)) {
                header = header.x509CertSHA256Thumbprint(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
                continue;
            }
            if ("x5c".equals(name)) {
                header = header.x509CertChain(X509CertChainUtils.toBase64List(JSONObjectUtils.getJSONArray(jsonObject, name)));
                continue;
            }
            if ("kid".equals(name)) {
                header = header.keyID(JSONObjectUtils.getString(jsonObject, name));
                continue;
            }
            if ("epk".equals(name)) {
                header = header.ephemeralPublicKey(JWK.parse(JSONObjectUtils.getJSONObject(jsonObject, name)));
                continue;
            }
            if ("zip".equals(name)) {
                String zipValue = JSONObjectUtils.getString(jsonObject, name);
                if (zipValue == null) continue;
                header = header.compressionAlgorithm(new CompressionAlgorithm(zipValue));
                continue;
            }
            if ("apu".equals(name)) {
                header = header.agreementPartyUInfo(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
                continue;
            }
            if ("apv".equals(name)) {
                header = header.agreementPartyVInfo(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
                continue;
            }
            if ("p2s".equals(name)) {
                header = header.pbes2Salt(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
                continue;
            }
            if ("p2c".equals(name)) {
                header = header.pbes2Count(JSONObjectUtils.getInt(jsonObject, name));
                continue;
            }
            if ("iv".equals(name)) {
                header = header.iv(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
                continue;
            }
            if ("tag".equals(name)) {
                header = header.authTag(Base64URL.from(JSONObjectUtils.getString(jsonObject, name)));
                continue;
            }
            header = header.customParam(name, jsonObject.get(name));
        }
        return header.build();
    }

    public static JWEHeader parse(String jsonString) throws ParseException {
        return JWEHeader.parse(JSONObjectUtils.parse(jsonString), null);
    }

    public static JWEHeader parse(String jsonString, Base64URL parsedBase64URL) throws ParseException {
        return JWEHeader.parse(JSONObjectUtils.parse(jsonString), parsedBase64URL);
    }

    public static JWEHeader parse(Base64URL base64URL) throws ParseException {
        return JWEHeader.parse(base64URL.decodeToString(), base64URL);
    }

    static {
        HashSet<String> p = new HashSet<String>();
        p.add("alg");
        p.add("enc");
        p.add("epk");
        p.add("zip");
        p.add("jku");
        p.add("jwk");
        p.add("x5u");
        p.add("x5t");
        p.add("x5t#S256");
        p.add("x5c");
        p.add("kid");
        p.add("typ");
        p.add("cty");
        p.add("crit");
        p.add("apu");
        p.add("apv");
        p.add("p2s");
        p.add("p2c");
        p.add("iv");
        p.add("authTag");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }

    public static class Builder {
        private final JWEAlgorithm alg;
        private final EncryptionMethod enc;
        private JOSEObjectType typ;
        private String cty;
        private Set<String> crit;
        private URI jku;
        private JWK jwk;
        private URI x5u;
        @Deprecated
        private Base64URL x5t;
        private Base64URL x5t256;
        private List<Base64> x5c;
        private String kid;
        private JWK epk;
        private CompressionAlgorithm zip;
        private Base64URL apu;
        private Base64URL apv;
        private Base64URL p2s;
        private int p2c;
        private Base64URL iv;
        private Base64URL tag;
        private Map<String, Object> customParams;
        private Base64URL parsedBase64URL;

        public Builder(JWEAlgorithm alg, EncryptionMethod enc) {
            if (alg.getName().equals(Algorithm.NONE.getName())) {
                throw new IllegalArgumentException("The JWE algorithm \"alg\" cannot be \"none\"");
            }
            this.alg = alg;
            if (enc == null) {
                throw new IllegalArgumentException("The encryption method \"enc\" parameter must not be null");
            }
            this.enc = enc;
        }

        public Builder(JWEHeader jweHeader) {
            this(jweHeader.getAlgorithm(), jweHeader.getEncryptionMethod());
            this.typ = jweHeader.getType();
            this.cty = jweHeader.getContentType();
            this.crit = jweHeader.getCriticalParams();
            this.customParams = jweHeader.getCustomParams();
            this.jku = jweHeader.getJWKURL();
            this.jwk = jweHeader.getJWK();
            this.x5u = jweHeader.getX509CertURL();
            this.x5t = jweHeader.getX509CertThumbprint();
            this.x5t256 = jweHeader.getX509CertSHA256Thumbprint();
            this.x5c = jweHeader.getX509CertChain();
            this.kid = jweHeader.getKeyID();
            this.epk = jweHeader.getEphemeralPublicKey();
            this.zip = jweHeader.getCompressionAlgorithm();
            this.apu = jweHeader.getAgreementPartyUInfo();
            this.apv = jweHeader.getAgreementPartyVInfo();
            this.p2s = jweHeader.getPBES2Salt();
            this.p2c = jweHeader.getPBES2Count();
            this.iv = jweHeader.getIV();
            this.tag = jweHeader.getAuthTag();
            this.customParams = jweHeader.getCustomParams();
        }

        public Builder type(JOSEObjectType typ) {
            this.typ = typ;
            return this;
        }

        public Builder contentType(String cty) {
            this.cty = cty;
            return this;
        }

        public Builder criticalParams(Set<String> crit) {
            this.crit = crit;
            return this;
        }

        public Builder jwkURL(URI jku) {
            this.jku = jku;
            return this;
        }

        public Builder jwk(JWK jwk) {
            this.jwk = jwk;
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

        public Builder keyID(String kid) {
            this.kid = kid;
            return this;
        }

        public Builder ephemeralPublicKey(JWK epk) {
            this.epk = epk;
            return this;
        }

        public Builder compressionAlgorithm(CompressionAlgorithm zip) {
            this.zip = zip;
            return this;
        }

        public Builder agreementPartyUInfo(Base64URL apu) {
            this.apu = apu;
            return this;
        }

        public Builder agreementPartyVInfo(Base64URL apv) {
            this.apv = apv;
            return this;
        }

        public Builder pbes2Salt(Base64URL p2s) {
            this.p2s = p2s;
            return this;
        }

        public Builder pbes2Count(int p2c) {
            if (p2c < 0) {
                throw new IllegalArgumentException("The PBES2 count parameter must not be negative");
            }
            this.p2c = p2c;
            return this;
        }

        public Builder iv(Base64URL iv) {
            this.iv = iv;
            return this;
        }

        public Builder authTag(Base64URL tag) {
            this.tag = tag;
            return this;
        }

        public Builder customParam(String name, Object value) {
            if (JWEHeader.getRegisteredParameterNames().contains(name)) {
                throw new IllegalArgumentException("The parameter name \"" + name + "\" matches a registered name");
            }
            if (this.customParams == null) {
                this.customParams = new HashMap<String, Object>();
            }
            this.customParams.put(name, value);
            return this;
        }

        public Builder customParams(Map<String, Object> customParameters) {
            this.customParams = customParameters;
            return this;
        }

        public Builder parsedBase64URL(Base64URL base64URL) {
            this.parsedBase64URL = base64URL;
            return this;
        }

        public JWEHeader build() {
            return new JWEHeader(this.alg, this.enc, this.typ, this.cty, this.crit, this.jku, this.jwk, this.x5u, this.x5t, this.x5t256, this.x5c, this.kid, this.epk, this.zip, this.apu, this.apv, this.p2s, this.p2c, this.iv, this.tag, this.customParams, this.parsedBase64URL);
        }
    }
}

