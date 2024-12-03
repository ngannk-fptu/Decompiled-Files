/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.federation.api;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.federation.api.FetchEntityStatementResponse;
import com.nimbusds.openid.connect.sdk.federation.entities.EntityStatement;
import net.jcip.annotations.Immutable;

@Immutable
public class FetchEntityStatementSuccessResponse
extends FetchEntityStatementResponse {
    private final EntityStatement entityStatement;

    public FetchEntityStatementSuccessResponse(EntityStatement entityStatement) {
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
        httpResponse.setEntityContentType(ContentType.APPLICATION_JOSE);
        httpResponse.setContent(this.getEntityStatement().getSignedStatement().serialize());
        return httpResponse;
    }

    public static FetchEntityStatementSuccessResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCode(200);
        httpResponse.ensureEntityContentType(ContentType.APPLICATION_JOSE);
        return new FetchEntityStatementSuccessResponse(EntityStatement.parse(httpResponse.getContent()));
    }
}

