/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  com.nimbusds.jwt.SignedJWT
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.federation.registration;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AbstractRequest;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import java.net.URI;
import net.jcip.annotations.Immutable;

@Immutable
public class ExplicitClientRegistrationRequest
extends AbstractRequest {
    private final EntityStatement entityStatement;

    public ExplicitClientRegistrationRequest(URI uri, EntityStatement entityStatement) {
        super(uri);
        this.entityStatement = entityStatement;
    }

    public EntityStatement getEntityStatement() {
        return this.entityStatement;
    }

    @Override
    public HTTPRequest toHTTPRequest() {
        if (this.getEndpointURI() == null) {
            throw new SerializeException("The endpoint URI is not specified");
        }
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.POST, this.getEndpointURI());
        httpRequest.setEntityContentType(ContentType.APPLICATION_JOSE);
        httpRequest.setQuery(this.getEntityStatement().getSignedStatement().serialize());
        return httpRequest;
    }

    public static ExplicitClientRegistrationRequest parse(HTTPRequest httpRequest) throws ParseException {
        SignedJWT signedJWT;
        URI uri = httpRequest.getURI();
        httpRequest.ensureMethod(HTTPRequest.Method.POST);
        httpRequest.ensureEntityContentType(ContentType.APPLICATION_JOSE);
        String jwtString = httpRequest.getQuery();
        if (StringUtils.isBlank(jwtString)) {
            throw new ParseException("Missing entity body");
        }
        try {
            signedJWT = SignedJWT.parse((String)jwtString);
        }
        catch (java.text.ParseException e) {
            throw new ParseException("Invalid entity statement: " + e.getMessage(), e);
        }
        return new ExplicitClientRegistrationRequest(uri, EntityStatement.parse(signedJWT));
    }
}

