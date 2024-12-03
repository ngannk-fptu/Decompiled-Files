/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.oauth2.sdk.AbstractOptionallyAuthenticatedRequest;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.auth.PKITLSClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.SelfSignedTLSClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.TLSClientAuthentication;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import java.net.URI;
import net.jcip.annotations.Immutable;
import net.minidev.json.JSONObject;

@Deprecated
@Immutable
public final class RequestObjectPOSTRequest
extends AbstractOptionallyAuthenticatedRequest {
    private final JWT requestObject;
    private final JSONObject requestJSONObject;

    public RequestObjectPOSTRequest(URI uri, JWT requestObject) {
        super(uri, null);
        if (requestObject == null) {
            throw new IllegalArgumentException("The request object must not be null");
        }
        if (requestObject instanceof PlainJWT) {
            throw new IllegalArgumentException("The request object must not be an unsecured JWT (alg=none)");
        }
        this.requestObject = requestObject;
        this.requestJSONObject = null;
    }

    public RequestObjectPOSTRequest(URI uri, TLSClientAuthentication tlsClientAuth, JSONObject requestJSONObject) {
        super(uri, tlsClientAuth);
        if (tlsClientAuth == null) {
            throw new IllegalArgumentException("The mutual TLS client authentication must not be null");
        }
        if (requestJSONObject == null) {
            throw new IllegalArgumentException("The request JSON object must not be null");
        }
        this.requestJSONObject = requestJSONObject;
        this.requestObject = null;
    }

    public JWT getRequestObject() {
        return this.requestObject;
    }

    public JSONObject getRequestJSONObject() {
        return this.requestJSONObject;
    }

    public TLSClientAuthentication getTLSClientAuthentication() {
        return (TLSClientAuthentication)this.getClientAuthentication();
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, this.getEndpointURI());
        if (this.getRequestObject() != null) {
            httpRequest.setEntityContentType(ContentType.APPLICATION_JWT);
            httpRequest.setQuery(this.getRequestObject().serialize());
        } else if (this.getRequestJSONObject() != null) {
            httpRequest.setEntityContentType(ContentType.APPLICATION_JSON);
            httpRequest.setQuery(this.getRequestJSONObject().toJSONString());
            this.getTLSClientAuthentication().applyTo(httpRequest);
        }
        return httpRequest;
    }

    public static RequestObjectPOSTRequest parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        if (httpRequest.getEntityContentType() == null) {
            throw new ParseException("Missing Content-Type");
        }
        if (ContentType.APPLICATION_JOSE.matches(httpRequest.getEntityContentType()) || ContentType.APPLICATION_JWT.matches(httpRequest.getEntityContentType())) {
            JWT requestObject;
            try {
                requestObject = JWTParser.parse(httpRequest.getQuery());
            }
            catch (java.text.ParseException e) {
                throw new ParseException("Invalid request object JWT: " + e.getMessage());
            }
            if (requestObject instanceof PlainJWT) {
                throw new ParseException("The request object is an unsecured JWT (alg=none)");
            }
            return new RequestObjectPOSTRequest(httpRequest.getURI(), requestObject);
        }
        if (ContentType.APPLICATION_JSON.matches(httpRequest.getEntityContentType())) {
            TLSClientAuthentication tlsClientAuth;
            JSONObject jsonObject = httpRequest.getQueryAsJSONObject();
            if (jsonObject.get("client_id") == null) {
                throw new ParseException("Missing client_id in JSON object");
            }
            ClientID clientID = new ClientID(JSONObjectUtils.getString(jsonObject, "client_id"));
            if (httpRequest.getClientX509Certificate() != null && httpRequest.getClientX509CertificateSubjectDN() != null && httpRequest.getClientX509CertificateSubjectDN().equals(httpRequest.getClientX509CertificateRootDN())) {
                tlsClientAuth = new SelfSignedTLSClientAuthentication(clientID, httpRequest.getClientX509Certificate());
            } else if (httpRequest.getClientX509Certificate() != null) {
                tlsClientAuth = new PKITLSClientAuthentication(clientID, httpRequest.getClientX509Certificate());
            } else {
                throw new ParseException("Missing mutual TLS client authentication");
            }
            return new RequestObjectPOSTRequest(httpRequest.getURI(), tlsClientAuth, jsonObject);
        }
        throw new ParseException("Unexpected Content-Type: " + httpRequest.getEntityContentType());
    }
}

