/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.jcip.annotations.Immutable;

@Immutable
public class BearerTokenError
extends ErrorObject {
    public static final BearerTokenError MISSING_TOKEN = new BearerTokenError(null, null, 401);
    public static final BearerTokenError INVALID_REQUEST = new BearerTokenError("invalid_request", "Invalid request", 400);
    public static final BearerTokenError INVALID_TOKEN = new BearerTokenError("invalid_token", "Invalid access token", 401);
    public static final BearerTokenError INSUFFICIENT_SCOPE = new BearerTokenError("insufficient_scope", "Insufficient scope", 403);
    private static final Pattern realmPattern = Pattern.compile("realm=\"(([^\\\\\"]|\\\\.)*)\"");
    private static final Pattern errorPattern = Pattern.compile("error=(\"([\\w\\_-]+)\"|([\\w\\_-]+))");
    private static final Pattern errorDescriptionPattern = Pattern.compile("error_description=\"([^\"]+)\"");
    private static final Pattern errorURIPattern = Pattern.compile("error_uri=\"([^\"]+)\"");
    private static final Pattern scopePattern = Pattern.compile("scope=\"([^\"]+)");
    private final String realm;
    private final Scope scope;

    @Deprecated
    public static boolean isCodeWithValidChars(String errorCode) {
        return ErrorObject.isLegal(errorCode);
    }

    @Deprecated
    public static boolean isDescriptionWithValidChars(String errorDescription) {
        return ErrorObject.isLegal(errorDescription);
    }

    public static boolean isScopeWithValidChars(Scope scope) {
        return ErrorObject.isLegal(scope.toString());
    }

    public BearerTokenError(String code, String description) {
        this(code, description, 0, null, null, null);
    }

    public BearerTokenError(String code, String description, int httpStatusCode) {
        this(code, description, httpStatusCode, null, null, null);
    }

    public BearerTokenError(String code, String description, int httpStatusCode, URI uri, String realm, Scope scope) {
        super(code, description, httpStatusCode, uri);
        this.realm = realm;
        this.scope = scope;
        if (scope != null && !BearerTokenError.isScopeWithValidChars(scope)) {
            throw new IllegalArgumentException("The scope contains illegal characters, see RFC 6750, section 3");
        }
    }

    @Override
    public BearerTokenError setDescription(String description) {
        return new BearerTokenError(super.getCode(), description, super.getHTTPStatusCode(), super.getURI(), this.realm, this.scope);
    }

    @Override
    public BearerTokenError appendDescription(String text) {
        String newDescription = this.getDescription() != null ? this.getDescription() + text : text;
        return new BearerTokenError(super.getCode(), newDescription, super.getHTTPStatusCode(), super.getURI(), this.realm, this.scope);
    }

    @Override
    public BearerTokenError setHTTPStatusCode(int httpStatusCode) {
        return new BearerTokenError(super.getCode(), super.getDescription(), httpStatusCode, super.getURI(), this.realm, this.scope);
    }

    @Override
    public BearerTokenError setURI(URI uri) {
        return new BearerTokenError(super.getCode(), super.getDescription(), super.getHTTPStatusCode(), uri, this.realm, this.scope);
    }

    public String getRealm() {
        return this.realm;
    }

    public BearerTokenError setRealm(String realm) {
        return new BearerTokenError(this.getCode(), this.getDescription(), this.getHTTPStatusCode(), this.getURI(), realm, this.getScope());
    }

    public Scope getScope() {
        return this.scope;
    }

    public BearerTokenError setScope(Scope scope) {
        return new BearerTokenError(this.getCode(), this.getDescription(), this.getHTTPStatusCode(), this.getURI(), this.getRealm(), scope);
    }

    public String toWWWAuthenticateHeader() {
        StringBuilder sb = new StringBuilder("Bearer");
        int numParams = 0;
        if (this.realm != null) {
            sb.append(" realm=\"");
            sb.append(this.getRealm().replaceAll("\"", "\\\\\""));
            sb.append('\"');
            ++numParams;
        }
        if (this.getCode() != null) {
            if (numParams > 0) {
                sb.append(',');
            }
            sb.append(" error=\"");
            sb.append(this.getCode());
            sb.append('\"');
            ++numParams;
            if (this.getDescription() != null) {
                if (numParams > 0) {
                    sb.append(',');
                }
                sb.append(" error_description=\"");
                sb.append(this.getDescription());
                sb.append('\"');
                ++numParams;
            }
            if (this.getURI() != null) {
                if (numParams > 0) {
                    sb.append(',');
                }
                sb.append(" error_uri=\"");
                sb.append(this.getURI().toString());
                sb.append('\"');
                ++numParams;
            }
        }
        if (this.scope != null) {
            if (numParams > 0) {
                sb.append(',');
            }
            sb.append(" scope=\"");
            sb.append(this.scope.toString());
            sb.append('\"');
        }
        return sb.toString();
    }

    public static BearerTokenError parse(String wwwAuth) throws ParseException {
        if (!wwwAuth.regionMatches(true, 0, "Bearer", 0, "Bearer".length())) {
            throw new ParseException("WWW-Authenticate scheme must be OAuth 2.0 Bearer");
        }
        Matcher m = realmPattern.matcher(wwwAuth);
        String realm = null;
        if (m.find()) {
            realm = m.group(1);
        }
        if (realm != null) {
            realm = realm.replace("\\\"", "\"");
        }
        String errorCode = null;
        String errorDescription = null;
        URI errorURI = null;
        m = errorPattern.matcher(wwwAuth);
        if (m.find()) {
            String string = errorCode = m.group(2) != null ? m.group(2) : m.group(3);
            if (!ErrorObject.isLegal(errorCode)) {
                errorCode = null;
            }
            if ((m = errorDescriptionPattern.matcher(wwwAuth)).find()) {
                errorDescription = m.group(1);
            }
            if ((m = errorURIPattern.matcher(wwwAuth)).find()) {
                try {
                    errorURI = new URI(m.group(1));
                }
                catch (URISyntaxException uRISyntaxException) {
                    // empty catch block
                }
            }
        }
        Scope scope = null;
        m = scopePattern.matcher(wwwAuth);
        if (m.find()) {
            scope = Scope.parse(m.group(1));
        }
        return new BearerTokenError(errorCode, errorDescription, 0, errorURI, realm, scope);
    }
}

