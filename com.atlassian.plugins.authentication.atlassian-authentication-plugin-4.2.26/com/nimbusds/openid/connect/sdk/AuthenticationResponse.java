/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.openid.connect.sdk;

import com.nimbusds.oauth2.sdk.Response;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationErrorResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import java.net.URI;

public interface AuthenticationResponse
extends Response {
    public URI getRedirectionURI();

    public State getState();

    public AuthenticationSuccessResponse toSuccessResponse();

    public AuthenticationErrorResponse toErrorResponse();
}

