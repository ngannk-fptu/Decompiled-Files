/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  com.nimbusds.jose.JWSAlgorithm
 *  com.nimbusds.jose.JWSObject$State
 *  com.nimbusds.jwt.SignedJWT
 */
package com.nimbusds.oauth2.sdk.auth;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.ClientSecretJWT;
import com.nimbusds.oauth2.sdk.auth.JWTAuthenticationClaimsSet;
import com.nimbusds.oauth2.sdk.auth.PrivateKeyJWT;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.oauth2.sdk.util.URLUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class JWTAuthentication
extends ClientAuthentication {
    public static final String CLIENT_ASSERTION_TYPE = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
    private final SignedJWT clientAssertion;
    private final JWTAuthenticationClaimsSet jwtAuthClaimsSet;

    private static ClientID parseClientID(SignedJWT jwt) {
        String issuerValue;
        String subjectValue;
        try {
            subjectValue = jwt.getJWTClaimsSet().getSubject();
            issuerValue = jwt.getJWTClaimsSet().getIssuer();
        }
        catch (java.text.ParseException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
        if (subjectValue == null) {
            throw new IllegalArgumentException("Missing subject in client JWT assertion");
        }
        if (issuerValue == null) {
            throw new IllegalArgumentException("Missing issuer in client JWT assertion");
        }
        if (!subjectValue.equals(issuerValue)) {
            throw new IllegalArgumentException("Issuer and subject in client JWT assertion must designate the same client identifier");
        }
        return new ClientID(subjectValue);
    }

    protected JWTAuthentication(ClientAuthenticationMethod method, SignedJWT clientAssertion) {
        super(method, JWTAuthentication.parseClientID(clientAssertion));
        if (!clientAssertion.getState().equals((Object)JWSObject.State.SIGNED)) {
            throw new IllegalArgumentException("The client assertion JWT must be signed");
        }
        this.clientAssertion = clientAssertion;
        try {
            this.jwtAuthClaimsSet = JWTAuthenticationClaimsSet.parse(clientAssertion.getJWTClaimsSet());
        }
        catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    public SignedJWT getClientAssertion() {
        return this.clientAssertion;
    }

    public JWTAuthenticationClaimsSet getJWTAuthenticationClaimsSet() {
        return this.jwtAuthClaimsSet;
    }

    @Override
    public Set<String> getFormParameterNames() {
        return Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("client_assertion", "client_assertion_type", "client_id")));
    }

    public Map<String, List<String>> toParameters() {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        try {
            params.put("client_assertion", Collections.singletonList(this.clientAssertion.serialize()));
        }
        catch (IllegalStateException e) {
            throw new SerializeException("Couldn't serialize JWT to a client assertion string: " + e.getMessage(), e);
        }
        params.put("client_assertion_type", Collections.singletonList(CLIENT_ASSERTION_TYPE));
        return params;
    }

    @Override
    public void applyTo(HTTPRequest httpRequest) {
        if (httpRequest.getMethod() != HTTPRequest.Method.POST) {
            throw new SerializeException("The HTTP request method must be POST");
        }
        ContentType ct = httpRequest.getEntityContentType();
        if (ct == null) {
            throw new SerializeException("Missing HTTP Content-Type header");
        }
        if (!ct.matches(ContentType.APPLICATION_URLENCODED)) {
            throw new SerializeException("The HTTP Content-Type header must be " + ContentType.APPLICATION_URLENCODED);
        }
        Map<String, List<String>> params = httpRequest.getQueryParameters();
        params.putAll(this.toParameters());
        String queryString = URLUtils.serializeParameters(params);
        httpRequest.setQuery(queryString);
    }

    protected static void ensureClientAssertionType(Map<String, List<String>> params) throws ParseException {
        String clientAssertionType = MultivaluedMapUtils.getFirstValue(params, "client_assertion_type");
        if (clientAssertionType == null) {
            throw new ParseException("Missing client_assertion_type parameter");
        }
        if (!clientAssertionType.equals(CLIENT_ASSERTION_TYPE)) {
            throw new ParseException("Invalid client_assertion_type parameter, must be urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        }
    }

    protected static SignedJWT parseClientAssertion(Map<String, List<String>> params) throws ParseException {
        String clientAssertion = MultivaluedMapUtils.getFirstValue(params, "client_assertion");
        if (clientAssertion == null) {
            throw new ParseException("Missing client_assertion parameter");
        }
        try {
            return SignedJWT.parse((String)clientAssertion);
        }
        catch (java.text.ParseException e) {
            throw new ParseException("Invalid client_assertion JWT: " + e.getMessage(), e);
        }
    }

    protected static ClientID parseClientID(Map<String, List<String>> params) {
        String clientIDString = MultivaluedMapUtils.getFirstValue(params, "client_id");
        if (clientIDString == null) {
            return null;
        }
        return new ClientID(clientIDString);
    }

    public static JWTAuthentication parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        httpRequest.ensureEntityContentType(ContentType.APPLICATION_URLENCODED);
        String query = httpRequest.getQuery();
        if (query == null) {
            throw new ParseException("Missing HTTP POST request entity body");
        }
        Map<String, List<String>> params = URLUtils.parseParameters(query);
        JWSAlgorithm alg = JWTAuthentication.parseClientAssertion(params).getHeader().getAlgorithm();
        if (ClientSecretJWT.supportedJWAs().contains(alg)) {
            return ClientSecretJWT.parse(params);
        }
        if (PrivateKeyJWT.supportedJWAs().contains(alg)) {
            return PrivateKeyJWT.parse(params);
        }
        throw new ParseException("Unsupported signed JWT algorithm: " + alg);
    }
}

