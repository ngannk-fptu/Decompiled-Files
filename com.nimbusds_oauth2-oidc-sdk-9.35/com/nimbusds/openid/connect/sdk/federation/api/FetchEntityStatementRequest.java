/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.federation.api;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.util.MultivaluedMapUtils;
import com.nimbusds.openid.connect.sdk.federation.api.FederationAPIRequest;
import com.nimbusds.openid.connect.sdk.federation.api.OperationType;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityID;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;

@Immutable
public class FetchEntityStatementRequest
extends FederationAPIRequest {
    private final Issuer issuer;
    private final Subject subject;
    private final Audience audience;

    public FetchEntityStatementRequest(URI endpoint, Issuer issuer, Subject subject, Audience audience) {
        super(endpoint, OperationType.FETCH);
        if (issuer == null) {
            throw new IllegalArgumentException("The issuer must not be null");
        }
        this.issuer = issuer;
        this.subject = subject;
        this.audience = audience;
    }

    public FetchEntityStatementRequest(URI endpoint, EntityID issuer, EntityID subject, EntityID audience) {
        this(endpoint, new Issuer(issuer.getValue()), subject != null ? new Subject(subject.getValue()) : null, audience != null ? new Audience(audience.getValue()) : null);
    }

    public Issuer getIssuer() {
        return this.issuer;
    }

    public EntityID getIssuerEntityID() {
        return new EntityID(this.getIssuer().getValue());
    }

    public Subject getSubject() {
        return this.subject;
    }

    public EntityID getSubjectEntityID() {
        return this.getSubject() != null ? new EntityID(this.getSubject().getValue()) : null;
    }

    public Audience getAudience() {
        return this.audience;
    }

    public EntityID getAudienceEntityID() {
        return this.getAudience() != null ? new EntityID(this.getAudience().getValue()) : null;
    }

    @Override
    public Map<String, List<String>> toParameters() {
        HashMap<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("iss", Collections.singletonList(this.getIssuer().getValue()));
        if (this.getSubject() != null) {
            params.put("sub", Collections.singletonList(this.getSubject().getValue()));
        }
        if (this.getAudience() != null) {
            params.put("aud", Collections.singletonList(this.getAudience().getValue()));
        }
        return params;
    }

    public static FetchEntityStatementRequest parse(Map<String, List<String>> params) throws ParseException {
        String value = MultivaluedMapUtils.getFirstValue(params, "operation");
        if (value != null && !value.equalsIgnoreCase(OperationType.FETCH.getValue())) {
            throw new ParseException("The operation type must be fetch or unspecified");
        }
        value = MultivaluedMapUtils.getFirstValue(params, "iss");
        if (value == null) {
            throw new ParseException("Missing iss (issuer) parameter");
        }
        Issuer issuer = new Issuer(value);
        value = MultivaluedMapUtils.getFirstValue(params, "sub");
        Subject subject = null;
        if (value != null) {
            subject = new Subject(value);
        }
        value = MultivaluedMapUtils.getFirstValue(params, "aud");
        Audience audience = null;
        if (value != null) {
            audience = new Audience(value);
        }
        return new FetchEntityStatementRequest(null, issuer, subject, audience);
    }

    public static FetchEntityStatementRequest parse(HTTPRequest httpRequest) throws ParseException {
        httpRequest.ensureMethod(HTTPRequest.Method.GET);
        FetchEntityStatementRequest request = FetchEntityStatementRequest.parse(httpRequest.getQueryParameters());
        return new FetchEntityStatementRequest(httpRequest.getURI(), request.getIssuer(), request.getSubject(), request.getAudience());
    }
}

