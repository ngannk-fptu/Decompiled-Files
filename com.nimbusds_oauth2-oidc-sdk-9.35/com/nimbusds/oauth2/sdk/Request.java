/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.oauth2.sdk;

import com.nimbusds.oauth2.sdk.Message;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import java.net.URI;

public interface Request
extends Message {
    public URI getEndpointURI();

    public HTTPRequest toHTTPRequest();
}

