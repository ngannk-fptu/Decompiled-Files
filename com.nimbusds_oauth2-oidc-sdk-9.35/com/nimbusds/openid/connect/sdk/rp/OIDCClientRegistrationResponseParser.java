/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.rp;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.client.ClientRegistrationErrorResponse;
import com.nimbusds.oauth2.sdk.client.ClientRegistrationResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformationResponse;
import net.jcip.annotations.Immutable;

@Immutable
public class OIDCClientRegistrationResponseParser {
    public static ClientRegistrationResponse parse(HTTPResponse httpResponse) throws ParseException {
        int statusCode = httpResponse.getStatusCode();
        if (statusCode == 200 || statusCode == 201) {
            return OIDCClientInformationResponse.parse(httpResponse);
        }
        return ClientRegistrationErrorResponse.parse(httpResponse);
    }

    private OIDCClientRegistrationResponseParser() {
    }
}

