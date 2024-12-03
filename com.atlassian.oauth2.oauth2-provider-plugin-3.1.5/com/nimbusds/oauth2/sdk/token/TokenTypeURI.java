/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk.token;

import com.nimbusds.oauth2.sdk.ParseException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public final class TokenTypeURI
implements Serializable {
    private static final long serialVersionUID = 1371197657238309877L;
    public static final TokenTypeURI ACCESS_TOKEN = new TokenTypeURI(URI.create("urn:ietf:params:oauth:token-type:access_token"));
    public static final TokenTypeURI REFRESH_TOKEN = new TokenTypeURI(URI.create("urn:ietf:params:oauth:token-type:refresh_token"));
    public static final TokenTypeURI ID_TOKEN = new TokenTypeURI(URI.create("urn:ietf:params:oauth:token-type:id_token"));
    public static final TokenTypeURI SAML1 = new TokenTypeURI(URI.create("urn:ietf:params:oauth:token-type:saml1"));
    public static final TokenTypeURI SAML2 = new TokenTypeURI(URI.create("urn:ietf:params:oauth:token-type:saml2"));
    public static final TokenTypeURI JWT = new TokenTypeURI(URI.create("urn:ietf:params:oauth:token-type:jwt"));
    private static final Map<String, TokenTypeURI> KNOWN_TOKEN_TYPE_URIS;
    private final URI uri;

    private TokenTypeURI(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("The URI must not be null");
        }
        this.uri = uri;
    }

    public URI getURI() {
        return this.uri;
    }

    public static TokenTypeURI parse(String uriValue) throws ParseException {
        if (uriValue == null) {
            throw new IllegalArgumentException("The URI value must not be null");
        }
        TokenTypeURI knownURI = KNOWN_TOKEN_TYPE_URIS.get(uriValue);
        if (knownURI != null) {
            return knownURI;
        }
        try {
            return new TokenTypeURI(new URI(uriValue));
        }
        catch (URISyntaxException e) {
            throw new ParseException("Illegal token type URI: " + uriValue);
        }
    }

    public String toString() {
        return this.getURI().toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TokenTypeURI that = (TokenTypeURI)o;
        return this.uri.equals(that.getURI());
    }

    public int hashCode() {
        return this.uri.hashCode();
    }

    static {
        HashMap<String, TokenTypeURI> knownTokenTypeUris = new HashMap<String, TokenTypeURI>();
        knownTokenTypeUris.put(ACCESS_TOKEN.getURI().toString(), ACCESS_TOKEN);
        knownTokenTypeUris.put(REFRESH_TOKEN.getURI().toString(), REFRESH_TOKEN);
        knownTokenTypeUris.put(ID_TOKEN.getURI().toString(), ID_TOKEN);
        knownTokenTypeUris.put(SAML1.getURI().toString(), SAML1);
        knownTokenTypeUris.put(SAML2.getURI().toString(), SAML2);
        knownTokenTypeUris.put(JWT.getURI().toString(), JWT);
        KNOWN_TOKEN_TYPE_URIS = Collections.unmodifiableMap(knownTokenTypeUris);
    }
}

