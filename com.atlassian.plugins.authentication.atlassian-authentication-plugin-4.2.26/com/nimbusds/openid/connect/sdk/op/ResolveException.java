/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk.op;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.GeneralException;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;

public class ResolveException
extends GeneralException {
    public ResolveException(ErrorObject error, AuthenticationRequest authRequest) {
        super(error.getDescription(), error, authRequest.getClientID(), authRequest.getRedirectionURI(), authRequest.getResponseMode(), authRequest.getState(), null);
    }

    public ResolveException(String exMessage, String clientMessage, AuthenticationRequest authRequest, Throwable cause) {
        super(exMessage, ResolveException.resolveErrorObject(clientMessage, authRequest), authRequest.getClientID(), authRequest.getRedirectionURI(), authRequest.getResponseMode(), authRequest.getState(), cause);
    }

    private static ErrorObject resolveErrorObject(String clientMessage, AuthenticationRequest authRequest) {
        ErrorObject errorObject = authRequest.getRequestURI() != null ? OAuth2Error.INVALID_REQUEST_URI : OAuth2Error.INVALID_REQUEST_OBJECT;
        if (clientMessage != null) {
            return errorObject.setDescription(clientMessage);
        }
        return errorObject;
    }
}

