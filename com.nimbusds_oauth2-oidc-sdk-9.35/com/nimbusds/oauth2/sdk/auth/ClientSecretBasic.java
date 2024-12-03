/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.jose.util.Base64
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.auth;

import com.nimbusds.jose.util.Base64;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.PlainClientSecret;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import net.jcip.annotations.Immutable;

@Immutable
public final class ClientSecretBasic
extends PlainClientSecret {
    private static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;

    public ClientSecretBasic(ClientID clientID, Secret secret) {
        super(ClientAuthenticationMethod.CLIENT_SECRET_BASIC, clientID, secret);
    }

    @Override
    public Set<String> getFormParameterNames() {
        return Collections.emptySet();
    }

    public String toHTTPAuthorizationHeader() {
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(URLEncoder.encode(this.getClientID().getValue(), UTF8_CHARSET.name()));
            sb.append(':');
            sb.append(URLEncoder.encode(this.getClientSecret().getValue(), UTF8_CHARSET.name()));
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
        return "Basic " + Base64.encode((byte[])sb.toString().getBytes(UTF8_CHARSET));
    }

    @Override
    public void applyTo(HTTPRequest httpRequest) {
        httpRequest.setAuthorization(this.toHTTPAuthorizationHeader());
    }

    public static ClientSecretBasic parse(String header) throws ParseException {
        String[] parts = header.split("\\s");
        if (parts.length != 2) {
            throw new ParseException("Malformed client secret basic authentication (see RFC 6749, section 2.3.1): Unexpected number of HTTP Authorization header value parts: " + parts.length);
        }
        if (!parts[0].equalsIgnoreCase("Basic")) {
            throw new ParseException("HTTP authentication must be Basic");
        }
        String credentialsString = new String(new Base64(parts[1]).decode(), UTF8_CHARSET);
        String[] credentials = credentialsString.split(":", 2);
        if (credentials.length != 2) {
            throw new ParseException("Malformed client secret basic authentication (see RFC 6749, section 2.3.1): Missing credentials delimiter (:)");
        }
        try {
            String decodedClientID = URLDecoder.decode(credentials[0], UTF8_CHARSET.name());
            String decodedSecret = URLDecoder.decode(credentials[1], UTF8_CHARSET.name());
            return new ClientSecretBasic(new ClientID(decodedClientID), new Secret(decodedSecret));
        }
        catch (UnsupportedEncodingException | IllegalArgumentException e) {
            throw new ParseException("Malformed client secret basic authentication (see RFC 6749, section 2.3.1): Invalid URL encoding", e);
        }
    }

    public static ClientSecretBasic parse(HTTPRequest httpRequest) throws ParseException {
        String header = httpRequest.getAuthorization();
        if (header == null) {
            throw new ParseException("Missing HTTP Authorization header");
        }
        return ClientSecretBasic.parse(header);
    }
}

