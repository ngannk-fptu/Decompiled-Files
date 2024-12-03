/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TokenSchemeError
extends ErrorObject {
    private static final long serialVersionUID = -1132784406578139418L;
    static final Pattern REALM_PATTERN = Pattern.compile("realm=\"(([^\\\\\"]|\\\\.){0,256})\"");
    static final Pattern ERROR_PATTERN = Pattern.compile("error=(\"([\\w\\_-]+)\"|([\\w\\_-]+))");
    static final Pattern ERROR_DESCRIPTION_PATTERN = Pattern.compile("error_description=\"([^\"]+)\"");
    static final Pattern ERROR_URI_PATTERN = Pattern.compile("error_uri=\"([^\"]+)\"");
    static final Pattern SCOPE_PATTERN = Pattern.compile("scope=\"([^\"]+)");
    private final AccessTokenType scheme;
    private final String realm;
    private final Scope scope;

    public static boolean isScopeWithValidChars(Scope scope) {
        return ErrorObject.isLegal(scope.toString());
    }

    protected TokenSchemeError(AccessTokenType scheme, String code, String description, int httpStatusCode, URI uri, String realm, Scope scope) {
        super(code, description, httpStatusCode, uri);
        if (scheme == null) {
            throw new IllegalArgumentException("The token scheme must not be null");
        }
        this.scheme = scheme;
        this.realm = realm;
        this.scope = scope;
        if (scope != null && !TokenSchemeError.isScopeWithValidChars(scope)) {
            throw new IllegalArgumentException("The scope contains illegal characters, see RFC 6750, section 3");
        }
    }

    public AccessTokenType getScheme() {
        return this.scheme;
    }

    public String getRealm() {
        return this.realm;
    }

    public Scope getScope() {
        return this.scope;
    }

    @Override
    public abstract TokenSchemeError setDescription(String var1);

    @Override
    public abstract TokenSchemeError appendDescription(String var1);

    @Override
    public abstract TokenSchemeError setHTTPStatusCode(int var1);

    @Override
    public abstract TokenSchemeError setURI(URI var1);

    public abstract TokenSchemeError setRealm(String var1);

    public abstract TokenSchemeError setScope(Scope var1);

    public String toWWWAuthenticateHeader() {
        StringBuilder sb = new StringBuilder(this.getScheme().getValue());
        int numParams = 0;
        if (this.getRealm() != null) {
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
                sb.append(',');
                sb.append(" error_description=\"");
                sb.append(this.getDescription());
                sb.append('\"');
                ++numParams;
            }
            if (this.getURI() != null) {
                sb.append(',');
                sb.append(" error_uri=\"");
                sb.append(this.getURI().toString());
                sb.append('\"');
                ++numParams;
            }
        }
        if (this.getScope() != null) {
            if (numParams > 0) {
                sb.append(',');
            }
            sb.append(" scope=\"");
            sb.append(this.getScope().toString());
            sb.append('\"');
        }
        return sb.toString();
    }

    static TokenSchemeError parse(String wwwAuth, AccessTokenType scheme) throws ParseException {
        if (!wwwAuth.regionMatches(true, 0, scheme.getValue(), 0, scheme.getValue().length())) {
            throw new ParseException("WWW-Authenticate scheme must be OAuth 2.0 DPoP");
        }
        Matcher m = REALM_PATTERN.matcher(wwwAuth);
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
        m = ERROR_PATTERN.matcher(wwwAuth);
        if (m.find()) {
            String string = errorCode = m.group(2) != null ? m.group(2) : m.group(3);
            if (!ErrorObject.isLegal(errorCode)) {
                errorCode = null;
            }
            if ((m = ERROR_DESCRIPTION_PATTERN.matcher(wwwAuth)).find()) {
                errorDescription = m.group(1);
            }
            if ((m = ERROR_URI_PATTERN.matcher(wwwAuth)).find()) {
                try {
                    errorURI = new URI(m.group(1));
                }
                catch (URISyntaxException uRISyntaxException) {
                    // empty catch block
                }
            }
        }
        Scope scope = null;
        m = SCOPE_PATTERN.matcher(wwwAuth);
        if (m.find()) {
            scope = Scope.parse(m.group(1));
        }
        return new TokenSchemeError(AccessTokenType.UNKNOWN, errorCode, errorDescription, 0, errorURI, realm, scope){
            private static final long serialVersionUID = -1629382220440634919L;

            @Override
            public TokenSchemeError setDescription(String description) {
                return null;
            }

            @Override
            public TokenSchemeError appendDescription(String text) {
                return null;
            }

            @Override
            public TokenSchemeError setHTTPStatusCode(int httpStatusCode) {
                return null;
            }

            @Override
            public TokenSchemeError setURI(URI uri) {
                return null;
            }

            @Override
            public TokenSchemeError setRealm(String realm) {
                return null;
            }

            @Override
            public TokenSchemeError setScope(Scope scope) {
                return null;
            }
        };
    }
}

