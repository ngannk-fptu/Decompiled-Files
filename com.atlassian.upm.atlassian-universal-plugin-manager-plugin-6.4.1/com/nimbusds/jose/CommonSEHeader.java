/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minidev.json.JSONObject
 */
package com.nimbusds.jose;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jose.util.Base64URL;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minidev.json.JSONObject;

abstract class CommonSEHeader
extends Header {
    private static final long serialVersionUID = 1L;
    private final URI jku;
    private final JWK jwk;
    private final URI x5u;
    private final Base64URL x5t;
    private final Base64URL x5t256;
    private final List<Base64> x5c;
    private final String kid;

    protected CommonSEHeader(Algorithm alg, JOSEObjectType typ, String cty, Set<String> crit, URI jku, JWK jwk, URI x5u, Base64URL x5t, Base64URL x5t256, List<Base64> x5c, String kid, Map<String, Object> customParams, Base64URL parsedBase64URL) {
        super(alg, typ, cty, crit, customParams, parsedBase64URL);
        this.jku = jku;
        this.jwk = jwk;
        this.x5u = x5u;
        this.x5t = x5t;
        this.x5t256 = x5t256;
        this.x5c = x5c != null ? Collections.unmodifiableList(new ArrayList<Base64>(x5c)) : null;
        this.kid = kid;
    }

    public URI getJWKURL() {
        return this.jku;
    }

    public JWK getJWK() {
        return this.jwk;
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
        return this.x5c;
    }

    public String getKeyID() {
        return this.kid;
    }

    @Override
    public Set<String> getIncludedParams() {
        Set<String> includedParameters = super.getIncludedParams();
        if (this.jku != null) {
            includedParameters.add("jku");
        }
        if (this.jwk != null) {
            includedParameters.add("jwk");
        }
        if (this.x5u != null) {
            includedParameters.add("x5u");
        }
        if (this.x5t != null) {
            includedParameters.add("x5t");
        }
        if (this.x5t256 != null) {
            includedParameters.add("x5t#S256");
        }
        if (this.x5c != null && !this.x5c.isEmpty()) {
            includedParameters.add("x5c");
        }
        if (this.kid != null) {
            includedParameters.add("kid");
        }
        return includedParameters;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject o = super.toJSONObject();
        if (this.jku != null) {
            o.put((Object)"jku", (Object)this.jku.toString());
        }
        if (this.jwk != null) {
            o.put((Object)"jwk", (Object)this.jwk.toJSONObject());
        }
        if (this.x5u != null) {
            o.put((Object)"x5u", (Object)this.x5u.toString());
        }
        if (this.x5t != null) {
            o.put((Object)"x5t", (Object)this.x5t.toString());
        }
        if (this.x5t256 != null) {
            o.put((Object)"x5t#S256", (Object)this.x5t256.toString());
        }
        if (this.x5c != null && !this.x5c.isEmpty()) {
            o.put((Object)"x5c", this.x5c);
        }
        if (this.kid != null) {
            o.put((Object)"kid", (Object)this.kid);
        }
        return o;
    }
}

