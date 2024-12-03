/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.nimbusds.common.contenttype.ContentType
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.oauth2.sdk.client;

import com.nimbusds.common.contenttype.ContentType;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SuccessResponse;
import com.nimbusds.oauth2.sdk.client.ClientInformation;
import com.nimbusds.oauth2.sdk.client.ClientRegistrationResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import net.jcip.annotations.Immutable;

@Immutable
public class ClientInformationResponse
extends ClientRegistrationResponse
implements SuccessResponse {
    private final ClientInformation clientInfo;
    private final boolean forNewClient;

    public ClientInformationResponse(ClientInformation clientInfo, boolean forNewClient) {
        if (clientInfo == null) {
            throw new IllegalArgumentException("The client information must not be null");
        }
        this.clientInfo = clientInfo;
        this.forNewClient = forNewClient;
    }

    @Override
    public boolean indicatesSuccess() {
        return true;
    }

    public ClientInformation getClientInformation() {
        return this.clientInfo;
    }

    public boolean isForNewClient() {
        return this.forNewClient;
    }

    @Override
    public HTTPResponse toHTTPResponse() {
        HTTPResponse httpResponse = new HTTPResponse(this.forNewClient ? 201 : 200);
        httpResponse.setEntityContentType(ContentType.APPLICATION_JSON);
        httpResponse.setCacheControl("no-store");
        httpResponse.setPragma("no-cache");
        httpResponse.setContent(this.clientInfo.toJSONObject().toString());
        return httpResponse;
    }

    public static ClientInformationResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCode(200, 201);
        ClientInformation clientInfo = ClientInformation.parse(httpResponse.getContentAsJSONObject());
        boolean forNewClient = 201 == httpResponse.getStatusCode();
        return new ClientInformationResponse(clientInfo, forNewClient);
    }
}

