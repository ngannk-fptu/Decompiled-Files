/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.rp;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.client.ClientInformationResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import net.jcip.annotations.Immutable;

@Immutable
public class OIDCClientInformationResponse
extends ClientInformationResponse {
    public OIDCClientInformationResponse(OIDCClientInformation clientInfo, boolean forNewClient) {
        super(clientInfo, forNewClient);
    }

    public OIDCClientInformation getOIDCClientInformation() {
        return (OIDCClientInformation)this.getClientInformation();
    }

    public static OIDCClientInformationResponse parse(HTTPResponse httpResponse) throws ParseException {
        httpResponse.ensureStatusCode(200, 201);
        OIDCClientInformation clientInfo = OIDCClientInformation.parse(httpResponse.getContentAsJSONObject());
        boolean forNewClient = 201 == httpResponse.getStatusCode();
        return new OIDCClientInformationResponse(clientInfo, forNewClient);
    }
}

