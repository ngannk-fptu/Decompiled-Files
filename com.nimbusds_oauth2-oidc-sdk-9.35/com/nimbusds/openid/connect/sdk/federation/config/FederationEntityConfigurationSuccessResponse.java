/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  com.nimbusds.jwt.SignedJWT
 */
package com.nimbusds.openid.connect.sdk.federation.config;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.federation.config.FederationEntityConfigurationResponse;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import java.nio.charset.StandardCharsets;

public class FederationEntityConfigurationSuccessResponse
extends FederationEntityConfigurationResponse {
    private static final ContentType CONTENT_TYPE = new ContentType("application", "jose", StandardCharsets.UTF_8);
    private final EntityStatement entityStatement;

    public FederationEntityConfigurationSuccessResponse(EntityStatement entityStatement) {
        if (entityStatement == null) {
            throw new IllegalArgumentException("The federation entity statement must not be null");
        }
        this.entityStatement = entityStatement;
    }

    public EntityStatement getEntityStatement() {
        return this.entityStatement;
    }

    @Override
    public boolean indicatesSuccess() {
        return true;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        HTTPResponse httpResponse = new HTTPResponse(200);
        httpResponse.setEntityContentType(CONTENT_TYPE);
        httpResponse.setContent(this.entityStatement.getSignedStatement().serialize());
        return httpResponse;
    }

    public static FederationEntityConfigurationSuccessResponse parse(HTTPResponse httpResponse) throws ParseException {
        SignedJWT signedJWT;
        httpResponse.ensureStatusCode(200);
        httpResponse.ensureEntityContentType(CONTENT_TYPE);
        String content = httpResponse.getContent();
        if (StringUtils.isBlank(content)) {
            throw new ParseException("Empty HTTP entity body");
        }
        try {
            signedJWT = SignedJWT.parse((String)httpResponse.getContent());
        }
        catch (java.text.ParseException e) {
            throw new ParseException(e.getMessage(), e);
        }
        return new FederationEntityConfigurationSuccessResponse(EntityStatement.parse(signedJWT));
    }
}

