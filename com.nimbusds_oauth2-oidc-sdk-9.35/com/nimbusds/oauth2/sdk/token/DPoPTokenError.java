/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.JWSAlgorithm
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.TokenSchemeError;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.jcip.annotations.Immutable;

@Immutable
public class DPoPTokenError
extends TokenSchemeError {
    private static final long serialVersionUID = 7399517620661603486L;
    static final Pattern ALGS_PATTERN = Pattern.compile("algs=\"([^\"]+)");
    public static final DPoPTokenError MISSING_TOKEN = new DPoPTokenError(null, null, 401);
    public static final DPoPTokenError INVALID_REQUEST = new DPoPTokenError("invalid_request", "Invalid request", 400);
    public static final DPoPTokenError INVALID_TOKEN = new DPoPTokenError("invalid_token", "Invalid access token", 401);
    public static final DPoPTokenError INSUFFICIENT_SCOPE = new DPoPTokenError("insufficient_scope", "Insufficient scope", 403);
    public static final DPoPTokenError INVALID_DPOP_PROOF = new DPoPTokenError("invalid_dpop_proof", "Invalid DPoP proof", 401);
    private final Set<JWSAlgorithm> jwsAlgs;

    public DPoPTokenError(String code, String description) {
        this(code, description, 0, null, null, null);
    }

    public DPoPTokenError(String code, String description, int httpStatusCode) {
        this(code, description, httpStatusCode, null, null, null);
    }

    public DPoPTokenError(String code, String description, int httpStatusCode, URI uri, String realm, Scope scope) {
        this(code, description, httpStatusCode, uri, realm, scope, null);
    }

    public DPoPTokenError(String code, String description, int httpStatusCode, URI uri, String realm, Scope scope, Set<JWSAlgorithm> jwsAlgs) {
        super(AccessTokenType.DPOP, code, description, httpStatusCode, uri, realm, scope);
        this.jwsAlgs = jwsAlgs;
    }

    @Override
    public DPoPTokenError setDescription(String description) {
        return new DPoPTokenError(this.getCode(), description, this.getHTTPStatusCode(), this.getURI(), this.getRealm(), this.getScope(), this.getJWSAlgorithms());
    }

    @Override
    public DPoPTokenError appendDescription(String text) {
        String newDescription = this.getDescription() != null ? this.getDescription() + text : text;
        return new DPoPTokenError(this.getCode(), newDescription, this.getHTTPStatusCode(), this.getURI(), this.getRealm(), this.getScope(), this.getJWSAlgorithms());
    }

    @Override
    public DPoPTokenError setHTTPStatusCode(int httpStatusCode) {
        return new DPoPTokenError(this.getCode(), this.getDescription(), httpStatusCode, this.getURI(), this.getRealm(), this.getScope(), this.getJWSAlgorithms());
    }

    @Override
    public DPoPTokenError setURI(URI uri) {
        return new DPoPTokenError(this.getCode(), this.getDescription(), this.getHTTPStatusCode(), uri, this.getRealm(), this.getScope(), this.getJWSAlgorithms());
    }

    @Override
    public DPoPTokenError setRealm(String realm) {
        return new DPoPTokenError(this.getCode(), this.getDescription(), this.getHTTPStatusCode(), this.getURI(), realm, this.getScope(), this.getJWSAlgorithms());
    }

    @Override
    public DPoPTokenError setScope(Scope scope) {
        return new DPoPTokenError(this.getCode(), this.getDescription(), this.getHTTPStatusCode(), this.getURI(), this.getRealm(), scope, this.getJWSAlgorithms());
    }

    public Set<JWSAlgorithm> getJWSAlgorithms() {
        return this.jwsAlgs;
    }

    public DPoPTokenError setJWSAlgorithms(Set<JWSAlgorithm> jwsAlgs) {
        return new DPoPTokenError(this.getCode(), this.getDescription(), this.getHTTPStatusCode(), this.getURI(), this.getRealm(), this.getScope(), jwsAlgs);
    }

    @Override
    public String toWWWAuthenticateHeader() {
        String header = super.toWWWAuthenticateHeader();
        if (CollectionUtils.isEmpty(this.getJWSAlgorithms())) {
            return header;
        }
        StringBuilder sb = new StringBuilder(header);
        if (header.contains("=")) {
            sb.append(',');
        }
        sb.append(" algs=\"");
        String delim = "";
        for (JWSAlgorithm alg : this.getJWSAlgorithms()) {
            sb.append(delim);
            delim = " ";
            sb.append(alg.getName());
        }
        sb.append("\"");
        return sb.toString();
    }

    public static DPoPTokenError parse(String wwwAuth) throws ParseException {
        TokenSchemeError genericError = TokenSchemeError.parse(wwwAuth, AccessTokenType.DPOP);
        HashSet<JWSAlgorithm> jwsAlgs = null;
        Matcher m = ALGS_PATTERN.matcher(wwwAuth);
        if (m.find()) {
            String algsString = m.group(1);
            jwsAlgs = new HashSet<JWSAlgorithm>();
            for (String algName : algsString.split("\\s+")) {
                jwsAlgs.add(JWSAlgorithm.parse((String)algName));
            }
        }
        return new DPoPTokenError(genericError.getCode(), genericError.getDescription(), genericError.getHTTPStatusCode(), genericError.getURI(), genericError.getRealm(), genericError.getScope(), jwsAlgs);
    }
}

